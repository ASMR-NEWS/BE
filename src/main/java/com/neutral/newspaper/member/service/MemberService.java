package com.neutral.newspaper.member.service;

import com.neutral.newspaper.global.CustomException;
import com.neutral.newspaper.global.ErrorType;
import com.neutral.newspaper.interest.InterestRepository;
import com.neutral.newspaper.interest.domain.Interest;
import com.neutral.newspaper.jwt.JwtToken;
import com.neutral.newspaper.jwt.JwtTokenProvider;
import com.neutral.newspaper.member.MemberRepository;
import com.neutral.newspaper.member.domain.Member;
import com.neutral.newspaper.member.dto.FindPasswordDto;
import com.neutral.newspaper.member.dto.JoinRequestDto;
import com.neutral.newspaper.member.dto.LoginRequestDto;
import com.neutral.newspaper.member.dto.ResetPasswordDto;
import com.neutral.newspaper.member.dto.UpdatePasswordDto;
import com.neutral.newspaper.member.dto.VerifyCodeDto;
import com.neutral.newspaper.redis.RedisService;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final InterestRepository interestRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisService redisService;
    private final EmailService emailService;

    @Transactional
    public String registerMember(JoinRequestDto joinRequest) {
        if (memberRepository.findByEmail(joinRequest.getEmail()).isPresent()) {
            throw new CustomException(ErrorType.DUPLICATED_EMAIL);
        }

        if (!isValidPassword(joinRequest.getPassword())) {
            throw new CustomException(ErrorType.INVALID_PASSWORD_FORMAT);
        }

        String encodedPassword = passwordEncoder.encode(joinRequest.getPassword());

        if (!isValidPhoneNumber(joinRequest.getPhoneNumber())) {
            throw new CustomException(ErrorType.INVALID_PHONE_NUMBER_FORMAT);
        }

        Member member = Member.builder()
                .name(joinRequest.getName())
                .email(joinRequest.getEmail())
                .password(encodedPassword)
                .phoneNumber(joinRequest.getPhoneNumber())
                .build();

        List<Interest> interests = joinRequest.getInterestNames().stream()
                        .map(name -> interestRepository.findByName(name)
                                .orElseGet(() -> interestRepository.save(new Interest(name))))
                        .toList();

        member.setInterests(interests);

        memberRepository.save(member);

        return "회원가입이 완료되었습니다.";
    }

    @Transactional
    public JwtToken login(LoginRequestDto loginRequest) {
        // 존재하지 않는 회원일 경우
        Member member = memberRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new CustomException(ErrorType.NOT_REGISTERED_MEMBER));

        // 패스워드가 일치하지 않는 경우
        if (!passwordEncoder.matches(loginRequest.getPassword(), member.getPassword())) {
            throw new CustomException(ErrorType.NOT_MATCHED_PASSWORD);
        }

        Authentication authentication = new UsernamePasswordAuthenticationToken(member.getEmail(), member.getPassword());

        return jwtTokenProvider.generateToken(authentication);
    }

    @Transactional
    public void updatePassword(UpdatePasswordDto updatePasswordRequest) {
        // 현재 인증된 사용자 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //authentication.getName()은 UsernamePasswordAuthenticationToken의 첫 번째 파라미터인 email이 저장되기 때문에 getName()을 이용
        String email = authentication.getName();

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorType.MEMBER_NOT_FOUND));

        // 기존 비밀번호가 일치하지 않는 경우
        if (!passwordEncoder.matches(updatePasswordRequest.getOldPassword(), member.getPassword())) {
            throw new CustomException(ErrorType.NOT_MATCHED_PASSWORD);
        }

        // 새로운 비밀번호가 조건을 충족하지 않은 경우
        if(!isValidPassword(updatePasswordRequest.getNewPassword())) {
            throw new CustomException(ErrorType.INVALID_PASSWORD_FORMAT);
        }

        member.updatePassword(passwordEncoder.encode(updatePasswordRequest.getNewPassword()));
    }

    @Transactional
    public void sendPasswordResetCode(FindPasswordDto findPasswordRequest) {
        Member member = memberRepository.findByEmail(findPasswordRequest.getEmail())
                .orElseThrow(() -> new CustomException(ErrorType.MEMBER_NOT_FOUND));

        if (!findPasswordRequest.getPhoneNumber().equals(member.getPhoneNumber())) {
            throw new CustomException(ErrorType.NOT_MATCHED_PHONE_NUMBER);
        }

        String resetCode = generateVerificationCode();
        emailService.sendVerificationCode(member.getEmail(), "비밀번호 찾기 인증번호", "비밀번호 초기화 인증번호는 " + resetCode);
        redisService.saveData(member.getEmail(), resetCode, 5, TimeUnit.MINUTES);
    }

    @Transactional
    public void verifyCode(VerifyCodeDto verifyCodeRequest) {
        String code = redisService.getData(verifyCodeRequest.getEmail());
        if (code == null || !code.equals(verifyCodeRequest.getVerificationCode())) {
            throw new CustomException(ErrorType.NOT_MATCHED_VERIFYING_CODE);
        }

        // 인증 성공 시 별도로 인증 완료 상태를 저장
        // "user@email.com:verified"의 형식으로 key를 저장하기 위해 ":verified"도 같이 삽입
        redisService.saveData(verifyCodeRequest.getEmail() + ":verified", "true", 5, TimeUnit.MINUTES);
    }

    @Transactional
    public void resetPassword(ResetPasswordDto resetPasswordRequest) {
        Member member = memberRepository.findByEmail(resetPasswordRequest.getEmail())
                .orElseThrow(() -> new CustomException(ErrorType.MEMBER_NOT_FOUND));

        String verified = redisService.getData(resetPasswordRequest.getEmail() + ":verified");

        // verified는 String이고 null일 수도 있기 때문에 !"true".equals()를 이용
        if (!"true".equals(verified)) {
            throw new CustomException(ErrorType.UNAUTHORIZED_VERIFICATION);
        }

        if (!isValidPassword(resetPasswordRequest.getNewPassword())) {
            throw new CustomException(ErrorType.INVALID_PASSWORD_FORMAT);
        }

        member.updatePassword(passwordEncoder.encode(resetPasswordRequest.getNewPassword()));

        // 인증 성공 후 인증 완료 상태를 삭제
        redisService.deleteData(resetPasswordRequest.getEmail() + ":verified");
    }

    private boolean isValidPassword(String password) {
        return password.matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,16}$");
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber.matches("^\\d{3}-\\d{3,4}-\\d{4}$");
    }

    private String generateVerificationCode() {
        return String.valueOf((int)(Math.random() * 900000) + 100000);
    }
}

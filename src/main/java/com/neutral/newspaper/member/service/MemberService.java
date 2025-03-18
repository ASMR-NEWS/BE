package com.neutral.newspaper.member.service;

import com.neutral.newspaper.jwt.JwtToken;
import com.neutral.newspaper.jwt.JwtTokenProvider;
import com.neutral.newspaper.member.MemberRepository;
import com.neutral.newspaper.member.domain.Member;
import com.neutral.newspaper.member.dto.MemberJoinRequestDto;
import com.neutral.newspaper.member.dto.MemberLoginRequestDto;
import com.neutral.newspaper.member.dto.MemberUpdatePasswordDto;
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
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public String registerMember(MemberJoinRequestDto joinRequest) {
        if (memberRepository.findByEmail(joinRequest.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 회원입니다.");
        }

        if (!isValidPassword(joinRequest.getPassword())) {
            throw new IllegalArgumentException("비밀번호는 8~16자의 영어, 숫자, 특수기호(@$!%*?&)를 포함해야 합니다.");
        }

        String encodedPassword = passwordEncoder.encode(joinRequest.getPassword());

        Member member = Member.builder()
                .name(joinRequest.getName())
                .email(joinRequest.getEmail())
                .password(encodedPassword)
                .build();

        memberRepository.save(member);

        return "회원가입이 완료되었습니다.";
    }

    @Transactional
    public JwtToken login(MemberLoginRequestDto loginRequest) {
        // 존재하지 않는 회원일 경우
        Member member = memberRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        // 패스워드가 일치하지 않는 경우
        if (!passwordEncoder.matches(loginRequest.getPassword(), member.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 잘못되었습니다.");
        }

        Authentication authentication = new UsernamePasswordAuthenticationToken(member.getEmail(), member.getPassword());

        return jwtTokenProvider.generateToken(authentication);
    }

    @Transactional
    public void updatePassword(MemberUpdatePasswordDto updatePasswordRequest) {
        // 현재 인증된 사용자 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //authentication.getName()은 UsernamePasswordAuthenticationToken의 첫 번째 파라미터인 email이 저장되기 때문에 getName()을 이용
        String email = authentication.getName();

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

        // 기존 비밀번호가 일치하지 않는 경우
        if (!passwordEncoder.matches(updatePasswordRequest.getOldPassword(), member.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }

        // 새로운 비밀번호가 조건을 충족하지 않은 경우
        if(!isValidPassword(updatePasswordRequest.getNewPassword())) {
            throw new IllegalArgumentException("비밀번호는 8~16자의 영어, 숫자, 특수기호(@$!%*?&)를 포함해야 합니다.");
        }

        member.updatePassword(passwordEncoder.encode(updatePasswordRequest.getNewPassword()));
    }

    private boolean isValidPassword(String password) {
        return password.matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,16}$");
    }
}

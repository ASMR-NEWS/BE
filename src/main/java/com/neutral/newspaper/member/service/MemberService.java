package com.neutral.newspaper.member.service;

import com.neutral.newspaper.member.MemberRepository;
import com.neutral.newspaper.member.domain.Member;
import com.neutral.newspaper.member.dto.MemberJoinRequestDto;
import com.neutral.newspaper.member.dto.MemberLoginRequestDto;
import com.neutral.newspaper.member.dto.MemberUpdatePasswordDto;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public MemberService(MemberRepository memberRepository, BCryptPasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

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
    public String login(MemberLoginRequestDto loginRequestDto) {
        // 존재하지 않는 회원일 경우
        Member member = memberRepository.findByEmail(loginRequestDto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        // 패스워드가 일치하지 않는 경우
        if (!passwordEncoder.matches(loginRequestDto.getPassword(), member.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 잘못되었습니다.");
        }

        return "로그인이 완료되었습니다.";
    }

    @Transactional
    public void updatePassword(MemberUpdatePasswordDto updatePasswordDto) {
        Member member = memberRepository.findByEmail(updatePasswordDto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("이메일을 잘못 입력했습니다."));

        // 기존 비밀번호가 일치하지 않는 경우
        if (!passwordEncoder.matches(updatePasswordDto.getOldPassword(), member.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }

        // 새로운 비밀번호가 조건을 충족하지 않은 경우
        if(!isValidPassword(updatePasswordDto.getNewPassword())) {
            throw new IllegalArgumentException("비밀번호는 8~16자의 영어, 숫자, 특수기호(@$!%*?&)를 포함해야 합니다.");
        }

        member.updatePassword(passwordEncoder.encode(updatePasswordDto.getNewPassword()));
    }

    private boolean isValidPassword(String password) {
        return password.matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,16}$");
    }
}

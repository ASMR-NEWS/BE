package com.neutral.newspaper.member.service;

import com.neutral.newspaper.member.MemberRepository;
import com.neutral.newspaper.member.domain.Member;
import com.neutral.newspaper.member.dto.MemberJoinRequestDTO;
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
    public String registerMember(MemberJoinRequestDTO joinRequest) {
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

    private boolean isValidPassword(String password) {
        return password.matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,16}$");
    }
}

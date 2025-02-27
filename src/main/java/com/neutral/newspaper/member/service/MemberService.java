package com.neutral.newspaper.member.service;

import com.neutral.newspaper.member.MemberRepository;
import com.neutral.newspaper.member.domain.Member;
import com.neutral.newspaper.member.dto.MemberJoinRequestDTO;
import org.springframework.transaction.annotation.Transactional;

public class MemberService {
    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Transactional
    public String registerMember(MemberJoinRequestDTO joinRequest) {
        if (memberRepository.findByEmail(joinRequest.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 회원입니다.");
        }

        if (!isValidPassword(joinRequest.getPassword())) {
            throw new IllegalArgumentException("비밀번호는 8~16자의 영어, 숫자, 특수기호(@$!%*?&)를 포함해야 합니다.");
        }

        Member member = Member.builder()
                .name(joinRequest.getName())
                .email(joinRequest.getEmail())
                .password(joinRequest.getPassword())
                .build();

        memberRepository.save(member);

        return "회원가입이 완료되었습니다.";
    }

    private boolean isValidPassword(String password) {
        return password.matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,16}$");
    }
}

package com.neutral.newspaper.member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.neutral.newspaper.member.dto.JoinRequestDto;
import com.neutral.newspaper.member.service.MemberService;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class MemberServiceTest {

    @Autowired
    private MemberService memberService;

    @Nested
    @DisplayName("회원가입 테스트")
    class signUpTest {

        @Test
        @DisplayName("회원가입 성공 케이스")
        void successSignUp() {
            // given
            JoinRequestDto joinRequestDto = new JoinRequestDto(
                    "홍길동", "email@example.com", "TestPassword12!", "010-1234-5678", List.of("경제", "스포츠")
            );

            // when
            String result = memberService.registerMember(joinRequestDto);

            // then
            assertThat(result).isEqualTo("회원가입이 완료되었습니다.");
        }

        @Test
        @DisplayName("회원가입 실패 케이스: 이메일 중복")
        void failSignUpDuplicatedEmail() {
            // given
            JoinRequestDto joinRequestDto1 = new JoinRequestDto(
                    "홍길동", "email@example.com", "TestPassword12!", "010-1234-5678", List.of("경제", "스포츠")
            );
            JoinRequestDto joinRequestDto2 = new JoinRequestDto(
                    "홍길순", "email@example.com", "TestPassword12!!", "010-1234-8765", List.of("경제", "스포츠")
            );

            // when
            memberService.registerMember(joinRequestDto1);

            // then
            assertThatThrownBy(() -> memberService.registerMember(joinRequestDto2))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("회원가입 실패 케이스: 비밀번호 유효성 검사 실패")
        void failSignUpInvalidPassword() {
            // given
            JoinRequestDto joinRequestDto = new JoinRequestDto(
                    "홍길동", "email@example.com", "Test", "010-1234-5678", List.of("경제", "스포츠")
            );

            // when, then
            assertThatThrownBy(() -> memberService.registerMember(joinRequestDto))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("회원가입 실패 케이스: 휴대전화 번호 유효성 검사 실패")
        void failSignUpInvalidPhoneNumber() {
            // given
            JoinRequestDto joinRequestDto = new JoinRequestDto(
                    "홍길동", "email@example.com", "TestPassword12!", "010-1234-56789", List.of("경제", "스포츠")
            );

            // when, then
            assertThatThrownBy(() -> memberService.registerMember(joinRequestDto))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }
}

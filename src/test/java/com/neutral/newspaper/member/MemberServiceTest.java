package com.neutral.newspaper.member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

import com.neutral.newspaper.jwt.JwtToken;
import com.neutral.newspaper.member.dto.JoinRequestDto;
import com.neutral.newspaper.member.dto.LoginRequestDto;
import com.neutral.newspaper.member.dto.UpdatePasswordDto;
import com.neutral.newspaper.member.service.MemberService;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class MemberServiceTest {

    @Autowired
    private MemberService memberService;

    @Nested
    @DisplayName("회원가입 테스트")
    class SignUpTest {

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
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("이미 존재하는 회원입니다.");
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
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("비밀번호는 8~16자의 영어, 숫자, 특수기호(@$!%*?&)를 포함해야 합니다.");
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
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("휴대폰 번호는 10~11자여야 합니다.");
        }
    }

    @Nested
    @DisplayName("로그인 테스트")
    class LoginTest {

        @Test
        @DisplayName("로그인 성공 케이스")
        void successLogin() {
            // given
            JoinRequestDto joinRequestDto = new JoinRequestDto(
                    "홍길동", "email@example.com", "TestPassword12!", "010-1234-5678", List.of("경제", "스포츠")
            );
            memberService.registerMember(joinRequestDto);

            LoginRequestDto loginRequestDto = new LoginRequestDto(
                    "email@example.com", "TestPassword12!"
            );

            // when
            JwtToken jwtToken = memberService.login(loginRequestDto);

            // then
            assertNotNull(jwtToken);
        }

        @Test
        @DisplayName("로그인 실패 케이스: 존재하지 않는 회원")
        void failLoginNotRegistered() {
            // given
            JoinRequestDto joinRequestDto = new JoinRequestDto(
                    "홍길동", "email@example.com", "TestPassword12!", "010-1234-5678", List.of("경제", "스포츠")
            );
            memberService.registerMember(joinRequestDto);

            LoginRequestDto loginRequestDto = new LoginRequestDto(
                    "fail@example.com", "TestPassword12!"
            );

            // when, then
            assertThatThrownBy(() -> memberService.login(loginRequestDto))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("존재하지 않는 회원입니다.");
        }

        @Test
        @DisplayName("로그인 실패 케이스: 비밀번호 불일치")
        void failLoginPasswordMismatch() {
            // given
            JoinRequestDto joinRequestDto = new JoinRequestDto(
                    "홍길동", "email@example.com", "TestPassword12!", "010-1234-5678", List.of("경제", "스포츠")
            );
            memberService.registerMember(joinRequestDto);

            LoginRequestDto loginRequestDto = new LoginRequestDto(
                    "email@example.com", "TestPassword12!!"
            );

            // when, then
            assertThatThrownBy(() -> memberService.login(loginRequestDto))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("비밀번호가 잘못되었습니다.");
        }
    }

    @Nested
    @DisplayName("비밀번호 변경 테스트")
    class ChangePasswordTest {

        @Test
        @DisplayName("비밀번호 변경 성공 케이스")
        void successChangingPassword() {
            // given
            JoinRequestDto joinRequestDto = new JoinRequestDto(
                    "홍길동", "email@example.com", "TestPassword12!", "010-1234-5678", List.of("경제", "스포츠")
            );
            memberService.registerMember(joinRequestDto);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken("email@example.com", null);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            UpdatePasswordDto updatePasswordDto = new UpdatePasswordDto(
                    "TestPassword12!", "Password12!"
            );

            // when, then
            assertDoesNotThrow(() -> memberService.updatePassword(updatePasswordDto));
        }

        @Test
        @DisplayName("비밀번호 변경 실패 케이스: 기존 비밀번호 불일치")
        void failChangingPasswordOldPasswordMismatch() {
            // given
            JoinRequestDto joinRequestDto = new JoinRequestDto(
                    "홍길동", "email@example.com", "TestPassword12!", "010-1234-5678", List.of("경제", "스포츠")
            );
            memberService.registerMember(joinRequestDto);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken("email@example.com", null);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            UpdatePasswordDto updatePasswordDto = new UpdatePasswordDto(
                    "TestPassword12!!", "Password12!"
            );

            // when, then
            assertThatThrownBy(() -> memberService.updatePassword(updatePasswordDto))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("현재 비밀번호가 일치하지 않습니다.");
        }

        @Test
        @DisplayName("비밀번호 변경 실패 케이스: 새 비밀번호 조건 미충족")
        void failChangingPasswordInvalidNewPassword() {
            // given
            JoinRequestDto joinRequestDto = new JoinRequestDto(
                    "홍길동", "email@example.com", "TestPassword12!", "010-1234-5678", List.of("경제", "스포츠")
            );
            memberService.registerMember(joinRequestDto);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken("email@example.com", null);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            UpdatePasswordDto updatePasswordDto = new UpdatePasswordDto(
                    "TestPassword12!", "AnotherPassword12!"
            );

            // when, then
            assertThatThrownBy(() -> memberService.updatePassword(updatePasswordDto))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("비밀번호는 8~16자의 영어, 숫자, 특수기호(@$!%*?&)를 포함해야 합니다.");
        }
    }
}

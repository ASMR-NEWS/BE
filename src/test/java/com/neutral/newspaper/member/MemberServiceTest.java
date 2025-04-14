package com.neutral.newspaper.member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.neutral.newspaper.global.CustomException;
import com.neutral.newspaper.jwt.JwtToken;
import com.neutral.newspaper.member.domain.Member;
import com.neutral.newspaper.member.dto.FindPasswordDto;
import com.neutral.newspaper.member.dto.JoinRequestDto;
import com.neutral.newspaper.member.dto.LoginRequestDto;
import com.neutral.newspaper.member.dto.ResetPasswordDto;
import com.neutral.newspaper.member.dto.UpdatePasswordDto;
import com.neutral.newspaper.member.dto.VerifyCodeDto;
import com.neutral.newspaper.member.service.EmailService;
import com.neutral.newspaper.member.service.MemberService;
import com.neutral.newspaper.redis.RedisService;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
                    .isInstanceOf(CustomException.class)
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
                    .isInstanceOf(CustomException.class)
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
                    .isInstanceOf(CustomException.class)
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
                    .isInstanceOf(CustomException.class)
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
                    .isInstanceOf(CustomException.class)
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
                    .isInstanceOf(CustomException.class)
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
                    .isInstanceOf(CustomException.class)
                    .hasMessageContaining("비밀번호는 8~16자의 영어, 숫자, 특수기호(@$!%*?&)를 포함해야 합니다.");
        }
    }

    // Mockito를 사용하기 위한 확장 선언
    @ExtendWith(MockitoExtension.class)
    @DisplayName("비밀번호 초기화 테스트")
    static class SendPasswordResetCodeTest {

        // @Mock 객체들을 자동으로 주입해서 테스트 대상 객체를 만들어줌
        @InjectMocks
        private MemberService memberService;

        // 실제 객체를 사용하는 대신 가짜 객체를 만들어주는 애노테이션
        @Mock
        private MemberRepository memberRepository;

        @Mock
        private EmailService emailService;

        @Mock
        private RedisService redisService;

        @Test
        @DisplayName("인증 코드 전송 성공 케이스")
        void successSendPasswordResetCode() {
            // given
            FindPasswordDto findPasswordRequest = new FindPasswordDto(
                    "email@example.com", "010-1234-5678"
            );
            Member member = Member.builder()
                    .name("홍길동")
                    .email("email@example.com")
                    .password("testPassword12!")
                    .phoneNumber("010-1234-5678")
                    .build();

            // Mock 객체가 어떤 값을 반환해야 하는지 설명
            // memberRepository.findByEmail(findPasswordRequest.getEmail())을 진행하면 멤버를 반환
            given(memberRepository.findByEmail(findPasswordRequest.getEmail()))
                    .willReturn(Optional.of(member));

            // when
            memberService.sendPasswordResetCode(findPasswordRequest);

            // then
            // Mock 객체가 특정 메서드를 실제로 호출했는지 검증하는 구문
            then(emailService).should().sendVerificationCode(
                    // eq(value): 정확히 value와 같은 값이어야 함
                    eq(findPasswordRequest.getEmail()), anyString(), contains("비밀번호 초기화 인증번호")
            );
            then(redisService).should().saveData(
                    eq(findPasswordRequest.getEmail()), anyString(), eq(5L), eq(TimeUnit.MINUTES)
            );
        }

        @Test
        @DisplayName("인증 코드 전송 실패 케이스: 이메일 오류")
        void failSendPasswordResetCodeEmailMismatch() {
            // given
            FindPasswordDto findPasswordRequest = new FindPasswordDto(
                    "emails@example.com", "010-1234-5678"
            );
            Member member = Member.builder()
                    .name("홍길동")
                    .email("email@example.com")
                    .password("testPassword12!")
                    .phoneNumber("010-1234-5678")
                    .build();

            given(memberRepository.findByEmail(findPasswordRequest.getEmail()))
                    .willReturn(Optional.empty());

            // when, then
            assertThatThrownBy(() -> memberService.sendPasswordResetCode(findPasswordRequest))
                    .isInstanceOf(CustomException.class)
                    .hasMessageContaining("회원 정보를 찾을 수 없습니다.");
        }

        @Test
        @DisplayName("인증 코드 전송 실패 케이스: 휴대전화 번호 불일치")
        void failSendPasswordResetCodePhoneNumberMismatch() {
            // given
            FindPasswordDto findPasswordRequest = new FindPasswordDto(
                    "emails@example.com", "010-4321-5678"
            );
            Member member = Member.builder()
                    .name("홍길동")
                    .email("email@example.com")
                    .password("testPassword12!")
                    .phoneNumber("010-1234-5678")
                    .build();

            given(memberRepository.findByEmail(findPasswordRequest.getEmail()))
                    .willReturn(Optional.of(member));

            // when, then
            assertThatThrownBy(() -> memberService.sendPasswordResetCode(findPasswordRequest))
                    .isInstanceOf(CustomException.class)
                    .hasMessageContaining("휴대폰 번호가 일치하지 않습니다.");
        }
    }

    @ExtendWith(MockitoExtension.class)
    @DisplayName("비밀번호 초기화 코드 유효성 검사 테스트")
    static class VerifyCodeTest {

        @InjectMocks
        private MemberService memberService;

        @Mock
        private RedisService redisService;

        @Test
        @DisplayName("비밀번호 초기화 코드 인증 성공 케이스")
        void successVerifyingCode() {
            // given
            VerifyCodeDto verifyCodeRequest = new VerifyCodeDto(
                    "email@example.com", "123456"
            );

            given(redisService.getData(verifyCodeRequest.getEmail()))
                    .willReturn("123456");

            // when
            memberService.verifyCode(verifyCodeRequest);

            // then
            then(redisService).should().saveData(
                    eq(verifyCodeRequest.getEmail() + ":verified"), eq("true"), eq(5L), eq(TimeUnit.MINUTES)
            );
        }

        @Test
        @DisplayName("비밀번호 초기화 코드 인증 실패 케이스")
        void failVerifyingCode() {
            // given
            VerifyCodeDto verifyCodeRequest = new VerifyCodeDto(
                    "email@example.com", "654321"
            );

            given(redisService.getData(verifyCodeRequest.getEmail()))
                    .willReturn("123456");

            // when, then
            assertThatThrownBy(() -> memberService.verifyCode(verifyCodeRequest))
                    .isInstanceOf(CustomException.class)
                    .hasMessageContaining("인증번호가 일치하지 않습니다.");
        }
    }

    @ExtendWith(MockitoExtension.class)
    @DisplayName("비밀번호 초기화  테스트")
    static class ResetPasswordTest {

        @InjectMocks
        private MemberService memberService;

        @Mock
        private RedisService redisService;

        @Mock
        private MemberRepository memberRepository;

        @Mock
        private BCryptPasswordEncoder passwordEncoder;

        @Test
        @DisplayName("비밀번호 초기화 성공 케이스")
        void successResetPassword() {
            // given
            ResetPasswordDto resetPasswordRequest = new ResetPasswordDto(
                    "email@example.com", "newPassword12!"
            );
            Member member = Member.builder()
                    .name("홍길동")
                    .email("email@example.com")
                    .password("testPassword12!")
                    .phoneNumber("010-1234-5678")
                    .build();

            given(memberRepository.findByEmail(resetPasswordRequest.getEmail()))
                    .willReturn(Optional.of(member));
            given(redisService.getData(resetPasswordRequest.getEmail() + ":verified"))
                    .willReturn("true");
            given(passwordEncoder.encode(resetPasswordRequest.getNewPassword()))
                    .willReturn("변환된비밀번호");

            // when
            memberService.resetPassword(resetPasswordRequest);

            // then
            then(redisService).should().deleteData(
                    eq(resetPasswordRequest.getEmail() + ":verified")
            );
        }

        @Test
        @DisplayName("비밀번호 초기화 실패 케이스: 존재하지 않는 회원일 경우")
        void failResetPasswordNotRegistered() {
            // given
            ResetPasswordDto resetPasswordRequest = new ResetPasswordDto(
                    "emails@example.com", "newPassword12!"
            );
            Member member = Member.builder()
                    .name("홍길동")
                    .email("email@example.com")
                    .password("testPassword12!")
                    .phoneNumber("010-1234-5678")
                    .build();

            given(memberRepository.findByEmail(resetPasswordRequest.getEmail()))
                    .willReturn(Optional.empty());

            // when, then
            assertThatThrownBy(() -> memberService.resetPassword(resetPasswordRequest))
                    .isInstanceOf(CustomException.class)
                    .hasMessageContaining("회원 정보를 찾을 수 없습니다.");
        }

        @Test
        @DisplayName("비밀번호 초기화 실패 케이스: 초기화 코드가 인증되지 않은 상황")
        void failResetPasswordUnverifiedCode() {
            // given
            ResetPasswordDto resetPasswordRequest = new ResetPasswordDto(
                    "email@example.com", "newPassword12!"
            );
            Member member = Member.builder()
                    .name("홍길동")
                    .email("email@example.com")
                    .password("testPassword12!")
                    .phoneNumber("010-1234-5678")
                    .build();

            given(memberRepository.findByEmail(resetPasswordRequest.getEmail()))
                    .willReturn(Optional.of(member));
            given(redisService.getData(resetPasswordRequest.getEmail() + ":verified"))
                    .willReturn(null);

            // when, then
            assertThatThrownBy(() -> memberService.resetPassword(resetPasswordRequest))
                    .isInstanceOf(CustomException.class)
                    .hasMessageContaining("인증이 완료되지 않았습니다.");
        }

        @Test
        @DisplayName("비밀번호 초기화 실패 케이스: 새 비밀번호가 조건에 맞지 않는 경우")
        void failResetPasswordInvalidNewPassword() {
            // given
            ResetPasswordDto resetPasswordRequest = new ResetPasswordDto(
                    "email@example.com", "newPassword1234567!"
            );
            Member member = Member.builder()
                    .name("홍길동")
                    .email("email@example.com")
                    .password("testPassword12!")
                    .phoneNumber("010-1234-5678")
                    .build();

            given(memberRepository.findByEmail(resetPasswordRequest.getEmail()))
                    .willReturn(Optional.of(member));
            given(redisService.getData(resetPasswordRequest.getEmail() + ":verified"))
                    .willReturn("true");

            // when, then
            assertThatThrownBy(() -> memberService.resetPassword(resetPasswordRequest))
                    .isInstanceOf(CustomException.class)
                    .hasMessageContaining("비밀번호는 8~16자의 영어, 숫자, 특수기호(@$!%*?&)를 포함해야 합니다.");
        }
    }
}

package com.neutral.newspaper.member;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neutral.newspaper.jwt.JwtToken;
import com.neutral.newspaper.member.controller.MemberController;
import com.neutral.newspaper.member.dto.FindPasswordDto;
import com.neutral.newspaper.member.dto.JoinRequestDto;
import com.neutral.newspaper.member.dto.LoginRequestDto;
import com.neutral.newspaper.member.dto.ResetPasswordDto;
import com.neutral.newspaper.member.dto.UpdatePasswordDto;
import com.neutral.newspaper.member.dto.VerifyCodeDto;
import com.neutral.newspaper.member.service.MemberService;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(MemberController.class)
@AutoConfigureMockMvc
public class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MemberService memberService;

    @Autowired
    private ObjectMapper objectMapper;

    @WithMockUser
    @Test
    @DisplayName("회원가입 요청 성공 시 200 반환")
    void successSignUp() throws Exception {
        JoinRequestDto joinRequest = new JoinRequestDto(
                "홍길동", "email@example.com", "TestPassword12!", "010-1234-5678", List.of("경제", "스포츠")
        );

        when(memberService.registerMember(any(JoinRequestDto.class))).thenReturn("회원가입이 완료되었습니다.");

        mockMvc.perform(post("/member/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(joinRequest))
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @WithMockUser
    @Test
    @DisplayName("로그인 성공 시 200 반환")
    void successLogin() throws Exception {
        LoginRequestDto loginRequest = new LoginRequestDto("email@example.com", "TestPassword12!");
        when(memberService.login(any())).thenReturn(new JwtToken("Bearer", "access-token", "refresh-token"));

        mockMvc.perform(post("/member/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest))
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @WithMockUser
    @Test
    @DisplayName("비밀번호 변경 성공 시 204 반환")
    void successChangingPassword() throws Exception {
        UpdatePasswordDto updatePassword = new UpdatePasswordDto("email@example.com", "TestPassword12!");
        doNothing().when(memberService).updatePassword(any());

        mockMvc.perform(post("/member/update-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePassword))
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @WithMockUser
    @Test
    @DisplayName("비밀번호 초기화 코드 전송 성공 시 204 반환")
    void successSendingPasswordResetCode() throws Exception {
        FindPasswordDto findPasswordRequest = new FindPasswordDto("email@example.com", "010-1234-5678");
        doNothing().when(memberService).sendPasswordResetCode(any());

        mockMvc.perform(post("/member/send-password-code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(findPasswordRequest))
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @WithMockUser
    @Test
    @DisplayName("비밀번호 초기화 코드 인증 성공 시 204 반환")
    void successVerifyingCode() throws Exception {
        VerifyCodeDto verifyCodeRequest = new VerifyCodeDto("email@example.com", "123456");
        doNothing().when(memberService).verifyCode(any());

        mockMvc.perform(post("/member/verify-code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(verifyCodeRequest))
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @WithMockUser
    @Test
    @DisplayName("비밀번호 초기화 성공 시 204 반환")
    void successResetCode() throws Exception {
        ResetPasswordDto resetPasswordRequest = new ResetPasswordDto("email@example.com", "NewPassword12!");
        doNothing().when(memberService).resetPassword(any());

        mockMvc.perform(post("/member/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resetPasswordRequest))
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }
}

package com.neutral.newspaper.member;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neutral.newspaper.global.CustomException;
import com.neutral.newspaper.global.ErrorType;
import com.neutral.newspaper.member.controller.MemberController;
import com.neutral.newspaper.member.dto.JoinRequestDto;
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
    @DisplayName("회원가입 성공")
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
    @DisplayName("회원가입 실패 - 이메일 중복")
    void failSignUpDuplicatedEmail() throws Exception {
        JoinRequestDto joinRequest = new JoinRequestDto(
                "홍길동", "email@example.com", "TestPassword12!", "010-1234-5678", List.of("경제", "스포츠")
        );

        when(memberService.registerMember(any(JoinRequestDto.class))).thenThrow(new CustomException(ErrorType.DUPLICATED_EMAIL));

        mockMvc.perform(post("/member/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(joinRequest))
                        .with(csrf()))
                .andExpect(status().isConflict());
    }

    @WithMockUser
    @Test
    @DisplayName("회원가입 실패 - 비밀번호 형식 오류")
    void failSignUpInvalidPassword() throws Exception {
        JoinRequestDto joinRequest = new JoinRequestDto(
                "홍길동", "email@example.com", "test", "010-1234-5678", List.of("경제", "스포츠")
        );

        when(memberService.registerMember(any(JoinRequestDto.class))).thenThrow(new CustomException(ErrorType.INVALID_PASSWORD_FORMAT));

        mockMvc.perform(post("/member/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(joinRequest))
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser
    @Test
    @DisplayName("회원가입 실패 - 휴대전화 번호 형식 오류")
    void failSignUpInvalidPhoneNumber() throws Exception {
        JoinRequestDto joinRequest = new JoinRequestDto(
                "홍길동", "email@example.com", "TestPassword12!", "010-1234-56789", List.of("경제", "스포츠")
        );

        when(memberService.registerMember(any(JoinRequestDto.class))).thenThrow(new CustomException(ErrorType.INVALID_PHONE_NUMBER_FORMAT));

        mockMvc.perform(post("/member/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(joinRequest))
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }
}

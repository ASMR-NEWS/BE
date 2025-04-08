package com.neutral.newspaper.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordDto {
    private String email;
    private String newPassword;
}

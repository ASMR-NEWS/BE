package com.neutral.newspaper.member.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdatePasswordDto {
    private String oldPassword;
    private String newPassword;
}

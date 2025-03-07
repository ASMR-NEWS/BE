package com.neutral.newspaper.member.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberUpdatePasswordDto {
    private String email;
    private String oldPassword;
    private String newPassword;
}

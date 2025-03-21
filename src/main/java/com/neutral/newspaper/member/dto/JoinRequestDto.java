package com.neutral.newspaper.member.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class JoinRequestDto {
    private String name;
    private String email;
    private String password;
    private String phoneNumber;
}

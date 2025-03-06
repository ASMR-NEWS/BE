package com.neutral.newspaper.member.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberJoinRequestDto {
    private String name;
    private String email;
    private String password;
}

package com.neutral.newspaper.member.controller;

import com.neutral.newspaper.jwt.JwtToken;
import com.neutral.newspaper.member.dto.MemberJoinRequestDto;
import com.neutral.newspaper.member.dto.MemberLoginRequestDto;
import com.neutral.newspaper.member.dto.MemberUpdatePasswordDto;
import com.neutral.newspaper.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody MemberJoinRequestDto joinRequest) {
        String response = memberService.registerMember(joinRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<JwtToken> login(@RequestBody MemberLoginRequestDto loginRequest) {
        JwtToken jwtToken = memberService.login(loginRequest);
        return ResponseEntity.ok(jwtToken);
    }

    @PostMapping("/update-password")
    public ResponseEntity<Void> updatePassword(@RequestBody MemberUpdatePasswordDto updatePasswordRequest) {
        memberService.updatePassword(updatePasswordRequest);
        return ResponseEntity.noContent().build();
    }
}

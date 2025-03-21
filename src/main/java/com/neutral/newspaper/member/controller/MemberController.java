package com.neutral.newspaper.member.controller;

import com.neutral.newspaper.jwt.JwtToken;
import com.neutral.newspaper.member.dto.JoinRequestDto;
import com.neutral.newspaper.member.dto.LoginRequestDto;
import com.neutral.newspaper.member.dto.UpdatePasswordDto;
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
    public ResponseEntity<String> signup(@RequestBody JoinRequestDto joinRequest) {
        String response = memberService.registerMember(joinRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<JwtToken> login(@RequestBody LoginRequestDto loginRequest) {
        JwtToken jwtToken = memberService.login(loginRequest);
        return ResponseEntity.ok(jwtToken);
    }

    @PostMapping("/update-password")
    public ResponseEntity<Void> updatePassword(@RequestBody UpdatePasswordDto updatePasswordRequest) {
        memberService.updatePassword(updatePasswordRequest);
        return ResponseEntity.noContent().build();
    }
}

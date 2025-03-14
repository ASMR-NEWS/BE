package com.neutral.newspaper.config.security;

import com.neutral.newspaper.jwt.JwtAuthenticationFilter;
import com.neutral.newspaper.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                // JWT 기반 인증을 사용하기 때문에 CSRF 보호 기능 비활성화
                .csrf(AbstractHttpConfigurer::disable)
                // 세션을 STATELESS로 설정
                .sessionManagement((sessionManagementConfig) ->
                        sessionManagementConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // 요청에 대한 인증 및 권한 부여 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("member/signup", "member/login", "member/update-password").permitAll() //인증 없이 접근 가능
                        .anyRequest().authenticated()
                )
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

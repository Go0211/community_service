package com.community.zerobase.config;

import com.community.zerobase.jwt.JwtFilter;
import com.community.zerobase.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
  private final TokenProvider tokenProvider;

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    RequestMatcher postMatcher = new RegexRequestMatcher("/board/.*/post/\\d+", null);

    http
        // CSRF 설정 Disable
        .csrf(AbstractHttpConfigurer::disable)
        // 프레임 옵션 비활성화
        // h2를 위해서 한 설정 -> 배포 시 삭제
        .headers(headers -> headers.frameOptions(FrameOptionsConfig::disable))

        // HTTP 세션 관리 설정
        .sessionManagement(session -> session
            // 세션 생성 정책 -> 세션을 사용하지 않도록 설정
            // jwt는 세션 필요없기 때문
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )

        // 로그인, 회원가입 API 는 토큰이 없는 상태에서 요청이 들어오기 때문에 permitAll
        // 나머지는 모두 권한이 필요하다
        .authorizeHttpRequests(request -> request
            // h2는 배포 시 주석 or 삭제해야한다.
            .requestMatchers(postMatcher).permitAll()
            .requestMatchers("/h2-console/**").permitAll()
            .requestMatchers("/login", "/join").permitAll()
            .requestMatchers("board/list").permitAll()
            .requestMatchers("/board/*/post/list").permitAll()
            .requestMatchers("/board/*/comment/list").permitAll()
            .requestMatchers("/board/*/manager/list").permitAll()
            .anyRequest().authenticated())

        // JwtFilter 를 addFilterBefore 로 등록했던 JwtSecurityConfig 클래스를 적용
        .addFilterBefore(
            new JwtFilter(tokenProvider),
            UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }
}


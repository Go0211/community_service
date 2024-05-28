package com.community.zerobase.service;

import com.community.zerobase.dto.LoginDto;
import com.community.zerobase.dto.TokenDto;
import com.community.zerobase.dto.UsersDto;
import com.community.zerobase.entity.Users;
import com.community.zerobase.jwt.TokenProvider;
import com.community.zerobase.repository.UsersRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
  // Spring Security에서 인증 처리를 담당하는 매니저를 생성하는 빌더
  private final AuthenticationManagerBuilder authenticationManagerBuilder;
  private final UsersRepository usersRepository;
  // 비밀번호를 암호화하는 인코더
  private final PasswordEncoder passwordEncoder;
  // JWT 토큰을 생성하고 검증하는 도구
  private final TokenProvider tokenProvider;

  @Transactional
  public String join(UsersDto.Join joinDto) {
    // 이메일로 회원 존재 여부를 체크
    if (usersRepository.existsByEmail(joinDto.getEmail())) {
      // 이메일이 이미 존재한다면 RuntimeException
      throw new RuntimeException("이미 가입되어 있는 유저입니다");
    }

    // 입력받은 dto를 비밀번호 인코딩 후 member로 변환
    joinDto.setPasswordEncoder(passwordEncoder);
    Users users = Users.joinDtotoToUsers(joinDto);

    // member를 db에 저장 후 email를 return
    usersRepository.save(users);
    return joinDto.getEmail();
  }

  @Transactional
  public TokenDto login(LoginDto.Request request) {
    // 1. Login ID/PW 를 기반으로 AuthenticationToken 생성
    UsernamePasswordAuthenticationToken authenticationToken = request.toAuthentication();

    // 2. 실제로 검증 (사용자 비밀번호 체크) 이 이루어지는 부분
    //    authenticate 메서드가 실행이 될 때 CustomUserDetailsService 에서 만들었던 loadUserByUsername 메서드가 실행됨
    Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

    // 3. 인증 정보를 기반으로 JWT 토큰 생성
    TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);

    // 4. 로그인 시간 갱신
    // 인증 필터나 인증 이벤트 리스너 사용방법도 있다고 한다.
    updateLoginDateTime(request.getEmail());

    // 5. 토큰 발급
    return tokenDto;
  }

  private void updateLoginDateTime(String email) {
    Users users = usersRepository.findByEmail(email)
        .orElseThrow(() -> new RuntimeException("user not found"));
    users.setLastLoginDate(LocalDateTime.now());
    usersRepository.save(users);
  }

  public String getUserName() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication != null && authentication.isAuthenticated()) {
      Object principal = authentication.getPrincipal();

      if (principal instanceof UserDetails) {
        return ((UserDetails) principal).getUsername();
      } else {
        return principal.toString();
      }
    }
    return null;
  }
}


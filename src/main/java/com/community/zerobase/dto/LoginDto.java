package com.community.zerobase.dto;

import com.community.zerobase.entity.Users;
import com.community.zerobase.role.Dormant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

public class LoginDto {

  @Getter
  @AllArgsConstructor
  @NoArgsConstructor
  public static class Request {
    private String email;
    private String password;

    public Users toUsers(PasswordEncoder passwordEncoder) {
      return Users.builder()
          .email(email)
          .password(passwordEncoder.encode(password))
          .dormant(Dormant.N)
          .build();
    }

    public UsernamePasswordAuthenticationToken toAuthentication() {
      return new UsernamePasswordAuthenticationToken(email, password);
    }
  }

  @Getter
  @AllArgsConstructor
  @NoArgsConstructor
  public static class Response {
    private String email;

    public static Response of(Users users) {
      return new Response(users.getEmail());
    }
  }
}

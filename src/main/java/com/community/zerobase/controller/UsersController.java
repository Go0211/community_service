package com.community.zerobase.controller;

import com.community.zerobase.dto.LoginDto;
import com.community.zerobase.dto.UsersDto;
import com.community.zerobase.service.AuthService;
import com.community.zerobase.service.UsersService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UsersController {
  private final UsersService usersService;
  private final AuthService authService;

  @PostMapping("/join")
  public ResponseEntity<?> join(@Valid @RequestBody UsersDto.Join joinDto) {
    return ResponseEntity.ok(
        authService.join(joinDto)+"님 회원가입 되었습니다.");
  }

  @PostMapping("/login")
  public ResponseEntity<?> join(@Valid @RequestBody LoginDto.Request request) {
    return ResponseEntity.ok(
        authService.login(request)
    );
  }

  @GetMapping("/info")
  public ResponseEntity<?> info() {
    return ResponseEntity.ok(
        usersService.getInfo(authService.getUserName())
    );
  }

  @PutMapping("/info")
  public ResponseEntity<?> info(@Valid @RequestBody UsersDto.Info usersDto) {
    return ResponseEntity.ok(
        usersService.updateInfo(usersDto)
    );
  }
}

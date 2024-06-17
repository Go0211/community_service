package com.community.zerobase.jwt;

import com.community.zerobase.dto.ErrorDto;
import com.community.zerobase.exception.ErrorException.ExpiredJwtTokenException;
import com.community.zerobase.exception.ErrorException.InvalidJwtTokenException;
import com.community.zerobase.exception.ErrorException.NotSupportJwtException;
import com.community.zerobase.exception.ErrorException.NullException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

  public static final String AUTHORIZATION_HEADER = "Authorization";
  public static final String BEARER_PREFIX = "Bearer ";

  private final TokenProvider tokenProvider;


  // 실제 필터링 로직은 doFilterInternal 에 들어감
  // JWT 토큰의 인증 정보를 현재 쓰레드의 SecurityContext 에 저장하는 역할 수행
  @Override
  protected void doFilterInternal(HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain) throws IOException, ServletException {

    // 1. Request Header 에서 토큰을 꺼냄
    String jwt = resolveToken(request);

    // 2. validateToken 으로 토큰 유효성 검사
    // 정상 토큰이면 해당 토큰으로 Authentication 을 가져와서 SecurityContext 에 저장
    try {
      if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
        Authentication authentication = tokenProvider.getAuthentication(jwt);
        SecurityContextHolder.getContext().setAuthentication(authentication);
      }
      filterChain.doFilter(request, response);
    } catch (InvalidJwtTokenException | ExpiredJwtTokenException |
             NotSupportJwtException | NullException ex) {
      handleException(response, ex);
    }
  }

  // Request Header 에서 토큰 정보를 꺼내오기
  private String resolveToken(HttpServletRequest request) {
    String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
      return bearerToken.split(" ")[1].trim();
    }
    return null;
  }

  private void handleException
      (HttpServletResponse response, RuntimeException ex) throws IOException {
    HttpStatus status;
    if (ex instanceof InvalidJwtTokenException) {
      status = HttpStatus.BAD_REQUEST;
    } else if (ex instanceof ExpiredJwtTokenException) {
      status = HttpStatus.UNAUTHORIZED;
    } else if (ex instanceof NotSupportJwtException) {
      status = HttpStatus.BAD_REQUEST;
    } else if (ex instanceof NullException) {
      status = HttpStatus.BAD_REQUEST;
    } else {
      status = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    response.setStatus(status.value());
    response.setContentType("application/json");
    ObjectMapper objectMapper = new ObjectMapper();
    ErrorDto errorDto = new ErrorDto(
        ex.getMessage(),
        status.value(),
        LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    );
    response.getWriter().write(objectMapper.writeValueAsString(errorDto));
  }
}


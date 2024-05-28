package com.community.zerobase.jwt;

import com.community.zerobase.dto.TokenDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import java.security.Key;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;
import javax.crypto.spec.SecretKeySpec;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TokenProvider {

  // JWT에 권한 정보를 저장하기 위한 키
  private static final String AUTHORITIES_KEY = "auth";
  // 토큰의 타입을 나타내는 문자열 -> 보통 Bearer
  private static final String BEARER_TYPE = "Bearer";
  // 토큰 만료 시간
  private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24 * 3;   // 3일
  // test용 토크 만료 시간
  private static final long ACCESS_TOKEN_EXPIRE_TIME_TEST = 1000 * 60 * 30;   // 30분
  // JWT를 서명하고 검증하는 데 사용되는 비밀 키
  private final Key key;

  // 생성자 -> yml에 저장된 secret키를 parameter로 받음
  public TokenProvider(@Value("${jwt.secret}") String secretKey) {
    // 비밀키를 디코딩
    byte[] keyBytes = Base64.getDecoder().decode(secretKey);
    // HMAC SHA 알고리즘을 사용하여 키 객체를 생성
    this.key = new SecretKeySpec(keyBytes, "HmacSHA512");
  }

  public TokenDto generateTokenDto(Authentication authentication) {
    // 권한들 가져오기
    String authorities = authentication.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .collect(Collectors.joining(","));

    long now = (new Date()).getTime();

    // Access Token 생성 -> test로 설정 시 시간 설정 변경하기
    Date accessTokenExpiresIn = new Date(now + ACCESS_TOKEN_EXPIRE_TIME_TEST);
    String accessToken = Jwts.builder()
        // JWT의 페이로드 부분에 사용자 이름이나 ID를 포함
        .setSubject(authentication.getName())
        // JWT의 페이로드 부분에 권한 포함
        .claim(AUTHORITIES_KEY, authorities)
        // 페이로드 부분에 토큰의 만료 시간 지정
        .setExpiration(accessTokenExpiresIn)
        //서명 부분을 생성
        .signWith(key, SignatureAlgorithm.HS512)
        .compact();

    // 정보를 담아서 return
    return TokenDto.builder()
        .grantType(BEARER_TYPE)
        .accessToken(accessToken)
        .accessTokenExpiresIn(accessTokenExpiresIn.getTime())
        .build();
  }

  public Authentication getAuthentication(String accessToken) {
    // 토큰 복호화
    Claims claims = parseClaims(accessToken);

    if (claims.get(AUTHORITIES_KEY) == null) {
      throw new RuntimeException("권한 정보가 없는 토큰입니다.");
    }

    // 클레임에서 권한 정보 가져오기
    Collection<? extends GrantedAuthority> authorities =
        Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());

    // UserDetails 객체를 만들어서 Authentication 리턴
    UserDetails principal = new User(claims.getSubject(), "", authorities);

    return new UsernamePasswordAuthenticationToken(principal, "", authorities);
  }

  // 유효성 검사
  public boolean validateToken(String token) {
    try {
      Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
      return true;
      // 잘못된 JWT 서명 / 서명이 not올바름 / 토큰이 변조
    } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
      log.info("잘못된 JWT 서명입니다.");
      // 만료된 토큰
    } catch (ExpiredJwtException e) {
      log.info("만료된 JWT 토큰입니다.");
      // 지원되지 않는 JWT 토큰
    } catch (UnsupportedJwtException e) {
      log.info("지원되지 않는 JWT 토큰입니다.");
      // 토큰이 null / 빈 문자열
    } catch (IllegalArgumentException e) {
      log.info("JWT 토큰이 잘못되었습니다.");
    }
    return false;
  }

  private Claims parseClaims(String accessToken) {
    try {
      return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
    } catch (ExpiredJwtException e) {
      return e.getClaims();
    }
  }
}

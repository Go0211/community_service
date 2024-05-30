package com.community.zerobase.service;

import com.community.zerobase.entity.Users;
import com.community.zerobase.exception.ErrorException.NotFoundException;
import com.community.zerobase.repository.UsersRepository;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

  private final UsersRepository usersRepository;

  @Override
  @Transactional
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    return usersRepository.findByEmail(email)
        .map(this::createUserDetails)
        .orElseThrow(() -> new NotFoundException("not found user"));
  }

  // DB 에 User 값이 존재한다면 UserDetails 객체로 만들어서 리턴
  private UserDetails createUserDetails(Users users) {
    GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(users.getDormant().toString());

    return new User(
        String.valueOf(users.getEmail()),
        users.getPassword(),
        Collections.singleton(grantedAuthority)
    );
  }
}

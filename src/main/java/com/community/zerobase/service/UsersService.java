package com.community.zerobase.service;

import com.community.zerobase.dto.UsersDto;
import com.community.zerobase.entity.Users;
import com.community.zerobase.exception.ErrorException;
import com.community.zerobase.exception.ErrorException.NotFoundException;
import com.community.zerobase.repository.UsersRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsersService{
  private final UsersRepository usersRepository;

  public UsersDto.Info getInfo(String email) {
    Users users = usersRepository.findByEmail(email).orElseThrow(
        () -> new NotFoundException("email not found"));

    return UsersDto.Info.usersToInfoDto(users);
  }

  @Transactional
  public UsersDto.Info updateInfo(UsersDto.Info infoDto) {
    Users users = usersRepository.findByEmail(infoDto.getEmail())
        .orElseThrow(() -> new NotFoundException("email not found"));

    //dirty checking
    users.updateUser(infoDto);

    return UsersDto.Info.usersToInfoDto(users);
  }
}

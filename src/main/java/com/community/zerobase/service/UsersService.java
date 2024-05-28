package com.community.zerobase.service;

import com.community.zerobase.dto.UsersDto;
import com.community.zerobase.entity.Users;
import com.community.zerobase.repository.UsersRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsersService{
  private final UsersRepository usersRepository;

  public UsersDto.Info getInfo(String email) {
    Users users = usersRepository.findByEmail(email).orElseThrow(
        () -> new UsernameNotFoundException("email not found"));

    return UsersDto.Info.usersToInfoDto(users);
  }

  public UsersDto.Info updateInfo(UsersDto.Info infoDto) {
    Users users = updateUsersInfo(infoDto);

    usersRepository.save(users);

    return UsersDto.Info.usersToInfoDto(users);
  }


  public Users updateUsersInfo(UsersDto.Info infoDto) {
    Users users = usersRepository.findByEmail(infoDto.getEmail())
        .orElseThrow(() -> new UsernameNotFoundException("email not found"));

    users.setName(infoDto.getName());
    users.setBirthDate(infoDto.getBirthDate());
    users.setPhoneNumber(infoDto.getPhoneNumber());
    users.setModificationDate(LocalDateTime.now());

    return users;
  }
}

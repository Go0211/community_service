package com.community.zerobase.service;

import com.community.zerobase.dto.UsersDto;
import com.community.zerobase.entity.Users;
import com.community.zerobase.exception.ErrorException;
import com.community.zerobase.exception.ErrorException.MissMatchedException;
import com.community.zerobase.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsersService{
  private final CommonService commonService;
  private final UsersRepository usersRepository;

  public UsersDto.Info getInfo() {
    return UsersDto.Info.usersToInfoDto(
        commonService.getUsers(commonService.getUserEmail()));
  }

  public UsersDto.Info updateInfo(UsersDto.Info infoDto) {
    if (!infoDto.getEmail().equals(commonService.getUserEmail())) {
      throw new MissMatchedException("not match users");
    }

    Users users = commonService.getUsers(infoDto.getEmail());

    //dirty checking
    users.updateUser(infoDto);

    return UsersDto.Info.usersToInfoDto(users);
  }
}

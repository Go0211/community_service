package com.community.zerobase.dto;

import com.community.zerobase.entity.Users;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

public class UsersDto {

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class Join {
    @Email(message = "잘못된 이메일 형식입니다.")
    @NotBlank(message = "이메일은 꼭 입력해야 합니다.")
    String email;

    @NotBlank(message = "비밀번호는 꼭 입력해야 합니다.")
    String password;

    @NotBlank(message = "이름은 꼭 입력해야 합니다.")
    String name;

    @Pattern(regexp = "\\d{4}.\\d{2}.\\d{2}", message = "생년월일은 yyyy.mm.dd형식으로 입력 되야합니다.")
    String birthDate;

    @Size(min = 11, max = 11, message = "입력은 11자리여야 합니다.")
    String phoneNumber;

    public void setPasswordEncoder
        (PasswordEncoder passwordEncoder) {
      this.password = passwordEncoder.encode(this.password);
    }
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class Info {
    @Email(message = "잘못된 이메일 형식입니다.")
    @NotBlank(message = "이메일은 꼭 입력해야 합니다.")
    String email;

    @NotBlank(message = "이름은 꼭 입력해야 합니다.")
    String name;

    @Pattern(regexp = "\\d{4}.\\d{2}.\\d{2}", message = "생년월일은 yyyy.mm.dd형식으로 입력 되야합니다.")
    String birthDate;

    @Size(min = 11, max = 11, message = "입력은 11자리여야 합니다.")
    String phoneNumber;

    public static UsersDto.Info usersToInfoDto(Users users) {
      return Info.builder()
          .email(users.getEmail())
          .name(users.getName())
          .birthDate(users.getBirthDate())
          .phoneNumber(users.getPhoneNumber())
          .build();
    }
  }
}

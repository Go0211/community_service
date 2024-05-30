package com.community.zerobase.entity;

import com.community.zerobase.dto.UsersDto;
import com.community.zerobase.role.Dormant;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class Users{
  @PrePersist
  protected void onCreated() {
    joinDate = LocalDateTime.now();
    lastLoginDate = LocalDateTime.now();
    modificationDate = LocalDateTime.now();
    dormant = Dormant.N;
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  @Column(name = "email", unique = true)
  String email;

  @Column(name = "password")
  String password;

  @Column(name = "name")
  String name;

  @Column(name = "birth_date")
  String birthDate;

  @Column(name = "phone_number")
  String phoneNumber;

  @Column(name = "join_date")
  LocalDateTime joinDate;

  @Column(name = "last_login_date")
  LocalDateTime lastLoginDate;

  @Column(name = "modification_date")
  LocalDateTime modificationDate;

  @Column(name = "dormant")
  @Enumerated(value = EnumType.STRING)
  Dormant dormant;

  public static Users joinDtotoToUsers(UsersDto.Join joinDto) {
    return Users.builder()
        .email(joinDto.getEmail())
        .password(joinDto.getPassword())
        .name(joinDto.getName())
        .birthDate(joinDto.getBirthDate())
        .phoneNumber(joinDto.getPhoneNumber())
        .build();
  }

  public void updateUser(UsersDto.Info infoDto) {
    this.name = infoDto.getName();
    this.birthDate = infoDto.getBirthDate();
    this.phoneNumber = infoDto.getPhoneNumber();
    this.modificationDate = LocalDateTime.now();
  }
}


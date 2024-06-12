package com.community.zerobase.dto;

import com.community.zerobase.entity.Manager;
import com.community.zerobase.entity.NoticeBoard;
import com.community.zerobase.entity.Users;
import com.community.zerobase.role.Auth;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


public class ManagerDto {
  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class Request {
    @NotBlank(message = "이메일은 입력해야합니다.")
    String email;
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class Response {
    String noticeBoardName;
    String userName;
    Auth auth;

    public static ManagerDto.Response ManagerToDto(NoticeBoard noticeBoard, Manager manager) {
      return Response.builder()
          .noticeBoardName(noticeBoard.getName())
          .userName(manager.getUsers().getEmail())
          .auth(manager.getAuth())
          .build();
    }
  }
}


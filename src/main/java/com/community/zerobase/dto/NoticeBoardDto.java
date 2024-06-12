package com.community.zerobase.dto;

import com.community.zerobase.entity.NoticeBoard;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class NoticeBoardDto {


  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class Response {
    String name;
    LocalDateTime createDate;
    List<String> managerList;

    public static Response toDto(
        NoticeBoard noticeBoard,
        List<String> managerList
    ) {
      return Response.builder()
          .name(noticeBoard.getName())
          .createDate(noticeBoard.getCreateDate())
          .managerList(managerList)
          .build();
    }

    public static Response toDto(
        NoticeBoard noticeBoard,
        ManagerDto.Response response
    ) {
      List<String> manager = new ArrayList<>();
      manager.add(response.userName);

      return Response.builder()
          .name(noticeBoard.getName())
          .createDate(noticeBoard.getCreateDate())
          .managerList(manager)
          .build();
    }
  }
}

package com.community.zerobase.dto;

import com.community.zerobase.entity.Comment;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class CommentDto {
  @Data
  public static class Request {
    @NotBlank(message = "내용은 꼭 입력해야 합니다.")
    private String content;
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class Response {
      private Long id;
      private String email;
      private Long postId;
      private String content;
      private int likes;
      private LocalDateTime writeDate;
      private LocalDateTime modification_date;

    public static CommentDto.Response commentToDto(Comment comment) {
      return Response.builder()
          .id(comment.getId())
          .email(comment.getUsers().getEmail())
          .postId(comment.getPost().getId())
          .content(comment.getContent())
          .likes(comment.getLikes())
          .writeDate(comment.getWriteDate())
          .modification_date(comment.getModificationDate())
          .build();
    }
  }
}

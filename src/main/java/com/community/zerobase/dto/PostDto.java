package com.community.zerobase.dto;

import com.community.zerobase.entity.Post;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class PostDto {
  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class Request {
    @NotBlank(message = "제목은 필수로 입력해야 합니다.")
    String title;
    @NotBlank(message = "내용은 필수로 입력해야 합니다.")
    String content;

    Long postId;
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class Response {
    Long id;
    String usersEmail;
    Long noticeBoardId;
    String title;
    String content;
    int views;
    int likes;
    LocalDateTime writeDate;
    LocalDateTime modificationDate;

    public static Response PostToDto(Post post) {
      return PostDto.Response.builder()
          .id(post.getId())
          .usersEmail(post.getUsers().getEmail())
          .noticeBoardId(post.getNoticeBoard().getId())
          .title(post.getTitle())
          .content(post.getContent())
          .views(post.getViews())
          .likes(post.getLikes())
          .writeDate(post.getWriteDate())
          .modificationDate(post.getModificationDate())
          .build();
    }
  }
}

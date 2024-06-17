package com.community.zerobase.entity;

import com.community.zerobase.dto.PostDto.Request;
import com.community.zerobase.dto.TemporaryDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@RedisHash(value = "temporaryPost")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TemporaryPost {
  @Id
  private String userid_boardId_title;
  private String title;
  private String content;

  public static TemporaryPost toTemporaryPost(Long id, Long boardId, TemporaryDto.Request request) {
    return TemporaryPost.builder()
        .userid_boardId_title(id + "_" + boardId + "_" + request.getTitle())
        .title(request.getTitle())
        .content(request.getContent())
        .build();
  }
}

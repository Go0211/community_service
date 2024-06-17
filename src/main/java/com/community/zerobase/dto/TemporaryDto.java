package com.community.zerobase.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class TemporaryDto {
  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class Request {
    @NotBlank(message = "제목은 필수로 입력해야 합니다.")
    String title;
    String content;
  }

}

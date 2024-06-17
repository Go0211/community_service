package com.community.zerobase.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorDto {
  private String message;
  private int state;
  private String errorTime;
}

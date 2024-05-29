package com.community.zerobase.exception;

import com.community.zerobase.dto.ErrorDto;
import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(ErrorException.NotFoundException.class)
  public ResponseEntity<ErrorDto> notFoundException
      (ErrorException.NotFoundException exception) {
    ErrorDto errorDto = new ErrorDto(
        exception.getMessage(),
        HttpStatus.NOT_FOUND.value(),
        LocalDateTime.now()
    );
    return new ResponseEntity<>(errorDto, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(ErrorException.AlreadyExistException.class)
  public ResponseEntity<ErrorDto> alreadyExistException
      (ErrorException.AlreadyExistException exception) {
    ErrorDto errorDto = new ErrorDto(
        exception.getMessage(),
        HttpStatus.CONFLICT.value(),
        LocalDateTime.now()
    );
    return new ResponseEntity<>(errorDto, HttpStatus.CONFLICT);
  }

  @ExceptionHandler(ErrorException.MissMatchedException.class)
  public ResponseEntity<ErrorDto> missMatchedException
      (ErrorException.MissMatchedException exception) {
    ErrorDto errorDto = new ErrorDto(
        exception.getMessage(),
        HttpStatus.BAD_REQUEST.value(),
        LocalDateTime.now()
    );
    return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
  }
}

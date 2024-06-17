package com.community.zerobase.exception;

import static com.community.zerobase.exception.ErrorException.AlreadyExistException;
import static com.community.zerobase.exception.ErrorException.ExpiredJwtTokenException;
import static com.community.zerobase.exception.ErrorException.InvalidJwtTokenException;
import static com.community.zerobase.exception.ErrorException.MissMatchedException;
import static com.community.zerobase.exception.ErrorException.NotAllowInputValueException;
import static com.community.zerobase.exception.ErrorException.NotFoundException;
import static com.community.zerobase.exception.ErrorException.NotSupportJwtException;
import static com.community.zerobase.exception.ErrorException.NullException;

import com.community.zerobase.dto.ErrorDto;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private String localDateTimeToString(LocalDateTime localDateTime) {
     return localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
  }

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<ErrorDto> notFoundException
      (NotFoundException exception) {
    ErrorDto errorDto = new ErrorDto(
        exception.getMessage(),
        HttpStatus.NOT_FOUND.value(),
        localDateTimeToString(LocalDateTime.now())
    );
    return new ResponseEntity<>(errorDto, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(AlreadyExistException.class)
  public ResponseEntity<ErrorDto> alreadyExistException
      (AlreadyExistException exception) {
    ErrorDto errorDto = new ErrorDto(
        exception.getMessage(),
        HttpStatus.CONFLICT.value(),
        localDateTimeToString(LocalDateTime.now())
    );
    return new ResponseEntity<>(errorDto, HttpStatus.CONFLICT);
  }

  @ExceptionHandler(MissMatchedException.class)
  public ResponseEntity<ErrorDto> missMatchedException
      (MissMatchedException exception) {
    ErrorDto errorDto = new ErrorDto(
        exception.getMessage(),
        HttpStatus.BAD_REQUEST.value(),
        localDateTimeToString(LocalDateTime.now())
    );
    return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(NullException.class)
  public ResponseEntity<ErrorDto> nullException
      (NullException exception) {
    ErrorDto errorDto = new ErrorDto(
        exception.getMessage(),
        HttpStatus.BAD_REQUEST.value(),
        localDateTimeToString(LocalDateTime.now())
    );
    return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(NotAllowInputValueException.class)
  public ResponseEntity<ErrorDto> notAllowInputValueException
      (NotAllowInputValueException exception) {
    ErrorDto errorDto = new ErrorDto(
        exception.getMessage(),
        HttpStatus.BAD_REQUEST.value(),
        localDateTimeToString(LocalDateTime.now())
    );
    return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
    Map<String, String> errors = new HashMap<>();

    ex.getBindingResult().getAllErrors().forEach((error) -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      errors.put(fieldName, errorMessage);
    });

    return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(InvalidJwtTokenException.class)
  public ResponseEntity<ErrorDto> invalidJwtTokenException(InvalidJwtTokenException exception) {
    ErrorDto errorDto = new ErrorDto(
        exception.getMessage(),
        HttpStatus.BAD_REQUEST.value(),
        localDateTimeToString(LocalDateTime.now())
    );
    return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(ExpiredJwtTokenException.class)
  public ResponseEntity<ErrorDto> expiredJwtTokenException(ExpiredJwtTokenException exception) {
    ErrorDto errorDto = new ErrorDto(
        exception.getMessage(),
        HttpStatus.UNAUTHORIZED.value(),
        localDateTimeToString(LocalDateTime.now())
    );
    return new ResponseEntity<>(errorDto, HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(NotSupportJwtException.class)
  public ResponseEntity<ErrorDto> notSupportJwtException(NotSupportJwtException exception) {
    ErrorDto errorDto = new ErrorDto(
        exception.getMessage(),
        HttpStatus.BAD_REQUEST.value(),
        localDateTimeToString(LocalDateTime.now())
    );
    return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
  }
}

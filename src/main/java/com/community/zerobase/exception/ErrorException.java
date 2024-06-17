package com.community.zerobase.exception;

public class ErrorException {

  public static class MissMatchedException extends RuntimeException {

    public MissMatchedException(String message) {
      super(message);
    }
  }

  public static class AlreadyExistException extends RuntimeException {

    public AlreadyExistException(String message) {
      super(message);
    }
  }

  public static class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
      super(message);
    }
  }

  public static class NullException extends RuntimeException {

    public NullException(String message) {
      super(message);
    }
  }

  public static class NotAllowInputValueException extends RuntimeException {

    public NotAllowInputValueException(String message) {
      super(message);
    }
  }

  public static class InvalidJwtTokenException extends RuntimeException {
    public InvalidJwtTokenException(String message) {super(message);
    }
  }

  public static class ExpiredJwtTokenException extends RuntimeException {
    public ExpiredJwtTokenException(String message) {super(message);
    }
  }

  public static class NotSupportJwtException extends RuntimeException {
    public NotSupportJwtException(String message) {super(message);
    }
  }
}

package com.community.zerobase.exception;

public class ErrorException {

  public static class MissMatchedException extends RuntimeException{
    public MissMatchedException(String message) {
      super(message);
    }
  }

  public static class AlreadyExistException extends RuntimeException{
    public AlreadyExistException(String message) {
      super(message);
    }
  }

  public static class NotFoundException extends RuntimeException{
    public NotFoundException(String message) {
      super(message);
    }
  }
}

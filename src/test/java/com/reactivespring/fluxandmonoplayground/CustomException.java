package com.reactivespring.fluxandmonoplayground;

//import lombok.EqualsAndHashCode;
//import lombok.Value;

//@Value
//@EqualsAndHashCode
public class CustomException extends Throwable {
  private final String message;

  public CustomException(Throwable exception) {
    this.message = exception.getMessage();
  }
}

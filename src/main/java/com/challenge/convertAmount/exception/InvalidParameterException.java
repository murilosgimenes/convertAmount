package com.challenge.convertAmount.exception;

import java.io.Serial;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class InvalidParameterException extends RuntimeException {
  @Serial
  private static final long serialVersionUID = 8194633927874898501L;

  private final HttpStatus statusCode;
  private final int errorCode;

  public InvalidParameterException(String message, int errorCode) {
    super(message);
    this.statusCode = HttpStatus.NOT_FOUND;
    this.errorCode = errorCode;
  }
}

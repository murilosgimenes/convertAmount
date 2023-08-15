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
public class CurrencyConversionException extends RuntimeException {
  @Serial
  private static final long serialVersionUID = -3655691689322771464L;

  private final HttpStatus statusCode;
  private final int errorCode;

  public CurrencyConversionException(String message,
                                     HttpStatus statusCode,
                                     int errorCode) {
    super(message);
    this.statusCode = statusCode;
    this.errorCode = errorCode;
  }
}

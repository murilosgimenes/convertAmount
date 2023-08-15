package com.challenge.convertAmount.commons;

import com.challenge.convertAmount.commons.dto.DefaultErrorMessageDto.ErrorMessage;
import com.challenge.convertAmount.exception.CurrencyConversionException;
import com.challenge.convertAmount.exception.InvalidParameterException;

import java.text.MessageFormat;

import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Order(0)
public class ControllerAdviceRest {

  @ExceptionHandler(InvalidParameterException.class)
  public ResponseEntity<ErrorMessage> invalidParameterErrorHandler(final InvalidParameterException e) {
    final var message = MessageFormat.format("{0} is invalid", e.getMessage());

    return ResponseEntity
        .status(e.getStatusCode())
        .body(ErrorMessage.builder()
            .developerMessage(message)
            .userMessage(message)
            .errorCode(e.getErrorCode())
            .build());
  }

  @ExceptionHandler(CurrencyConversionException.class)
  public ResponseEntity<ErrorMessage> invalidCurrencyConversionErrorHandler(final CurrencyConversionException e) {
    return ResponseEntity
        .status(e.getStatusCode())
        .body(ErrorMessage.builder()
            .developerMessage(e.getMessage())
            .userMessage(e.getMessage())
            .errorCode(e.getErrorCode())
            .build());
  }

}

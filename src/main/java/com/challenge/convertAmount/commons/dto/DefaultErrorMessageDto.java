package com.challenge.convertAmount.commons.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class DefaultErrorMessageDto<T> implements Serializable {

  @Serial
  private static final long serialVersionUID = -2754997796826937592L;

  private List<ErrorMessage> error;

  @NoArgsConstructor
  @AllArgsConstructor
  @Data
  @Builder
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class ErrorMessage implements Serializable {

    @Serial
    private static final long serialVersionUID = 482881720014522629L;

    private String developerMessage;
    private String userMessage;
    private int errorCode;
  }
}

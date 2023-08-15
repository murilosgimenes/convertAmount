package com.challenge.convertAmount.config.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthenticationErrorResponse {
  private String userMessage;
  private String developerMessage;
  private Integer errorCode;
}

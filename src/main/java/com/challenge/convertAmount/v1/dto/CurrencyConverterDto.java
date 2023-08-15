package com.challenge.convertAmount.v1.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CurrencyConverterDto {

  private String inputCurrency;
  private String outputCurrency;
  private double amount;
}

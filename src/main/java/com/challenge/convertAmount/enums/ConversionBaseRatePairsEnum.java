package com.challenge.convertAmount.enums;

import java.util.Arrays;
import java.util.Optional;

import lombok.Getter;
import lombok.NonNull;


/**
 * Exchange rate pairs:
 * CAD -> USD = 0.76158
 * USD -> GBP = 0.76700
 * CHF -> GBP = 0.84295
 * AUD -> EUR = 0.61175
 *
 * @author Murilo Gimenes - (12/08/2023)
 * @version 0.0.1
 * @since 0.0.1
 */
@Getter
public enum ConversionBaseRatePairsEnum {

  CAD_TO_USD("CAD", "USD", 0.76158),
  USD_TO_GBP("USD", "GBP", 0.76700),
  CHF_TO_GBP("CHF", "GBP", 0.84295),
  AUD_TO_EUR("AUD", "EUR", 0.61175);

  private final String input;
  private final String output;
  private final double exchangeRateValue;

  ConversionBaseRatePairsEnum(final String input,
                              final String output,
                              final double exchangeRateValue) {
    this.input = input;
    this.output = output;
    this.exchangeRateValue = exchangeRateValue;
  }

  public static Optional<ConversionBaseRatePairsEnum> getConversion(@NonNull final String input,
                                                                    @NonNull final String output) {
    return Arrays.stream(ConversionBaseRatePairsEnum.values())
        .filter(row -> row.getInput().equals(input.toUpperCase()) && row.getOutput().equals(output.toUpperCase()))
        .findAny();
  }

  public static Optional<ConversionBaseRatePairsEnum> getConversion(@NonNull final String input) {
    return Arrays.stream(ConversionBaseRatePairsEnum.values())
        .filter(row -> row.getInput().equals(input.toUpperCase()))
        .findAny();
  }

  public static Optional<ConversionBaseRatePairsEnum> getConversionByOutput(@NonNull final String output) {
    return Arrays.stream(ConversionBaseRatePairsEnum.values())
        .filter(row -> row.getOutput().equals(output.toUpperCase()))
        .findAny();
  }

}

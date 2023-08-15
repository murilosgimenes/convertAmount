package com.challenge.convertAmount.usecase.impl;

import com.challenge.convertAmount.enums.ConversionBaseRatePairsEnum;
import com.challenge.convertAmount.exception.CurrencyConversionException;
import com.challenge.convertAmount.exception.InvalidParameterException;
import com.challenge.convertAmount.usecase.CurrencyConverterInterface;
import com.challenge.convertAmount.v1.dto.CurrencyConverterDto;

import java.util.Objects;

import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
public class CurrencyConverterImpl implements CurrencyConverterInterface {

  private record CurrentConversion(Double amount, String input, String output, boolean isReversed) {
  }

  @Override
  public CurrencyConverterDto convertAmount(@NonNull CurrencyConverterDto currencyConverterDto) {
    this.validateConvertAmountParam(currencyConverterDto);

    var calculatedAmount = this.getConversionValue(currencyConverterDto.getInputCurrency(), currencyConverterDto.getOutputCurrency(), currencyConverterDto.getAmount());
    currencyConverterDto.setAmount(calculatedAmount);
    return currencyConverterDto;
  }

  private double getConversionValue(@NonNull final String input,
                                    @NonNull final String output,
                                    final double amount) {
    if (input.equals(output))
      return amount;

    var conversionValue = this.simpleConversion(input, output, amount);
    if (conversionValue != null)
      return conversionValue.amount();

    return this.getCrossConversion(input, output, amount);
  }

  private double getCrossConversion(String input, String output, double amount) {
    var currentValue = new CurrentConversion(amount, input, output, false);
    currentValue = this.crossConversion(currentValue, output);

    var securityCounter = 0;
    while (!Objects.isNull(currentValue) && this.isNotFinalRate(currentValue, output)) {
      var newConversion = this.crossConversion(currentValue, output);

      if (newConversion.input().equals(currentValue.input())
          && newConversion.output().equals(currentValue.output()))
        break;

      if (securityCounter >= 5)
        break;

      currentValue = newConversion;
      securityCounter++;
    }

    if (Objects.isNull(currentValue)) {
      throw new CurrencyConversionException("Param not found in the possibles conversions", HttpStatus.NOT_FOUND, 6);
    }

    if (this.isNotFinalRate(currentValue, output))
      throw new CurrencyConversionException("The conversion not is possible - Error in getCross rate", HttpStatus.MULTIPLE_CHOICES, 5);

    return currentValue.amount();
  }

  private boolean isNotFinalRate(@NonNull final CurrentConversion currentConversion, @NonNull final String output) {
    if (currentConversion.isReversed())
      return !currentConversion.input().equals(output);

    return !currentConversion.output().equals(output);
  }

  private static CurrentConversion updateCurrentValue(double amount,
                                                      ConversionBaseRatePairsEnum conversionByInput,
                                                      boolean isReverse) {


    var calculateAmount = isReverse
        ? amount * (1 / conversionByInput.getExchangeRateValue())
        : amount * conversionByInput.getExchangeRateValue();

    if (ObjectUtils.isEmpty(calculateAmount))
      throw new CurrencyConversionException("Conversion rate pair not found", HttpStatus.NOT_FOUND, 4);

    return new CurrentConversion(calculateAmount, conversionByInput.getInput(), conversionByInput.getOutput(), isReverse);
  }

  /**
   * <p>CurrentConversion is used to get de possibles combinations of conversion
   * </p>
   *
   * @return Returns the already calculated conversion
   */
  private CurrentConversion crossConversion(@NonNull final CurrentConversion currentConversion, @NonNull final String outputFinal) {

    var conversion = ConversionBaseRatePairsEnum.getConversion(outputFinal, currentConversion.input());
    if (conversion.isPresent())
      return CurrencyConverterImpl.updateCurrentValue(currentConversion.amount(), conversion.get(), true);

    conversion = ConversionBaseRatePairsEnum.getConversionByOutput(currentConversion.input());
    if (conversion.isPresent())
      return CurrencyConverterImpl.updateCurrentValue(currentConversion.amount(), conversion.get(), true);

    conversion = ConversionBaseRatePairsEnum.getConversion(currentConversion.output());
    if (conversion.isPresent())
      return CurrencyConverterImpl.updateCurrentValue(currentConversion.amount(), conversion.get(), false);

    conversion = ConversionBaseRatePairsEnum.getConversion(currentConversion.input());
    return conversion
        .map(row -> CurrencyConverterImpl.updateCurrentValue(currentConversion.amount(), row, false))
        .orElse(null);
  }

  /**
   * <p>Simple Conversion is tried convert A to B and B to A
   * </p>
   *
   * @param input  Is the amount to be converted
   * @param output Is the amount target to be converted
   * @return Return the possibility of conversion (A to B) or (B to A)
   */
  private CurrentConversion simpleConversion(@NonNull final String input,
                                             @NonNull final String output,
                                             double amount) {
    var conversion = ConversionBaseRatePairsEnum.getConversion(input, output);
    if (conversion.isPresent())
      return CurrencyConverterImpl.updateCurrentValue(amount, conversion.get(), false);

    conversion = ConversionBaseRatePairsEnum.getConversion(output, input);
    return conversion
        .map(row -> CurrencyConverterImpl.updateCurrentValue(amount, row, true))
        .orElse(null);
  }

  private void validateConvertAmountParam(@NonNull CurrencyConverterDto currencyConverterDto) {
    if (ObjectUtils.isEmpty(currencyConverterDto.getAmount()) || currencyConverterDto.getAmount() <= 0)
      throw new InvalidParameterException("amount", 1);

    if (ObjectUtils.isEmpty(currencyConverterDto.getInputCurrency()))
      throw new InvalidParameterException("inputCurrency", 2);

    if (ObjectUtils.isEmpty(currencyConverterDto.getOutputCurrency()))
      throw new InvalidParameterException("outputCurrency", 3);
  }

}

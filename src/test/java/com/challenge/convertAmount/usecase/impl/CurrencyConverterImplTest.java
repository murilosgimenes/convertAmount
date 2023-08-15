package com.challenge.convertAmount.usecase.impl;

import static org.mockito.MockitoAnnotations.openMocks;

import com.challenge.convertAmount.enums.ConversionBaseRatePairsEnum;
import com.challenge.convertAmount.exception.CurrencyConversionException;
import com.challenge.convertAmount.exception.InvalidParameterException;
import com.challenge.convertAmount.v1.dto.CurrencyConverterDto;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class CurrencyConverterImplTest {

  private CurrencyConverterImpl currencyConverter;

  @BeforeEach
  void setUp() {
    openMocks(this);
    this.currencyConverter = new CurrencyConverterImpl();
  }

  @ParameterizedTest
  @ValueSource(strings = {"CAD>CAD", "USD>USD"})
  @DisplayName("Basic test,convert to same rate")
  void convertToSameRate(String conversionRatePair) {
    var amount = 1;
    var param = conversionRatePair.split(">");

    var responseValue = this.currencyConverter.convertAmount(
        CurrencyConverterDto.builder()
            .inputCurrency(param[0])
            .outputCurrency(param[1])
            .amount(amount)
            .build());

    Assertions.assertEquals(1, responseValue.getAmount());
    Assertions.assertEquals(param[0], responseValue.getInputCurrency());
    Assertions.assertEquals(param[1], responseValue.getOutputCurrency());
  }

  @ParameterizedTest
  @ValueSource(strings = {"CAD>USD", "USD>GBP", "CHF>GBP", "AUD>EUR"})
  @DisplayName("Basic test, without recursive analise")
  void basicConversion(String conversionRatePair) {
    var amount = 1;
    var param = conversionRatePair.split(">");

    var responseValue = this.currencyConverter.convertAmount(
        CurrencyConverterDto.builder()
            .inputCurrency(param[0])
            .outputCurrency(param[1])
            .amount(amount)
            .build());
    var expectedValue = ConversionBaseRatePairsEnum.getConversion(param[0], param[1]).get().getExchangeRateValue();

    Assertions.assertEquals(expectedValue, responseValue.getAmount());
    Assertions.assertEquals(param[0], responseValue.getInputCurrency());
    Assertions.assertEquals(param[1], responseValue.getOutputCurrency());
  }

  @ParameterizedTest
  @ValueSource(strings = {"USD>CAD", "GBP>USD", "GBP>CHF", "EUR>AUD"})
  @DisplayName("Reverse test,without recursive analise")
  void reverseConversion(String conversionRatePair) {
    var amount = 35;
    var param = conversionRatePair.split(">");
    var responseValue = this.currencyConverter.convertAmount(
        CurrencyConverterDto.builder()
            .inputCurrency(param[0])
            .outputCurrency(param[1])
            .amount(amount)
            .build());
    var conversionBaseRatePair = ConversionBaseRatePairsEnum.getConversion(param[1], param[0]).get().getExchangeRateValue();
    var expectedValue = amount * (1 / conversionBaseRatePair);

    Assertions.assertEquals(expectedValue, responseValue.getAmount());
    Assertions.assertEquals(param[0], responseValue.getInputCurrency());
    Assertions.assertEquals(param[1], responseValue.getOutputCurrency());
  }

  @Test
  @DisplayName("CAD > GBP - The expected value is to convert Cad to USD and the response is converted USD to GBP")
  void conversionCadToGbp() {
    var amount = 1;
    var input = "CAD";
    var output = "GBP";
    var expectedValue = 0.5841318600000001;

    var responseValue = this.currencyConverter.convertAmount(
        CurrencyConverterDto.builder()
            .inputCurrency(input)
            .outputCurrency(output)
            .amount(amount)
            .build());

    Assertions.assertEquals(expectedValue, responseValue.getAmount());
    Assertions.assertEquals(input, responseValue.getInputCurrency());
    Assertions.assertEquals(output, responseValue.getOutputCurrency());
  }

  @Test
  @DisplayName("GBP > CAD - The expected value is to convert GBP to USD and the response is converted USD to CAD")
  void conversionGbpToCad() {
    var amount = 1;
    var input = "GBP";
    var output = "CAD";
    var expectedValue = 1.711942231673513;

    var responseValue = this.currencyConverter.convertAmount(
        CurrencyConverterDto.builder()
            .inputCurrency(input)
            .outputCurrency(output)
            .amount(amount)
            .build());

    Assertions.assertEquals(expectedValue, responseValue.getAmount());
    Assertions.assertEquals(input, responseValue.getInputCurrency());
    Assertions.assertEquals(output, responseValue.getOutputCurrency());
  }

  @ParameterizedTest
  @ValueSource(strings = {"USD>AUD", "EUR>CAD", "AUD>GBP"})
  @DisplayName("This conversion is impossible")
  void conversionUsdToAud(String conversionRatePair) {
    var param = conversionRatePair.split(">");
    Assertions.assertThrows(CurrencyConversionException.class,
        () -> this.currencyConverter.convertAmount(
            CurrencyConverterDto.builder()
                .inputCurrency(param[0])
                .outputCurrency(param[1])
                .amount(1)
                .build()));
  }

  @Test
  void invalidRatePair() {
    Assertions.assertThrows(CurrencyConversionException.class,
        () -> this.currencyConverter.convertAmount(CurrencyConverterDto.builder()
            .inputCurrency("Murilo")
            .outputCurrency("QuesTrade")
            .amount(1)
            .build()));
  }

  @Test
  void invalidAmountParam() {
    Assertions.assertThrows(InvalidParameterException.class,
        () -> this.currencyConverter.convertAmount(CurrencyConverterDto.builder()
            .inputCurrency("CAD")
            .outputCurrency("USD")
            .build()));
  }

  @Test
  void invalidInputParam() {
    Assertions.assertThrows(InvalidParameterException.class,
        () -> this.currencyConverter.convertAmount(CurrencyConverterDto.builder()
            .outputCurrency("USD")
            .amount(1)
            .build()));
  }

  @Test
  void invalidOutputParam() {
    Assertions.assertThrows(InvalidParameterException.class,
        () -> this.currencyConverter.convertAmount(CurrencyConverterDto.builder()
            .inputCurrency("CAD")
            .amount(1)
            .build()));
  }
}
package com.challenge.convertAmount.v1.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.challenge.convertAmount.commons.dto.DefaultErrorMessageDto;
import com.challenge.convertAmount.enums.ConversionBaseRatePairsEnum;
import com.challenge.convertAmount.v1.dto.CurrencyConverterDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.NonNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;


@SpringBootTest
@AutoConfigureMockMvc
class CurrencyConverterControllerTest {

  private static final String DEFAULT_END_POINT = "/api/v1/currency_converter/convert";
  private static final String DEFAULT_TOKEN_API = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.EY26NYcY2Syqu9K0_IvOcl02SzPampgz3nayllBJ9RQ";

  @Autowired
  private transient MockMvc mockMvc;

  @Autowired
  private transient ObjectMapper objectMapper;

  @ParameterizedTest
  @ValueSource(strings = {"CAD>CAD", "USD>USD"})
  @DisplayName("Basic test,convert to same rate")
  void convertToSameRate(String conversionRatePair) throws Exception {
    var amount = 35;
    var param = conversionRatePair.split(">");
    var input = param[0];
    var output = param[1];
    var responseRequest = this.makeRequest(input, output, amount);

    Assertions.assertEquals(amount, responseRequest.getAmount());
  }

  @ParameterizedTest
  @ValueSource(strings = {"CAD>USD", "USD>GBP", "CHF>GBP", "AUD>EUR"})
  @DisplayName("Basic test, without recursive analise")
  void basicConversion(String conversionRatePair) throws Exception {
    var amount = 1;
    var param = conversionRatePair.split(">");
    var input = param[0];
    var output = param[1];
    var responseRequest = this.makeRequest(input, output, amount);

    var expectedValue = ConversionBaseRatePairsEnum.getConversion(input, output).get().getExchangeRateValue();

    Assertions.assertEquals(expectedValue, responseRequest.getAmount());
  }

  @ParameterizedTest
  @ValueSource(strings = {"USD>CAD", "GBP>USD", "GBP>CHF", "EUR>AUD"})
  @DisplayName("Reverse test,without recursive analise")
  void reverseConversion(String conversionRatePair) throws Exception {
    var amount = 1;
    var param = conversionRatePair.split(">");
    var input = param[0];
    var output = param[1];
    var responseRequest = this.makeRequest(input, output, amount);

    var conversionBaseRatePair = ConversionBaseRatePairsEnum.getConversion(output, input).get().getExchangeRateValue();
    var expectedValue = amount * (1 / conversionBaseRatePair);


    Assertions.assertEquals(expectedValue, responseRequest.getAmount());
  }

  @Test
  @DisplayName("CAD > GBP - The expected value is to convert Cad to USD and the response is converted USD to GBP")
  void conversionCadToGbp() throws Exception {
    var amount = 1;
    var input = "CAD";
    var output = "GBP";
    var expectedValue = 0.5841318600000001;
    var responseRequest = this.makeRequest(input, output, amount);

    Assertions.assertEquals(expectedValue, responseRequest.getAmount());
    Assertions.assertEquals(input, responseRequest.getInputCurrency());
    Assertions.assertEquals(output, responseRequest.getOutputCurrency());
  }

  @Test
  @DisplayName("GBP > CAD - The expected value is to convert GBP to USD and the response is converted USD to CAD")
  void conversionGbpToCad() throws Exception {
    var amount = 1;
    var input = "GBP";
    var output = "CAD";
    var expectedValue = 1.711942231673513;
    var responseRequest = this.makeRequest(input, output, amount);

    Assertions.assertEquals(expectedValue, responseRequest.getAmount());
    Assertions.assertEquals(input, responseRequest.getInputCurrency());
    Assertions.assertEquals(output, responseRequest.getOutputCurrency());
  }

  @ParameterizedTest
  @ValueSource(strings = {"USD>AUD", "EUR>CAD", "AUD>GBP"})
  @DisplayName("This conversion is impossible")
  void conversionUsdToAud(String conversionRatePair) throws Exception {
    var param = conversionRatePair.split(">");
    var amount = 1;
    var input = param[0];
    var output = param[1];

    var responseMvc = this.mockMvc.perform(post(CurrencyConverterControllerTest.DEFAULT_END_POINT)
            .header("authorization", "Bearer " + CurrencyConverterControllerTest.DEFAULT_TOKEN_API)
            .contentType(MediaType.APPLICATION_JSON)
            .content(this.buildParam(input, output, amount))
        )
        .andExpect(status().isMultipleChoices())
        .andReturn();

    var response = this.objectMapper.readValue(responseMvc.getResponse().getContentAsString(), DefaultErrorMessageDto.ErrorMessage.class);

    Assertions.assertEquals(5, response.getErrorCode());
    Assertions.assertFalse(response.getUserMessage().isBlank());
    Assertions.assertFalse(response.getDeveloperMessage().isBlank());
  }

  @Test
  void amountCurrencyNotFound() throws Exception {
    var input = "USD";
    var output = "CAD";

    var dto = CurrencyConverterDto.builder()
        .inputCurrency(input)
        .outputCurrency(output)
        .build();
    var jsonParam = this.objectMapper.writeValueAsString(dto);

    var responseMvc = this.mockMvc.perform(post(CurrencyConverterControllerTest.DEFAULT_END_POINT)
            .header("authorization", "Bearer " + CurrencyConverterControllerTest.DEFAULT_TOKEN_API)
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonParam)
        )
        .andExpect(status().isNotFound())
        .andReturn();

    var response = this.objectMapper.readValue(responseMvc.getResponse().getContentAsString(), DefaultErrorMessageDto.ErrorMessage.class);

    Assertions.assertEquals(1, response.getErrorCode());
    Assertions.assertFalse(response.getUserMessage().isBlank());
    Assertions.assertFalse(response.getDeveloperMessage().isBlank());
  }

  @Test
  void inputCurrencyNotFound() throws Exception {
    var amount = 1;
    var input = "";
    var output = "CAD";

    var responseMvc = this.mockMvc.perform(post(CurrencyConverterControllerTest.DEFAULT_END_POINT)
            .header("authorization", "Bearer " + CurrencyConverterControllerTest.DEFAULT_TOKEN_API)
            .contentType(MediaType.APPLICATION_JSON)
            .content(this.buildParam(input, output, amount))
        )
        .andExpect(status().isNotFound())
        .andReturn();

    var response = this.objectMapper.readValue(responseMvc.getResponse().getContentAsString(), DefaultErrorMessageDto.ErrorMessage.class);

    Assertions.assertEquals(2, response.getErrorCode());
    Assertions.assertFalse(response.getUserMessage().isBlank());
    Assertions.assertFalse(response.getDeveloperMessage().isBlank());
  }

  @Test
  void outputCurrencyNotFound() throws Exception {
    var amount = 1;
    var input = "CAD";
    var output = "";

    var responseMvc = this.mockMvc.perform(post(CurrencyConverterControllerTest.DEFAULT_END_POINT)
            .header("authorization", "Bearer " + CurrencyConverterControllerTest.DEFAULT_TOKEN_API)
            .contentType(MediaType.APPLICATION_JSON)
            .content(this.buildParam(input, output, amount))
        )
        .andExpect(status().isNotFound())
        .andReturn();

    var response = this.objectMapper.readValue(responseMvc.getResponse().getContentAsString(), DefaultErrorMessageDto.ErrorMessage.class);

    Assertions.assertEquals(3, response.getErrorCode());
    Assertions.assertFalse(response.getUserMessage().isBlank());
    Assertions.assertFalse(response.getDeveloperMessage().isBlank());
  }

  @Test
  void InvalidRatePair() throws Exception {
    var amount = 1;
    var input = "Murilo";
    var output = "QuesTrade";

    var responseMvc = this.mockMvc.perform(post(CurrencyConverterControllerTest.DEFAULT_END_POINT)
            .header("authorization", "Bearer " + CurrencyConverterControllerTest.DEFAULT_TOKEN_API)
            .contentType(MediaType.APPLICATION_JSON)
            .content(this.buildParam(input, output, amount))
        )
        .andExpect(status().isNotFound())
        .andReturn();

    var response = this.objectMapper.readValue(responseMvc.getResponse().getContentAsString(), DefaultErrorMessageDto.ErrorMessage.class);

    Assertions.assertEquals(6, response.getErrorCode());
    Assertions.assertFalse(response.getUserMessage().isBlank());
    Assertions.assertFalse(response.getDeveloperMessage().isBlank());
  }

  private CurrencyConverterDto makeRequest(String input, String output, int amount) throws Exception {
    MvcResult responseMvc = this.mockMvc.perform(post(CurrencyConverterControllerTest.DEFAULT_END_POINT)
            .header("authorization", "Bearer " + CurrencyConverterControllerTest.DEFAULT_TOKEN_API)
            .contentType(MediaType.APPLICATION_JSON)
            .content(this.buildParam(input, output, amount))
        )
        .andReturn();
    return this.objectMapper.readValue(responseMvc.getResponse().getContentAsString(), CurrencyConverterDto.class);
  }

  private String buildParam(@NonNull final String inputCurrency,
                            @NonNull final String outputCurrency,
                            final long amount) throws JsonProcessingException {
    var dto = CurrencyConverterDto.builder()
        .inputCurrency(inputCurrency)
        .outputCurrency(outputCurrency)
        .amount(amount)
        .build();
    return this.objectMapper.writeValueAsString(dto);
  }
}
package com.challenge.convertAmount.v1.controller;

import com.challenge.convertAmount.usecase.CurrencyConverterInterface;
import com.challenge.convertAmount.v1.dto.CurrencyConverterDto;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/currency_converter/")
public class CurrencyConverterController {

  private final CurrencyConverterInterface currencyConverter;

  public CurrencyConverterController(CurrencyConverterInterface currencyConverter) {
    this.currencyConverter = currencyConverter;
  }

  @ResponseBody
  @PostMapping("convert")
  public ResponseEntity<CurrencyConverterDto> postConvert(@RequestBody final CurrencyConverterDto currencyConverterDto) {
    var response = currencyConverter.convertAmount(currencyConverterDto);
    return ResponseEntity.of(Optional.of(response));
  }
}

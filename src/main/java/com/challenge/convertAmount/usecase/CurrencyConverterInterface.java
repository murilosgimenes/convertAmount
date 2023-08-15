package com.challenge.convertAmount.usecase;

import com.challenge.convertAmount.v1.dto.CurrencyConverterDto;

import lombok.NonNull;
import org.springframework.stereotype.Service;

@Service
public interface CurrencyConverterInterface {

  CurrencyConverterDto convertAmount(@NonNull final CurrencyConverterDto currencyConverterDto);
}

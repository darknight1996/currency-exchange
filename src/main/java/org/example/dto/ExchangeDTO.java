package org.example.dto;

import org.example.model.currency.Currency;

import java.math.BigDecimal;

public record ExchangeDTO(Currency baseCurrency, Currency targetCurrency, BigDecimal rate, BigDecimal amount,
                          BigDecimal convertedAmount) {

}
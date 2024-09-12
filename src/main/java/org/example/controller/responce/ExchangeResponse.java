package org.example.controller.responce;

import org.example.model.currency.Currency;
import java.math.BigDecimal;

public class ExchangeResponse {

    private Currency baseCurrency;
    private Currency targetCurrency;
    private BigDecimal rate;
    private BigDecimal amount;
    private BigDecimal convertedAmount;

}
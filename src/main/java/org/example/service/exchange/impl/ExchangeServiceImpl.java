package org.example.service.exchange.impl;

import org.example.dto.ExchangeDTO;
import org.example.exception.ServiceException;
import org.example.model.exchange.ExchangeRate;
import org.example.service.exchange.ExchangeRateService;
import org.example.service.exchange.ExchangeService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

public class ExchangeServiceImpl implements ExchangeService {

    private final ExchangeRateService exchangeRateService = new ExchangeRateServiceImpl();

    @Override
    public Optional<ExchangeDTO> getExchange(final String baseCurrencyCode, final String targetCurrencyCode,
                                             final BigDecimal amount) throws ServiceException {

        final Optional<ExchangeRate> exchangeRateOptional = getExchangeRate(baseCurrencyCode, targetCurrencyCode);

        return exchangeRateOptional.map(exchangeRate -> new ExchangeDTO(
                exchangeRate.getBaseCurrency(),
                exchangeRate.getTargetCurrency(),
                exchangeRate.getRate(),
                amount,
                amount.multiply(exchangeRate.getRate()).setScale(2, RoundingMode.HALF_EVEN)
        ));
    }

    private Optional<ExchangeRate> getExchangeRate(String baseCurrencyCode, String targetCurrencyCode) throws ServiceException {
        Optional<ExchangeRate> exchangeRateOptional = exchangeRateService.getByCodes(baseCurrencyCode, targetCurrencyCode);
        if (exchangeRateOptional.isEmpty()) {
            exchangeRateOptional = exchangeRateService.getByCodesReverse(baseCurrencyCode, targetCurrencyCode);
        }
        if (exchangeRateOptional.isEmpty()) {
            exchangeRateOptional = exchangeRateService.getByCodesCrossUSD(baseCurrencyCode, targetCurrencyCode);
        }
        return exchangeRateOptional;
    }

}

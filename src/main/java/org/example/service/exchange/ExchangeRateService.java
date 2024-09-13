package org.example.service.exchange;

import org.example.exception.ServiceException;
import org.example.model.exchange.ExchangeRate;

import java.util.List;
import java.util.Optional;

public interface ExchangeRateService {

    List<ExchangeRate> getAll() throws ServiceException;
    Optional<ExchangeRate> add(final ExchangeRate exchangeRate) throws ServiceException;
    void update(final ExchangeRate exchangeRate) throws ServiceException;
    Optional<ExchangeRate> getByCodes(final String baseCurrencyCode, final String targetCurrencyCode) throws ServiceException;
    Optional<ExchangeRate> getByCodesReverse(String baseCurrencyCode, String targetCurrencyCode) throws ServiceException;
    Optional<ExchangeRate> getByCodesCrossUSD(String baseCurrencyCode, String targetCurrencyCode) throws ServiceException;

}

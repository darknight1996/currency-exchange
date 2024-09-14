package org.example.service.exchange.impl;

import org.example.exception.RepositoryException;
import org.example.exception.ServiceException;
import org.example.model.exchange.ExchangeRate;
import org.example.repository.exchange.ExchangeRateRepository;
import org.example.repository.exchange.impl.JdbcExchangeRateRepository;
import org.example.service.exchange.ExchangeRateService;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;
import java.util.Optional;

public class ExchangeRateServiceImpl implements ExchangeRateService {

    private final ExchangeRateRepository exchangeRateRepository = new JdbcExchangeRateRepository();

    @Override
    public List<ExchangeRate> getAll() throws ServiceException {
        try {
            return exchangeRateRepository.getAll();
        } catch (RepositoryException e) {
            throw new ServiceException(e.getMessage());
        }
    }

    @Override
    public Optional<ExchangeRate> add(final ExchangeRate exchangeRate) throws ServiceException {
        try {
            return exchangeRateRepository.add(exchangeRate);
        } catch (RepositoryException e) {
            throw new ServiceException(e.getMessage());
        }
    }

    @Override
    public void update(final ExchangeRate exchangeRate) throws ServiceException {
        try {
            exchangeRateRepository.update(exchangeRate);
        } catch (RepositoryException e) {
            throw new ServiceException(e.getMessage());
        }
    }

    @Override
    public Optional<ExchangeRate> getByCodes(final String baseCurrencyCode,
                                             final String targetCurrencyCode) throws ServiceException {
        try {
            return exchangeRateRepository.getByCodes(baseCurrencyCode, targetCurrencyCode);
        } catch (RepositoryException e) {
            throw new ServiceException(e.getMessage());
        }
    }

    @Override
    public Optional<ExchangeRate> getByCodesReverse(final String baseCurrencyCode,
                                                    final String targetCurrencyCode) throws ServiceException {
        try {
            final Optional<ExchangeRate> exchangeRateReversedOptional =
                    exchangeRateRepository.getByCodes(targetCurrencyCode, baseCurrencyCode);

            if (exchangeRateReversedOptional.isPresent()) {
                final ExchangeRate exchangeRateReversed = exchangeRateReversedOptional.get();

                final ExchangeRate exchangeRate = new ExchangeRate(
                        exchangeRateReversed.getTargetCurrency(),
                        exchangeRateReversed.getBaseCurrency(),
                        BigDecimal.ONE.divide(exchangeRateReversed.getRate(), MathContext.DECIMAL64)
                );

                return Optional.of(exchangeRate);
            }
            return Optional.empty();
        } catch (RepositoryException e) {
            throw new ServiceException(e.getMessage());
        }
    }

    @Override
    public Optional<ExchangeRate> getByCodesCrossUSD(final String baseCurrencyCode,
                                                     final String targetCurrencyCode) throws ServiceException {
        try {
            final Optional<ExchangeRate> usdToBaseExchangeRateOptional =
                    exchangeRateRepository.getByCodes("USD", baseCurrencyCode);

            final Optional<ExchangeRate> usdToTargetExchangeRateOptional =
                    exchangeRateRepository.getByCodes("USD", targetCurrencyCode);

            if (usdToBaseExchangeRateOptional.isPresent() && usdToTargetExchangeRateOptional.isPresent()) {
                final ExchangeRate usdToBaseExchangeRate = usdToBaseExchangeRateOptional.get();
                final ExchangeRate usdToTargetExchangeRate = usdToTargetExchangeRateOptional.get();

                final BigDecimal usdToBaseRate = usdToBaseExchangeRate.getRate();
                final BigDecimal usdToTargetRate = usdToTargetExchangeRate.getRate();

                final BigDecimal baseToTargetRate = usdToTargetRate.divide(usdToBaseRate, MathContext.DECIMAL64);

                final ExchangeRate exchangeRate = new ExchangeRate(
                        usdToBaseExchangeRate.getTargetCurrency(),
                        usdToTargetExchangeRate.getTargetCurrency(),
                        baseToTargetRate
                );

                return Optional.of(exchangeRate);
            }
            return Optional.empty();
        } catch (RepositoryException e) {
            throw new ServiceException(e.getMessage());
        }
    }

}

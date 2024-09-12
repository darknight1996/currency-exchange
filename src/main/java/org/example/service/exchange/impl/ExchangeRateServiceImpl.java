package org.example.service.exchange.impl;

import org.example.exception.RepositoryException;
import org.example.exception.ServiceException;
import org.example.model.exchange.ExchangeRate;
import org.example.repository.exchange.ExchangeRateRepository;
import org.example.repository.exchange.impl.JdbcExchangeRateRepository;
import org.example.service.exchange.ExchangeRateService;

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
    public Optional<ExchangeRate> getByCodes(final String baseCurrencyCode, final String targetCurrencyCode) throws ServiceException {
        try {
            return exchangeRateRepository.getByCodes(baseCurrencyCode, targetCurrencyCode);
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

}

package org.example.service.exchange.impl;

import org.example.exception.RepositoryException;
import org.example.exception.ServiceException;
import org.example.model.exchange.ExchangeRate;
import org.example.repository.exchange.ExchangeRateRepository;
import org.example.repository.exchange.impl.JdbcExchangeRateRepository;
import org.example.service.exchange.ExchangeRateService;

import java.util.List;

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

}

package org.example.repository.exchange;

import org.example.exception.RepositoryException;
import org.example.model.exchange.ExchangeRate;

import java.util.List;
import java.util.Optional;

public interface ExchangeRateRepository {

    List<ExchangeRate> getAll() throws RepositoryException;
    Optional<ExchangeRate> getByCodes(final String baseCurrencyCode, final String targetCurrencyCode) throws RepositoryException;
    Optional<ExchangeRate> add(ExchangeRate exchangeRate) throws RepositoryException;

}

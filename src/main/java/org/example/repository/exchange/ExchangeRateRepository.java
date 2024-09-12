package org.example.repository.exchange;

import org.example.exception.RepositoryException;
import org.example.model.exchange.ExchangeRate;

import java.util.List;

public interface ExchangeRateRepository {

    List<ExchangeRate> getAll() throws RepositoryException;

}

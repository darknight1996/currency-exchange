package org.example.service.impl;

import org.example.exception.RepositoryException;
import org.example.exception.ServiceException;
import org.example.model.Currency;
import org.example.repository.CurrencyRepository;
import org.example.repository.impl.JdbcCurrencyRepository;
import org.example.service.CurrencyService;

import java.util.List;

public class CurrencyServiceImpl implements CurrencyService {

    private final CurrencyRepository currencyRepository = new JdbcCurrencyRepository();

    @Override
    public List<Currency> getAll() throws ServiceException {
        try {
            return currencyRepository.getAll();
        } catch (RepositoryException e) {
            throw new ServiceException(e.getMessage());
        }
    }

}

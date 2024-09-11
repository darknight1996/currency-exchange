package org.example.service.currency.impl;

import org.example.exception.RepositoryException;
import org.example.exception.ServiceException;
import org.example.model.currency.Currency;
import org.example.repository.currency.CurrencyRepository;
import org.example.repository.currency.impl.JdbcCurrencyRepository;
import org.example.service.currency.CurrencyService;

import java.util.List;
import java.util.Optional;

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

    @Override
    public Optional<Currency> getByCode(final String code) throws ServiceException {
        try {
            return currencyRepository.getByCode(code);
        } catch (RepositoryException e) {
            throw new ServiceException(e.getMessage());
        }
    }

    @Override
    public Optional<Currency> add(final Currency currency) throws ServiceException {
        try {
            return currencyRepository.add(currency);
        } catch (RepositoryException e) {
            throw new ServiceException(e.getMessage());
        }
    }

}

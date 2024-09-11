package org.example.service.currency;

import org.example.exception.ServiceException;
import org.example.model.currency.Currency;

import java.util.List;
import java.util.Optional;

public interface CurrencyService {

    List<Currency> getAll() throws ServiceException;
    Optional<Currency> getByCode(final String code) throws ServiceException;
    Optional<Currency> add(final Currency currency) throws ServiceException;

}

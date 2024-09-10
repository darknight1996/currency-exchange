package org.example.service;

import org.example.exception.ServiceException;
import org.example.model.Currency;

import java.util.List;

public interface CurrencyService {

    List<Currency> getAll() throws ServiceException;

}

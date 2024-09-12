package org.example.service.exchange;

import org.example.exception.ServiceException;
import org.example.model.exchange.ExchangeRate;

import java.util.List;

public interface ExchangeRateService {

    List<ExchangeRate> getAll() throws ServiceException;

}

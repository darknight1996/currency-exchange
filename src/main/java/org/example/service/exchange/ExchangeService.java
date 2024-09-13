package org.example.service.exchange;

import org.example.dto.ExchangeDTO;
import org.example.exception.ServiceException;

import java.math.BigDecimal;
import java.util.Optional;

public interface ExchangeService {

    Optional<ExchangeDTO> getExchange(final String baseCurrencyCode, final String targetCurrencyCode,
                                      final BigDecimal amount) throws ServiceException;

}

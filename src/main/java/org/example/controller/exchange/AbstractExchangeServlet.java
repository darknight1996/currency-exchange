package org.example.controller.exchange;

import jakarta.servlet.http.HttpServletResponse;
import org.example.controller.AbstractServlet;
import org.example.service.exchange.ExchangeRateService;
import org.example.service.exchange.impl.ExchangeRateServiceImpl;

import java.io.IOException;
import java.math.BigDecimal;

public abstract class AbstractExchangeServlet extends AbstractServlet {

    protected final ExchangeRateService exchangeRateService = new ExchangeRateServiceImpl();

    protected BigDecimal parseBigDecimal(final String rateParam, final HttpServletResponse response,
                                         final String message) throws IOException {
        try {
            return BigDecimal.valueOf(Double.parseDouble(rateParam));
        } catch (NumberFormatException e) {
            handleBadRequest(response, message);
            return null;
        }
    }
}

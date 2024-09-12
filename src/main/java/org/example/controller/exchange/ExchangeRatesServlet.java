package org.example.controller.exchange;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.controller.AbstractServlet;
import org.example.exception.ServiceException;
import org.example.model.exchange.ExchangeRate;
import org.example.service.exchange.ExchangeRateService;
import org.example.service.exchange.impl.ExchangeRateServiceImpl;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends AbstractServlet {

    private final ExchangeRateService exchangeRateService = new ExchangeRateServiceImpl();

    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {
        try (final PrintWriter writer = resp.getWriter()) {
            final List<ExchangeRate> exchangeRates = exchangeRateService.getAll();
            objectMapper.writeValue(writer, exchangeRates);
        } catch (ServiceException e) {
            handleInternalServerError(resp, e);
        }
    }

}

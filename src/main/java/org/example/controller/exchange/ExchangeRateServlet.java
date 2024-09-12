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
import java.util.Optional;

@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet extends AbstractServlet {

    private final ExchangeRateService exchangeRateService = new ExchangeRateServiceImpl();

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        final String codePair = req.getPathInfo().replaceFirst("/", "");

        if (codePair.trim().length() != 6) {
            handleBadRequest(resp, "Codes pair are invalid");
            return;
        }

        final String baseCurrencyCode = codePair.substring(0, 3);
        final String targetCurrencyCode = codePair.substring(3);

        try (final PrintWriter writer = resp.getWriter()) {
            final Optional<ExchangeRate> exchangeRateOptional = exchangeRateService.getByCodes(baseCurrencyCode, targetCurrencyCode);

            if (exchangeRateOptional.isPresent()) {
                objectMapper.writeValue(writer, exchangeRateOptional.get());
            } else {
                handleNotFound(resp, "Exchange rate for provided codes: "
                        + baseCurrencyCode + ", " + targetCurrencyCode + " not found");
            }
        } catch (ServiceException e) {
            handleInternalServerError(resp, e);
        }
    }
}

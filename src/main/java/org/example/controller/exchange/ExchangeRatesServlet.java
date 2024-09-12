package org.example.controller.exchange;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.controller.AbstractServlet;
import org.example.exception.ServiceException;
import org.example.model.currency.Currency;
import org.example.model.exchange.ExchangeRate;
import org.example.service.currency.CurrencyService;
import org.example.service.currency.impl.CurrencyServiceImpl;
import org.example.service.exchange.ExchangeRateService;
import org.example.service.exchange.impl.ExchangeRateServiceImpl;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends AbstractServlet {

    private final ExchangeRateService exchangeRateService = new ExchangeRateServiceImpl();
    private final CurrencyService currencyService = new CurrencyServiceImpl();

    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {
        try (final PrintWriter writer = resp.getWriter()) {
            final List<ExchangeRate> exchangeRates = exchangeRateService.getAll();
            objectMapper.writeValue(writer, exchangeRates);
        } catch (ServiceException e) {
            handleInternalServerError(resp, e);
        }
    }

    @Override
    protected void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        final String baseCurrencyCode = req.getParameter("baseCurrencyCode");
        final String targetCurrencyCode = req.getParameter("targetCurrencyCode");
        final String rateParam = req.getParameter("rate");

        if (isInvalidParameters(baseCurrencyCode, targetCurrencyCode, rateParam, resp)) {
            return;
        }

        final BigDecimal rate = parseRate(rateParam, resp);
        if (rate == null) {
            return;
        }

        processExchangeRate(baseCurrencyCode, targetCurrencyCode, rate, resp);
    }

    private boolean isInvalidParameters(final String baseCurrencyCode, final String targetCurrencyCode,
                                       final String rateParam, final HttpServletResponse resp) throws IOException {
        if (isNullOrEmpty(baseCurrencyCode)) {
            handleBadRequest(resp, "Missing parameter - baseCurrencyCode");
            return true;
        }

        if (isNullOrEmpty(targetCurrencyCode)) {
            handleBadRequest(resp, "Missing parameter - targetCurrencyCode");
            return true;
        }

        if (isNullOrEmpty(rateParam)) {
            handleBadRequest(resp, "Missing parameter - rate");
            return true;
        }

        return false;
    }

    private BigDecimal parseRate(String rateParam, HttpServletResponse resp) throws IOException {
        try {
            return BigDecimal.valueOf(Double.parseDouble(rateParam));
        } catch (NumberFormatException e) {
            handleBadRequest(resp, "Rate is invalid");
            return null;
        }
    }

    private void processExchangeRate(final String baseCurrencyCode, final String targetCurrencyCode,
                                     final BigDecimal rate, final HttpServletResponse resp) throws IOException {
        try (final PrintWriter writer = resp.getWriter()) {
            final Optional<ExchangeRate> exchangeRateOptional = exchangeRateService.getByCodes(baseCurrencyCode, targetCurrencyCode);

            if (exchangeRateOptional.isPresent()) {
                handleConflict(resp, "Exchange rate with this currencies already exists");
                return;
            }

            final Optional<Currency> baseCurrencyOptional = currencyService.getByCode(baseCurrencyCode);
            final Optional<Currency> targetCurrencyOptional = currencyService.getByCode(targetCurrencyCode);

            if (baseCurrencyOptional.isEmpty() || targetCurrencyOptional.isEmpty()) {
                handleNotFound(resp, "One or both currencies not found");
                return;
            }

            final ExchangeRate exchangeRate = new ExchangeRate(
                    baseCurrencyOptional.get(),
                    targetCurrencyOptional.get(),
                    rate
            );

            final Optional<ExchangeRate> exchangeRateNewOptional = exchangeRateService.add(exchangeRate);

            if (exchangeRateNewOptional.isPresent()) {
                objectMapper.writeValue(writer, exchangeRateNewOptional.get());
            }
        } catch (ServiceException e) {
            handleInternalServerError(resp, e);
        }
    }

}

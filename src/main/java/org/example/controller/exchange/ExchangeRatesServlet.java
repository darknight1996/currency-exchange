package org.example.controller.exchange;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.exception.ServiceException;
import org.example.model.currency.Currency;
import org.example.model.exchange.ExchangeRate;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends AbstractExchangeServlet {

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        try (final PrintWriter writer = response.getWriter()) {
            final List<ExchangeRate> exchangeRates = exchangeRateService.getAll();
            objectMapper.writeValue(writer, exchangeRates);
        } catch (ServiceException e) {
            handleInternalServerError(response, e.getMessage());
        }
    }

    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws  IOException {
        final String baseCurrencyCode = request.getParameter("baseCurrencyCode");
        final String targetCurrencyCode = request.getParameter("targetCurrencyCode");
        final String rateParam = request.getParameter("rate");

        if (isInvalidParameters(baseCurrencyCode, targetCurrencyCode, rateParam, response)) {
            return;
        }

        final BigDecimal rate = parseBigDecimal(rateParam, response, "Invalid rate");
        if (rate == null) {
            return;
        }

        processAddExchangeRate(baseCurrencyCode, targetCurrencyCode, rate, response);
    }

    private boolean isInvalidParameters(final String baseCurrencyCode, final String targetCurrencyCode,
                                       final String rateParam, final HttpServletResponse response) throws IOException {
        if (isNullOrEmpty(baseCurrencyCode)) {
            handleBadRequest(response, "Missing parameter - baseCurrencyCode");
            return true;
        }

        if (isNullOrEmpty(targetCurrencyCode)) {
            handleBadRequest(response, "Missing parameter - targetCurrencyCode");
            return true;
        }

        if (isNullOrEmpty(rateParam)) {
            handleBadRequest(response, "Missing parameter - rate");
            return true;
        }

        return false;
    }

    private void processAddExchangeRate(final String baseCurrencyCode, final String targetCurrencyCode,
                                     final BigDecimal rate, final HttpServletResponse response) throws IOException {
        try (final PrintWriter writer = response.getWriter()) {
            final Optional<ExchangeRate> exchangeRateOptional =
                    exchangeRateService.getByCodes(baseCurrencyCode, targetCurrencyCode);

            if (exchangeRateOptional.isPresent()) {
                handleConflict(response, "Exchange rate with this currencies already exists");
                return;
            }

            final Optional<Currency> baseCurrencyOptional = currencyService.getByCode(baseCurrencyCode);
            final Optional<Currency> targetCurrencyOptional = currencyService.getByCode(targetCurrencyCode);

            if (baseCurrencyOptional.isEmpty() || targetCurrencyOptional.isEmpty()) {
                handleNotFound(response, "One or both currencies not found");
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
            handleInternalServerError(response, e.getMessage());
        }
    }

}

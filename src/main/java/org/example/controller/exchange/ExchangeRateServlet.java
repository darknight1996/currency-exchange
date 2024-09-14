package org.example.controller.exchange;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.exception.ServiceException;
import org.example.model.currency.Currency;
import org.example.model.exchange.ExchangeRate;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.Optional;

@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet extends AbstractExchangeServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if ("PATCH".equals(request.getMethod())) {
            doPatch(request, response);
        } else {
            super.service(request, response);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String codePair = getPathParam(request);

        if (isCodePairInvalid(codePair, response)) {
            return;
        }

        final String baseCurrencyCode = codePair.substring(0, 3);
        final String targetCurrencyCode = codePair.substring(3);

        try (final PrintWriter writer = response.getWriter()) {
            final Optional<ExchangeRate> exchangeRateOptional =
                    exchangeRateService.getByCodes(baseCurrencyCode, targetCurrencyCode);

            if (exchangeRateOptional.isPresent()) {
                objectMapper.writeValue(writer, exchangeRateOptional.get());
            } else {
                handleNotFound(response, "Exchange rate for provided currencies: "
                        + baseCurrencyCode + ", " + targetCurrencyCode + " not found");
            }
        } catch (ServiceException e) {
            handleInternalServerError(response, e.getMessage());
        }
    }

    @Override
    protected void doPatch(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        final String codePair = getPathParam(request);

        final String parameter = request.getReader().readLine();
        if (parameter == null || !parameter.contains("rate")) {
            handleBadRequest(response, "Missing parameter - rate");
            return;
        }

        final String rateParam = parameter.replace("rate=", "");

        if (isInvalidParameters(codePair, rateParam, response)) {
            return;
        }

        final BigDecimal rate = parseBigDecimal(rateParam, response, "Invalid rate");
        if (rate == null) {
            return;
        }

        final String baseCurrencyCode = codePair.substring(0, 3);
        final String targetCurrencyCode = codePair.substring(3);

        processUpdateExchangeRate(baseCurrencyCode, targetCurrencyCode, rate, response);
    }

    private boolean isInvalidParameters(final String codePair, final String rateParam,
                                        final HttpServletResponse response) throws IOException {
        if (isCodePairInvalid(codePair, response)) {
            return true;
        }

        if (isNullOrEmpty(rateParam)) {
            handleBadRequest(response, "Missing parameter - rate");
            return true;
        }

        return false;
    }

    private boolean isCodePairInvalid(final String codePair, final HttpServletResponse response) throws IOException {
        if (codePair.trim().length() != 6) {
            handleBadRequest(response, "Codes pair are invalid");
            return true;
        }

        return false;
    }

    private void processUpdateExchangeRate(final String baseCurrencyCode, final String targetCurrencyCode,
                                     final BigDecimal rate, final HttpServletResponse response) throws IOException {
        try (final PrintWriter writer = response.getWriter()) {
            final Optional<ExchangeRate> exchangeRateOptional =
                    exchangeRateService.getByCodes(baseCurrencyCode, targetCurrencyCode);

            if (exchangeRateOptional.isPresent()) {
                final Optional<Currency> baseCurrencyOptional = currencyService.getByCode(baseCurrencyCode);
                final Optional<Currency> targetCurrencyOptional = currencyService.getByCode(targetCurrencyCode);

                if (baseCurrencyOptional.isEmpty() || targetCurrencyOptional.isEmpty()) {
                    handleNotFound(response, "One or both currencies not found");
                    return;
                }

                final ExchangeRate exchangeRate = new ExchangeRate(
                        exchangeRateOptional.get().getId(),
                        baseCurrencyOptional.get(),
                        targetCurrencyOptional.get(),
                        rate
                );

                exchangeRateService.update(exchangeRate);

                objectMapper.writeValue(writer, exchangeRate);
            } else {
                handleNotFound(response, "Exchange rate for provided currencies: "
                        + baseCurrencyCode + ", " + targetCurrencyCode + " not found");
            }
        } catch (ServiceException e) {
            handleInternalServerError(response, e.getMessage());
        }
    }

}

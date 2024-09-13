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
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        final String codePair = getPathParam(req);

        if (isCodePairInvalid(codePair, resp)) {
            return;
        }

        final String baseCurrencyCode = codePair.substring(0, 3);
        final String targetCurrencyCode = codePair.substring(3);

        try (final PrintWriter writer = resp.getWriter()) {
            final Optional<ExchangeRate> exchangeRateOptional =
                    exchangeRateService.getByCodes(baseCurrencyCode, targetCurrencyCode);

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

    @Override
    protected void doPatch(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {
        final String codePair = getPathParam(req);

        final String parameter = req.getReader().readLine();
        if (parameter == null || !parameter.contains("rate")) {
            handleBadRequest(resp, "Missing parameter - rate");
            return;
        }

        final String rateParam = parameter.replace("rate=", "");

        if (isInvalidParameters(codePair, rateParam, resp)) {
            return;
        }

        final BigDecimal rate = parseRate(rateParam, resp);
        if (rate == null) {
            handleBadRequest(resp, "Invalid rate");
            return;
        }

        final String baseCurrencyCode = codePair.substring(0, 3);
        final String targetCurrencyCode = codePair.substring(3);

        processUpdateExchangeRate(baseCurrencyCode, targetCurrencyCode, rate, resp);
    }

    private boolean isInvalidParameters(final String codePair, final String rateParam,
                                        final HttpServletResponse resp) throws IOException {
        if (isCodePairInvalid(codePair, resp)) {
            return true;
        }

        if (isNullOrEmpty(rateParam)) {
            handleBadRequest(resp, "Missing parameter - rate");
            return true;
        }

        return false;
    }

    private boolean isCodePairInvalid(final String codePair, final HttpServletResponse resp) throws IOException {
        if (codePair.trim().length() != 6) {
            handleBadRequest(resp, "Codes pair are invalid");
            return true;
        }

        return false;
    }

    private void processUpdateExchangeRate(final String baseCurrencyCode, final String targetCurrencyCode,
                                     final BigDecimal rate, final HttpServletResponse resp) throws IOException {
        try (final PrintWriter writer = resp.getWriter()) {
            final Optional<ExchangeRate> exchangeRateOptional =
                    exchangeRateService.getByCodes(baseCurrencyCode, targetCurrencyCode);

            if (exchangeRateOptional.isPresent()) {
                final Optional<Currency> baseCurrencyOptional = currencyService.getByCode(baseCurrencyCode);
                final Optional<Currency> targetCurrencyOptional = currencyService.getByCode(targetCurrencyCode);

                if (baseCurrencyOptional.isEmpty() || targetCurrencyOptional.isEmpty()) {
                    handleNotFound(resp, "One or both currencies not found");
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
                handleNotFound(resp, "Exchange rate for provided codes: "
                        + baseCurrencyCode + ", " + targetCurrencyCode + " not found");
            }
        } catch (ServiceException e) {
            handleInternalServerError(resp, e);
        }
    }

}

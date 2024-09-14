package org.example.controller.exchange;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.dto.ExchangeDTO;
import org.example.exception.ServiceException;
import org.example.service.exchange.ExchangeService;
import org.example.service.exchange.impl.ExchangeServiceImpl;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.Optional;

@WebServlet("/exchange")
public class ExchangeServlet extends AbstractExchangeServlet {

    private final ExchangeService exchangeRateService = new ExchangeServiceImpl();

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        final String baseCurrencyCode = request.getParameter("from");
        final String targetCurrencyCode = request.getParameter("to");
        final String amountParam = request.getParameter("amount");

        if (isInvalidParameters(baseCurrencyCode, targetCurrencyCode, amountParam, response)) {
            return;
        }

        final BigDecimal amount = parseBigDecimal(amountParam, response, "Invalid amount");
        if (amount == null) {
            return;
        }

        processGetExchange(baseCurrencyCode, targetCurrencyCode, amount, response);
    }

    private boolean isInvalidParameters(final String baseCurrencyCode, final String targetCurrencyCode,
                                        final String amountParam, final HttpServletResponse response) throws IOException {
        if (isNullOrEmpty(baseCurrencyCode)) {
            handleBadRequest(response, "Missing parameter - from");
            return true;
        }

        if (isNullOrEmpty(targetCurrencyCode)) {
            handleBadRequest(response, "Missing parameter - to");
            return true;
        }

        if (isNullOrEmpty(amountParam)) {
            handleBadRequest(response, "Missing parameter - amount");
            return true;
        }

        return false;
    }

    private void processGetExchange(final String baseCurrencyCode, final String targetCurrencyCode,
                                    final BigDecimal amount, final HttpServletResponse response) throws IOException {
        try (final PrintWriter writer = response.getWriter()) {
            final Optional<ExchangeDTO> exchangeOptional =
                    exchangeRateService.getExchange(baseCurrencyCode, targetCurrencyCode, amount);

            if (exchangeOptional.isPresent()) {
                objectMapper.writeValue(writer, exchangeOptional.get());
            } else {
                handleNotFound(response, "Exchange for provided currencies: "
                        + baseCurrencyCode + ", " + targetCurrencyCode + " not found");
            }
        } catch (ServiceException e) {
            handleInternalServerError(response, e.getMessage());
        }
    }

}

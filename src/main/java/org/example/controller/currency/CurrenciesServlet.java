package org.example.controller.currency;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.controller.AbstractServlet;
import org.example.exception.ServiceException;
import org.example.model.currency.Currency;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;

@WebServlet("/currencies")
public class CurrenciesServlet extends AbstractServlet {

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        try (final PrintWriter writer = response.getWriter()) {
            final List<Currency> currencies = currencyService.getAll();
            objectMapper.writeValue(writer, currencies);
        } catch (IOException | ServiceException e) {
            handleInternalServerError(response, e.getMessage());
        }
    }

    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        final String code = request.getParameter("code");
        final String name = request.getParameter("name");
        final String sign = request.getParameter("sign");

        if (isInvalidParameters(code, name, sign, response)) {
            return;
        }

        try (final PrintWriter writer = response.getWriter()) {
            if (currencyService.getByCode(code).isPresent()) {
                handleConflict(response, "Currency with this code already exists");
                return;
            }

            final Currency currency = new Currency(code, name, sign);
            final Optional<Currency> currencyOptional = currencyService.add(currency);

            if (currencyOptional.isPresent()) {
                objectMapper.writeValue(writer, currencyOptional.get());
            }
        } catch (ServiceException e) {
            handleInternalServerError(response, e.getMessage());
        }
    }

    private boolean isInvalidParameters(final String code, final String name, final String sign,
                                        final HttpServletResponse response) throws IOException {
        if (isNullOrEmpty(code)) {
            handleBadRequest(response, "Missing parameter - code");
            return true;
        }

        if (isNullOrEmpty(name)) {
            handleBadRequest(response, "Missing parameter - name");
            return true;
        }

        if (isNullOrEmpty(sign)) {
            handleBadRequest(response, "Missing parameter - sign");
            return true;
        }

        return false;
    }

}

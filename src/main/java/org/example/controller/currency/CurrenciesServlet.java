package org.example.controller.currency;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.controller.AbstractServlet;
import org.example.exception.ServiceException;
import org.example.model.currency.Currency;
import org.example.service.currency.CurrencyService;
import org.example.service.currency.impl.CurrencyServiceImpl;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;

@WebServlet("/currencies")
public class CurrenciesServlet extends AbstractServlet {

    private final CurrencyService currencyService = new CurrencyServiceImpl();

    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {
        try (final PrintWriter writer = resp.getWriter()) {
            final List<Currency> currencies = currencyService.getAll();
            objectMapper.writeValue(writer, currencies);
        } catch (IOException | ServiceException e) {
            handleInternalServerError(resp, e);
        }
    }

    @Override
    protected void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {
        final String code = req.getParameter("code");
        final String name = req.getParameter("name");
        final String sign = req.getParameter("sign");

        if (isInvalidParameters(code, name, sign, resp)) {
            return;
        }

        try (final PrintWriter writer = resp.getWriter()) {
            if (currencyService.getByCode(code).isPresent()) {
                handleConflict(resp, "Currency with this code already exists");
                return;
            }

            final Currency currency = new Currency(code, name, sign);
            final Optional<Currency> currencyOptional = currencyService.add(currency);

            if (currencyOptional.isPresent()) {
                objectMapper.writeValue(writer, currencyOptional.get());
            }
        } catch (ServiceException e) {
            handleInternalServerError(resp, e);
        }
    }

    private boolean isInvalidParameters(final String code, final String name, final String sign,
                                        final HttpServletResponse resp) throws IOException {
        if (isNullOrEmpty(code)) {
            handleBadRequest(resp, "Missing parameter - code");
            return true;
        }

        if (isNullOrEmpty(name)) {
            handleBadRequest(resp, "Missing parameter - name");
            return true;
        }

        if (isNullOrEmpty(sign)) {
            handleBadRequest(resp, "Missing parameter - sign");
            return true;
        }

        return false;
    }

}

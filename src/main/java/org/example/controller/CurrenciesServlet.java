package org.example.controller;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.controller.responce.ErrorResponse;
import org.example.exception.ServiceException;
import org.example.model.Currency;
import org.example.service.CurrencyService;
import org.example.service.impl.CurrencyServiceImpl;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;

@WebServlet("/currencies")
public class CurrenciesServlet extends AbstractServlet {

    private final CurrencyService currencyService = new CurrencyServiceImpl();

    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {
        try {
            final List<Currency> currencies = currencyService.getAll();
            objectMapper.writeValue(resp.getWriter(), currencies);
        } catch (IOException | ServiceException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponse(e.getMessage()));
        }
    }

    @Override
    protected void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {
        final String code = req.getParameter("code");
        final String name = req.getParameter("name");
        final String sign = req.getParameter("sign");

        if (isNullOrEmpty(code)) {
            handleBadRequest(resp, "Missing parameter - code");
            return;
        }
        if (isNullOrEmpty(name)) {
            handleBadRequest(resp, "Missing parameter - name");
            return;
        }
        if (isNullOrEmpty(sign)) {
            handleBadRequest(resp, "Missing parameter - sign");
            return;
        }

        try (PrintWriter writer = resp.getWriter()) {
            if (currencyService.getByCode(code).isPresent()) {
                resp.setStatus(HttpServletResponse.SC_CONFLICT);
                objectMapper.writeValue(writer, new ErrorResponse("Currency with this code already exists"));
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

    private boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

}

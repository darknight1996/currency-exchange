package org.example.controller.currency;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.controller.AbstractServlet;
import org.example.controller.responce.ErrorResponse;
import org.example.exception.ServiceException;
import org.example.model.currency.Currency;
import org.example.service.currency.CurrencyService;
import org.example.service.currency.impl.CurrencyServiceImpl;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;

@WebServlet("/currency/*")
public class CurrencyServlet extends AbstractServlet {

    private final CurrencyService currencyService = new CurrencyServiceImpl();

    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {
        try (PrintWriter writer = resp.getWriter()) {
            final String code = req.getPathInfo().replaceFirst("/", "");
            if (code.trim().isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                objectMapper.writeValue(writer, new ErrorResponse("Code is empty"));
                return;
            }

            final Optional<Currency> currencyOptional = currencyService.getByCode(code);
            if (currencyOptional.isPresent()) {
                objectMapper.writeValue(writer, currencyOptional.get());
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                objectMapper.writeValue(writer, new ErrorResponse("Currency with code: " + code + " not found"));
            }
        } catch (ServiceException e) {
            handleInternalServerError(resp, e);
        }
    }

}

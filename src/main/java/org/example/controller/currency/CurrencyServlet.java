package org.example.controller.currency;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.controller.AbstractServlet;
import org.example.exception.ServiceException;
import org.example.model.currency.Currency;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;

@WebServlet("/currency/*")
public class CurrencyServlet extends AbstractServlet {

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        try (final PrintWriter writer = response.getWriter()) {
            final String code = getPathParam(request);
            if (code.trim().isEmpty()) {
                handleBadRequest(response, "Code is empty");
                return;
            }

            final Optional<Currency> currencyOptional = currencyService.getByCode(code);

            if (currencyOptional.isPresent()) {
                objectMapper.writeValue(writer, currencyOptional.get());
            } else {
                handleNotFound(response, "Currency with code: " + code + " not found");
            }
        } catch (ServiceException e) {
            handleInternalServerError(response, e.getMessage());
        }
    }

}

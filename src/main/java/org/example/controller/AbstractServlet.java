package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.controller.responce.ErrorResponse;
import org.example.service.currency.CurrencyService;
import org.example.service.currency.impl.CurrencyServiceImpl;

import java.io.IOException;
import java.io.PrintWriter;

public abstract class AbstractServlet extends HttpServlet {

    protected final CurrencyService currencyService = new CurrencyServiceImpl();
    protected final ObjectMapper objectMapper = new ObjectMapper();

    protected void handleInternalServerError(final HttpServletResponse response, final String message) throws IOException {
        handleError(response, message, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    protected void handleNotFound(final HttpServletResponse response, final String message) throws IOException {
        handleError(response, message, HttpServletResponse.SC_NOT_FOUND);
    }

    protected void handleBadRequest(final HttpServletResponse response, final String message) throws IOException {
        handleError(response, message, HttpServletResponse.SC_BAD_REQUEST);
    }

    protected void handleConflict(final HttpServletResponse response, final String message) throws IOException {
        handleError(response, message, HttpServletResponse.SC_CONFLICT);
    }

    private void handleError(final HttpServletResponse response, final String message,
                             final int status) throws IOException {
        response.setStatus(status);
        try (final PrintWriter writer = response.getWriter()) {
            objectMapper.writeValue(writer, new ErrorResponse(message));
        }
    }

    protected String getPathParam(final HttpServletRequest request) {
        return request.getPathInfo().replaceFirst("/", "");
    }

    protected boolean isNullOrEmpty(final String string) {
        return string == null || string.trim().isEmpty();
    }

}

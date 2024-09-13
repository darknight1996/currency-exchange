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

    protected void handleInternalServerError(final HttpServletResponse resp, final Exception e) throws IOException {
        resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        try (final PrintWriter writer = resp.getWriter()) {
            objectMapper.writeValue(writer, new ErrorResponse(e.getMessage()));
        }
    }

    protected void handleNotFound(final HttpServletResponse resp, final String message) throws IOException {
        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        try (final PrintWriter writer = resp.getWriter()) {
            objectMapper.writeValue(writer, new ErrorResponse(message));
        }
    }

    protected void handleBadRequest(final HttpServletResponse resp, final String message) throws IOException {
        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        try (final PrintWriter writer = resp.getWriter()) {
            objectMapper.writeValue(writer, new ErrorResponse(message));
        }
    }

    protected void handleConflict(final HttpServletResponse resp, final String message) throws IOException {
        resp.setStatus(HttpServletResponse.SC_CONFLICT);
        try (final PrintWriter writer = resp.getWriter()) {
            objectMapper.writeValue(writer, new ErrorResponse(message));
        }
    }

    protected String getPathParam(final HttpServletRequest req) {
        return req.getPathInfo().replaceFirst("/", "");
    }

    protected boolean isNullOrEmpty(final String str) {
        return str == null || str.trim().isEmpty();
    }

}

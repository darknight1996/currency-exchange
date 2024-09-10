package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.exception.ServiceException;
import org.example.model.Currency;
import org.example.service.CurrencyService;
import org.example.service.impl.CurrencyServiceImpl;

import java.io.IOException;
import java.util.List;

@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {

    private final CurrencyService currencyService = new CurrencyServiceImpl();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {
        try {
            final List<Currency> currencies = currencyService.getAll();
            objectMapper.writeValue(resp.getWriter(), currencies);
        } catch (IOException | ServiceException e) {
            objectMapper.writeValue(resp.getWriter(), e);
        }
    }
}

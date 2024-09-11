package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletResponse;
import org.example.controller.responce.ErrorResponse;

import java.io.IOException;
import java.io.PrintWriter;

public abstract class AbstractServlet extends HttpServlet {

    protected final ObjectMapper objectMapper = new ObjectMapper();

    protected void handleInternalServerError(HttpServletResponse resp, Exception e) throws IOException {
        resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        try (PrintWriter writer = resp.getWriter()) {
            objectMapper.writeValue(writer, new ErrorResponse(e.getMessage()));
        }
    }

    protected void handleBadRequest(HttpServletResponse resp, String message) throws IOException {
        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        try (PrintWriter writer = resp.getWriter()) {
            objectMapper.writeValue(writer, new ErrorResponse(message));
        }
    }

}

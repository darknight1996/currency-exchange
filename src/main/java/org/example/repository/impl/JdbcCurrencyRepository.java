package org.example.repository.impl;

import org.example.exception.RepositoryException;
import org.example.model.Currency;
import org.example.repository.CurrencyRepository;
import org.example.repository.util.JdbcConnectionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.sql.*;

public class JdbcCurrencyRepository implements CurrencyRepository {

    private final JdbcConnectionManager jdbcConnectionManager = new JdbcConnectionManager();

    @Override
    public List<Currency> getAll() throws RepositoryException {
        List<Currency> currencies = new ArrayList<>();
        final String query = "SELECT * FROM Currency";

        try (Connection connection = jdbcConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                final int id = resultSet.getInt("ID");
                final String code = resultSet.getString("Code");
                final String fullName = resultSet.getString("FullName");
                final String sign = resultSet.getString("Sign");

                final Currency currency = new Currency(id, code, fullName, sign);
                currencies.add(currency);
            }

        } catch (SQLException e) {
            throw new RepositoryException("Error fetching all currencies");
        }

        return currencies;
    }

    @Override
    public Optional<Currency> getById(final int id) throws RepositoryException {
        final String query = "SELECT * FROM Currency WHERE id = ?";

        try (Connection connection = jdbcConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    final String code = resultSet.getString("Code");
                    final String fullName = resultSet.getString("FullName");
                    final String sign = resultSet.getString("Sign");

                    final Currency currency = new Currency(id, code, fullName, sign);
                    return Optional.of(currency);
                }
            }

        } catch (SQLException e) {
            throw new RepositoryException("Error fetching currency by ID: " + id);
        }

        return Optional.empty();
    }

    @Override
    public int add(final Currency currency) {
        return 0;
    }

    @Override
    public void delete(final Long id) {

    }

    @Override
    public void update(final Currency currency) {

    }
}

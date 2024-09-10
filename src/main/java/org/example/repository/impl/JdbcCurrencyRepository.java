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

        try (final Connection connection = jdbcConnectionManager.getConnection()) {
            final String query = "SELECT * FROM Currency";
            final PreparedStatement preparedStatement = connection.prepareStatement(query);
            final ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                final int id = resultSet.getInt("ID");
                final String code = resultSet.getString("Code");
                final String fullName = resultSet.getString("FullName");
                final String sign = resultSet.getString("Sign");

                final Currency currency = new Currency(id, code, fullName, sign);

                currencies.add(currency);
            }

            resultSet.close();
            preparedStatement.close();
        } catch (SQLException e) {
            throw new RepositoryException(e.getMessage());
        }

        return currencies;
    }

    @Override
    public Optional<Currency> getById(final int id) {
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

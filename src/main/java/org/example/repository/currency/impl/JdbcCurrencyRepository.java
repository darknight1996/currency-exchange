package org.example.repository.currency.impl;

import org.example.exception.RepositoryException;
import org.example.model.currency.Currency;
import org.example.repository.currency.CurrencyRepository;
import org.example.repository.util.JdbcConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcCurrencyRepository implements CurrencyRepository {

    private final JdbcConnectionManager jdbcConnectionManager = new JdbcConnectionManager();

    @Override
    public List<Currency> getAll() throws RepositoryException {
        List<Currency> currencies = new ArrayList<>();
        final String query = "SELECT * FROM Currency";

        try (final Connection connection = jdbcConnectionManager.getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement(query);
             final ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                final Currency currency = getCurrency(resultSet);
                currencies.add(currency);
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error fetching all currencies: " + e.getMessage());
        }

        return currencies;
    }

    @Override
    public Optional<Currency> getByCode(final String code) throws RepositoryException {
        final String query = "SELECT * FROM Currency WHERE Code = ?";

        try (final Connection connection = jdbcConnectionManager.getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, code);

            try (final ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    final Currency currency = getCurrency(resultSet);
                    return Optional.of(currency);
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error fetching currency by Code: " + code + ". " + e.getMessage());
        }

        return Optional.empty();
    }

    @Override
    public Optional<Currency> add(final Currency currency) throws RepositoryException {
        final String query = "INSERT INTO Currency(Code, FullName, Sign) VALUES(?, ?, ?)";

        try (final Connection connection = jdbcConnectionManager.getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, currency.getCode());
            preparedStatement.setString(2, currency.getFullName());
            preparedStatement.setString(3, currency.getSign());

            preparedStatement.executeUpdate();

            try (final ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    final int id = resultSet.getInt(1);

                    currency.setId(id);
                    return Optional.of(currency);
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error saving currency: " + e.getMessage());
        }

        return Optional.empty();
    }

    private Currency getCurrency(final ResultSet resultSet) throws SQLException {
        final int id = resultSet.getInt("ID");
        final String code = resultSet.getString("Code");
        final String fullName = resultSet.getString("FullName");
        final String sign = resultSet.getString("Sign");

        return new Currency(id, code, fullName, sign);
    }

}

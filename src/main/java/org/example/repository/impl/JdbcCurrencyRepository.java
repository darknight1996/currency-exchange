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

    private static final String ID_COLUMN = "ID";
    private static final String CODE_COLUMN = "Code";
    private static final String FULL_NAME_COLUMN = "FullName";
    private static final String SIGN_COLUMN = "Sign";

    private final JdbcConnectionManager jdbcConnectionManager = new JdbcConnectionManager();

    @Override
    public List<Currency> getAll() throws RepositoryException {
        List<Currency> currencies = new ArrayList<>();
        final String query = "SELECT * FROM Currency";

        try (Connection connection = jdbcConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                final int id = resultSet.getInt(ID_COLUMN);
                final String code = resultSet.getString(CODE_COLUMN);
                final String fullName = resultSet.getString(FULL_NAME_COLUMN);
                final String sign = resultSet.getString(SIGN_COLUMN);

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
                    final String code = resultSet.getString(CODE_COLUMN);
                    final String fullName = resultSet.getString(FULL_NAME_COLUMN);
                    final String sign = resultSet.getString(SIGN_COLUMN);

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
    public Optional<Currency> getByCode(final String code) throws RepositoryException {
        final String query = "SELECT * FROM Currency WHERE Code = ?";

        try (Connection connection = jdbcConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, code);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    final int id = resultSet.getInt(ID_COLUMN);
                    final String fullName = resultSet.getString(FULL_NAME_COLUMN);
                    final String sign = resultSet.getString(SIGN_COLUMN);

                    final Currency currency = new Currency(id, code, fullName, sign);
                    return Optional.of(currency);
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error fetching currency by Code: " + code);
        }

        return Optional.empty();
    }

    @Override
    public Optional<Currency> add(final Currency currency) throws RepositoryException {
        Optional<Currency> currencyOptional = Optional.empty();
        final String query = "INSERT INTO Currency(Code, FullName, Sign) VALUES(?, ?, ?)";

        try (Connection connection = jdbcConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, currency.getCode());
            preparedStatement.setString(2, currency.getFullName());
            preparedStatement.setString(3, currency.getSign());

            preparedStatement.executeUpdate();

            try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    final int id = resultSet.getInt(1);

                    currency.setId(id);
                    currencyOptional = Optional.of(currency);
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error saving currency");
        }

        return currencyOptional;
    }

    @Override
    public void delete(final Long id) {

    }

    @Override
    public void update(final Currency currency) {

    }

}

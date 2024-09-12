package org.example.repository.exchange.impl;

import org.example.exception.RepositoryException;
import org.example.model.currency.Currency;
import org.example.model.exchange.ExchangeRate;
import org.example.repository.exchange.ExchangeRateRepository;
import org.example.repository.util.JdbcConnectionManager;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcExchangeRateRepository implements ExchangeRateRepository {

    private final JdbcConnectionManager jdbcConnectionManager = new JdbcConnectionManager();

    @Override
    public List<ExchangeRate> getAll() throws RepositoryException {
        List<ExchangeRate> exchangeRates = new ArrayList<>();

        // @formatter:off
        final String query = """
                SELECT
                    ExchangeRate.ID as ID,
                    BaseCurrency.ID as BaseCurrency_ID,
                    BaseCurrency.Code as BaseCurrency_Code,
                    BaseCurrency.FullName as BaseCurrency_FullName,
                    BaseCurrency.Sign as BaseCurrency_Sign,
                    TargetCurrency.ID as TargetCurrency_ID,
                    TargetCurrency.Code as TargetCurrency_Code,
                    TargetCurrency.FullName as TargetCurrency_FullName,
                    TargetCurrency.Sign as TargetCurrency_Sign,
                    ExchangeRate.Rate as Rate
                FROM ExchangeRate
                LEFT JOIN Currency AS BaseCurrency ON ExchangeRate.BaseCurrencyId = BaseCurrency.ID
                LEFT JOIN Currency AS TargetCurrency ON ExchangeRate.TargetCurrencyId = TargetCurrency.ID
            """;
        // @formatter:on

        try (final Connection connection = jdbcConnectionManager.getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement(query);
             final ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                final ExchangeRate exchangeRate = getExchangeRate(resultSet);
                exchangeRates.add(exchangeRate);
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error fetching all exchange rates: " + e.getMessage());
        }

        return exchangeRates;
    }

    @Override
    public Optional<ExchangeRate> getByCodes(final String baseCurrencyCode, final String targetCurrencyCode) throws RepositoryException {
        Optional<ExchangeRate> exchangeRateOptional = Optional.empty();

        // @formatter:off
        final String query = """
                SELECT
                    ExchangeRate.ID as ID,
                    BaseCurrency.ID as BaseCurrency_ID,
                    BaseCurrency.Code as BaseCurrency_Code,
                    BaseCurrency.FullName as BaseCurrency_FullName,
                    BaseCurrency.Sign as BaseCurrency_Sign,
                    TargetCurrency.ID as TargetCurrency_ID,
                    TargetCurrency.Code as TargetCurrency_Code,
                    TargetCurrency.FullName as TargetCurrency_FullName,
                    TargetCurrency.Sign as TargetCurrency_Sign,
                    ExchangeRate.Rate as Rate
                FROM ExchangeRate
                LEFT JOIN Currency AS BaseCurrency ON ExchangeRate.BaseCurrencyId = BaseCurrency.ID
                LEFT JOIN Currency AS TargetCurrency ON ExchangeRate.TargetCurrencyId = TargetCurrency.ID
                WHERE BaseCurrency_Code = ? AND TargetCurrency_Code = ?
            """;
        // @formatter:on

        try (final Connection connection = jdbcConnectionManager.getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, baseCurrencyCode);
            preparedStatement.setString(2, targetCurrencyCode);

            try (final ResultSet resultSet = preparedStatement.executeQuery()) {

                if (resultSet.next()) {
                    final ExchangeRate exchangeRate = getExchangeRate(resultSet);
                    exchangeRateOptional = Optional.of(exchangeRate);
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error fetching exchange rate: " + e.getMessage());
        }

        return exchangeRateOptional;
    }

    @Override
    public Optional<ExchangeRate> add(final ExchangeRate exchangeRate) throws RepositoryException {
        Optional<ExchangeRate> exchangeRateOptional = Optional.empty();
        final String query = "INSERT INTO ExchangeRate(BaseCurrencyId, TargetCurrencyId, Rate) VALUES(?, ?, ?)";

        try (final Connection connection = jdbcConnectionManager.getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setInt(1, exchangeRate.getBaseCurrency().getId());
            preparedStatement.setInt(2, exchangeRate.getTargetCurrency().getId());
            preparedStatement.setBigDecimal(3, exchangeRate.getRate());

            preparedStatement.executeUpdate();

            try (final ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    final int id = resultSet.getInt(1);

                    exchangeRate.setId(id);
                    exchangeRateOptional = Optional.of(exchangeRate);
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error saving exchange rate: " + e.getMessage());
        }

        return exchangeRateOptional;
    }

    private ExchangeRate getExchangeRate(final ResultSet resultSet) throws SQLException {
        final int id = resultSet.getInt("ID");

        final Currency baseCurrency = new Currency(
                resultSet.getInt("BaseCurrency_ID"),
                resultSet.getString("BaseCurrency_Code"),
                resultSet.getString("BaseCurrency_FullName"),
                resultSet.getString("BaseCurrency_Sign")
        );

        final Currency targetCurrency = new Currency(
                resultSet.getInt("TargetCurrency_ID"),
                resultSet.getString("TargetCurrency_Code"),
                resultSet.getString("TargetCurrency_FullName"),
                resultSet.getString("TargetCurrency_Sign")
        );

        final BigDecimal rate = resultSet.getBigDecimal("Rate");

        return new ExchangeRate(id, baseCurrency, targetCurrency, rate);
    }

}

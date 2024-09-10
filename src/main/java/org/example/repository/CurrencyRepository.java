package org.example.repository;

import org.example.exception.RepositoryException;
import org.example.model.Currency;

import java.util.List;
import java.util.Optional;

public interface CurrencyRepository {

    List<Currency> getAll() throws RepositoryException;
    Optional<Currency> getById(final int id) throws RepositoryException;
    int add(final Currency currency) throws RepositoryException;
    void delete(final Long id) throws RepositoryException;
    void update(final Currency currency) throws RepositoryException;

}

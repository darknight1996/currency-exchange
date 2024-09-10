package org.example;

import org.example.exception.RepositoryException;
import org.example.repository.CurrencyRepository;
import org.example.repository.impl.JdbcCurrencyRepository;

public class Main {

    public static void main(String[] args) throws RepositoryException {
        final CurrencyRepository currencyRepository = new JdbcCurrencyRepository();
        System.out.println(currencyRepository.getAll().size());
    }
}

package org.example;

import org.example.exception.RepositoryException;
import org.example.model.currency.Currency;
import org.example.repository.currency.CurrencyRepository;
import org.example.repository.currency.impl.JdbcCurrencyRepository;

public class Main {

    public static void main(String[] args) throws RepositoryException {
        final CurrencyRepository currencyRepository = new JdbcCurrencyRepository();
        System.out.println(currencyRepository.getAll().size());
        Currency currency = new Currency("AUU", "Australia Dollar", "$");
        currencyRepository.add(currency);
    }
}

CREATE TABLE IF NOT EXISTS Currency (
    ID INTEGER PRIMARY KEY AUTOINCREMENT,
    Code VARCHAR(255) NOT NULL UNIQUE,
    FullName VARCHAR(255) NOT NULL,
    Sign VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS ExchangeRate (
    ID INTEGER PRIMARY KEY AUTOINCREMENT,
    BaseCurrencyId INTEGER NOT NULL,
    TargetCurrencyId INTEGER NOT NULL,
    Rate DECIMAL(6) NOT NULL,
    FOREIGN KEY (BaseCurrencyId) REFERENCES Currency(ID),
    FOREIGN KEY (TargetCurrencyId) REFERENCES Currency(ID)
);

INSERT INTO Currency(Code, FullName, Sign)
VALUES ('USD', 'United States Dollar', '$');

INSERT INTO Currency(Code, FullName, Sign)
VALUES ('EUR', 'Euro', '€');

INSERT INTO Currency(Code, FullName, Sign)
VALUES ('GBP', 'Pound Sterling', '£');

INSERT INTO Currency(Code, FullName, Sign)
VALUES ('PLN', 'Zloty', 'zł');

-- USD to EUR
INSERT INTO ExchangeRate(BaseCurrencyId, TargetCurrencyId, Rate)
VALUES (1, 2, 0.91);

-- USD to GBP
INSERT INTO ExchangeRate(BaseCurrencyId, TargetCurrencyId, Rate)
VALUES (1, 3, 0.76);

-- USD to PLN
INSERT INTO ExchangeRate(BaseCurrencyId, TargetCurrencyId, Rate)
VALUES (1, 4, 3.88);

-- EUR to USD
INSERT INTO ExchangeRate(BaseCurrencyId, TargetCurrencyId, Rate)
VALUES (2, 1, 1.1);

-- EUR to GBP
INSERT INTO ExchangeRate(BaseCurrencyId, TargetCurrencyId, Rate)
VALUES (2, 3, 0.84);

-- EUR to PLN
INSERT INTO ExchangeRate(BaseCurrencyId, TargetCurrencyId, Rate)
VALUES (2, 4, 4.28);

-- GBP to USD
INSERT INTO ExchangeRate(BaseCurrencyId, TargetCurrencyId, Rate)
VALUES (3, 1, 1.31);

-- GBP to EUR
INSERT INTO ExchangeRate(BaseCurrencyId, TargetCurrencyId, Rate)
VALUES (3, 2, 1.19);

-- GBP to PLN
INSERT INTO ExchangeRate(BaseCurrencyId, TargetCurrencyId, Rate)
VALUES (3, 4, 5.08);

-- PLN to USD
INSERT INTO ExchangeRate(BaseCurrencyId, TargetCurrencyId, Rate)
VALUES (4, 1, 0.26);

-- PLN to EUR
INSERT INTO ExchangeRate(BaseCurrencyId, TargetCurrencyId, Rate)
VALUES (4, 2, 0.23);

-- PLN to GBP
INSERT INTO ExchangeRate(BaseCurrencyId, TargetCurrencyId, Rate)
VALUES (4, 3, 0.20);




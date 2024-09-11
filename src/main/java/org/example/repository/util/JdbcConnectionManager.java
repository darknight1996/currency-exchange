package org.example.repository.util;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JdbcConnectionManager {

    private static final String DRIVER = "org.sqlite.JDBC";
    private static final String DB_FILE = "database/currency_exchange.db";

    public Connection getConnection() throws SQLException {
        String path;

        try {
            Class.forName(DRIVER);
            final URL resource = getClass().getClassLoader().getResource(DB_FILE);
            if (resource == null) {
                throw new RuntimeException("Database file not found: " + DB_FILE);
            }
            path = new File(resource.toURI()).getAbsolutePath();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to load JDBC driver: " + DRIVER, e);
        } catch (URISyntaxException e) {
            throw new RuntimeException("Invalid URI for the database file: " + DB_FILE, e);
        }

        return DriverManager.getConnection(String.format("jdbc:sqlite:%s", path));
    }

}

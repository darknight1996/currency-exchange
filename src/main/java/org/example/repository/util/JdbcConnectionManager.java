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

        URL resource;
        String path = null;
        try {
            Class.forName(DRIVER);
            resource = getClass().getClassLoader().getResource(DB_FILE);
            if (resource != null) {
                path = new File(resource.toURI()).getAbsolutePath();
            }
        } catch (ClassNotFoundException | URISyntaxException e) {
            throw new RuntimeException(e);
        }

        return DriverManager.getConnection(String.format("jdbc:sqlite:%s", path));
    }

}

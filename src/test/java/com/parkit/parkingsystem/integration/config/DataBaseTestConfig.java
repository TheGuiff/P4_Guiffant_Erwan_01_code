package com.parkit.parkingsystem.integration.config;

import com.parkit.parkingsystem.config.DataBaseConfig;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public final class DataBaseTestConfig extends DataBaseConfig {

    @Override
    public Connection getConnection() throws ClassNotFoundException, SQLException, IOException {
        logger.info("Create DB connection");
        Class.forName("com.mysql.cj.jdbc.Driver");
        InputStream inputStream = null;
        String username;
        String password;
        String urlTest;
        try {
            Properties loginProperties = new Properties();
            inputStream = getClass().getResourceAsStream(loginPropertiesFile);
            loginProperties.load(inputStream);
            username = loginProperties.getProperty("username");
            password = loginProperties.getProperty("password");
            urlTest = loginProperties.getProperty("urltest");
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return DriverManager.getConnection(urlTest, username, password);
    }
}

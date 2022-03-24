package com.parkit.parkingsystem.integration.config;

import com.parkit.parkingsystem.config.DataBaseConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.List;
import java.util.Properties;

public class DataBaseTestConfig extends DataBaseConfig {

    private static final Logger logger = LogManager.getLogger("DataBaseTestConfig");
    private static final String loginPropertiesFile = "/login.properties";


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

    public void closeConnection(Connection con){
        if(con!=null){
            try {
                con.close();
                logger.info("Closing DB connection");
            } catch (SQLException e) {
                logger.error("Error while closing connection",e);
            }
        }
    }

    public void closePreparedStatement(PreparedStatement ps) {
        if(ps!=null){
            try {
                ps.close();
                logger.info("Closing Prepared Statement");
            } catch (SQLException e) {
                logger.error("Error while closing prepared statement",e);
            }
        }
    }

    public void closeResultSet(ResultSet rs) {
        if(rs!=null){
            try {
                rs.close();
                logger.info("Closing Result Set");
            } catch (SQLException e) {
                logger.error("Error while closing result set",e);
            }
        }
    }
}

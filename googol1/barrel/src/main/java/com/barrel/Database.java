package com.barrel;

import com.utils.Utils;

import javax.xml.crypto.Data;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {

    private final String DB_URL;
    private final String USER;
    private final String PASSWORD;

    Database(){
        this.DB_URL = Utils.readProperties(this, "DB_URL", "jdbc:postgresql://localhost:5433/googol_db", "database.properties");
        this.USER  = Utils.readProperties(this, "USER", "user", "database.properties");
        this.PASSWORD = Utils.readProperties(this, "PASSWORD", "admin", "database.properties");
    }

    public Connection getConnection() {
        try {
            return DriverManager.getConnection(this.DB_URL, this.USER, this.PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}

package com.barrel;

import com.utils.Utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Database {

    private final String DB_URL;
    private final String USER;
    private final String PASSWORD;
    private final String DB_ID;
    private final Connection connection;

    Database(String db_id) throws SQLException {
        this.DB_URL = Utils.readProperties(this, "DB_URL", "jdbc:postgresql://localhost:5433/", "database.properties");
        this.USER  = Utils.readProperties(this, "USER", "user", "database.properties");
        this.PASSWORD = Utils.readProperties(this, "PASSWORD", "admin", "database.properties");
        // TODO filter invalid db_id change to function
        if (!db_id.matches("^[a-zA-Z0-9_]+$"))
            throw new SQLException("Invalid database name");
        this.DB_ID = db_id;
        this.connection = dbConnect();
    }

    Connection dbConnect() throws SQLException {
        try {
            Connection conn = DriverManager.getConnection(this.DB_URL + this.DB_ID, this.USER, this.PASSWORD);
            System.out.println("Connected to DB: " + this.DB_ID);
            return conn;
        } catch (SQLException e){
            Connection conn = createDB();
            initDB(conn);
            return conn;
        }
    }

    void initDB(Connection conn) throws SQLException {
        System.out.println("Initializing database");
        conn.createStatement().execute("CREATE SCHEMA " + this.DB_ID);
    }

    Connection createDB() throws SQLException {
        System.out.println("Attempting to create DB");

        Connection conn = DriverManager.getConnection(this.DB_URL, this.USER, this.PASSWORD);
        // Check if the database already exists
        ResultSet resultSet = conn.getMetaData().getCatalogs();
        boolean databaseExists = false;
        while (resultSet.next()) {
            String databaseName = resultSet.getString(1);
            if (databaseName.equals(this.DB_ID)) {
                databaseExists = true;
                break;
            }
        }
        resultSet.close();
        if (!databaseExists) {
            conn.createStatement().execute("CREATE DATABASE " + this.DB_ID);
            System.out.println("Database " + this.DB_ID + " created successfully.");
        } else {
            System.out.println("Database " + this.DB_ID + " already exists.");
        }
        return DriverManager.getConnection(this.DB_URL  + this.DB_ID, this.USER, this.PASSWORD);
    }
}

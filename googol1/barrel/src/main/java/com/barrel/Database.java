package com.barrel;

import com.utils.Utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

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

    private void initDB(Connection conn) throws SQLException {
        System.out.println("Initializing database");
        String statement = """
                CREATE TABLE words (
                    id   BIGSERIAL,
                    word VARCHAR(128) NOT NULL,
                    PRIMARY KEY(id)
                );

                CREATE TABLE links (
                    id   BIGSERIAL,
                    link     VARCHAR(512) NOT NULL,
                    searches BIGINT NOT NULL DEFAULT 0,
                    indexed  BOOLEAN NOT NULL DEFAULT FALSE,
                    PRIMARY KEY(id)
                );

                CREATE TABLE words_links (
                    count    INTEGER,
                    links_id BIGINT,
                    words_id BIGINT,
                    PRIMARY KEY(links_id,words_id)
                );

                CREATE TABLE links_links (
                    links_id     BIGINT,
                    links_id1 BIGINT,
                    PRIMARY KEY(links_id,links_id1)
                );

                ALTER TABLE words ADD UNIQUE (word);
                ALTER TABLE links ADD UNIQUE (link);
                ALTER TABLE words_links ADD CONSTRAINT words_links_fk1 FOREIGN KEY (links_id) REFERENCES links(id);
                ALTER TABLE words_links ADD CONSTRAINT words_links_fk2 FOREIGN KEY (words_id) REFERENCES words(id);
                ALTER TABLE links_links ADD CONSTRAINT links_links_fk1 FOREIGN KEY (links_id) REFERENCES links(id);
                ALTER TABLE links_links ADD CONSTRAINT links_links_fk2 FOREIGN KEY (links_id1) REFERENCES links(id);
                """;
        conn.createStatement().execute(statement);
    }

    private Connection createDB() throws SQLException {
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

    long indexUrl(String url, String[] words) {
        System.out.println("Indexing URL: " + url);
        long linkId = getLinkId(url);
        if (linkId == -1) {
            System.out.println("Failed to index URL: " + url);
            return -1;
        }
//        for (String word : words) {
//            int wordId = getWordId(word);
//            if (wordId == -1) {
//                System.out.println("Failed to index word: " + word);
//                return -1;
//            }
//            if (!indexWordLink(wordId, linkId)) {
//                System.out.println("Failed to index word link: " + word + " " + url);
//                return -1;
//            }
//        }
        return linkId;
    }

    long getLinkId(String url) {
        System.out.println("Getting link ID for URL: " + url);
        String stmt = "SELECT id FROM links WHERE link = ? AND indexed = FALSE;";
        try {
            PreparedStatement statement = connection.prepareStatement(stmt);
            statement.setString(1, url);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                long linkId = resultSet.getLong("id");
                System.out.println("Link ID for URL " + url + " is " + linkId);
                return linkId;
            } else {
                System.out.println("Link ID not found for URL: " + url + " Inserting URL");
                return insertUrl(url);
            }
        } catch (SQLException e) {
            System.out.println("Failed to get link ID for URL: " + url);
        }
        return -1;
    }
    long insertUrl(String url) {
        System.out.println("Inserting URL: " + url);
        String stmt = "INSERT INTO links (link) VALUES (?) RETURNING id;";
        long id;
        try {
            PreparedStatement statement = connection.prepareStatement(stmt);
            statement.setString(1, url);
            ResultSet res = statement.executeQuery();
            res.next();
            id = res.getLong("id");
            System.out.println("Inserted URL: " + url);
        } catch (SQLException e) {
            System.out.println("Failed to insert URL: " + e.getMessage());
            return -1;
        }
        return id;
    }

    boolean indexedUrl(String url){
        System.out.println("Verifying if URL is indexed: " + url);
        String stmt = "SELECT indexed FROM links WHERE link = ? AND indexed = TRUE;";
        try {
            PreparedStatement statement = connection.prepareStatement(stmt);
            statement.setString(1, url);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                System.out.println("URL is indexed: " + url);
                return true;
            } else {
                System.out.println("URL is not indexed: " + url);
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Failed to verify if URL is indexed: " + url);
        }
        return false;
    }
}

package com.barrel;

import com.common.Site;
import com.utils.Utils;
import org.json.JSONArray;

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
        connection.setAutoCommit(false);
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
                    title    VARCHAR(512),
                    searches BIGINT NOT NULL DEFAULT 0,
                    indexed  BOOLEAN NOT NULL DEFAULT FALSE,
                    PRIMARY KEY(id)
                );

                CREATE TABLE words_links (
                    count    INTEGER NOT NULL DEFAULT 1,
                    links_id BIGINT,
                    words_id BIGINT,
                    PRIMARY KEY(links_id,words_id)
                );

                CREATE TABLE links_links (
                    links_id     BIGINT,
                    links_id1    BIGINT,
                    count INTEGER DEFAULT 1,
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

    long indexUrl(String url, JSONArray words, String title) throws SQLException {
        System.out.println("Indexing URL: " + url);
        if(indexedUrl(url)){
            System.out.println("URL already indexed: " + url);
            return -1;
        }
        long linkId = getUrlId(url);
        if (linkId == -1) {
            System.out.println("Failed to index URL: " + url);
            connection.rollback();
            return -1;
        }

        for (var word : words) {
            long wordId = getWordId(word.toString());
            if (wordId == -1) {
                System.out.println("Failed to index word: " + word);
                connection.rollback();
                return -1;
            }
            if (!indexWordLink(wordId, linkId)) {
                System.out.println("Failed to index word link: " + word + " " + url);
                connection.rollback();
                return -1;
            }
        }
        String stmt = "UPDATE links SET indexed = TRUE, title = ? WHERE id = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(stmt);
            statement.setString(1, title);
            statement.setLong(2, linkId);
            statement.execute();
        } catch (SQLException e) {
            System.out.println("Failed to update link: " + url);
            connection.rollback();
            return -1;
        }

        connection.commit();
        return linkId;
    }
    boolean indexWordLink(long wordId, long linkId) {
        String stmt = """
                INSERT INTO words_links (links_id, words_id)
                VALUES (?, ?)
                ON CONFLICT (links_id, words_id) DO UPDATE
                SET count = words_links.count + 1
                """;
        try {
            PreparedStatement statement = connection.prepareStatement(stmt);
            statement.setLong(1, linkId);
            statement.setLong(2, wordId);
            statement.execute();
            System.out.println("Indexed word link: " + wordId + " " + linkId);
            return true;
        } catch (SQLException e) {
            System.out.println("Failed to index word link: " + wordId + " " + linkId);
        }
        return false;
    }
    boolean addLink(String url, String url1) throws SQLException {
        System.out.println("Adding link: " + url + " " + url1);
        long linkId = getUrlId(url);
        long linkId1 = getUrlId(url1);
        if (linkId == -1 || linkId1 == -1) {
            System.out.println("Failed to add link: " + url + " " + url1);
            return false;
        }
        String stmt = """
                INSERT INTO links_links (links_id, links_id1)
                VALUES (?, ?)
                ON CONFLICT (links_id, links_id1) DO UPDATE
                SET count = links_links.count + 1
                """;
        try {
            PreparedStatement statement = connection.prepareStatement(stmt);
            statement.setLong(1, linkId);
            statement.setLong(2, linkId1);
            statement.execute();
            System.out.println("Added link: " + url + " " + url1);
            connection.commit();
            return true;
        } catch (SQLException e) {
            System.out.println("Failed to add link: " + url + " " + url1);
            connection.rollback();
        }
        return true;
    }
    long getUrlId(String url) {
        String stmt = """
        INSERT INTO links (link)
        VALUES (?)
        ON CONFLICT (link) DO UPDATE
        SET link = excluded.link
        RETURNING id;
        """;
        long id;
        try {
            PreparedStatement statement = connection.prepareStatement(stmt);
            statement.setString(1, url);
            ResultSet res = statement.executeQuery();
            res.next();
            id = res.getLong("id");
        } catch (SQLException e) {
            System.out.println("Failed to insert URL: " + e.getMessage());
            return -1;
        }
        return id;
    }
    boolean indexedUrl(String url){
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
    long getWordId(String word) {
        String stmt = """
        INSERT INTO words (word)
        VALUES (?)
        ON CONFLICT (word) DO UPDATE
        SET word = excluded.word
        RETURNING id;
        """;
        long id;
        try {
            PreparedStatement statement = connection.prepareStatement(stmt);
            statement.setString(1, word);
            ResultSet res = statement.executeQuery();
            res.next();
            id = res.getLong("id");
            System.out.println("Inserted word: " + word);
        } catch (SQLException e) {
            System.out.println("Failed to insert word: " + e.getMessage());
            return -1;
        }
        return id;
    }


    Site search(String[] words) {
        ArrayList<Site> sites = new ArrayList<>();
        String stmt = """
                SELECT l.link, l.title, count(*)
                FROM words_links as wl, links as l, words as w
                WHERE wl.words_id  = w.id AND wl.links_id  = l.id AND w.word IN (?)
                GROUP BY l.link, l.title;
                """;
        try {
            PreparedStatement statement = connection.prepareStatement(stmt);
            statement.setArray(1, words);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                sites.add(new Site(resultSet.getString("link"), resultSet.getString("title")));
            }
        } catch (SQLException e) {
            System.out.println("Failed to search for words: " + e.getMessage());
            return null;

        }
        System.out.println("Found " + sites+ " sites");
        return sites.get(0);
    }
}

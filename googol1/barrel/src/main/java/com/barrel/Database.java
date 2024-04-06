package com.barrel;

import com.common.Site;
import com.utils.Utils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Set;

/**
 * Database class for handling interactions with the database, including initialization, indexing and searching operations.
 */
public class Database {

    private final String DB_URL;
    private final String USER;
    private final String PASSWORD;
    private final String DB_ID;
    private final Connection connection;
    long startId;

    /**
     * Constructs a Database object with the given database ID.
     * Initializes the database connection and starts the ID counter.
     *
     * @param db_id The identifier for the specific database instance.
     * @throws SQLException If an SQL error occurs while connecting or creating to the database.
     */
    Database(String db_id) throws SQLException {
        this.DB_URL = Utils.readProperties(this, "DB_URL", "jdbc:postgresql://localhost:5433/", "database.properties");
        this.USER  = Utils.readProperties(this, "USER", "user", "database.properties");
        this.PASSWORD = Utils.readProperties(this, "PASSWORD", "admin", "database.properties");
        startId = 0;
        // TODO filter invalid db_id change to function
        if (!db_id.matches("^[a-zA-Z0-9_]+$"))
            throw new SQLException("Invalid database name");
        this.DB_ID = db_id;
        this.connection = dbConnect();
        connection.setAutoCommit(false);
    }

    /**
     * Connects to the database or creates it if it does not exist.
     *
     * @return The connection to the database.
     * @throws SQLException If an SQL exception occurs during database connection or creation.
     */
    Connection dbConnect() throws SQLException {
        try {
            Connection conn = DriverManager.getConnection(this.DB_URL + this.DB_ID, this.USER, this.PASSWORD);
            this.startId = getMaxId(conn);
            System.out.println("Connected to DB: " + this.DB_ID + " started with id " + startId);
            return conn;
        } catch (SQLException e){
            Connection conn = createDB();
            initDB(conn);
            return conn;
        }
    }

    /**
     * Retrieves the maximum ID from the messages table in the database.
     *
     * @param conn The connection to the database.
     * @return The maximum ID from the messages table.
     */
    private long getMaxId(Connection conn){
        String stmt = "SELECT MAX(id) FROM messages";
        try {
            PreparedStatement statement = conn.prepareStatement(stmt);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getLong(1);
            }
        } catch (SQLException e) {
            System.out.println("Failed to get max id: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Initializes the database by creating necessary tables and constraints.
     *
     * @param conn The connection to the database.
     * @throws SQLException If an SQL exception occurs during the initialization process.
     */
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
                    link     VARCHAR(2048) NOT NULL,
                    title    VARCHAR(1024),
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
                
                CREATE TABLE messages (
                   id BIGINT,
                   message JSONB NOT NULL,
                   PRIMARY KEY(id)
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

    /**
     * Creates a new database if it does not already exist.
     *
     * @return The connection to the newly created database.
     * @throws SQLException If an SQL exception occurs during the database creation process.
     */
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

    /**
     * Indexes a URL with the given words and title.
     *
     * @param url   The URL to index.
     * @param words The words associated with the URL..
     * @param title The title of the URL.
     * @return The ID of the indexed URL, or -1 if the URL could not be indexed.
     * @throws SQLException If an SQL error occurs while indexing the URL.
     */
    long indexUrl(String url, JSONArray words, String title) throws SQLException {
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

    /**
     * Indexes a link-word relationship in the database.
     *
     * @param wordId The ID of the word to index.
     * @param linkId The ID of the link to index.
     * @return True if the indexing operation was successful, false otherwise.
     */
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
            return true;
        } catch (SQLException e) {
            System.out.println("Failed to index word link: " + wordId + " " + linkId);
        }
        return false;
    }

    /**
     * Adds a link-link relationship to the database.
     *
     * @param url  The URL of the first link.
     * @param url1 The URL of the second link.
     * @return True if the addition operation was successful, false otherwise.
     * @throws SQLException If an SQL exception occurs during the operation.
     */
    boolean addLink(String url, String url1) throws SQLException {
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
            connection.commit();
            return true;
        } catch (SQLException e) {
            System.out.println("Failed to add link: " + url + " " + url1);
            connection.rollback();
        }
        return true;
    }

    /**
     * Retrieves the ID of a URL from the database.
     *
     * @param url The URL to retrieve the ID for.
     * @return The ID of the URL, or -1 if the URL insertion failed.
     * @throws SQLException If an SQL exception occurs during the operation.
     */
    long getUrlId(String url) throws SQLException {
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
            connection.rollback();
            return -1;
        }
        return id;
    }

    /**
     * Checks if a URL is already indexed in the database.
     *
     * @param url The URL to check for indexing status.
     * @return True if the URL is indexed, false otherwise.
     */
    boolean indexedUrl(String url){
        String stmt = "SELECT indexed FROM links WHERE link = ? AND indexed = TRUE;";
        try {
            PreparedStatement statement = connection.prepareStatement(stmt);
            statement.setString(1, url);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            System.out.println("Failed to verify if URL is indexed: " + url);
        }
        return false;
    }

    /**
     * Retrieves the ID of a word from the database. Inserts the word if it doesn't exist.
     *
     * @param word The word to retrieve the ID for or insert into the database.
     * @return The ID of the word, or -1 if the insertion failed.
     */
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
        } catch (SQLException e) {
            System.out.println("Failed to insert word: " + e.getMessage());
            return -1;
        }
        return id;
    }

    /**
     * Searches for sites containing all specified words.
     *
     * @param words An array of words to search for.
     * @param page  The page number for pagination.
     * @return An array of Site objects matching the search criteria.
     */
    Site[] search(String[] words, int page) {
        Set<String> wordsSet = Set.of(words);
        ArrayList<Site> sites = new ArrayList<>();
        String stmt = """
                SELECT l.link, l.title, l.searches, count(distinct W.id), coalesce(sum(ll.count), 0) as occurrences
                FROM words_links as wl
                    JOIN links as l ON wl.links_id = l.id
                    JOIN words as w ON wl.words_id = w.id
                    LEFT JOIN links_links as ll ON l.id = ll.links_id1\s
                WHERE w.word IN (?"""
                + ",?".repeat(wordsSet.size() - 1) +
                """
                )
                GROUP BY l.link, l.title, l.searches
                ORDER BY occurrences desc
                LIMIT 10 OFFSET\s""" + page*10 + ";";
        try {
            PreparedStatement statement = connection.prepareStatement(stmt);
            int i = 1;
            for (String word : wordsSet) {
                statement.setString(i++, word.toLowerCase());
            }
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                if (resultSet.getInt("count") == wordsSet.size())
                    sites.add(new Site(resultSet.getString("link"), resultSet.getString("title"), resultSet.getInt("occurrences")));
            }
        } catch (SQLException e) {
            System.out.println("Failed to search for words: " + e.getMessage());
            return null;

        }
        Site[] sitesArray = new Site[sites.size()];
        return sites.toArray(sitesArray);
    }

    /**
     * Searches for a specific URL in the database.
     *
     * @param url The URL to search for.
     * @return A Site object representing the URL if it is found, or null if the URL is not found.
     */
    Site searchUrl(String url){
        String stmt = """
                SELECT link, title FROM links WHERE link = ? AND indexed = TRUE;
                """;
        try {
            PreparedStatement statement = connection.prepareStatement(stmt);
            statement.setString(1, url);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return new Site(resultSet.getString("link"), resultSet.getString("title"));
            }
        } catch (SQLException e) {
            System.out.println("Failed to search for URL: " + e.getMessage());
            return null;
        }
        return null;
    }

    /**
     * Retrieves linked pages for a given URL from the database.
     *
     * @param url The URL for which linked pages are to be retrieved.
     * @return An array of Site objects representing linked pages.
     */
    Site[] linkedPages(String url){
        String stmt = """
                select l.link, l.title, sum(ll.count)  as occurrences
                FROM links_links as ll
                    JOIN links as l ON ll.links_id1 = l.id
                WHERE l.link = ?
                group by l.id , l.title
                """;
        ArrayList<Site> sites = new ArrayList<>();
        try {
            PreparedStatement statement = connection.prepareStatement(stmt);
            statement.setString(1, url);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                sites.add(new Site(resultSet.getString("link"), resultSet.getString("title")));
            }
        } catch (SQLException e) {
            System.out.println("Failed to search for linked pages: " + e.getMessage());
            return null;
        }
        Site[] sitesArray = new Site[sites.size()];
        return sites.toArray(sitesArray);
    }

    /**
     * Inserts a message into the database.
     *
     * @param id      The ID of the message.
     * @param message The message content to be inserted.
     * @throws SQLException if an SQL exception occurs during the insertion process.
     */
    void insertMessage(long id, String message) throws SQLException {
        String stmt = """
                INSERT INTO messages (id, message)
                VALUES (?, ? :: JSONB)
                """;
        try {
            PreparedStatement statement = connection.prepareStatement(stmt);
            statement.setLong(1, id);
            statement.setString(2, message);
            statement.execute();
        } catch (SQLException e) {
            System.out.println("Failed to insert message: " + e.getMessage());
            connection.rollback();
        }
    }

    /**
     * Retrieves a message from the database based on its ID.
     *
     * @param id The ID of the message to retrieve.
     * @return A JSONObject representing the retrieved message, or null if not found.
     */
    JSONObject getMessage(long id) {
        String stmt = """
                SELECT message FROM messages WHERE id = ?
                """;
        try {
            PreparedStatement statement = connection.prepareStatement(stmt);
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return new JSONObject(resultSet.getString("message"));
            }
        } catch (SQLException e) {
            System.out.println("Failed to get message: " + e.getMessage());
        }
        return null;
    }
}

/**
 * AuthDatabase.java
 * Name: Addison Klein
 * G#01331326
 * CS321-009
 * Professor Steven Ernst
 * Spring 2025
 */

package edu.gmu.cs321;

import java.sql.*;
import java.util.UUID;

/**
 * Class AuthDatabase is responsible for managing permissions
 * for users. It registers and givers the necessary permissions
 * to new users, and authenticates users logging in.
 */
public class AuthDatabase {
    // JDBC parameters
    private static final String URL  = "jdbc:mysql://localhost:3306/cs321?serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASS = "";

    static {
        try {
        Class.forName("com.mysql.cj.jdbc.Driver"); //loads the MySQL JDBC driver
        init();
        } catch (Exception e) {
        e.printStackTrace();
        }
    }

    /**
     * Creates the users table if it does not currently exist.
     */
    private static void init() {
        String ddl = """
        CREATE TABLE IF NOT EXISTS users (
            user_id       CHAR(36)       PRIMARY KEY,
            username      VARCHAR(50)    NOT NULL UNIQUE,
            password_hash CHAR(64)       NOT NULL,
            access_screen ENUM('Approval','Review','DataEntry') NOT NULL
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
        """;
        try (Connection c = DriverManager.getConnection(URL,USER,PASS);
            Statement  s = c.createStatement()) {
        s.executeUpdate(ddl);
        } catch (SQLException e) {
        e.printStackTrace();
        }
    }

    /**
     * Registers a new user with given username, hashed password, and access level.
     * @param username the given username
     * @param hash SHA-256 hash of the inputted password
     * @param access the screen access the user has access too
     * @return true if inserted successfully, false otherwise.
     */
    public static boolean registerUser(String username, String hash, String access) {
        String sql = "INSERT INTO users(user_id,username,password_hash,access_screen) VALUES(?,?,?,?)";
        try (Connection c = DriverManager.getConnection(URL,USER,PASS);
            PreparedStatement p = c.prepareStatement(sql)) {
        p.setString(1, UUID.randomUUID().toString());
        p.setString(2, username);
        p.setString(3, hash);
        p.setString(4, access);
        return p.executeUpdate()==1;
        } catch (SQLException e) {
        return false;
        }
    }

    /**
     * Authenticates the users credentials.
     * @param username the trimmed input for username
     * @param hash SHA-256 hash of input password
     * @param access has value if verified, null if invalid
     * @return the access_scren on success, or null if invalid.
     */
    public static String authenticate(String username, String hash) {
        String sql = "SELECT access_screen FROM users WHERE username=? AND password_hash=?";
        try (Connection c = DriverManager.getConnection(URL,USER,PASS);
            PreparedStatement p = c.prepareStatement(sql)) {
        p.setString(1, username);
        p.setString(2, hash);
        try (ResultSet r = p.executeQuery()) {
            if (r.next()) return r.getString("access_screen");
        }
        } catch (SQLException e) { }
        return null;
    }
}

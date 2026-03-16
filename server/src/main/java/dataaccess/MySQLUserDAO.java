package dataaccess;


import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

public class MySQLUserDAO implements UserDAO {

    public MySQLUserDAO() throws DataAccessException {
        createUserTable();
    }

    public void createUser(UserData user) throws DataAccessException {
        var sql = "INSERT INTO user (username, password, email) VALUES (?, ?, ?)";
        try (Connection con = DatabaseManager.getConnection();
             PreparedStatement statement = con.prepareStatement(sql);
        ) {
            String hashedPassword = BCrypt.hashpw(user.password(), BCrypt.gensalt());

            statement.setString(1, user.username());
            statement.setString(2, hashedPassword);
            statement.setString(3, user.email());
            statement.executeUpdate();
        } catch (Exception e) {
            throw new DataAccessException("Error creating user:" + e.getMessage());
        }
    }

    public UserData getUser(String username) throws DataAccessException {
        var sql = "SELECT username, password, email FROM user WHERE username=?";
        try (Connection con = DatabaseManager.getConnection();
             PreparedStatement statement = con.prepareStatement(sql);
        ) {

            statement.setString(1, username);
            try (var rs = statement.executeQuery()) {
                if (rs.next()) {
                    return new UserData(
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("email")
                    );
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("Error getting user:" + e.getMessage());
        }
        return null;
    }

    public void clear() throws DataAccessException {
        var sql = "DELETE FROM user";
        try (Connection con = DatabaseManager.getConnection();
             PreparedStatement statement = con.prepareStatement(sql);
        ) {
            statement.executeUpdate();
        } catch (Exception e) {
            throw new DataAccessException("Error clearing users:" + e.getMessage());
        }
    }


    private void createUserTable() throws DataAccessException {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS user " +
                "(username VARCHAR(255) PRIMARY KEY," +
                "password VARCHAR(255) NOT NULL," +
                "email VARCHAR(255) NOT NULL) ";

        try (Connection con = DatabaseManager.getConnection();
             Statement statement = con.createStatement();
        ) {
            statement.executeUpdate(createTableSQL);

        } catch (Exception e) {
            throw new DataAccessException("Error creating table:" + e.getMessage());
        }
    }
}

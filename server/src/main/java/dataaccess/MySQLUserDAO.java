package dataaccess;


import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

public class MySQLUserDAO {

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

    public void createUserTable() throws DataAccessException {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS user (username VARCHAR(255) PRIMARY KEY,password VARCHAR(255) NOT NULL,email VARCHAR(255) NOT NULL) ";

        try (Connection con = DatabaseManager.getConnection();
             Statement statement = con.createStatement();
        ) {
            statement.executeUpdate(createTableSQL);

        } catch (Exception e) {
            throw new DataAccessException("Error creating table:" + e.getMessage());
        }
    }
}

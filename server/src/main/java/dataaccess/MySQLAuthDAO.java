package dataaccess;

import model.UserData;

import javax.xml.crypto.Data;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MySQLAuthDAO implements AuthDAO {

    public MySQLAuthDAO() throws DataAccessException {
        createAuthTable();
    }

    public void clear() {
        authTokens.clear();
    }

    public String createAuth(UserData user) {
        String token = UUID.randomUUID().toString();
        String sql = "INSERT INTO auth (authToken, username) VALUES (?, ?)";
        try (Connection con = DatabaseManager.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, token);
            stmt.setString(2, user.username());
            stmt.executeUpdate();

        } catch (Exception e) {
            System.out.println("Error creating auth: " + e.getMessage());
            return null;
        }
        return token;
    }

    public UserData getAuth(String token) {

        var sql = "SELECT username from auth WHERE authToken=?";
        try (Connection con = DatabaseManager.getConnection();
             PreparedStatement statement = con.prepareStatement(sql);
        ) {

            statement.setString(1, token);
            try (var rs = statement.executeQuery()) {
                if (rs.next()) {
                    return new UserData(
                            rs.getString("username"),
                            null,
                            null
                    );
                }
            }
        } catch (Exception e) {
            System.out.println("Error getting auth: " + e.getMessage());
        }
        return null;
    }

    public void deleteAuth(String token) {

        String sql = "DELETE FROM auth WHERE authToken = ?";
        try (Connection con = DatabaseManager.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, token);
            stmt.executeUpdate();

        } catch (Exception e) {
            System.out.println("Error deleting auth: " + e.getMessage());
        }
    }

    public void createAuthTable() throws DataAccessException {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS auth(authToken VARCHAR(255) PRIMARY KEY,username VARCHAR(255) NOT NULL ";

        try (Connection con = DatabaseManager.getConnection();
             Statement statement = con.createStatement();
        ) {
            statement.executeUpdate(createTableSQL);

        } catch (Exception e) {
            throw new DataAccessException("Error creating auth table:" + e.getMessage());
        }
    }
}

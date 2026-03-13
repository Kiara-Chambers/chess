package dataaccess;

import model.UserData;

import java.sql.Connection;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MySQLAuthDAO implements AuthDAO{
    Map<String, UserData> authTokens = new HashMap<>();

    public MySQLAuthDAO() throws DataAccessException {
        createAuthTable();
    }

    public void clear() {
        authTokens.clear();
    }

    public String createAuth(UserData user) {
        String token = UUID.randomUUID().toString();
        authTokens.put(token, user);
        return token;
    }

    public UserData getAuth(String token) {
        return authTokens.get(token);
    }

    public void deleteAuth(String token) {
        authTokens.remove(token);
    }

    public void createAuthTable() throws DataAccessException {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS auth(authToken VARCHAR(255), PRIMARY KEY,username VARCHAR(255) NOT NULL ";

        try (Connection con = DatabaseManager.getConnection();
             Statement statement = con.createStatement();
        ) {
            statement.executeUpdate(createTableSQL);

        } catch (Exception e) {
            throw new DataAccessException("Error creating auth table:" + e.getMessage());
        }
    }
}

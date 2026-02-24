package dataaccess;

import model.UserData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MemoryAuthDAO {
    Map<String, UserData> authTokens = new HashMap<>();

    public void clear() throws DataAccessException {
        authTokens.clear();
    }

    public String createAuth(UserData user) throws DataAccessException {
        String token = UUID.randomUUID().toString();
        authTokens.put(token, user);
        return token;
    }

    public UserData getAuth(String token) throws DataAccessException {
        return authTokens.get(token);
    }

    public void deleteAuth(String token) throws DataAccessException {
        authTokens.remove(token);
    }
}

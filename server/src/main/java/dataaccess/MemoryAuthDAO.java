package dataaccess;

import model.UserData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MemoryAuthDAO implements AuthDAO{
    Map<String, UserData> authTokens = new HashMap<>();

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
}

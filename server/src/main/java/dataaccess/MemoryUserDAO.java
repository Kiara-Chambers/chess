package dataaccess;

import model.UserData;

import java.util.HashMap;
import java.util.Map;

public class MemoryUserDAO implements UserDAO {
    Map<String,UserData> users = new HashMap<>();
    public void clear() throws DataAccessException{
        users.clear();
    }
    public void createUser(UserData u) throws DataAccessException{
        users.put(u.username(),u);
    }
    public UserData getUser(String username) throws DataAccessException{
        return users.get(username);
    }

}

package dataaccess;

import model.UserData;

import java.util.HashMap;
import java.util.Map;

public class MemoryUserDAO {
    Map<String,UserData> users = new HashMap<>();
    public void clear() throws DataAccessException{
        users.clear();
    }
    public void createUser(UserData u) throws DataAccessException{

    }
    public void getUser(UserData u) throws DataAccessException{

    }

}

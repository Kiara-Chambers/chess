package dataaccess;

import model.UserData;

import java.util.HashMap;
import java.util.Map;

public class MemoryAuthDAO {
    Map<String,UserData> authTokens = new HashMap<>();
    public void clear() throws DataAccessException{
        authTokens.clear();
    }
    void createAuth(UserData u) throws DataAccessException{

    }
    void getAuth(UserData u) throws DataAccessException{

    }
    void deleteAuth(UserData u) throws DataAccessException{

    }
}

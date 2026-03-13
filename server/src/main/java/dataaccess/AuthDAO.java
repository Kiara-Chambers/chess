package dataaccess;

import model.UserData;

public interface AuthDAO {
    void clear() throws DataAccessException;

    String createAuth(UserData user) throws DataAccessException;

    UserData getAuth(String token) throws DataAccessException;

    void deleteAuth(String token) throws DataAccessException;

}

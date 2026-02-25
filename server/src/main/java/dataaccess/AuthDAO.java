package dataaccess;

import model.UserData;

public interface AuthDAO {
    void clear();

    String createAuth(UserData user);

    UserData getAuth(String token);

    void deleteAuth(String token);

}

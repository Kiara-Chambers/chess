package dataaccess;

import model.UserData;

public interface UserDAO {
    void clear();

    void createUser(UserData u);

    UserData getUser(String u);
}

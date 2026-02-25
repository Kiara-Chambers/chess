package dataaccess;

import model.UserData;

import java.util.UUID;

public interface AuthDAO {
    public void clear();

    public String createAuth(UserData user);


    public UserData getAuth(String token);


    public void deleteAuth(String token);

}

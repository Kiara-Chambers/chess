package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.util.HashMap;
import java.util.Map;

public class MemoryUserDAO implements UserDAO {
    Map<String,UserData> users = new HashMap<>();
    public void clear() throws DataAccessException{
        users.clear();
    }
    public void createUser(UserData u) throws DataAccessException{
        String hashed = BCrypt.hashpw(u.password(),BCrypt.gensalt());
        users.put(u.username(),new UserData(u.username(),hashed,u.email()));
    }
    public UserData getUser(String username) throws DataAccessException{
        return users.get(username);
    }

}

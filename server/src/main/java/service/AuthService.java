package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import model.UserData;
import org.eclipse.jetty.server.Authentication;

public class AuthService {
    MemoryUserDAO userDAO;
    MemoryAuthDAO authDAO;

    public AuthService(MemoryUserDAO userDAO, MemoryAuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public String register(String username, String password, String email) throws DataAccessException {
        UserData user = new UserData(username,password,email);
        userDAO.createUser(user);
        return authDAO.createAuth(user);
    }
    public String login(){}
    public void logout(){}
    public void clear(){}
    public UserData getUserFromToken(){}
}

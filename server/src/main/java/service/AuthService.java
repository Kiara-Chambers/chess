package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import model.UserData;

public class AuthService {
    MemoryUserDAO userDAO;
    MemoryAuthDAO authDAO;

    public AuthService(MemoryUserDAO userDAO, MemoryAuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public String register(String username, String password, String email) throws DataAccessException {
        UserData user = new UserData(username, password, email);
        userDAO.createUser(user);
        return authDAO.createAuth(user);
    }

    public String login(String username, String password) throws DataAccessException {
        UserData user = userDAO.getUser(username);
        return authDAO.createAuth(user);
    }

    public void logout(String authToken) {
        authDAO.deleteAuth(authToken);
    }

    public void clear() throws DataAccessException {
        userDAO.clear();
        authDAO.clear();
    }

    public UserData getUserFromToken(String authToken) {
        return authDAO.getAuth(authToken);
    }
}

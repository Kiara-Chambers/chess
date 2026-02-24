package service;

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

    public String register(String username, String password, String email){

    }
    public String login(){}
    public void logout(){}
    public void clear(){}
    public UserData getUserFromToken(){}
}

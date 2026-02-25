package service;

import dataaccess.*;
import model.UserData;

public class UserService {
    UserDAO userDAO;
    AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public String register(UserData user) throws DataAccessException {
        //TODO: validation and whatnot
        userDAO.createUser(user);
        return authDAO.createAuth(user);
    }
   /* public void login( loginRequest) {}
    public void logout( logoutRequest) {}*/
}

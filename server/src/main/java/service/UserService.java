package service;

import dataaccess.*;
import model.UserData;
import org.eclipse.jetty.server.Authentication;

public class UserService {
    UserDAO userDAO;
    AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public String register(UserData user) throws DataAccessException {

        //already a user
        if(userDAO.getUser(user.username()) !=null){
            throw new IllegalStateException();
        }

        userDAO.createUser(user);
        return authDAO.createAuth(user);
    }
   /* public void login( loginRequest) {}
    public void logout( logoutRequest) {}*/
}

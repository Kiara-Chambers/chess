package service;

import dataaccess.*;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.UnauthorizedResponse;
import model.UserData;
import org.eclipse.jetty.server.Authentication;

import java.util.Objects;

public class UserService {
    UserDAO userDAO;
    AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public String register(UserData user) throws DataAccessException {
        //bad request -> 400
        if (user.username() == null ||
                user.password() == null ||
                user.email() == null) {
            throw new BadRequestResponse();
        }

        //user is already taken -> 403
        if (userDAO.getUser(user.username()) != null) {
            throw new IllegalStateException();
        }

        userDAO.createUser(user);
        return authDAO.createAuth(user);
    }

    public String login(UserData user) throws DataAccessException {
        //bad request -> 400
        if (user.username() == null ||
                user.password() == null) {
            throw new BadRequestResponse();
        }
        //unauthorized -> 401
        UserData data = userDAO.getUser(user.username());

        if (data == null || !Objects.equals(data.password(), user.password())) {
            throw new UnauthorizedResponse();
        }

        return authDAO.createAuth(data);
    }

    public void logout(String authToken) throws DataAccessException {
        //unauthorized -> 401
        if (authDAO.getAuth(authToken) == null || authToken == null) {
            throw new UnauthorizedResponse();
        }
        authDAO.deleteAuth(authToken);
    }
}

package service;

import dataaccess.*;
import io.javalin.http.UnauthorizedResponse;
import model.UserData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTests {
    @Test
    void testRegisterPositive() throws DataAccessException {
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        UserService service = new UserService(userDAO, authDAO);

        UserData user = new UserData("Kaladin", "pw", "stormblessed@byu.edu");

        service.register(user);

        assertNotNull(userDAO.getUser("Kaladin"));
    }

    @Test
    void testRegisterNegative() throws DataAccessException {
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        UserService service = new UserService(userDAO, authDAO);

        UserData user = new UserData("Kaladin", "pw", "stormblessed@byu.edu");

        service.register(user);

        assertThrows(IllegalStateException.class, () -> service.register(user)
        );
    }

    @Test
    void testLoginPositive() {

    }

    @Test
    void testLoginNegative() {

    }

    @Test
    void testLogoutPositive() {

    }

    @Test
    void testLogoutNegative() {

    }
}

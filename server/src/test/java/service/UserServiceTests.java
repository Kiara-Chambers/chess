package service;

import dataaccess.*;
import io.javalin.http.BadRequestResponse;
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
    void testLoginPositive() throws DataAccessException {
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        UserService service = new UserService(userDAO, authDAO);

        UserData user = new UserData("Kaladin", "pw", "stormblessed@byu.edu");

        service.register(user);
        String token = service.login(user);
        assertNotNull(token);
    }

    @Test
    void testLoginNegative() throws DataAccessException {
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        UserService service = new UserService(userDAO, authDAO);

        UserData user = new UserData("Kaladin", "pw", "stormblessed@byu.edu");

        assertThrows(UnauthorizedResponse.class, () -> service.login(user)
        );
    }

    @Test
    void testLogoutPositive() throws DataAccessException {
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        UserService service = new UserService(userDAO, authDAO);

        UserData user = new UserData("Kaladin", "pw", "stormblessed@byu.edu");

        service.register(user);
        String token = service.login(user);
        service.logout(token);

        assertNull(authDAO.getAuth(token));

    }

    @Test
    void testLogoutNegative() throws DataAccessException {

    }
}

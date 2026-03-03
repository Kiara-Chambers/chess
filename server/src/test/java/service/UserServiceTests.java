package service;

import dataaccess.*;
import model.UserData;
import org.eclipse.jetty.server.Authentication;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTests {
    @Test
    void testRegisterPositive() throws DataAccessException {
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        UserService service = new UserService(userDAO,authDAO);

        UserData user = new UserData("Kaladin", "pw", "stormblessed@byu.edu");

        service.register(user);

        assertNotNull(userDAO.getUser("Kaladin"));
    }
    @Test
    void testRegisterNegative(){

    }
    @Test
    void testLoginPositive(){

    }
    @Test
    void testLoginNegative(){

    }
    @Test
    void testLogoutPositive(){

    }
    @Test
    void testLogoutNegative(){

    }
}

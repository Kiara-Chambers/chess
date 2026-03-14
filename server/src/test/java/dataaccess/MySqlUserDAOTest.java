package dataaccess;

import model.UserData;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import static org.junit.jupiter.api.Assertions.*;

public class MySqlUserDAOTest {
    @Test
    void createUserPositive() throws DataAccessException {
        MySQLUserDAO userDAO = new MySQLUserDAO();
        userDAO.clear();

        UserData user = new UserData("Vin","pw","Mistborn@byu.edu");
        userDAO.createUser(user);

        UserData checkDB = userDAO.getUser("Vin");
        assertEquals("Vin",checkDB.username());
    }
    @Test
    void createUserNegative() throws DataAccessException {
        MySQLUserDAO userDAO = new MySQLUserDAO();
        userDAO.clear();

        UserData user = new UserData("Vin","pw","Mistborn@byu.edu");
        userDAO.createUser(user);

        assertThrows(DataAccessException.class, ()->userDAO.createUser(user));
    }
    @Test
    void getUserPositive() throws DataAccessException {
        MySQLUserDAO userDAO = new MySQLUserDAO();
        userDAO.clear();

        UserData user = new UserData("Vin","pw","Mistborn@byu.edu");
        userDAO.createUser(user);

        UserData checkDB = userDAO.getUser("Vin");
        assertEquals("Vin",checkDB.username());
        //assertEquals(user.password(),checkDB.password());
        assertEquals(user.email(),checkDB.email());

        assertTrue(BCrypt.checkpw("pw",checkDB.password()));

    }

    @Test
    void getUserNegative() throws DataAccessException {
        MySQLUserDAO userDAO = new MySQLUserDAO();
        userDAO.clear();

        UserData user = userDAO.getUser("NotRealUser");
        assertEquals(null,user);
    }

    @Test
    void clearTest() throws DataAccessException {
        MySQLUserDAO userDAO = new MySQLUserDAO();
        userDAO.clear();

        UserData user = new UserData("Vin","pw","Mistborn@byu.edu");
        userDAO.createUser(user);

        //exists
        assertEquals("Vin",userDAO.getUser("vin").username());

        userDAO.clear();
        //after clear
        assertEquals(null,userDAO.getUser("Vin"));
    }
}

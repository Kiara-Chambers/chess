package dataaccess;

import model.UserData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}

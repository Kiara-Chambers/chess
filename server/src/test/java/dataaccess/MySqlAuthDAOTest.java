package dataaccess;

import model.UserData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MySqlAuthDAOTest {
    @Test
    void createAuthPositive() throws DataAccessException {
        MySQLAuthDAO authDAO = new MySQLAuthDAO();
        authDAO.clear();

        UserData user = new UserData("Vis Telimus","pw","bis@byu.edu");
        String token = authDAO.createAuth(user);

        assertNotNull(token);
        assertEquals("Vis Telimus",authDAO.getAuth(token).username());
    }
    @Test
    void createAuthNegative() throws DataAccessException {
        MySQLAuthDAO authDAO = new MySQLAuthDAO();
        authDAO.clear();

        assertThrows(DataAccessException.class,()->authDAO.createAuth(null));
    }
    @Test
    void getAuthPositive() throws DataAccessException {
        MySQLAuthDAO authDAO = new MySQLAuthDAO();
        authDAO.clear();

        UserData user = new UserData("Vis Telimus","pw","bis@byu.edu");
        String token = authDAO.createAuth(user);
        UserData fromDB = authDAO.getAuth(token);

        assertNotNull(fromDB);
        assertEquals("Vis Telimus",fromDB.username());
    }
    @Test
    void getAuthNegative() throws DataAccessException {
        MySQLAuthDAO authDAO = new MySQLAuthDAO();
        authDAO.clear();

        UserData fromDB = authDAO.getAuth("CatenanRepublic");

        assertNull(fromDB);
    }
    @Test
    void deleteAuthPositive() throws DataAccessException {}
    @Test
    void deleteAuthNegative() throws DataAccessException {}
    @Test
    void clearTest() throws DataAccessException {}
}

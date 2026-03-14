package dataaccess;

import chess.ChessGame;
import model.GameData;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MySqlGameDAOTest {
    @Test
    void createGamePositive() throws DataAccessException {
        MySQLGameDAO gameDAO = new MySQLGameDAO();
        gameDAO.clear();

        GameData data = new GameData(0,"Rand","Mat","OnePower",new ChessGame());
        int gameID = gameDAO.createGame(data);

        GameData checkDB = gameDAO.getGame(gameID);
        assertNotNull(checkDB);
        assertNotNull(checkDB.game());
        assertEquals("Rand",checkDB.whiteUsername());
        assertEquals("Mat",checkDB.blackUsername());
        assertEquals("OnePower",checkDB.gameName());

    }
    @Test
    void createGameNegative() throws DataAccessException {
        MySQLGameDAO gameDAO = new MySQLGameDAO();
        gameDAO.clear();

        GameData data = gameDAO.getGame(42);
        assertEquals(null,data);
    }
    @Test
    void getGamePositive() throws DataAccessException {
        MySQLGameDAO gameDAO = new MySQLGameDAO();
        gameDAO.clear();

        GameData data = new GameData(0, "Rand", "Mat", "OnePower", new ChessGame());
        int gameID = gameDAO.createGame(data);

        GameData retrieved = gameDAO.getGame(gameID);
        assertEquals("Rand", retrieved.whiteUsername());
        assertEquals("Mat", retrieved.blackUsername());
        assertEquals("OnePower", retrieved.gameName());
        assertNotNull(retrieved.game());
    }

    @Test
    void getGameNegative() throws DataAccessException {
        MySQLGameDAO gameDAO = new MySQLGameDAO();
        gameDAO.clear();

        GameData retrieved = gameDAO.getGame(3);
        assertEquals(null, retrieved);
    }
    @Test
    void ListGamePositive(){

    }
    @Test
    void ListGameNegative(){

    }
    @Test
    void UpdateGamePositive(){

    }
    @Test
    void UpdateGameNegative(){

    }

    @Test
    void clearTest() throws DataAccessException {

    }
}

package service;

import chess.ChessGame;
import dataaccess.*;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTests {
    @Test
    void testListGamesPositive() throws DataAccessException {
        GameDAO gameDAO = new MemoryGameDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        GameService service = new GameService(gameDAO,authDAO);

        UserData user = new UserData("Kaladin", "pw", "stormblessed@byu.edu");
        authDAO.createAuth(user);
        String token  = authDAO.createAuth(user);


        service.createGame("1",token);
        service.createGame("2",token);
        service.createGame("3",token);

        var games = service.listGames(token);

        assertEquals(3,games.size());



    }
    @Test
    void testListGamesNegative(){

    }
    @Test
    void testCreateGamePositive(){

    }
    @Test
    void testCreateGameNegative(){

    }
    @Test
    void testJoinGamePositive(){

    }
    @Test
    void testJoinGameNegative(){

    }

}

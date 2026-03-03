package service;

import chess.ChessGame;
import dataaccess.*;
import io.javalin.http.UnauthorizedResponse;
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
        String token  = authDAO.createAuth(user);


        service.createGame("1",token);
        service.createGame("2",token);
        service.createGame("3",token);

        var games = service.listGames(token);

        assertEquals(3,games.size());



    }
    @Test
    void testListGamesNegative() throws DataAccessException {
        GameDAO gameDAO = new MemoryGameDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        GameService service = new GameService(gameDAO,authDAO);

        String token  = "honorIsDead";

        assertThrows(UnauthorizedResponse.class,()->service.listGames(token));

    }
    @Test
    void testCreateGamePositive() throws DataAccessException {
        GameDAO gameDAO = new MemoryGameDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        GameService service = new GameService(gameDAO,authDAO);

        UserData user = new UserData("Kaladin", "pw", "stormblessed@byu.edu");
        String token  = authDAO.createAuth(user);


        int game = service.createGame("Storms",token);
        assertTrue(game>0);
    }
    @Test
    void testCreateGameNegative() throws DataAccessException {
        GameDAO gameDAO = new MemoryGameDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        GameService service = new GameService(gameDAO,authDAO);

       // UserData user = new UserData("Kaladin", "pw", "stormblessed@byu.edu");
        String token  = "youWillBeWarmAgain";

        assertThrows(UnauthorizedResponse.class,()->service.createGame("Storms",token));
    }
    @Test
    void testJoinGamePositive() throws DataAccessException {
        GameDAO gameDAO = new MemoryGameDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        GameService service = new GameService(gameDAO,authDAO);

        UserData user = new UserData("Kaladin", "pw", "stormblessed@byu.edu");
        String token  = authDAO.createAuth(user);

        int game = service.createGame("Storms",token);
        service.joinGame("WHITE",game,token);

        assertEquals("Kaladin",gameDAO.getGame(game).whiteUsername());
    }
    @Test
    void testJoinGameNegative(){

    }

}

package client;

import org.junit.jupiter.api.*;
import server.Server;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        facade = new ServerFacade(port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    void clearDB() throws Exception {
        facade.clearDatabase();
    }

    @Test
    void registerPositive() throws Exception {
        var authData = facade.register("player1", "password", "p1@email.com");
        assertNotNull(authData);
        assertTrue(authData.authToken().length() > 10);
    }

    @Test
    void registerNegative() throws Exception {
        facade.register("player1", "password", "p1@email.com");
        assertThrows(Exception.class, () -> facade.register("player1", "password", "p1@email.com"));
    }

    @Test
    void loginPositive() throws Exception {
        facade.register("player1", "password", "p1@email.com");
        var authData = facade.login("player1", "password");
        assertNotNull(authData);
        assertTrue(authData.authToken().length() > 10);
    }

    @Test
    void loginNegative() throws Exception {
        facade.register("player1", "password", "p1@email.com");
        assertThrows(Exception.class, () -> facade.login("player1", "wrongpassword"));
    }

    @Test
    void logoutPositive() throws Exception {
        var authData = facade.register("player1", "password", "p1@email.com");
        assertDoesNotThrow(() -> facade.logout(authData.authToken()));
    }

    @Test
    void logoutNegative() {
        assertThrows(Exception.class, () -> facade.logout("invalidToken"));
    }

    @Test
    void listGamesPositive() throws Exception {
        var authData = facade.register("player1", "password", "p1@email.com");
        facade.createGame("ChessGame1", authData.authToken());
        List<?> games = facade.listGames();
        assertNotNull(games);
        assertEquals(1, games.size());
    }

    @Test
    void listGamesNegative() throws Exception {
        var authData = facade.register("player1", "password", "p1@email.com");
        List<?> games = facade.listGames();
        assertNotNull(games);
        assertEquals(0, games.size());
    }

    @Test
    void createGamePositive() throws Exception {
        var authData = facade.register("player1", "password", "p1@email.com");
        var game = facade.createGame("NewGame", authData.authToken());
        assertNotNull(game);
        assertEquals("NewGame", game.gameName());
    }

    @Test
    void createGameNegative() throws Exception {
        var authData = facade.register("player1", "password", "p1@email.com");
        assertThrows(Exception.class, () -> facade.createGame(null, authData.authToken()));
    }

    @Test
    void joinGamePositive() throws Exception {
        var authData = facade.register("player1", "password", "p1@email.com");
        var game = facade.createGame("JoinTest", authData.authToken());
        assertDoesNotThrow(() -> facade.joinGame(game.gameID(), "WHITE",  authData.authToken()));
    }

    @Test
    void joinGameNegative() throws Exception {
        var authData = facade.register("player1", "password", "p1@email.com");
        var game = facade.createGame("TakenColorTest", authData.authToken());
        facade.joinGame(game.gameID(), "WHITE",  authData.authToken());
        assertThrows(Exception.class, () -> facade.joinGame(game.gameID(), "player1", authData.authToken()));
    }
}
package service;

import chess.ChessGame;
import dataaccess.*;
import model.GameData;
import model.UserData;
import org.eclipse.jetty.server.Authentication;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ClearServiceTest {

    @Test
    void clearRemovesEverything() throws DataAccessException {
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();

        ClearService service = new ClearService(userDAO, authDAO, gameDAO);

        UserData user = new UserData("Kaladin", "pw", "stormblessed@byu.edu");
        userDAO.createUser(user);
        gameDAO.createGame(new GameData(1, "Kaladin","Szeth","Storms",new ChessGame()));

        String token  = authDAO.createAuth(user);

        service.clear();
        assertNull(userDAO.getUser("Kaladin"));
        assertNull(authDAO.getAuth(token));
        assertNull(gameDAO.getGame(1));

    }
}

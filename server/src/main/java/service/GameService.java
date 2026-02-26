package service;

import dataaccess.*;
import io.javalin.http.UnauthorizedResponse;
import model.GameData;
import model.UserData;

import java.util.List;

public class GameService {
    GameDAO gameDAO;
    AuthDAO authDAO;

    public GameService(GameDAO gameDAO,AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public List<GameData> listGames(String authToken) throws DataAccessException {

        UserData user = authDAO.getAuth(authToken);

        if(user==null){
            throw new UnauthorizedResponse();
        }

        return gameDAO.listGames();
    }

    public int createGame(String gameName, String authToken) throws DataAccessException {
        UserData user = authDAO.getAuth(authToken);
    
        GameData newGame = new GameData(0,null,null,gameName,null);
        return gameDAO.createGame(newGame);
    }

    public void joinGame() {

    }
}

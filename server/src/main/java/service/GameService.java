package service;

import chess.ChessGame;
import dataaccess.*;
import io.javalin.http.UnauthorizedResponse;
import model.UserData;

import java.util.List;

public class GameService {
    GameDAO gameDAO;
    AuthDAO authDAO;

    public GameService(GameDAO gameDAO,AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public List<ChessGame> listGames(String authToken) throws DataAccessException {
        UserData user = authDAO.getAuth(authToken);

        if(user==null){
            throw new UnauthorizedResponse();
        }

        //return gameDAO.listGames(user);
        return List.of();
    }

    public void createGame() {

    }

    public void joinGame() {

    }
}

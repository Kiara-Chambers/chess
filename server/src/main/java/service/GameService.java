package service;

import chess.ChessGame;
import dataaccess.*;

import java.util.List;

public class GameService {
    GameDAO gameDAO;
    AuthDAO authDAO;

    public GameService(GameDAO gameDAO,AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public List<ChessGame> listGames() {

        return List.of();
    }

    public void createGame() {

    }

    public void joinGame() {

    }
}

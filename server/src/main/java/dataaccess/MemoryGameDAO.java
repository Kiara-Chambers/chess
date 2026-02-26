package dataaccess;

import chess.ChessGame;
import model.GameData;
import model.UserData;

import java.util.*;

public class MemoryGameDAO implements GameDAO {
    Map<Integer, GameData> games = new HashMap<>();
    int gameID =1;

    public void clear() throws DataAccessException {
        games.clear();
        gameID = 1;
    }

    public int createGame(GameData game) throws DataAccessException
    {
        int newID = gameID++;

        GameData newGame = new GameData(newID, game.whiteUsername(), game.blackUsername(),game.gameName(),game.game());
        games.put(newID,newGame);
        return newID;
    }

    public GameData getGame(int gameID) throws DataAccessException {
        return games.get(gameID);
    }

    public  List<GameData> listGames() throws DataAccessException {

        return (List<GameData>) games.values();
    }

    public void updateGame(GameData game) throws DataAccessException {
        games.put(game.gameID(),game);
    }
}

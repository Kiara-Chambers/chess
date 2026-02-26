package dataaccess;

import chess.ChessGame;
import model.GameData;
import model.UserData;

import java.util.*;

public class MemoryGameDAO implements GameDAO {
    Map<String, ChessGame> games = new HashMap<>();

    public void clear() throws DataAccessException {
        games.clear();
    }

    public int createGame(GameData game) throws DataAccessException
    {

        return 0;
    }

    public GameData getGame(int gameID) throws DataAccessException {
        return null;
    }

    public  List<GameData> listGames() throws DataAccessException {

        return List.of();
    }

    public void updateGame(GameData game) throws DataAccessException {

    }
}

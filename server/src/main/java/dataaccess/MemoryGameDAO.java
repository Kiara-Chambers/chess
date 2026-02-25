package dataaccess;

import chess.ChessGame;
import model.UserData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemoryGameDAO implements GameDAO {
    Map<String,ChessGame> games = new HashMap<>();
    public void clear() throws DataAccessException{
        games.clear();
    }
    public void createGame(UserData u) throws DataAccessException{

    }
    public ChessGame getGame(UserData u) throws DataAccessException{

        return null;
    }
    public List<ChessGame> listGames(UserData u) throws DataAccessException{

        return List.of();
    }
    public void updateGame(UserData u) throws DataAccessException{

    }
}

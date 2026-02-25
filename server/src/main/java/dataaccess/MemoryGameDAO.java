package dataaccess;

import chess.ChessGame;
import model.UserData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemoryGameDAO implements GameDAO {
    Map<String,UserData> games = new HashMap<>();
    public void clear() throws DataAccessException{
        games.clear();
    }
    public void createGame(UserData u) throws DataAccessException{

    }
    public ChessGame getGame(UserData u) throws DataAccessException{

    }
    public List<ChessGame> listGames(UserData u) throws DataAccessException{

    }
    public void updateGame(UserData u) throws DataAccessException{

    }
}

package dataaccess;

import model.UserData;

import java.util.HashMap;
import java.util.Map;

public class MemoryGameDAO {
    Map<String,UserData> games = new HashMap<>();
    public void clear() throws DataAccessException{
        games.clear();
    }
    void createGame(UserData u) throws DataAccessException{

    }
    void getGame(UserData u) throws DataAccessException{

    }
    void listGames(UserData u) throws DataAccessException{

    }
    void updateGame(UserData u) throws DataAccessException{

    }
}

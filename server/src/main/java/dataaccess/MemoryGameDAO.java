package dataaccess;

import chess.ChessGame;
import model.UserData;

import java.util.*;

public class MemoryGameDAO implements GameDAO {
    Map<String,ChessGame> games = new HashMap<>();
    public void clear() throws DataAccessException{
        games.clear();
    }
    public void createGame(UserData u) throws DataAccessException{
        ChessGame game = new ChessGame();
        String gameID = UUID.randomUUID().toString();
        games.put(gameID,game);
    }
    public ChessGame getGame(UserData u) throws DataAccessException{
        return null;
    }
    public List<ChessGame> listGames(UserData u) throws DataAccessException{
        return new ArrayList<>(games.values());
        /* List<ChessGame> gamesList = new ArrayList<>();
        for(ChessGame game: games.values()){
            if(game!=null){
                gamesList.add(game);
            }
        }
        return gamesList;*/
    }
    public void updateGame(UserData u) throws DataAccessException{

    }
}

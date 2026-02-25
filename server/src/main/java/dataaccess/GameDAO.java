package dataaccess;

import chess.ChessGame;
import model.UserData;

import java.util.List;

public interface GameDAO {
    void clear() throws DataAccessException;

    void createGame(UserData u) throws DataAccessException;

    ChessGame getGame(UserData u) throws DataAccessException;

    List<ChessGame> listGames(UserData u) throws DataAccessException;

    void updateGame(UserData u) throws DataAccessException;
}

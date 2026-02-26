package service;

import chess.ChessGame;
import dataaccess.*;
import io.javalin.http.BadRequestResponse;
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
        if(user==null){
            throw new UnauthorizedResponse();
        }
        if(gameName==null){
            throw new BadRequestResponse();
        }
        GameData newGame = new GameData(0,null,null,gameName,new ChessGame());

        return gameDAO.createGame(newGame);
    }

    public void joinGame(String playerColor, Integer gameID, String authToken) throws DataAccessException {
        UserData user = authDAO.getAuth(authToken);
        if(user==null){
            throw new UnauthorizedResponse();
        }
        if(playerColor==null||gameID==null){
            throw new BadRequestResponse();
        }

        //update game with user based on color
        GameData game = gameDAO.getGame(gameID);

        GameData updatedGame;
        if(playerColor.equals("WHITE")){
            if(game.whiteUsername()!=null){
                throw new IllegalStateException();
            }
            updatedGame = new GameData(game.gameID(),user.username(),game.blackUsername(),game.gameName(),game.game());

        }else if(playerColor.equals("BLACK")){
            if(game.blackUsername()!=null){
                throw new IllegalStateException();
            }
            updatedGame = new GameData(game.gameID(),game.whiteUsername(),user.username(),game.gameName(),game.game());
        }else{
            throw new BadRequestResponse();
        }


        gameDAO.updateGame(updatedGame);
    }
}

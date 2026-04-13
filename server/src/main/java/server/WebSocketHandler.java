package server;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import model.GameData;
import websocket.commands.UserGameCommand;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {
    private final ConnectionManager connections = new ConnectionManager();
    private final Set<String> resignedPlayers = new HashSet<>();

    private final Gson gson = new Gson();
    AuthDAO authDAO;
    GameDAO gameDAO;

    public WebSocketHandler(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }
    public void connect(UserGameCommand command,WsMessageContext ctx) throws DataAccessException, IOException {
        System.out.println("Connect");
        String username = requireAuth(command, ctx);
        if (username == null) {
            return;
        }

        GameData gameData = requireGame(command, ctx);
        if (gameData == null) {
            return;
        }

        connections.add(command.getGameID(), ctx.session);

        connections.broadcast(command.getGameID(), ctx.session,
                new Notification("NOTIFICATION", username + " joined!")
        );
        ctx.send(gson.toJson(Map.of("serverMessageType","LOAD_GAME",
                "game",gameData)));
    }
    void makeMove(UserGameCommand command,WsMessageContext ctx) throws DataAccessException, IOException, InvalidMoveException {
        System.out.println("Move");
        String username = requireAuth(command, ctx);
        if (username == null) {
            return;
        }

        GameData gameData = requireGame(command, ctx);
        if (gameData == null) {
            return;
        }
//        System.out.println("USERNAME: " + username);
//        System.out.println("TURN: " + gameData.game().getTeamTurn());
//        System.out.println("WHITE: " + gameData.whiteUsername());
//        System.out.println("BLACK: " + gameData.blackUsername());
        int gameID = command.getGameID();
        String gameKey = String.valueOf(gameID);
        if (resignedPlayers.contains(gameKey)) {
            ctx.send(gson.toJson(Map.of(
                    "serverMessageType","ERROR",
                    "errorMessage","Error: Game is already over"
            )));
            return;
        }

        String currentPlayer;
        if (gameData.game().getTeamTurn() == ChessGame.TeamColor.WHITE) {
            currentPlayer = gameData.whiteUsername();
        } else {
            currentPlayer = gameData.blackUsername();
        }
        if (!username.equals(currentPlayer)) {
            ctx.send(gson.toJson(Map.of(
                    "serverMessageType","ERROR",
                    "errorMessage","Not your turn"
            )));
            return;
        }

        ChessMove move = new ChessMove(
                command.getMove().getStartPosition(),
                command.getMove().getEndPosition(),
                command.getMove().getPromotionPiece()
        );

        //System.out.println("BEFORE MOVE");
        gameData.game().makeMove(move);
        //System.out.println("AFTER MOVE");
        gameDAO.updateGame(gameData);

        var loadGameMsg = gson.toJson(Map.of(
                "serverMessageType", "LOAD_GAME",
                "game", gameData.game()
        ));

        for (var session : connections.getGameSessions(gameID)) {
            if (session.isOpen()) {
                session.getRemote().sendString(loadGameMsg);
            }
        }

        connections.broadcast(gameID, ctx.session,
                new Notification("NOTIFICATION", username + " moved!")
        );

        if (gameData.game().isInCheckmate(ChessGame.TeamColor.WHITE) ||
                gameData.game().isInCheckmate(ChessGame.TeamColor.BLACK)) {

            resignedPlayers.add(gameKey);

            connections.broadcast(gameID, null,
                    new Notification("NOTIFICATION", "Game over!")
            );
        }
    }
    void leave(UserGameCommand command,WsMessageContext ctx) throws DataAccessException, IOException, InvalidMoveException{
        System.out.println("Leave");

        String username = requireAuth(command, ctx);
        if (username == null) {
            return;
        }

        GameData gameData = requireGame(command, ctx);
        if (gameData == null) {
            return;
        }
        //remove them from teh game
        if (username.equals(gameData.whiteUsername())) {
            gameData=new GameData(gameData.gameID(),null,gameData.blackUsername(),gameData.gameName(),gameData.game());
        } else if (username.equals(gameData.blackUsername())) {
            gameData=new GameData(gameData.gameID(),gameData.whiteUsername(),null,gameData.gameName(),gameData.game());
        }
        gameDAO.updateGame(gameData);
        //connections.remove(ctx.session);

        connections.remove(command.getGameID(), ctx.session);

        connections.broadcast(command.getGameID(), ctx.session,
                new Notification("NOTIFICATION", username + " left the game!")
        );
    }
    void resign(UserGameCommand command,WsMessageContext ctx) throws DataAccessException, IOException, InvalidMoveException{
        System.out.println("Resign");

        String username = requireAuth(command, ctx);
        if (username == null) {
            return;
        }

        GameData gameData = requireGame(command, ctx);
        if (gameData == null) {
            return;
        }
        String gameKey = String.valueOf(gameData.gameID());
        if (resignedPlayers.contains(gameKey)) {
            ctx.send(gson.toJson(Map.of(
                    "serverMessageType","ERROR",
                    "errorMessage","Error: Game is already over"
            )));
            return;
        }
        //resign -but only if you're playing...
        if(!username.equals(gameData.whiteUsername())
                &&!username.equals(gameData.blackUsername())){
            ctx.send(gson.toJson(Map.of("serverMessageType","ERROR",
                    "errorMessage","Error: Observers can't resign")));
            return;
        }


        resignedPlayers.add(gameKey);
        gameDAO.updateGame(gameData);
        connections.broadcast(command.getGameID(), ctx.session,
                new Notification("NOTIFICATION", username + " resigned!")
        );
        ctx.send(gson.toJson(new Notification(
                "NOTIFICATION",
                username + " resigned!"
        )));

    }

    @Override
    public void handleMessage(WsMessageContext ctx) throws DataAccessException {
        try {
            UserGameCommand command = gson.fromJson(ctx.message(), UserGameCommand.class);
            switch (command.getCommandType()) {
                case CONNECT -> {
                    connect(command,ctx);
                }
                case MAKE_MOVE -> {
                    makeMove(command,ctx);
                }
                case LEAVE -> {
                    leave(command,ctx);
                }
                case RESIGN -> {
                    resign(command,ctx);
                }
            }
        }catch (DataAccessException e){
            ctx.send("Error :(");

        } catch (IOException e) {
            ctx.send(gson.toJson(Map.of("serverMessageType","ERROR",
                    "errorMessage","Error: :(")));
        }  catch (InvalidMoveException e) {
        System.out.println("INVALID MOVE: " + e.getMessage());
        ctx.send(gson.toJson(Map.of(
                "serverMessageType","ERROR",
                "errorMessage", e.getMessage()
        )));
    }

    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }


    private String sendError(WsMessageContext ctx, String msg) throws IOException {
        ctx.send(gson.toJson(Map.of(
                "serverMessageType", "ERROR",
                "errorMessage", msg
        )));
        return null;
    }

    private String requireAuth(UserGameCommand command, WsMessageContext ctx)
            throws DataAccessException, IOException {

        var authData = authDAO.getAuth(command.getAuthToken());
        if (authData == null) {
            sendError(ctx, "Error: Unauthorized");
            return null;
        }
        return authData.username();
    }

    private GameData requireGame(UserGameCommand command, WsMessageContext ctx)
            throws DataAccessException, IOException {

        var gameData = gameDAO.getGame(command.getGameID());
        if (gameData == null) {
            sendError(ctx, "Error: Game's Invalid");
            return null;
        }
        return gameData;
    }
}
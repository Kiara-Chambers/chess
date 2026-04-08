package server;

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
import model.AuthData;
import websocket.commands.UserGameCommand;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {
    private final ConnectionManager connections = new ConnectionManager();
    Set<String> resignedPlayers = new HashSet<>();

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

    @Override
    public void handleMessage(WsMessageContext ctx) throws DataAccessException {
        try {
            UserGameCommand command = gson.fromJson(ctx.message(), UserGameCommand.class);
            switch (command.getCommandType()) {
                case CONNECT -> {
                    System.out.println("Connect");
                    var authData = authDAO.getAuth(command.getAuthToken());
                    if (authData == null) {
                        ctx.send(gson.toJson(Map.of("serverMessageType","ERROR",
                                "errorMessage","Error: Unauthorized")));
                        return;
                    }
                    var gameData = gameDAO.getGame(command.getGameID());
                    if (gameData == null) {
                        ctx.send(gson.toJson(Map.of("serverMessageType","ERROR",
                                "errorMessage","Error: Game's Invalid")));
                        return;
                    }

                    connections.add(ctx.session);
                    connections.broadcast(ctx.session, new Notification(
                            "NOTIFICATION",
                            authData.username() + " joined!"
                    ));
                    ctx.send(gson.toJson(Map.of("serverMessageType","LOAD_GAME",
                            "game",gameData)));
                }
                case MAKE_MOVE -> {
                    System.out.println("Move");
                    var authData = authDAO.getAuth(command.getAuthToken());
                    if (authData == null) {
                        ctx.send(gson.toJson(Map.of("serverMessageType","ERROR",
                                "errorMessage","Error: Unauthorized")));
                        return;
                    }
                    var gameData = gameDAO.getGame(command.getGameID());
                    if (gameData == null) {
                        ctx.send(gson.toJson(Map.of("serverMessageType","ERROR",
                                "errorMessage","Error: Game's Invalid")));
                        return;
                    }

                    //resigning
                    if(resignedPlayers.contains(authData.username())){
                        ctx.send(gson.toJson(Map.of("serverMessageType","ERROR",
                                "errorMessage","Error: Player has resigned, you can't move")));
                        return;
                    }


                    ChessMove move = new ChessMove(command.getMove().getStartPosition(),
                            command.getMove().getEndPosition(),
                            command.getMove().getPromotionPiece());
                    gameData.game().makeMove(move);
                    gameDAO.updateGame(gameData);

                    ctx.send(gson.toJson(Map.of(
                            "serverMessageType","LOAD_GAME",
                            "game",gameData
                    )));

                    connections.broadcast(ctx.session, new Notification(
                            "NOTIFICATION",
                            authData.username() + " moved!"
                    ));

                    for (var session : connections.connections.values()) {
                        if (session.isOpen() && !session.equals(ctx.session)) {
                            session.getRemote().sendString(gson.toJson(Map.of(
                                    "serverMessageType","LOAD_GAME",
                                    "game",gameData
                            )));
                        }
                    }
                }
                case LEAVE -> {
                    System.out.println("Leave");

                    var authData = authDAO.getAuth(command.getAuthToken());
                    if (authData == null) {
                        ctx.send(gson.toJson(Map.of("serverMessageType","ERROR",
                                "errorMessage","Error: Unauthorized")));
                        return;
                    }
                    var gameData = gameDAO.getGame(command.getGameID());
                    if (gameData == null) {
                        ctx.send(gson.toJson(Map.of("serverMessageType","ERROR",
                                "errorMessage","Error: Game's Invalid")));
                        return;
                    }
                    //leave
                    gameDAO.updateGame(gameData);
                    ctx.send(gson.toJson(Map.of("serverMessageType","LOAD_GAME",
                            "game",gameData)));
                    connections.broadcast(ctx.session, new Notification("LOAD_GAME", gson.toJson(Map.of(
                            "serverMessageType", "LOAD_GAME",
                            "game", gameData
                    ))));
                }
                case RESIGN -> {
                    System.out.println("Resign");

                    var authData = authDAO.getAuth(command.getAuthToken());
                    if (authData == null) {
                        ctx.send(gson.toJson(Map.of("serverMessageType","ERROR",
                                "errorMessage","Error: Unauthorized")));
                        return;
                    }
                    var gameData = gameDAO.getGame(command.getGameID());
                    if (gameData == null) {
                        ctx.send(gson.toJson(Map.of("serverMessageType","ERROR",
                                "errorMessage","Error: Game's Invalid")));
                        return;
                    }
                    //resign -but only if you're playing...
                    if(!authData.username().equals(gameData.whiteUsername())
                            &&!authData.username().equals(gameData.blackUsername())){
                        ctx.send(gson.toJson(Map.of("serverMessageType","ERROR",
                                "errorMessage","Error: Observers can't resign")));
                        return;
                    }

                    //why would you resign twice...
                    if(resignedPlayers.contains(authData.username())){
                        ctx.send(gson.toJson(Map.of("serverMessageType","ERROR",
                                "errorMessage","Error:Already resigned")));
                        return;
                    }


                    resignedPlayers.add(authData.username());
                    gameDAO.updateGame(gameData);
                    connections.broadcast(ctx.session, new Notification(
                            "NOTIFICATION",
                            authData.username() + " resigned!"
                    ));
                    ctx.send(gson.toJson(new Notification(
                            "NOTIFICATION",
                            authData.username() + " resigned!"
                    )));


                }
            }
        }catch (DataAccessException e){
            ctx.send("Error :(");

        } catch (IOException e) {
            ctx.send(gson.toJson(Map.of("serverMessageType","ERROR",
                    "errorMessage","Error: :(")));
        } catch (InvalidMoveException e) {
            ctx.send(gson.toJson(Map.of("serverMessageType","ERROR",
                    "errorMessage","Error: Invalid move")));
        }

    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

}
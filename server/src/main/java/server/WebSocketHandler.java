package server;

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


public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {
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
        UserGameCommand command = gson.fromJson(ctx.message(), UserGameCommand.class);
        switch (command.getCommandType()) {
            case CONNECT -> {
                System.out.println("Connect");
                var authData =authDAO.getAuth(command.getAuthToken());
                if(authData==null){
                    ctx.send(gson.toJson("Unauthorized"));
                    return;
                }
                var gameData = gameDAO.getGame(command.getGameID());
                if(gameData==null){
                    ctx.send(gson.toJson("Game's Invalid"));
                    return;
                }
                ctx.send(gson.toJson(gameData));
            }
            case MAKE_MOVE -> System.out.println("Move");
            case LEAVE -> System.out.println("Leave");
            case RESIGN -> System.out.println("Resign");
        }

    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

}
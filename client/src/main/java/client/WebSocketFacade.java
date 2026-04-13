package client;

import com.google.gson.Gson;

import jakarta.websocket.*;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;


public class WebSocketFacade extends Endpoint {

    Session session;
    NotificationHandler notificationHandler;

    public WebSocketFacade(String url, NotificationHandler notificationHandler) throws Exception {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage serverMessage =
                            new Gson().fromJson(message, ServerMessage.class);
                    notificationHandler.notify();
                }
            });
        } catch ( IOException | URISyntaxException ex) {
            System.out.println("Error :(");
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void connect(String authToken, int gameID) throws Exception {
        send(Map.of(
                "commandType", "CONNECT",
                "authToken", authToken,
                "gameID", gameID
        ));
    }

    public void makeMove(String authToken, int gameID, Object move) throws Exception {
        send(Map.of(
                "commandType", "MAKE_MOVE",
                "authToken", authToken,
                "gameID", gameID,
                "move", move
        ));
    }

    public void leave(String authToken, int gameID) throws Exception {
        send(Map.of(
                "commandType", "LEAVE",
                "authToken", authToken,
                "gameID", gameID
        ));
    }

    public void resign(String authToken, int gameID) throws Exception {
        send(Map.of(
                "commandType", "RESIGN",
                "authToken", authToken,
                "gameID", gameID
        ));
    }

    private void send(Object obj) throws Exception {
        try {
            session.getBasicRemote().sendText(new Gson().toJson(obj));
        } catch (IOException e) {
            System.out.println("Error");
        }
    }
}

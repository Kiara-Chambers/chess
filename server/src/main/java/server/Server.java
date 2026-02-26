package server;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.*;
import io.javalin.*;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import io.javalin.http.UnauthorizedResponse;
import model.GameData;
import model.UserData;
import org.jetbrains.annotations.NotNull;
import service.ClearService;
import service.GameService;
import service.UserService;

import java.util.List;
import java.util.Map;

public class Server {
    private final Gson gson = new Gson();
    private final Javalin javalin;

    UserService userService;
    GameService gameService;
    ClearService clearService;

    AuthDAO authDAO;


    public Server() {
        var userDAO = new MemoryUserDAO();
        var gameDAO = new MemoryGameDAO();
        this.authDAO = new MemoryAuthDAO();

        userService = new UserService(userDAO, authDAO);
        gameService = new GameService(gameDAO, authDAO);
        clearService = new ClearService(userDAO, authDAO, gameDAO);


        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        javalin.delete("/db", this::clearHandler);

        javalin.post("/user", this::registerHandler);

        javalin.post("/session", this::loginHandler);
        javalin.delete("/session", this::logoutHandler);

        javalin.get("/game", this::listGamesHandler);
        javalin.post("/game", this::createGameHandler);
        javalin.put("/game", this::joinGameHandler);


    }

    private void clearHandler(@NotNull Context context) {
        try {
            clearService.clear();
            context.status(200);
            context.contentType("application/json");
            context.result(gson.toJson(Map.of("message", "clear worked")));
        } catch (DataAccessException e) {
            context.status(500);
            context.contentType("application/json");
            context.result(gson.toJson(Map.of("message", "Error: " + e.getMessage())));
        }
    }

    private void registerHandler(@NotNull Context context) {
        try {
            UserData newUser = new Gson().fromJson(context.body(), UserData.class);

            String authToken = userService.register(newUser);

            context.status(200);
            context.contentType("application/json");
            context.result(gson.toJson(Map.of("username", newUser.username(), "authToken", authToken)));
        } catch (BadRequestResponse e) {
            context.status(400);
            context.result(gson.toJson(Map.of("message", "Error: bad request")));
        } catch (IllegalStateException e) {
            context.status(403);
            context.result(gson.toJson(Map.of("message", "Error: already taken")));
        } catch (Exception e) {
            context.status(500);
            context.contentType("application/json");
            context.result(gson.toJson(Map.of("message", "Error: " + e.getMessage())));
        }
    }

    private void loginHandler(@NotNull Context context) {
        try {
            UserData newUser = new Gson().fromJson(context.body(), UserData.class);

            String authToken = userService.login(newUser);

            context.status(200);
            context.contentType("application/json");
            context.result(gson.toJson(Map.of("username", newUser.username(), "password", newUser.password(), "authToken", authToken)));
        } catch (BadRequestResponse e) {
            context.status(400);
            context.result(gson.toJson(Map.of("message", "Error: bad request")));
        } catch (UnauthorizedResponse e) {
            context.status(401);
            context.result(gson.toJson(Map.of("message", "Error: unauthorized")));
        } catch (Exception e) {
            context.status(500);
            context.contentType("application/json");
            context.result(gson.toJson(Map.of("message", "Error: " + e.getMessage())));
        }
    }

    private void logoutHandler(@NotNull Context context) {
        try {
            String authToken = context.header("Authorization");
            userService.logout(authToken);

            context.status(200);
            context.contentType("application/json");
            context.result();
        } catch (UnauthorizedResponse e) {
            context.status(401);
            context.result(gson.toJson(Map.of("message", "Error: unauthorized")));
        } catch (Exception e) {
            context.status(500);
            context.contentType("application/json");
            context.result(gson.toJson(Map.of("message", "Error: " + e.getMessage())));
        }
    }

    private void listGamesHandler(@NotNull Context context) {
        try {

            String authToken = context.header("Authorization");
            List<GameData> games  = gameService.listGames(authToken);

            context.status(200);
            context.contentType("application/json");
            context.result(gson.toJson(Map.of("games",games)));
        } catch (UnauthorizedResponse e) {
            context.status(401);
            context.result(gson.toJson(Map.of("message", "Error: unauthorized")));
        } catch (Exception e) {
            context.status(500);
            context.contentType("application/json");
            context.result(gson.toJson(Map.of("message", "Error: " + e.getMessage())));
        }
    }

    private void createGameHandler(@NotNull Context context) {
        try {

            context.status(200);
            context.contentType("application/json");
            context.result(gson.toJson(Map.of("gameID",gameID)));
        } catch (BadRequestResponse e) {
            context.status(400);
            context.result(gson.toJson(Map.of("message", "Error: bad request")));
        } catch (UnauthorizedResponse e) {
            context.status(401);
            context.result(gson.toJson(Map.of("message", "Error: unauthorized")));
        } catch (Exception e) {
            context.status(500);
            context.contentType("application/json");
            context.result(gson.toJson(Map.of("message", "Error: " + e.getMessage())));
        }
    }

    private void joinGameHandler(@NotNull Context context) {
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

}

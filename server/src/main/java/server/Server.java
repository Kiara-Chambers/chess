package server;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import io.javalin.*;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;
import service.ClearService;
import service.GameService;
import service.UserService;

import java.util.Map;

public class Server {

    private final Javalin javalin;
    UserService userService;
    GameService gameService;
    ClearService clearService;

    public Server() {
        var userDAO = new MemoryUserDAO();
        var gameDAO = new MemoryGameDAO();
        var authDAO = new MemoryAuthDAO();

        userService = new UserService(userDAO, authDAO);
        gameService = new GameService(gameDAO, authDAO);
        clearService = new ClearService(userDAO, authDAO, gameDAO);


        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        javalin.post("/user", this::registerHandler);

        javalin.post("/session", this::loginHandler);
        javalin.delete("/session", this::logoutHandler);

        javalin.get("/game", this::listGamesHandler);
        javalin.put("/game", this::joinGameHandler);
        javalin.post("/game", this::createGameHandler);

        javalin.delete("/db", this::clearHandler);

    }

    private void createGameHandler(@NotNull Context context) {
    }

    private void clearHandler(@NotNull Context context) {
        try {
            clearService.clear();
            context.status(200);
            context.json(Map.of());
        } catch (DataAccessException e) {
            context.status(500);
            context.json(Map.of("message", "Error:" + e.getMessage()));
        }
    }

    private void joinGameHandler(@NotNull Context context) {
    }

    private void listGamesHandler(@NotNull Context context) {
    }

    private void logoutHandler(@NotNull Context context) {
    }

    private void loginHandler(@NotNull Context context) {
    }

    private void registerHandler(@NotNull Context context) {
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

}

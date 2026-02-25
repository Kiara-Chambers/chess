package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import io.javalin.*;
import io.javalin.http.Context;
import model.UserData;
import org.jetbrains.annotations.NotNull;
import service.ClearService;
import service.GameService;
import service.UserService;

import java.util.Map;

public class Server {
    private final Gson gson = new Gson();
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
            context.result(gson.toJson(Map.of("message", "register worked")));
        }
        //TODO make these the right message
        catch(IllegalArgumentException e){
            context.status(400);
            context.result(gson.toJson(Map.of("message", "Error: " + e.getMessage())));
        }
        catch(RuntimeException e){
            context.status(403);
            context.result(gson.toJson(Map.of("message", "Error: " + e.getMessage())));
        }
        catch (Exception e) {
            context.status(500);
            context.contentType("application/json");
            context.result(gson.toJson(Map.of("message", "Error: " + e.getMessage())));
        }
    }

    private void loginHandler(@NotNull Context context) {
    }

    private void logoutHandler(@NotNull Context context) {
    }

    private void listGamesHandler(@NotNull Context context) {
    }

    private void createGameHandler(@NotNull Context context) {
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

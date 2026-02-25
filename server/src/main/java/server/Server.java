package server;

import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import io.javalin.*;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;
import service.ClearService;
import service.GameService;
import service.UserService;

public class Server {

    private final Javalin javalin;


    public Server() {
        var userDAO = new MemoryUserDAO();
        var gameDAO = new MemoryGameDAO();
        var authDAO = new MemoryAuthDAO();

        UserService userService= new UserService(userDAO,authDAO);
        GameService gameService = new GameService(gameDAO, authDAO);
        ClearService clearService = new ClearService(userDAO,authDAO,gameDAO);


        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        javalin.post("/user",this::registerHandler);

        javalin.post("/session",this::loginHandler);
        javalin.delete("/session",this::logoutHandler);

        javalin.get("/game",this::listGamesHandler);
        javalin.put("/game",this::joinGameHandler);
        javalin.post("/game",this::joinGameHandler);

        javalin.delete("/db",this::clearHandler);

    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

}

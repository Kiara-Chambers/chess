package server;

import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import io.javalin.*;
import service.ClearService;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        var clearService = new ClearService(new MemoryUserDAO(),new MemoryAuthDAO(), new MemoryGameDAO());
        javalin.delete("/db",ctx->{
            clearService.clear();
            ctx.status(200);
            ctx.json(new java.util.HashMap<>());
        });

        //TODO - make these actually do something
        javalin.post("/user",ctx->{
            ctx.status(200);
            ctx.json(new java.util.HashMap<>());
        });

        javalin.post("/session",ctx->{
            ctx.status(200);
            ctx.json(new java.util.HashMap<>());
        });
        javalin.delete("/session",ctx->{
            ctx.status(200);
            ctx.json(new java.util.HashMap<>());
        });

        javalin.get("/game",ctx->{
            ctx.status(200);
            ctx.json(new java.util.HashMap<>());
        });
        javalin.post("/game",ctx->{
            ctx.status(200);
            ctx.json(new java.util.HashMap<>());
        });
        javalin.delete("/game",ctx->{
            ctx.status(200);
            ctx.json(new java.util.HashMap<>());
        });

    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}

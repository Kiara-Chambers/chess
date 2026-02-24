package server;

import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import io.javalin.*;
import service.AuthService;
import service.ClearService;

import javax.security.sasl.AuthorizeCallback;

public class Server {

    private final Javalin javalin;
    AuthService authService;


    public Server() {
     /*   MemoryUserDAO userDAO = new MemoryUserDAO();
        MemoryAuthDAO authDAO = new MemoryAuthDAO();
        this.authService = new AuthService(userDAO,authDAO);
*/

        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        //var clearService = new ClearService(new MemoryUserDAO(),new MemoryAuthDAO(), new MemoryGameDAO());
        javalin.post("/user",this::registerHandler);
        javalin.post("/session",this::loginHandler);
        javalin.delete("/session",this::logoutHandler);


    }

    private void registerHandler(Context ctx){
        /*if (authorized(ctx)) {
            names.add(ctx.pathParam("name"));
            listNames(ctx);
        }*/
    }

    private void loginHandler(Context ctx){

    }

    private void logoutHandler(Context ctx){

    }


    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}

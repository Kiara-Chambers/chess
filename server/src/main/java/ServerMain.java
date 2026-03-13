import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import server.Server;

public class ServerMain {
    public static void main(String[] args) {
        try {
            DatabaseManager.createDatabase();
        } catch (DataAccessException e) {
            System.err.println("Failed to create database: " + e.getMessage());
            return;
        }

        Server server = new Server();
        server.run(8080);

        System.out.println("♕ 240 Chess Server");
    }
}
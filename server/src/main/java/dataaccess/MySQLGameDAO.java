package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MySQLGameDAO implements GameDAO {
    private Gson gson = new Gson();
    public MySQLGameDAO() throws DataAccessException {
        createGameTable();
    }

    public void clear() throws DataAccessException {
        var sql = "DELETE FROM game";
        try (Connection con = DatabaseManager.getConnection();
             PreparedStatement statement = con.prepareStatement(sql)) {
            statement.executeUpdate();
        } catch (Exception e) {
            throw new DataAccessException("Error clearing games: " + e.getMessage());
        }
    }

    public int createGame(GameData game) throws DataAccessException {
        var sql = "INSERT INTO game (gameID, whiteUsername, blackUsername, gameName, gameState) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = DatabaseManager.getConnection();
             PreparedStatement statement = con.prepareStatement(sql)) {

            statement.setInt(1, game.gameID());
            statement.setString(2, game.whiteUsername());
            statement.setString(3, game.blackUsername());
            statement.setString(4, game.gameName());
            statement.setString(5, gson.toJson(game.game()));
            statement.executeUpdate();

        } catch (Exception e) {
            throw new DataAccessException("Error creating game: " + e.getMessage());
        }
        return game.gameID();
    }

    public GameData getGame(int gameID) throws DataAccessException {
        var sql = "SELECT * FROM game WHERE gameID=?";
        try (Connection con = DatabaseManager.getConnection();
             PreparedStatement statement = con.prepareStatement(sql)) {

            statement.setInt(1, gameID);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    ChessGame theGame = gson.fromJson(rs.getString("gameState"), ChessGame.class);
                    return new GameData(
                            rs.getInt("gameID"),
                            rs.getString("whiteUsername"),
                            rs.getString("blackUsername"),
                            rs.getString("gameName"),
                            theGame
                    );
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("Error getting game: " + e.getMessage());
        }
        return null;
    }

    public List<GameData> listGames() throws DataAccessException {
        var sql = "SELECT * FROM game";
        List<GameData> games = new ArrayList<>();
        try (Connection con = DatabaseManager.getConnection();
             PreparedStatement statement = con.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                ChessGame theGame = gson.fromJson(rs.getString("gameState"), ChessGame.class);

                games.add(new GameData(
                        rs.getInt("gameID"),
                        rs.getString("whiteUsername"),
                        rs.getString("blackUsername"),
                        rs.getString("gameName"),
                        theGame

                ));
            }
        } catch (Exception e) {
            throw new DataAccessException("Error listing games: " + e.getMessage());
        }
        return games;
    }

    public void updateGame(GameData game) throws DataAccessException {
        var sql = "UPDATE game SET whiteUsername=?, blackUsername=?, gameName=?, gameState=? WHERE gameID=?";
        try (Connection con = DatabaseManager.getConnection();
             PreparedStatement statement = con.prepareStatement(sql)) {

            statement.setString(1, game.whiteUsername());
            statement.setString(2, game.blackUsername());
            statement.setString(3, game.gameName());
            statement.setString(4, gson.toJson(game.game()));
            statement.setInt(5, game.gameID());
            statement.executeUpdate();

        } catch (Exception e) {
            throw new DataAccessException("Error updating game: " + e.getMessage());
        }
    }

    public void createGameTable() throws DataAccessException {
        var createTableSQL = "CREATE TABLE IF NOT EXISTS game (" +
                "gameID INT PRIMARY KEY, " +
                "whiteUsername VARCHAR(255), " +
                "blackUsername VARCHAR(255), " +
                "gameName VARCHAR(255), " +
                "gameState TEXT" +
                ")";
        try (Connection con = DatabaseManager.getConnection();
             Statement statement = con.createStatement()) {
            statement.executeUpdate(createTableSQL);
        } catch (Exception e) {
            throw new DataAccessException("Error creating game table: " + e.getMessage());
        }
    }
}
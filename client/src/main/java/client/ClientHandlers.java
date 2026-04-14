package client;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import model.AuthData;

import java.util.Scanner;

import static client.ClientMain.*;
import static client.ChessUI.*;

public class ClientHandlers {
    public static void handleRegister(String username, String password, String email) throws Exception {
        try {
            ws = new WebSocketFacade(
                    "http://localhost:8080",new ClientMain()
            );
            AuthData authData = facade.register(username, password, email);
            authToken = authData.authToken();
            loggedIn = true;
            System.out.println("Registered and logged in as " + username);
            menu();
        } catch (Exception e) {
            System.out.println("error with registration");
            menu();
        }
    }

    public static void handleLogin(String username, String password) throws Exception {
        try {
            ws = new WebSocketFacade(
                    "http://localhost:8080",new ClientMain()
            );
            AuthData authData = facade.login(username, password);
            authToken = authData.authToken();
            loggedIn = true;
            System.out.println("Logged in as " + username + "!\nEnter Help to see what commands are available.");
            menu();
        } catch (Exception e) {
            System.out.println("username or password is wrong. Please reattempt the command.");
            menu();
        }

    }

    public static void handleLogout() throws Exception {
        try {
            facade.logout(authToken);
            authToken = null;
            loggedIn = false;
            System.out.println("You've logged out successfully");
            menu();
        } catch (Exception e) {
            System.out.println("failed to log out");
            menu();
        }
    }

    public static void handleListGames() throws Exception {
        try {
            lastGames = facade.listGames(authToken);
            if (lastGames == null || lastGames.isEmpty()) {
                System.out.println("There are currently no games");
            } else {

                for (int i = 0; i < lastGames.size(); i++) {
                    var map = (java.util.Map<?, ?>) lastGames.get(i);

                    String name = (String) map.get("gameName");
                    Object whiteObj = map.get("whiteUsername");
                    Object blackObj = map.get("blackUsername");

                    String white = (whiteObj != null) ? whiteObj.toString() : "None";
                    String black = (blackObj != null) ? blackObj.toString() : "None";

                    System.out.println((i + 1) + ". " + name
                            + "\n    White: " + white
                            + "\n    Black: " + black);
                }
            }

            menu();
        } catch (Exception e) {
            System.out.println("failed to list games");
            menu();
        }
    }

    public static void handleCreateGame(String gameName) throws Exception {
        try {
            facade.createGame(gameName, authToken);
            System.out.println("The game " + gameName + " has been created");
            menu();
        } catch (Exception e) {
            System.out.println("failed to create game");
            menu();
        }
    }

    public static void handleJoinGame(String id, String color) throws Exception {
        try {
            int index = Integer.parseInt(id) - 1;
            String team = color.toUpperCase();
            if (!team.equals("WHITE") && !team.equals("BLACK")) {
                System.out.println("Color must be WHITE or BLACK.");
                menu();
                return;
            }
            if (lastGames == null || lastGames.isEmpty()) {
                System.out.println("No games loaded. Run list before joining and pick a number from the list.");
                menu();
                return;
            }
            //gotta be within the list
            if (index < 0 || index >= lastGames.size()) {
                System.out.println("Invalid game number. Run create to make a new game. You can then run list to update available games.");
                menu();
                return;
            }
            var map = (java.util.Map<?, ?>) lastGames.get(index);

            Object idObj = map.get("gameID");
            int gameID;

            if (idObj instanceof Double) {
                gameID = ((Double) idObj).intValue();
            } else if (idObj instanceof Integer) {
                gameID = (Integer) idObj;
            } else {
                gameID = Integer.parseInt(idObj.toString());
            }

            String name = (String) map.get("gameName");
            facade.joinGame(gameID, color.toUpperCase(), authToken);
            pers = team;
            System.out.println("You've successfully joined the game " + name + "!");

            ws.connect(authToken,gameID);
            currentGameID =gameID;
            inGame = true;
            //chessBoard.resetBoard();
            menu();
        } catch (Exception e) {
            System.out.println("retry with valid input");
            menu();
        }
    }

    public static void handleObserveGame(String gameID) throws Exception {
        try {
            if (lastGames == null || lastGames.isEmpty()) {
                System.out.println("No games available. Run create and list first.");
                menu();
                return;
            }

            int index = Integer.parseInt(gameID) - 1;

            if (index < 0 || index >= lastGames.size()) {
                System.out.println("Invalid game number.");
                menu();
                return;
            }
            System.out.println("You are observing the game!");

            var map = (java.util.Map<?, ?>) lastGames.get(index);
            int actualGameID = ((Number) map.get("gameID")).intValue();

            ws.connect(authToken, actualGameID);
            currentGameID = actualGameID;
            inGame = true;
           // pers = "WHITE";
            chessBoard = new ChessBoard();
            chessBoard.resetBoard();

            //drawChessBoard(chessBoard,"WHITE");
            menu();
        } catch (Exception e) {
            System.out.println("failed to observe game");
            menu();
        }
    }
    public static void handleMakeMove(String sr, String sc, String er, String ec, String promo) throws Exception {
        try {
            //I made it be like the fancy chess notation
            int srInt = Integer.parseInt(sr);
            int scInt = letterToNum(sc);
            int erInt = Integer.parseInt(er);
            int ecInt = letterToNum(ec);

            if (scInt == 0 || ecInt == 0) {
                System.out.println("Error: invalid move");
                menu();
                return;
            }

            ChessPosition start = new ChessPosition(srInt, scInt);
            ChessPosition end = new ChessPosition(erInt, ecInt);
            ChessPiece.PieceType promotion = null;
            if (promo != null) {
                switch (promo) {
                    case "QUEEN" -> promotion = ChessPiece.PieceType.QUEEN;
                    case "ROOK" -> promotion = ChessPiece.PieceType.ROOK;
                    case "BISHOP" -> promotion = ChessPiece.PieceType.BISHOP;
                    case "KNIGHT" -> promotion = ChessPiece.PieceType.KNIGHT;
                }
            }

            ChessMove move = new ChessMove(start, end,promotion);

            ws.makeMove(authToken, currentGameID, move);
           // System.out.println("You've made a move!");

        } catch (Exception e) {
            System.out.println("Error: invalid move"+e.getMessage());
            menu();
        }
    }
    public static void handleLeaveGame() throws Exception {
        try {
            ws.leave(authToken, currentGameID);

            inGame = false;
            currentGameID = 0;
            chessBoard = new ChessBoard();
            chessBoard.resetBoard();

            System.out.println("You left the game.");

            menu();
        } catch (Exception e) {
            System.out.println("Error leaving game");
            menu();
        }
    }

    public static void handleResign() throws Exception {
        try {
            Scanner scanner = new Scanner(System.in);

            System.out.println("You sure you want to resign? (yes/no)");
            String input = scanner.nextLine().trim().toLowerCase();

            if (!input.equals("yes")) {
                System.out.println("Resign cancelled. Continue playing.");
                menu();
                return;
            }

            ws.resign(authToken, currentGameID);
            //System.out.println("You resigned.");

            menu();

        } catch (Exception e) {
            System.out.println("Error resigning from game");
            menu();
        }
    }
    public static void handleHighlightMoves(String r, String c) throws Exception {
        try {
            int row = Integer.parseInt(r);
            int col = letterToNum(c);

            if (col == 0) {
                System.out.println("Error: invalid column");
                menu();
                return;
            }

            ChessPosition pos = new ChessPosition(row, col);
            ChessPiece piece = chessBoard.getPiece(pos);

            if (piece == null) {
                System.out.println("There's no piece there...");
                menu();
                return;
            }
            var moves = currentGame.validMoves(pos);
            drawChessBoardWithHighlights(chessBoard, pers, pos, moves);
            menu();

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error showing valid moves");
            menu();
        }
    }
}

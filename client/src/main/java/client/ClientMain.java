package client;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import model.AuthData;
import model.GameData;
import ui.EscapeSequences;

import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import static java.lang.System.exit;

public class ClientMain {
    public static ServerFacade facade;
    public static boolean loggedIn = false;
    static Scanner scanner = new Scanner(System.in);

    static String authToken;
    static List<?> lastGames;

    public static void main(String[] args) throws Exception {
        facade = new ServerFacade(8080);
        System.out.println("♕ Welcome to 240 chess. Type Help to get started.");
        menu();
    }

    public static void menu() throws Exception {
        String userInput = scanner.next();

        userInput = userInput.toLowerCase();
        if (!loggedIn) {
            switch (userInput) {
                case "help":
                    help();
                    break;
                case "quit":
                    quit();
                    break;
                case "login":
                    handleLogin(scanner.next(), scanner.next());
                    break;
                case "register":
                    handleRegister(scanner.next(), scanner.next(), scanner.next());
                    break;
                default:
                    System.out.println("Please enter a valid option");
                    menu();
                    break;

            }
        } else {
            switch (userInput) {
                case "create":
                    handleCreateGame(scanner.next());
                    break;
                case "list":
                    handleListGames();
                    break;
                case "join":
                    handleJoinGame(scanner.next(),scanner.next());
                    break;
                case "observe":
                    handleObserveGame(scanner.next());
                    break;
                case "logout":
                    handleLogout();
                    break;
                case "quit":
                    quit();
                    break;
                case "help":
                    help();
                    break;
                default:
                    System.out.println("Please enter a valid option");
                    menu();
                    break;
            }
        }
    }

    public static void help() throws Exception {
        if (!loggedIn) {
            System.out.println("register <USERNAME> <PASSWORD> <EMAIL> - to create an account");
            System.out.println("login <USERNAME> <PASSWORD> - to play chess");
            System.out.println("quit - playing chess");
            System.out.println("help - list possible actions");
        } else {
            System.out.println("create <NAME> - a game");
            System.out.println("list - games");
            System.out.println("join <ID> [WHITE][BLACK] - a game");
            System.out.println("observe <ID> - a game");
            System.out.println("logout - when you're done");
            System.out.println("quit - playing chess");
            System.out.println("help - list possible actions");
        }
        menu();
    }

    public static void quit() {
        System.out.println("You have successfully quit the program.\nThanks for playing!");
        exit(0);
    }

    public static void handleRegister(String username, String password, String email) throws Exception {
        try {
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
            AuthData authData = facade.login(username, password);
            authToken = authData.authToken();
            loggedIn = true;
            System.out.println("Logged in as " + username+"!\nEnter Help to see what commands are available.");
            menu();
        } catch (Exception e) {
            System.out.println("username or password is wrong. Please reattempt the command.");
            menu();
        }

    }

    public static void handleLogout() throws Exception {
        try {
            facade.logout(authToken);
            authToken=null;
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
            if(lastGames.isEmpty()){
                System.out.println("There are currently no games");
            }else {

                for (int i = 0; i < lastGames.size(); i++) {
                    var map = (java.util.Map<?, ?>) lastGames.get(i);

                    String name = (String) map.get("gameName");
                    Object whiteObj = map.get("whiteUsername");
                    Object blackObj = map.get("blackUsername");

                    String white = (whiteObj != null) ? whiteObj.toString() : "null";
                    String black = (blackObj != null) ? blackObj.toString() : "null";

                    System.out.println((i + 1) + ". " + name
                            + "\n    White: " + white
                            + "\n    Black: " + black);
                }
            }

            menu();
        } catch (Exception e) {
            System.out.println("failed to list games");
            menu();
            //e.printStackTrace();
        }
    }

    public static void handleCreateGame(String gameName) throws Exception {
        try {
            facade.createGame(gameName,authToken);
            System.out.println("The game "+gameName+" has been created");
            menu();
        } catch (Exception e) {
            System.out.println("failed to create game");
            menu();
        }
    }
    public static void handleJoinGame(String id, String color) throws Exception {
        try {
            int index = Integer.parseInt(id) - 1;
            //List<?> gameList = facade.listGames(authToken);
            if (lastGames == null || lastGames.isEmpty()) {
                System.out.println("No games loaded. Run list before joining.");
                menu();
                return;
            }
            java.util.Map<?, ?> map = (java.util.Map<?, ?>) lastGames.get(index);

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

            System.out.println("You've successfully joined the game " + name + "!");

            drawChessBoard(color.toUpperCase());
            menu();
        } catch (Exception e) {
            System.out.println("retry with valid input");
            menu();
        }
    }
    public static void handleObserveGame(String gameID) throws Exception {
        try {
            System.out.println("You are observing the game!");
            drawChessBoard("WHITE");
            menu();
        } catch (Exception e) {
            System.out.println("failed to observe game");
            menu();
        }
    }
    public static String getPieceSymbol(ChessPiece piece) {
        if (piece == null) return EscapeSequences.EMPTY;

        if(piece.getTeamColor()== ChessGame.TeamColor.WHITE) {
            return switch (piece.getPieceType()) {
                case KING -> " ♔ ";
                case QUEEN -> " ♕ ";
                case ROOK -> " ♖ ";
                case BISHOP -> " ♗ ";
                case KNIGHT -> " ♘ ";
                case PAWN -> " ♙ ";
            };
        }else{
            return switch (piece.getPieceType()) {
                case KING -> " ♚ ";
                case QUEEN -> " ♛ ";
                case ROOK -> " ♜ ";
                case BISHOP -> " ♝ ";
                case KNIGHT -> " ♞ ";
                case PAWN -> " ♟ ";
            };
        }

    }

    public static void drawChessBoard(String perspective) {
        ChessBoard chessBoard = new ChessBoard();
        chessBoard.resetBoard();

        String[][] board = new String[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if ((i + j) % 2 == 0) {
                    board[i][j] = EscapeSequences.SET_BG_COLOR_BLACK;
                } else {
                    board[i][j] = EscapeSequences.SET_BG_COLOR_BLUE;
                }
            }
        }

        if (Objects.equals(perspective, "BLACK")) {
            for (int r = 7; r >= 0; r--) {
                System.out.print((8 - r) + " ");
                for (int c = 7; c >= 0; c--) {
                    ChessPiece piece = chessBoard.getPiece(new ChessPosition(r + 1, c + 1));
                    System.out.print(board[r][c] + getPieceSymbol(piece));                }
                System.out.println(EscapeSequences.RESET_BG_COLOR);
            }
            System.out.println("  h   g   f   e   d   c   b   a");

        } else if (Objects.equals(perspective, "WHITE")) {
            {
                for (int r = 7; r >= 0; r--) {
                    System.out.print((r + 1) + " ");
                    for (int c = 0; c < 8; c++) {
                        ChessPiece piece = chessBoard.getPiece(new ChessPosition(r + 1, c + 1));
                        System.out.print(board[r][c] + getPieceSymbol(piece));                    }
                    System.out.println(EscapeSequences.RESET_BG_COLOR);
                }
                System.out.println("  a   b   c   d   e   f   g   h");

            }

        }
    }
}

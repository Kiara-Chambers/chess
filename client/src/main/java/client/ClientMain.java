package client;

import chess.*;
import model.AuthData;
import ui.EscapeSequences;
import websocket.messages.ServerMessage;

import java.util.List;

import java.util.Scanner;

import static java.lang.System.exit;

public class ClientMain implements NotificationHandler{
    public static ServerFacade facade;
    public static boolean loggedIn = false;
    static Scanner scanner = new Scanner(System.in);

    static ChessBoard chessBoard = new ChessBoard();
    static ChessGame currentGame;
    static String pers = "WHITE";
    static int currentGameID;
    static boolean inGame = false;

    static String authToken;
    static List<?> lastGames;
    static WebSocketFacade ws;

    public static void main(String[] args) throws Exception {
        facade = new ServerFacade(8080);
        System.out.println("♕ Welcome to 240 chess. Type Help to get started.");
        menu();
    }

    public static void menu() throws Exception {

        String[] parts = scanner.nextLine().split(" ");
        String userInput = parts[0].toLowerCase();

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
                    if (parts.length != 3) {
                        System.out.println("Try again and use like this: login <username> <password>");
                        menu();
                    }
                    handleLogin(parts[1], parts[2]);
                    break;
                case "register":
                    if (parts.length != 4) {
                        System.out.println("Try again and use like this: register <username> <password> <email>");
                        menu();
                    }
                    handleRegister(parts[1], parts[2], parts[3]);
                    break;
                case "move":
                    if (parts.length != 5 && parts.length != 6) {
                        System.out.println("Usage: move <sr> <sc> <er> <ec> [QUEEN|ROOK|BISHOP|KNIGHT]");
                        menu();
                        break;
                    }

                    String promo = null;
                    if (parts.length == 6) {
                        promo = parts[5].toUpperCase();
                    }

                    handleMakeMove(parts[1], parts[2], parts[3], parts[4], promo);
                    break;
                default:
                    System.out.println("Please enter a valid option");
                    menu();
                    break;

            }
        } else if(!inGame) {
            switch (userInput) {
                case "create":
                    if (parts.length != 2) {
                        System.out.println("Try again and use like this: create <NAME>");
                        menu();
                    }
                    handleCreateGame(parts[1]);
                    break;
                case "list":
                    handleListGames();
                    break;
                case "join":
                    if (parts.length != 3) {
                        System.out.println("Try again and use like this: join <NUMBER> <WHITE|BLACK>");
                        menu();
                    }
                    handleJoinGame(parts[1], parts[2]);
                    break;
                case "observe":
                    if (parts.length != 2) {
                        System.out.println("Try again and use like this: observe <NUMBER>");
                        menu();
                    }
                    handleObserveGame(parts[1]);
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
        }else{
            switch (userInput) {
                case "move":
                    handleMakeMove(parts[1],parts[2],parts[3],parts[4],parts[5]);
                    break;
                case "leave":
                    handleLeaveGame();
                    break;
                case "resign":
                    handleResign();
                    break;
                case "highlight":
                    handleHighlightMoves(parts[1], parts[2]);
                    break;
                case "redraw":
                    drawChessBoard(pers);
                    break;
                case "help":
                    help();
                    break;
                default:
                    System.out.println("Invalid command");
                    menu();
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
            if (!inGame) {
                System.out.println("create <NAME> - a game");
                System.out.println("list - games");
                System.out.println("join <NUMBER> <WHITE or BLACK> - a game");
                System.out.println("observe <NUMBER> - a game");
                System.out.println("logout - when you're done");
                System.out.println("quit - playing chess");
                System.out.println("help - list possible actions");
            } else {
                System.out.println("move <START_ROW> <START_COL> <END_ROW> <END_COL> - make a chess move");
                System.out.println("leave - leave the current game");
                System.out.println("resign - forfeit the game");
                System.out.println("help - list possible actions");
            }
        }
        menu();
    }

    public static void quit() {
        System.out.println("You have successfully quit the program.\nThanks for playing!");
        exit(0);
    }

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
            //e.printStackTrace();
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
            chessBoard.resetBoard();
            //drawChessBoard(team);
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

            inGame= true;
            currentGameID= Integer.parseInt(gameID);
            ws.connect(authToken, Integer.parseInt(gameID));

            drawChessBoard("WHITE");
            menu();
        } catch (Exception e) {
            System.out.println("failed to observe game");
            menu();
        }
    }
    public static void handleMakeMove(String sr, String sc, String er, String ec, String promo) throws Exception {
        try {
            ChessPosition start = new ChessPosition(Integer.parseInt(sr), Integer.parseInt(sc));
            ChessPosition end = new ChessPosition(Integer.parseInt(er), Integer.parseInt(ec));

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

            menu();
        } catch (Exception e) {
            System.out.println("Error: invalid move");
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
            ws.resign(authToken, currentGameID);

            inGame = false;
            currentGameID = 0;

            chessBoard = new ChessBoard();
            chessBoard.resetBoard();

            menu();
        } catch (Exception e) {
            System.out.println("Error resigning from game");
            menu();
        }
    }
    public static void handleHighlightMoves(String r, String c) throws Exception {
        try {
            int row = Integer.parseInt(r);
            int col = Integer.parseInt(c);
            ChessPosition pos = new ChessPosition(row, col);
            ChessPiece piece = chessBoard.getPiece(pos);

            if (piece == null) {
                System.out.println("There's no piece there...");
                menu();
                return;
            }
            var moves = currentGame.validMoves(pos);
            drawChessBoardWithHighlights(pers, pos, moves);
            menu();

        } catch (Exception e) {
            System.out.println("Error showing valid  moves");
            menu();
        }
    }


    public static String getPieceSymbol(ChessPiece piece) {
        if (piece == null) {
            return EscapeSequences.EMPTY;
        }

        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            return switch (piece.getPieceType()) {
                case KING -> " ♔ ";
                case QUEEN -> " ♕ ";
                case ROOK -> " ♖ ";
                case BISHOP -> " ♗ ";
                case KNIGHT -> " ♘ ";
                case PAWN -> " ♙ ";
            };
        } else {
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
        if (chessBoard == null) {
            chessBoard = new ChessBoard();
            chessBoard.resetBoard();
        }

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


        if (perspective.equalsIgnoreCase("black")) {
            for (int r = 7; r >= 0; r--) {
                System.out.print((8 - r) + " ");
                for (int c = 0; c < 8; c++) {

                    int boardRow = 8 - r;
                    int boardCol = 8 - c;

                    ChessPiece piece = chessBoard.getPiece(new ChessPosition(boardRow, boardCol));
                    System.out.print(board[r][c] + getPieceSymbol(piece));
                }
                System.out.println(EscapeSequences.RESET_BG_COLOR);
            }
        } else if (perspective.equalsIgnoreCase("white")) {
            for (int r = 7; r >= 0; r--) {
                System.out.print((r + 1) + " ");
                for (int c = 0; c < 8; c++) {

                    int boardRow = r + 1;
                    int boardCol = c + 1;

                    ChessPiece piece = chessBoard.getPiece(new ChessPosition(boardRow, boardCol));
                    System.out.print(board[r][c] + getPieceSymbol(piece));
                }
                System.out.println(EscapeSequences.RESET_BG_COLOR);
            }
        }
        System.out.println("  h   g   f   e   d   c   b   a");


    }
    public static void drawChessBoardWithHighlights(String perspective, ChessPosition selected, java.util.Collection<ChessMove> moves) {
        java.util.Set<ChessPosition> highlights = new java.util.HashSet<>();
        for (ChessMove m : moves) {
            highlights.add(m.getEndPosition());
        }

        String[][] board = new String[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                ChessPosition pos = new ChessPosition(i + 1, j + 1);

                //the piece we wanna see the moves for
                if (pos.equals(selected)) {
                    board[i][j] = EscapeSequences.SET_BG_COLOR_YELLOW;
                } else if (highlights.contains(pos)) {
                    //and this is the color of possible moves
                    board[i][j] = EscapeSequences.SET_BG_COLOR_GREEN;
                } else if ((i + j) % 2 == 0) {
                    board[i][j] = EscapeSequences.SET_BG_COLOR_BLACK;
                } else {
                    board[i][j] = EscapeSequences.SET_BG_COLOR_BLUE;
                }
            }
        }

        if (perspective.equalsIgnoreCase("white")) {
            for (int r = 7; r >= 0; r--) {
                System.out.print((r + 1) + " ");
                for (int c = 0; c < 8; c++) {
                    ChessPiece piece = chessBoard.getPiece(new ChessPosition(r + 1, c + 1));
                    System.out.print(board[r][c] + getPieceSymbol(piece));
                }
                System.out.println(EscapeSequences.RESET_BG_COLOR);
            }
        } else {
            for (int r = 7; r >= 0; r--) {
                System.out.print((8 - r) + " ");
                for (int c = 0; c < 8; c++) {
                    ChessPiece piece = chessBoard.getPiece(new ChessPosition(8 - r, 8 - c));
                    System.out.print(board[r][c] + getPieceSymbol(piece));
                }
                System.out.println(EscapeSequences.RESET_BG_COLOR);
            }
        }

        System.out.println("  h   g   f   e   d   c   b   a");
    }

    @Override
    public void notify(ServerMessage message) {
        if (message.getServerMessageType() == ServerMessage.ServerMessageType.LOAD_GAME) {

            currentGame = message.getGame();
            chessBoard = currentGame.getBoard();

            drawChessBoard(pers);

        } else {
            System.out.println(message.getMessage());
        }
    }
}

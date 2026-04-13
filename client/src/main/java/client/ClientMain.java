package client;

import chess.*;
import model.AuthData;
import ui.EscapeSequences;
import websocket.messages.ServerMessage;

import java.util.List;

import java.util.Scanner;

import static client.ChessUI.*;
import static java.lang.System.exit;
import static client.ClientHandlers.*;

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
                    if (parts.length != 5 && parts.length != 6) {
                        System.out.println("Usage: move <sr> <sc> <er> <ec> [QUEEN|ROOK|BISHOP|KNIGHT]");
                        menu();
                        return;
                    }

                    String promo = (parts.length == 6) ? parts[5].toUpperCase() : null;

                    handleMakeMove(parts[1], parts[2], parts[3], parts[4], promo);
                    break;
                case "leave":
                    handleLeaveGame();
                    break;
                case "resign":
                    handleResign();
                    break;
                case "highlight":
                    if (parts.length < 3) {
                        System.out.println("Try again and use like this: highlight <ROW> <COL>");
                        menu();
                        return;
                    }
                    handleHighlightMoves(parts[1], parts[2]);
                    break;
                case "redraw":
                    drawChessBoard(chessBoard,pers);
                    menu();
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
                System.out.println("highlight <ROW> <COL> - show legal moves for a piece");
                System.out.println("redraw - redraw the chess board");
                System.out.println("help - list possible actions");
            }
        }
        menu();
    }

    public static void quit() {
        System.out.println("You have successfully quit the program.\nThanks for playing!");
        exit(0);
    }



    @Override
    public void notify(ServerMessage message) {
        System.out.println("WS IN: " + message.getServerMessageType());

        if (message.getServerMessageType() == ServerMessage.ServerMessageType.LOAD_GAME) {

            currentGame = message.getGame();

            if (currentGame == null) {
                System.out.println("ERROR: game is null in LOAD_GAME");
                return;
            }

            chessBoard = currentGame.getBoard();

            drawChessBoard(chessBoard,pers);
        } else {
            System.out.println(message.getMessage());
        }
    }
}

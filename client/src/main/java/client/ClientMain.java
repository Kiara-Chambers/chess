package client;

import chess.*;
import model.AuthData;
import ui.EscapeSequences;

import java.util.Locale;
import java.util.Objects;
import java.util.Scanner;

public class ClientMain {
    public static ServerFacade facade;
    public static boolean loggedIn = false;
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws Exception {
        facade = new ServerFacade(8080);
        System.out.println("♕ Welcome to 240 chess. Type Help to get started.");
        //drawChessBoard("WHITE");
        //drawChessBoard("BLACK");
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
                    System.out.println("Please enter a valid option\n");
                    menu();
                    break;

            }
        } else {
            switch (userInput) {
                case "help":
                    help();
                    break;
                case "logout":
                    handleLogout();
                    break;
                case "createGame":
                    handleCreateGame();
                    break;
                case "listGames":
                    handleListGames();
                    break;
                case "playGame":
                    handleJoinGame();
                    break;
                case "observe":
                    handleObserveGame();
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
    }

    public static void handleRegister(String username, String password, String email) throws Exception {
        try {
            AuthData authData = facade.register(username, password, email);
            loggedIn = true;
            System.out.println("Registered and logged in as " + username);
            menu();
        } catch (Exception e) {
            System.out.println("error with registration");
        }
    }

    public static void handleLogin(String username, String password) throws Exception {
        try {
            facade.login(username, password);
            loggedIn = true;
            System.out.println("Logged in as" + username);
            menu();
        } catch (Exception e) {
            System.out.println("username or password is wrong");
        }

    }

    public static void handleLogout(String authToken) throws Exception {
        try {
            facade.logout(authToken);
            loggedIn = false;
            System.out.println("You've logged out successfully");
            menu();
        } catch (Exception e) {
            System.out.println("failed to log out");
        }
    }

    public static void handleListGames() {
        //listGames();
    }

    public static void handleCreateGame() {
        //createGame();
    }

    public static void handleJoinGame() {
        //joinGame();
    }

    public static void handleObserveGame() {
    }


    public static void drawChessBoard(String perspective) {
        String[][] board = new String[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if ((i + j) % 2 == 0) {
                    board[i][j] = EscapeSequences.SET_BG_COLOR_BLACK + EscapeSequences.EMPTY;
                } else {
                    board[i][j] = EscapeSequences.SET_BG_COLOR_BLUE + EscapeSequences.EMPTY;
                }
            }
        }

        if (Objects.equals(perspective, "BLACK")) {
            for (int r = 7; r >= 0; r--) {
                System.out.print((8 - r) + " ");
                for (int c = 7; c >= 0; c--) {
                    System.out.print(board[r][c]);
                }
                System.out.println(EscapeSequences.RESET_BG_COLOR);
            }
            System.out.println("  h   g   f   e   d   c   b   a");

        } else if (Objects.equals(perspective, "WHITE")) {
            {
                for (int r = 7; r >= 0; r--) {
                    System.out.print((r + 1) + " ");
                    for (int c = 0; c < 8; c++) {
                        System.out.print(board[r][c]);
                    }
                    System.out.println(EscapeSequences.RESET_BG_COLOR);
                }
                System.out.println("  a   b   c   d   e   f   g   h");

            }

        }
    }
}

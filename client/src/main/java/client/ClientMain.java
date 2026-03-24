package client;

import chess.*;
import ui.EscapeSequences;

import java.util.Objects;

public class ClientMain {
    public static ServerFacade facade;
    public static boolean loggedIn = false;

    public static void main(String[] args) {
        facade = new ServerFacade(8080);
        System.out.println("♕ Welcome to 240 chess. Type Help to get started.");
        //drawChessBoard("WHITE");
        //drawChessBoard("BLACK");

        //menu
    }


    public static void menu() {
    }

    public static void help() {
        if(!loggedIn) {
            System.out.println("register <USERNAME> <PASSWORD> <EMAIL> - to create an account");
            System.out.println("login <USERNAME> <PASSWORD> - to play chess");
            System.out.println("quit - quit playing chess");
            System.out.println("help - list possible actions");
        }else{
            System.out.println("create <NAME> - a game");
            System.out.println("list - games");
            System.out.println("join <ID> [WHITE][BLACK] - a game");
            System.out.println("observe <ID> - a game");
            System.out.println("logout - when you're done");
            System.out.println("quit - playing chess");
            System.out.println("help - list possible actions");
        }
    }

    public static void register() {
        System.out.println("Enter your username:");
        System.out.println("Enter your password:");
        System.out.println("Enter your email:");
        register(username,password, email);
        System.out.println("Registered and logged in as NAME");
    }

    public static void login() {
        //login  and add name
        login();
        System.out.println("Logged in as NAME");

    }

    public static void logout() {
        logout();
    }

    public static void listGames() {
        listGames();
    }

    public static void createGame() {
        createGame();
    }

    public static void joinGame() {
        joinGame();
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

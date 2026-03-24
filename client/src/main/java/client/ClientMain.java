package client;

import chess.*;
import model.AuthData;
import ui.EscapeSequences;

import java.util.Objects;
import java.util.Scanner;

public class ClientMain {
    public static ServerFacade facade;
    public static boolean loggedIn = false;
     Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        facade = new ServerFacade(8080);
        System.out.println("♕ Welcome to 240 chess. Type Help to get started.");
        drawChessBoard("WHITE");
        //drawChessBoard("BLACK");

        //menu
    }


    public static void menu() {
    }

    public void help() {
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

    public void handleRegister() throws Exception {
        System.out.println("Enter your username:");
        String username = scanner.nextLine();
        System.out.println("Enter your password:");
        String password = scanner.nextLine();
        System.out.println("Enter your email:");
        String email = scanner.nextLine();

        AuthData authData = facade.register(username,password, email);
        loggedIn=true;

        System.out.println("Registered and logged in as NAME");

    }

    public  void handleLogin() {
        //login  and add name
        //login();
        System.out.println("Logged in as NAME");

    }

    public void handleLogout() {
        //logout();
    }

    public void handleListGames() {
        //listGames();
    }

    public void handleCreateGame() {
        //createGame();
    }

    public void handleJoinGame() {
        //joinGame();
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

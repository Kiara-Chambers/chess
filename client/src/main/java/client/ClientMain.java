package client;

import chess.*;
import ui.EscapeSequences;

import java.util.Objects;

public class ClientMain {
    public static ServerFacade facade;
    public static void main(String[] args) {
        facade = new ServerFacade(8080);
        System.out.println("♕ 240 Chess Client");
        drawChessBoard("WHITE");
        //drawChessBoard("BLACK");

        //menu
    }

    public static void menu(){}

    public static void register(){}
    public static void login(){}
    public static void logout(){}
    public static void listGames(){}
    public static void createGame(){}
    public static void joinGame(){}

    public static void drawChessBoard(String perspective){
        String[][] board = new String[8][8];
        for (int i=0;i<8;i++){
            for(int j =0;j<8;j++){
                if((i+j)%2==0){
                    board[i][j] = EscapeSequences.SET_BG_COLOR_BLACK+EscapeSequences.EMPTY;
                }else{
                    board[i][j] = EscapeSequences.SET_BG_COLOR_BLUE+EscapeSequences.EMPTY;
                }
            }

        }


        if (Objects.equals(perspective, "BLACK")) {
            for (int r =7; r >= 0; r--) {
                System.out.print((8-r) + " ");
                for (int c=7; c >= 0; c--) {
                    System.out.print(board[r][c]);
                }
                System.out.println(EscapeSequences.RESET_BG_COLOR);
            }
            System.out.println("  h   g   f   e   d   c   b   a");

        } else {
            for (int r =7; r >= 0; r--) {
                System.out.print((r + 1) + " ");
                for (int c=0; c < 8; c++) {
                    System.out.print(board[r][c]);
                }
                System.out.println(EscapeSequences.RESET_BG_COLOR);
            }
            System.out.println("  a   b   c   d   e   f   g   h");

        }

    }
}

package client;

import chess.*;

public class ClientMain {
    public static ServerFacade facade;
    public static void main(String[] args) {
        facade = new ServerFacade(8080);
        System.out.println("♕ 240 Chess Client");

        //menu
    }

    public static void menu(){}
    
    public static void register(){}
    public static void login(){}
    public static void logout(){}
    public static void listGames(){}
    public static void createGame(){}
    public static void joinGame(){}

    public static void drawChessBoard(boolean perspective){

    }
}

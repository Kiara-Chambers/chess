package client;

import chess.*;

public class ClientMain {
    public static ServerFacade facade;
    public static void main(String[] args) {
        //var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        facade = new ServerFacade(8080);
        System.out.println("♕ 240 Chess Client");

        //menu
    }

    public static void menu(){}
    


    public static void drawChessBoard(boolean perspective){

    }
}

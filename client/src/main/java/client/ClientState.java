package client;

import chess.ChessBoard;
import chess.ChessGame;

import java.util.List;

public class ClientState {
    static ChessBoard chessBoard = new ChessBoard();
    static ChessGame currentGame;
    static String pers = "WHITE";
    static int currentGameID;
    static boolean inGame = false;

    static String authToken;
    static List<?> lastGames;
    static WebSocketFacade ws;
}

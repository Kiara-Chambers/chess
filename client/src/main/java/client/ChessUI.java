package client;

import chess.*;
import ui.EscapeSequences;

public class ChessUI {
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

    public static int letterToNum(String letter){
        letter = letter.toLowerCase();
        return switch (letter) {
            case "a" -> 1;
            case "b" -> 2;
            case "c" -> 3;
            case "d" -> 4;
            case "e" -> 5;
            case "f" -> 6;
            case "g" -> 7;
            case "h" -> 8;
            default -> 0;
        };
    }
    public static void printBottomLabels(String perspective) {
        if (perspective.equalsIgnoreCase("black")) {
            System.out.println("  h   g   f   e   d   c   b   a");
        } else {
            //white and the observer...
            System.out.println("  a   b   c   d   e   f   g   h");
        }
    }


    public static void drawChessBoard(ChessBoard chessBoard,String perspective) {
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
        printBottomLabels(perspective);


    }
    public static void drawChessBoardWithHighlights(ChessBoard chessBoard,String perspective,
                                                    ChessPosition selected,
                                                    java.util.Collection<ChessMove> moves) {

        java.util.Set<ChessPosition> highlights = new java.util.HashSet<>();
        for (ChessMove m : moves) {
            highlights.add(m.getEndPosition());
        }

        String[][] board = new String[8][8];
        boolean blackView = perspective.equalsIgnoreCase("black");

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {

                ChessPosition pos = blackView
                        ? new ChessPosition(row + 1, 8 - col)
                        : new ChessPosition(8 - row, col + 1);

                boolean isLightSquare = (pos.getRow() + pos.getColumn()) % 2 == 0;

                if (pos.equals(selected)) {
                    board[row][col] = EscapeSequences.SET_BG_COLOR_YELLOW;
                } else if (highlights.contains(pos)) {
                    board[row][col] = EscapeSequences.SET_BG_COLOR_GREEN;
                } else {
                    board[row][col] = isLightSquare
                            ? EscapeSequences.SET_BG_COLOR_BLACK
                            : EscapeSequences.SET_BG_COLOR_BLUE;
                }
            }
        }

        for (int row = 0; row < 8; row++) {
            System.out.print((blackView ? row + 1 : 8 - row) + " ");

            for (int col = 0; col < 8; col++) {

                ChessPosition pos = blackView
                        ? new ChessPosition(row + 1, 8 - col)
                        : new ChessPosition(8 - row, col + 1);

                ChessPiece piece = chessBoard.getPiece(pos);
                System.out.print(board[row][col] + getPieceSymbol(piece));
            }

            System.out.println(EscapeSequences.RESET_BG_COLOR);
        }

        printBottomLabels(perspective);
    }

}

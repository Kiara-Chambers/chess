package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    TeamColor currentTeamColor = TeamColor.WHITE;
    ChessBoard currentBoard = new ChessBoard();

    public ChessGame() {
        currentBoard.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return currentTeamColor;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        currentTeamColor = team;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return currentTeamColor == chessGame.currentTeamColor && Objects.equals(currentBoard, chessGame.currentBoard);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currentTeamColor, currentBoard);
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    //Returns the opposite of the color whose turn it is
    public TeamColor oppositeTeamColor(TeamColor color) {
        if (color == TeamColor.WHITE) {
            return TeamColor.BLACK;
        } else {
            return TeamColor.WHITE;
        }
    }

    public ChessBoard boardAfterMoving(ChessBoard board, ChessMove move){
        //try the move
        //set up the temp board same as the normal one
        ChessBoard tempBoard = new ChessBoard(board);
        ChessPiece piece = board.getPiece(move.getStartPosition());

        //If the move is in the piece's possible moves list
        if (move.getPromotionPiece() != null) {
            tempBoard.addPiece(move.getEndPosition(), new ChessPiece(currentTeamColor, move.getPromotionPiece()));
        } else {
            tempBoard.addPiece(move.getEndPosition(), piece);
        }

        //set the start spot to null
        tempBoard.addPiece(move.getStartPosition(), null);
        return tempBoard;
    }
    private boolean kingIsLeftInCheck(ChessMove move){
        ChessBoard saved = currentBoard;
        currentBoard = boardAfterMoving(currentBoard,move);
        boolean stillChecked = isInCheck(currentTeamColor);
        currentBoard = saved;
        return stillChecked;
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = currentBoard.getPiece(startPosition);
        Collection<ChessMove> moveList = piece.pieceMoves(currentBoard, startPosition);

        //prevent a king from moving into check
        if (piece.getPieceType() == ChessPiece.PieceType.KING) {
            for (ChessMove move : moveList.toArray(new ChessMove[0])) {
                if (kingIsLeftInCheck(move)) {
                    moveList.remove(move);
                }
            }
        }
        return moveList;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        //piece is the piece at the first part of the attempted move
        ChessPiece piece = currentBoard.getPiece(move.getStartPosition());

        if (piece == null) {
            throw new InvalidMoveException();
        }
        if (currentTeamColor != piece.getTeamColor()) {
            throw new InvalidMoveException();
        }

        //try the move
        //set up the temp board same as the normal one
        ChessBoard tempBoard = new ChessBoard(currentBoard);
        //If the move is in the piece's possible moves list
        if (piece.pieceMoves(currentBoard, move.getStartPosition()).contains(move)) {
            if (move.getPromotionPiece() != null) {
                tempBoard.addPiece(move.getEndPosition(), new ChessPiece(currentTeamColor, move.getPromotionPiece()));
            } else {
                tempBoard.addPiece(move.getEndPosition(), piece);
            }

            //set the start spot to null
            tempBoard.addPiece(move.getStartPosition(), null);

        } else {
            throw new InvalidMoveException();
        }

        //test the actual board if it looked like the temp one
        ChessBoard saved = currentBoard;
        currentBoard = tempBoard;
        boolean stillChecked = isInCheck(currentTeamColor);
        currentBoard = saved;

        //if it's still in check:
        if (stillChecked) {
            throw new InvalidMoveException();
        }


        //If the move is in the piece's possible moves list
        if (piece.pieceMoves(currentBoard, move.getStartPosition()).contains(move)) {
            //change the end spot of the move to the piece from the start
            if (move.getPromotionPiece() != null) {
                currentBoard.addPiece(move.getEndPosition(), new ChessPiece(currentTeamColor, move.getPromotionPiece()));
            } else {
                currentBoard.addPiece(move.getEndPosition(), piece);
            }
            //set the start spot to null
            currentBoard.addPiece(move.getStartPosition(), null);

            setTeamTurn(oppositeTeamColor(currentTeamColor));
        } else {
            throw new InvalidMoveException();
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        //find and assign king
        ChessPiece kingPiece;
        ChessPosition kingPosition = null;
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition piecePosition = new ChessPosition(i, j);
                ChessPiece piece = currentBoard.getPiece(piecePosition);
                if (piece != null && piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == teamColor) {
                    //kingPiece = piece;
                    kingPosition = new ChessPosition(i, j);
                }
            }
        }

        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition piecePosition = new ChessPosition(i, j);
                ChessPiece piece = currentBoard.getPiece(piecePosition);

                //check if the piece is on the other team
                if (piece != null && piece.getTeamColor() == oppositeTeamColor(teamColor)) {
                    //check if it can attack the king.
                    if (kingPosition != null && piece.pieceMoves(currentBoard, piecePosition).contains(new ChessMove(piecePosition, kingPosition, null))) {
                        return true;
                    }
                }
            }

        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        currentBoard = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return currentBoard;
    }
}

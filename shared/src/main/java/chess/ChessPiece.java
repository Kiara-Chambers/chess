package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private ChessGame.TeamColor pieceColor;
    private PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece piece = board.getPiece(myPosition);
        List<ChessMove> moveList = new ArrayList<>();
        //Bishop
        if (piece.getPieceType() == PieceType.BISHOP) {
            for (int i = 1; i <= 8; i++) {
                for (int j = 1; j <= 8; j++) {
                    if (myPosition.getRow() == i && myPosition.getColumn() == j) {
                        continue;
                    }
                    if ((myPosition.getRow() - i) == myPosition.getColumn() - j) {
                        if (board.getPiece(new ChessPosition(i, j)) != null) {
                            continue;
                        }
                        moveList.add(new ChessMove(myPosition, new ChessPosition(i, j), null));
                    }
                    if ((myPosition.getRow() - i) == -(myPosition.getColumn() - j)) {
                        if (board.getPiece(new ChessPosition(i, j)) != null) {
                            continue;
                        }
                        moveList.add(new ChessMove(myPosition, new ChessPosition(i, j), null));
                    }
                }

            }
        }
        //King
        if (piece.getPieceType() == PieceType.KING) {
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    //King can't move to the pos it starts at
                    if (i == 0 && j == 0) {
                        continue;
                    }
                    ChessPosition newPos = new ChessPosition(i + myPosition.getRow(), j + myPosition.getColumn());
                    //Can't move off the grid
                    if(newPos.getColumn()>8 || newPos.getRow()>8){
                        continue;
                    }
                    ChessMove newSpot = new ChessMove(myPosition, newPos, null);
                    //You can move on top of enemies
                    if(board.getPiece(newPos) != null && board.getPiece(newPos).getTeamColor().equals(piece.getTeamColor())){
                        continue;
                    }
                    moveList.add(newSpot);
                }
            }
        }
        //Knight
        if (piece.getPieceType() == PieceType.KNIGHT) {
            for (int i = 1; i <= 8; i++) {

            }
        }
        //Pawn
        if (piece.getPieceType() == PieceType.PAWN) {
            for (int i = 1; i <= 8; i++) {

            }
        }
        //Queen
        if (piece.getPieceType() == PieceType.QUEEN) {
            for (int i = 1; i <= 8; i++) {

            }
        }
        //Rook
        if (piece.getPieceType() == PieceType.ROOK) {
            for (int i = 1; i <= 8; i++) {
                ChessMove move1 = new ChessMove(myPosition, new ChessPosition(myPosition.getRow(), i), null);
                ChessMove move2 = new ChessMove(myPosition, new ChessPosition(i, myPosition.getColumn()), null);
                if (!(i == myPosition.getColumn())) {
                    moveList.add(move1);
                }
                if (!(i == myPosition.getRow())) {
                    moveList.add(move2);
                }
            }
        }


        return moveList;
    }
}

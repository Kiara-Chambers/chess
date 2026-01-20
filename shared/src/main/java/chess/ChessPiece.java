package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
        //Bishop
        if (piece.getPieceType() == PieceType.BISHOP) {
            List<ChessMove> bishopList = new ArrayList<>();
            for (int i = 1; i <= 8; i++) {
                for (int j = 1; j <= 8; j++) {
                    if (myPosition.getRow() == i && myPosition.getColumn() == j) {
                        continue;
                    }
                    if ((myPosition.getRow() - i) == myPosition.getColumn() - j) {
                        if (board.getPiece(new ChessPosition(i, j)) != null) {
                            continue;
                        }
                        bishopList.add(new ChessMove(myPosition, new ChessPosition(i, j), null));
                    }
                    if ((myPosition.getRow() - i) == -(myPosition.getColumn() - j)) {
                        if (board.getPiece(new ChessPosition(i, j)) != null) {
                            continue;
                        }
                        bishopList.add(new ChessMove(myPosition, new ChessPosition(i, j), null));
                    }
                }

            }
            return bishopList;
        }
        //King
        if (piece.getPieceType() == PieceType.KING) {
            List<ChessMove> kingList = new ArrayList<>();
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    if (i == 0 && j == 0) {
                        continue;
                    }
                    kingList.add(new ChessMove(myPosition, new ChessPosition(i + myPosition.getRow(), j + myPosition.getColumn()), null));
                }
            }
            return kingList;
        }
        //Rook
        if (piece.getPieceType() == PieceType.ROOK) {
            List<ChessMove> rookList = new ArrayList<>();
            for (int i =1; i<=8;i++){
                ChessMove move1 = new ChessMove(myPosition, new ChessPosition(myPosition.getRow(), i), null);
                ChessMove move2 = new ChessMove(myPosition, new ChessPosition(i, myPosition.getColumn()), null);
                if (!(i == myPosition.getColumn())){
                    rookList.add(move1);
                }
                if (!(i == myPosition.getRow())){
                    rookList.add(move2);
                }
            }
            return rookList;
        }

        return List.of();
    }
}

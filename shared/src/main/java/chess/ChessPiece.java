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

    public ChessPiece(ChessGame.TeamColor pieceColor, PieceType type) {
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
                    //Can't move to the pos you start at
                    if (myPosition.getRow() == i && myPosition.getColumn() == j) {
                        continue;
                    }

                    ChessPosition newPos = new ChessPosition(i, j);
                    ChessMove newSpot = new ChessMove(myPosition, newPos, null);

                    //diagonal movement
                    if ((myPosition.getRow() - i) == myPosition.getColumn() - j) {
                        moveList.add(newSpot);
                    }
                    if ((myPosition.getRow() - i) == -(myPosition.getColumn() - j)) {
                        moveList.add(newSpot);
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
                    if (newPos.getColumn() > 8 || newPos.getRow() > 8) {
                        continue;
                    }
                    ChessMove newSpot = new ChessMove(myPosition, newPos, null);
                    //You can't move on top of your own team
                    if (board.getPiece(newPos) != null && board.getPiece(newPos).getTeamColor().equals(piece.getTeamColor())) {
                        continue;
                    }
                    moveList.add(newSpot);
                }
            }
        }
        //Knight
        if (piece.getPieceType() == PieceType.KNIGHT) {


        }
        //Pawn
        if (piece.getPieceType() == PieceType.PAWN) {
            //Moving forward 1
            ChessPosition newPos;
            if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                newPos = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn());
            } else {
                newPos = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn());
            }
            ChessMove newSpot = new ChessMove(myPosition, newPos, null);

            //move forward 1 if the space in front of pawn is empty
            if (board.getPiece(newPos) == null) {
                moveList.add(newSpot);
            }

            //Moving forward 2 at the start
            ChessPosition newPosStart;
            boolean isStartingPos = false;
            if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                newPosStart = new ChessPosition(myPosition.getRow() + 2, myPosition.getColumn());
                if (myPosition.getRow() == 2) {
                    isStartingPos = true;
                }
            } else {
                newPosStart = new ChessPosition(myPosition.getRow() - 2, myPosition.getColumn());
                if (myPosition.getRow() == 7) {
                    isStartingPos = true;
                }
            }
            ChessMove newSpotStart = new ChessMove(myPosition, newPosStart, null);

            //move forward 2 if the spaces in front of pawn are empty
            if (board.getPiece(newPosStart) == null && board.getPiece(newPos) == null && isStartingPos) {
                moveList.add(newSpotStart);
            }

            //Moving Diagonally to capture
            ChessPosition newPos2, newPos3, newPos4, newPos5;
            //white moves diagonally to capture black
            if (myPosition.getRow() + 1 <= 8 && myPosition.getColumn() + 1 <= 8) {
                newPos2 = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 1);
                if (board.getPiece(newPos2) != null && board.getPiece(newPos2).getTeamColor() == ChessGame.TeamColor.BLACK) {
                    moveList.add(new ChessMove(myPosition, newPos2, null));
                }
            }

            if (myPosition.getRow() + 1 <= 8 && myPosition.getColumn() - 1 >= 1) {
                newPos3 = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() - 1);
                if (board.getPiece(newPos3) != null && board.getPiece(newPos3).getTeamColor() == ChessGame.TeamColor.BLACK) {
                    moveList.add(new ChessMove(myPosition, newPos3, null));
                }
            }

            //black moves diagonally to capture white
            if (myPosition.getRow() - 1 >= 1 && myPosition.getColumn() + 1 <= 8) {

                newPos4 = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() + 1);
                if (board.getPiece(newPos4) != null && board.getPiece(newPos4).getTeamColor() == ChessGame.TeamColor.WHITE) {
                    moveList.add(new ChessMove(myPosition, newPos4, null));
                }
            }
            if (myPosition.getRow() - 1 >= 1 && myPosition.getColumn() - 1 >= 1) {
                newPos5 = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() - 1);
                if (board.getPiece(newPos5) != null && board.getPiece(newPos5).getTeamColor() == ChessGame.TeamColor.WHITE) {
                    moveList.add(new ChessMove(myPosition, newPos5, null));
                }
            }
        }
        //Queen
        if (piece.getPieceType() == PieceType.QUEEN) {
            for (int i = 1; i <= 8; i++) {

            }
        }
        //Rook
        if (piece.getPieceType() == PieceType.ROOK) {
            //row move right
            for (int i = myPosition.getRow() + 1; i <= 8; i++) {
                ChessPosition newPos = new ChessPosition(i, myPosition.getColumn());
                ChessMove moveRight = new ChessMove(myPosition, newPos, null);
                //If the piece is the same color you cant move there
                if (board.getPiece(newPos) != null && board.getPiece(newPos).getTeamColor().equals(piece.getTeamColor())) {
                    break;
                }
                //If the piece is a different color, capture, but no more moving that way
                else if (board.getPiece(newPos) != null && !board.getPiece(newPos).getTeamColor().equals(piece.getTeamColor())) {
                    moveList.add(moveRight);
                    break;
                } else {
                    moveList.add(moveRight);
                }

            }
            //row move left
            for (int i = myPosition.getRow() - 1; i >= 1; i--) {
                ChessPosition newPos = new ChessPosition(i, myPosition.getColumn());
                ChessMove moveLeft = new ChessMove(myPosition, newPos, null);
                //If the piece is the same color you cant move there
                if (board.getPiece(newPos) != null && board.getPiece(newPos).getTeamColor().equals(piece.getTeamColor())) {
                    break;
                }
                //If the piece is a different color, capture, but no more moving that way
                else if (board.getPiece(newPos) != null && !board.getPiece(newPos).getTeamColor().equals(piece.getTeamColor())) {
                    moveList.add(moveLeft);
                    break;
                } else {
                    moveList.add(moveLeft);
                }
            }

            //column move up
            for (int i = myPosition.getColumn() + 1; i <= 8; i++) {

                ChessPosition newPos = new ChessPosition(myPosition.getRow(), i);
                ChessMove moveUp = new ChessMove(myPosition, newPos, null);
                //If the piece is the same color you cant move there
                if (board.getPiece(newPos) != null && board.getPiece(newPos).getTeamColor().equals(piece.getTeamColor())) {
                    break;
                }
                //If the piece is a different color, capture, but no more moving that way
                else if (board.getPiece(newPos) != null && !board.getPiece(newPos).getTeamColor().equals(piece.getTeamColor())) {
                    moveList.add(moveUp);
                    break;
                } else {
                    moveList.add(moveUp);
                }
            }

            //column move down
            for (int i = myPosition.getColumn() - 1; i >= 1; i--) {
                ChessPosition newPos = new ChessPosition(myPosition.getRow(), i);
                ChessMove moveDown = new ChessMove(myPosition, newPos, null);
                //If the piece is the same color you cant move there
                if (board.getPiece(newPos) != null && board.getPiece(newPos).getTeamColor().equals(piece.getTeamColor())) {
                    break;
                }
                //If the piece is a different color, capture, but no more moving that way
                else if (board.getPiece(newPos) != null && !board.getPiece(newPos).getTeamColor().equals(piece.getTeamColor())) {
                    moveList.add(moveDown);
                    break;
                } else {
                    moveList.add(moveDown);
                }
            }
        }


        return moveList;
    }
}

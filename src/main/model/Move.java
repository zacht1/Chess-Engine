package model;

import enumerations.MoveType;

import java.awt.*;
import java.util.Objects;

import static java.lang.Math.abs;

public class Move {
    private Board board;

    private int startX;
    private int startY;
    private int endX;
    private int endY;

    private int movedPiece;
    private int capturedPiece;

    private boolean check = false;
    private boolean checkmate = false;

    private boolean isWhiteMove;
    private boolean computerMove;
    private boolean isPromotionMove = false;
    private MoveType moveType;

    /**
     * Constructs a new chess move with the specified starting square, ending square, move piece, and captured piece
     * Call helper methods to determine move type
     * Move may or may not be legal
     */
    public Move(Board board, int startX, int startY, int endX, int endY) {
        this.board = board;
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;

        this.movedPiece = board.getPiece(startX, startY);
        this.capturedPiece = board.getPiece(endX, endY);

        this.moveType = MoveType.NORMAL;
        determineMoveType();

        if (moveType == MoveType.EN_PASSANT) {
            this.capturedPiece = board.getPiece(endX, startY);
        }

        this.isWhiteMove = movedPiece > 0;
        this.computerMove = false;
    }

    private void determineMoveType() {
        // capture
        if (capturedPiece != 0) {
            this.moveType = MoveType.CAPTURE;
        }

        // castle
        if (abs(movedPiece) == Piece.wKing && startX == 5 && (startY == 1 || startY == 8)) {
            if (endX == 7 && (endY == 1 || endY == 8)) {
                this.moveType = MoveType.KING_SIDE_CASTLE; // king side
            }

            if (endX == 3 && (endY == 1 || endY == 8)) {
                this.moveType = MoveType.QUEEN_SIDE_CASTLE; // queen side
            }
        }

        // en passant
        if (abs(movedPiece) == 1 && capturedPiece == 0 && (startY == 5 || startY == 4) && startX != endX) {
            this.moveType = MoveType.EN_PASSANT;
        }

        // promotion move
        if ((movedPiece == Piece.wPawn && endY == 8) || (movedPiece == Piece.bPawn && endY == 1)) {
            isPromotionMove = true;
        }
    }

    /**
     * Return the given move formatted in standard chess move notation (e.g. e5, Nxd4, O-O, etc.)
     *
     * @return String formatted move
     */
    public String formatMove() {
        String pieceChar;
        String endCoordLetter;
        String checkSymbol = "";

        pieceChar = Piece.getPieceChar(movedPiece);

        endCoordLetter = Board.getCharCoord(endX);

        switch (moveType) {
            case QUEEN_SIDE_CASTLE:
                return "O-O-O";
            case KING_SIDE_CASTLE:
                return "O-O";
            case EN_PASSANT:
                return Board.getCharCoord(startX) + "x" + endCoordLetter + endY + checkSymbol;
            case QUEEN_PROMOTION:
                return endCoordLetter + endY + "=" + "Q";
            case KNIGHT_PROMOTION:
                return endCoordLetter + endY + "=" + "N";
            case ROOK_PROMOTION:
                return endCoordLetter + endY + "=" + "R";
            case BISHOP_PROMOTION:
                return endCoordLetter + endY + "=" + "B";
        }

        if (check) {
            checkSymbol = "+";
        } else if (checkmate) {
            checkSymbol = "#";
        }

        if (capturedPiece == 0) {
            return pieceChar + endCoordLetter + endY + checkSymbol;
        } else {
            if (abs(movedPiece) == 1) {
                return Board.getCharCoord(startX) + "x" + endCoordLetter + endY + checkSymbol;
            }
            return pieceChar + "x" + endCoordLetter + endY + checkSymbol;
        }
    }

    public String formatPerftMove() {
        return Board.getChessNotation(Board.getSquareIndex(startX, startY)) +
                Board.getChessNotation(Board.getSquareIndex(endX, endY));
    }

    @Override
    public boolean equals(Object v) {
        boolean retVal = false;

        if (v instanceof Move){
            Move move = (Move) v;

            if (move.board == this.board && move.startX == this.startX && move.startY == this.startY &&
                    move.endX == this.endX && move.endY == this.endY && move.movedPiece == this.movedPiece &&
                    move.capturedPiece == this.capturedPiece && move.moveType == this.moveType &&
                    move.isWhiteMove == this.isWhiteMove) {
                retVal = true;
            }
        }

        return retVal;
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, startX, startY, endX, endY, movedPiece, capturedPiece, check, checkmate, isWhiteMove, computerMove, moveType);
    }

    /**
     * Getters & Setters
     */
    public MoveType getMoveType() {
        return moveType;
    }

    public int getStartX() {
        return startX;
    }

    public int getStartY() {
        return startY;
    }

    public int getEndX() {
        return endX;
    }

    public int getEndY() {
        return endY;
    }

    public int getMovedPiece() {
        return movedPiece;
    }

    public void setMovedPiece(int movedPiece) {
        this.movedPiece = movedPiece;
    }

    public int getCapturedPiece() {
        return capturedPiece;
    }

    public void setCapturedPiece(int capturedPiece) {
        this.capturedPiece = capturedPiece;
    }

    public boolean isPromotionMove() {
        return this.isPromotionMove || this.moveType == MoveType.BISHOP_PROMOTION || this.moveType == MoveType.KNIGHT_PROMOTION ||
                this.moveType == MoveType.QUEEN_PROMOTION || this.moveType == MoveType.ROOK_PROMOTION;
    }

    public void setKingSideCastleMove() {
        this.moveType = MoveType.KING_SIDE_CASTLE;
    }

    public void setQueenSideCastleMove() {
        this.moveType = MoveType.QUEEN_SIDE_CASTLE;
    }

    public void setEnPassantMove() {
        this.moveType = MoveType.EN_PASSANT;
    }

    public void setQueenPromotionMove() {
        this.moveType = MoveType.QUEEN_PROMOTION;
    }

    public void setKnightPromotionMove() {
        this.moveType = MoveType.KNIGHT_PROMOTION;
    }

    public void setBishopPromotionMove() {
        this.moveType = MoveType.BISHOP_PROMOTION;
    }

    public void setRookPromotionMove() {
        this.moveType = MoveType.ROOK_PROMOTION;
    }

    public boolean isKingSideCastleMove() {
        return this.moveType == MoveType.KING_SIDE_CASTLE;
    }

    public boolean isQueenSideCastleMove() {
        return this.moveType == MoveType.QUEEN_SIDE_CASTLE;
    }

    public boolean isEnPassantMove() {
        return this.moveType == MoveType.EN_PASSANT;
    }

    public boolean isQueenPromotionMove() {
        return this.moveType == MoveType.QUEEN_PROMOTION;
    }

    public boolean isKnightPromotionMove() {
        return this.moveType == MoveType.KNIGHT_PROMOTION;
    }

    public boolean isRookPromotionMove() {
        return this.moveType == MoveType.ROOK_PROMOTION;
    }

    public boolean isBishopPromotionMove() {
        return this.moveType == MoveType.BISHOP_PROMOTION;
    }

    public boolean isBasicMove() {
        return this.moveType == MoveType.NORMAL;
    }

    public boolean isCapture() {
        return this.moveType == MoveType.CAPTURE;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    public boolean isCheckmate() {
        return checkmate;
    }

    public void setCheckmate(boolean checkmate) {
        this.checkmate = checkmate;
    }

    public boolean isWhiteMove() {
        return isWhiteMove;
    }

    public void setWhiteMove(boolean whiteMove) {
        isWhiteMove = whiteMove;
    }

    public boolean isWhiteRookMove() {
        return movedPiece == Piece.wRook;
    }

    public boolean isBlackRookMove() {
        return movedPiece == Piece.bRook;
    }

    public boolean isWhiteKingMove() {
        return movedPiece == Piece.wKing;
    }

    public boolean isBlackKingMove() {
        return movedPiece == Piece.bKing;
    }

    public void setMoveType(MoveType moveType) {
        this.moveType = moveType;
    }

    public boolean isComputerMove() {
        return computerMove;
    }

    public void setComputerMove(boolean computerMove) {
        this.computerMove = computerMove;
    }

    public Board getBoard() {
        return board;
    }

    public Point getStartPoint() {
        return new Point(startX, startY);
    }

    public Point getEndPoint() {
        return new Point(endX, endY);
    }
}

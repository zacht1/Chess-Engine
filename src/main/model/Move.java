package model;

import enumerations.MoveType;

public class Move {
    private Board board;

    private int startX;
    private int startY;
    private int endX;
    private int endY;

    private int movedPiece;
    private int capturedPiece;

    private MoveType moveType;

    /**
     * Constructs a new chess move with the specified starting square, ending square, move piece, and capture piece
     * Call helper methods to determine move type
     * Move may or may not be legal
     */
    public Move(Board board, int startX, int startY, int endX, int endY) {
        // stub
    }

    /**
     * Return the given move formatted in standard chess move notation (e.g. e5, Nxd4, O-O, etc.)
     *
     * @return String formatted move
     */
    public String formatMove() {
        return ""; // stub
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

    public void setStartX(int startX) {
        this.startX = startX;
    }

    public int getStartY() {
        return startY;
    }

    public void setStartY(int startY) {
        this.startY = startY;
    }

    public int getEndX() {
        return endX;
    }

    public void setEndX(int endX) {
        this.endX = endX;
    }

    public int getEndY() {
        return endY;
    }

    public void setEndY(int endY) {
        this.endY = endY;
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
        return false; // stub
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
}

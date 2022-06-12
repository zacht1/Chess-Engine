package model;

import enumerations.MoveType;

import java.awt.*;

public class Board {
    private int[] board;

    /**
     * Construct a new chess board in the starting position
     */
    public Board() {
        this.board = new int[64];
        setBoardFEN(FenUtility.START_POS_FEN);
    }

    /**
     * Perform the given move on this board
     * @param move a legal chess move
     */
    public void makeMove(Move move) {
        int startIndex = getSquareIndex(move.getStartX(), move.getStartY());
        int endIndex = getSquareIndex(move.getEndX(), move.getEndY());

         if (move.isEnPassantMove()) {
            board[startIndex] = 0;
            board[endIndex] = move.getMovedPiece();
            board[getSquareIndex(move.getEndX(), move.getStartY())] = 0;
        } else if (move.isPromotionMove()) {
            makePromotionMove(move, startIndex, endIndex);
        } else if (move.isKingSideCastleMove() || move.isQueenSideCastleMove()) {
            makeCastleMove(move, startIndex, endIndex);
        } else {
            board[startIndex] = Piece.empty;
            board[endIndex] = move.getMovedPiece();
        }
    }

    private void makePromotionMove(Move move, int startIndex, int endIndex) {
        board[startIndex] = 0;
        int piece;

        if (move.getMovedPiece() > 0) {
            // piece is white
            switch (move.getMoveType()) {
                case QUEEN_PROMOTION:
                    piece = Piece.wQueen;
                    break;
                case KNIGHT_PROMOTION:
                    piece = Piece.wKnight;
                    break;
                case ROOK_PROMOTION:
                    piece = Piece.wRook;
                    break;
                case BISHOP_PROMOTION:
                    piece = Piece.wBishop;
                    break;
                default:
                    piece = 0;
            }
        } else {
            switch (move.getMoveType()) {
                // piece is black
                case QUEEN_PROMOTION:
                    piece = Piece.bQueen;
                    break;
                case KNIGHT_PROMOTION:
                    piece = Piece.bKnight;
                    break;
                case ROOK_PROMOTION:
                    piece = Piece.bRook;
                    break;
                case BISHOP_PROMOTION:
                    piece = Piece.bBishop;
                    break;
                default:
                    piece = 0;
            }
        }

        board[endIndex] = piece;
    }

    private void makeCastleMove(Move move, int startIndex, int endIndex) {
        board[startIndex] = 0;
        board[endIndex] = move.getMovedPiece();

        if (move.getMoveType() == MoveType.KING_SIDE_CASTLE) {
            int y = move.getEndY();
            int piece = getPiece(8, y);
            board[getSquareIndex(8, y)] = 0;
            board[getSquareIndex(6, y)] = piece;
        } else if (move.getMoveType() == MoveType.QUEEN_SIDE_CASTLE) {
            int y = move.getEndY();
            int piece = getPiece(1, y);
            board[getSquareIndex(1, y)] = 0;
            board[getSquareIndex(4, y)] = piece;
        }
    }

    /**
     * Get the piece at the given (x,y) coordinate on this chess board
     */
    public int getPiece(int x, int y) {
        return board[getSquareIndex(x,y)];
    }

    /**
     * Return the index of the given coordinates in the chess board integer array
     *
     * @throws IndexOutOfBoundsException if given x or y is larger than 8
     */
    public static int getSquareIndex(int x, int y) {
        int fileIndex = x - 1;
        int rankIndex = y - 1;

        if (fileIndex > 7 || rankIndex > 7) {
            throw new IndexOutOfBoundsException();
        }

        return 8*rankIndex + fileIndex;
    }

    /**
     * Return the coordinates of a chess board square given the squares index in the board integer array
     *
     * @throws IndexOutOfBoundsException if given index is larger than 63
     */
    public static Point getSquareCoordinates(int index) {
        if (index > 63) {
            throw new IndexOutOfBoundsException();
        }

        int fileIndex = index % 8;
        int rankIndex = index / 8;

        return new Point(fileIndex + 1, rankIndex + 1);
    }

    /**
     * Get the character that corresponds to the given x value on a chess board
     */
    public static String getCharCoord(int x) {
        switch(x) {
            case 1:
                return "a";
            case 2:
                return "b";
            case 3:
                return "c";
            case 4:
                return "d";
            case 5:
                return "e";
            case 6:
                return "f";
            case 7:
                return "g";
            case 8:
                return "h";
            default:
                throw new IndexOutOfBoundsException();
        }
    }

    /**
     *  Set this board to the position in the given Forsyth–Edwards Notation (FEN) string
     */
    private void setBoardFEN(String fen) {
        FenUtility fenUtility = new FenUtility();

        fenUtility.loadPositionFromFEN(this, fen);
    }

    /**
     * Getters & Setters
     */
    public int[] getBoard() {
        return board;
    }

    public void setBoard(int[] board) {
        this.board = board;
    }
}
package model;

import enumerations.MoveType;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Board {
    private int[] board;
    private List<Move> moveList;

    /**
     * Construct a new chess board in the starting position
     */
    public Board() {
        this.board = new int[64];
        this.moveList = new ArrayList<>();
        setBoardFEN(FenUtility.START_POS_FEN);
    }

    /**
     * Perform the given move on this board
     * @param move a legal chess move
     */
    public void makeMove(Move move) {
        int startIndex = getSquareIndex(move.getStartX(), move.getStartY());
        int endIndex = getSquareIndex(move.getEndX(), move.getEndY());

        moveList.add(move);

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

    public void makePromotionMove(Move move, int startIndex, int endIndex) {
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
     * Undo the given move on the current board
     * @param move a legal chess move that was the last played move on the current board
     */
    public void unMakeMove(Move move) {
        int startIndex = getSquareIndex(move.getStartX(), move.getStartY());
        int endIndex = getSquareIndex(move.getEndX(), move.getEndY());

        moveList.remove(move);

        if (move.isEnPassantMove()) {
            board[startIndex] = move.getMovedPiece();
            board[endIndex] = Piece.empty;
            board[getSquareIndex(move.getEndX(), move.getStartY())] = move.getCapturedPiece();
        } else if (move.isPromotionMove()) {
            board[startIndex] = move.getMovedPiece();
            board[endIndex] = move.getCapturedPiece();
        } else if (move.isKingSideCastleMove()) {
            board[startIndex] = move.getMovedPiece();
            board[endIndex] = Piece.empty;

            if (move.getMovedPiece() > 0) { //white
                board[getSquareIndex(8, 1)] = Piece.wRook;
                board[getSquareIndex(6,1)] = Piece.empty;
            } else {
                board[getSquareIndex(8, 8)] = Piece.bRook;
                board[getSquareIndex(6,8)] = Piece.empty;
            }
        } else if (move.isQueenSideCastleMove()) {
            board[startIndex] = move.getMovedPiece();
            board[endIndex] = Piece.empty;

            if (move.getMovedPiece() > 0) { //white
                board[getSquareIndex(1, 1)] = Piece.wRook;
                board[getSquareIndex(4,1)] = Piece.empty;
            } else {
                board[getSquareIndex(1, 8)] = Piece.bRook;
                board[getSquareIndex(4,8)] = Piece.empty;
            }
        } else {
            board[startIndex] = move.getMovedPiece();
            board[endIndex] = move.getCapturedPiece();
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
     * Get the chess notation coordinate of the square with the given index
     */
    public static String getChessNotation(int index) {
        String x = getCharCoord(getSquareCoordinates(index).x);
        int y = getSquareCoordinates(index).y;

        return x + y;
    }

    /**
     * Get the index of the square given by the regular chess notation (e.g. a4, f8, h1...)
     */
    public static int getSquareIndexFromString(String chessNotation) {
        char xStr = chessNotation.toCharArray()[0];
        char yStr = chessNotation.toCharArray()[1];

        int x;
        int y;

        switch (xStr) {
            case 'a': x = 1;
                break;
            case 'b': x = 2;
                break;
            case 'c': x = 3;
                break;
            case 'd': x = 4;
                break;
            case 'e': x = 5;
                break;
            case 'f': x = 6;
                break;
            case 'g': x = 7;
                break;
            case 'h': x = 8;
                break;
            default:
                throw new IndexOutOfBoundsException();
        }

        switch (yStr) {
            case '1': y = 1;
                break;
            case '2': y = 2;
                break;
            case '3': y = 3;
                break;
            case '4': y = 4;
                break;
            case '5': y = 5;
                break;
            case '6': y = 6;
                break;
            case '7': y = 7;
                break;
            case '8': y = 8;
                break;
            default:
                throw new IndexOutOfBoundsException();
        }

        return getSquareIndex(x,y);
    }

    /**
     *  Set this board to the position in the given Forsyth–Edwards Notation (FEN) string
     */
    public void setBoardFEN(String fen) {
        FenUtility fenUtility = new FenUtility();

        fenUtility.loadPositionFromFEN(this, fen);
    }

    public void formatBoard() {
        System.out.println(board[56] + "  " + board[57] + "  " + board[58] + "  " + board[59] + "  " + board[60] + "  " + board[61] + "  " + board[62] + "  " + board[63]);
        System.out.println(board[48] + "  " + board[49] + "  " + board[50] + "  " + board[51] + "  " + board[52] + "  " + board[53] + "  " + board[54] + "  " + board[55]);
        System.out.println(board[40] + "  " + board[41] + "  " + board[42] + "  " + board[43] + "  " + board[44] + "  " + board[45] + "  " + board[46] + "  " + board[47]);
        System.out.println(board[32] + "  " + board[33] + "  " + board[34] + "  " + board[35] + "  " + board[36] + "  " + board[37] + "  " + board[38] + "  " + board[39]);
        System.out.println(board[24] + "  " + board[25] + "  " + board[26] + "  " + board[27] + "  " + board[28] + "  " + board[29] + "  " + board[30] + "  " + board[31]);
        System.out.println(board[16] + "  " + board[17] + "  " + board[18] + "  " + board[19] + "  " + board[20] + "  " + board[21] + "  " + board[22] + "  " + board[23]);
        System.out.println(board[8] + "  " + board[8] + "  " + board[10] + "  " + board[11] + "  " + board[12] + "  " + board[13] + "  " + board[14] + "  " + board[15]);
        System.out.println(board[0] + "  " + board[1] + "  " + board[2] + "  " + board[3] + "  " + board[4] + "  " + board[5] + "  " + board[6] + "  " + board[7]);
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

    public List<Move> getMoveList() {
        return moveList;
    }
}
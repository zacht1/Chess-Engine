package model;

import static java.lang.Math.abs;

public class Piece {
    public static int empty = 0;

    public static int wPawn = 1;
    public static int wKnight = 2;
    public static int wBishop = 3;
    public static int wRook = 4;
    public static int wQueen = 5;
    public static int wKing = 6;

    public static int bPawn = -1;
    public static int bKnight = -2;
    public static int bBishop = -3;
    public static int bRook = -4;
    public static int bQueen = -5;
    public static int bKing = -6;

    /**
     * Return the first character of the given pieces name, unless piece is a pawn then return an empty string
     *
     * @return first character in piece name, or empty string for a pawn
     * @throws IndexOutOfBoundsException if given int is not a piece
     */
    public static String getPieceChar(int piece) {
        switch (abs(piece)) {
            case 1:
                return "";
            case 2:
                return "N";
            case 3:
                return "B";
            case 4:
                return "R";
            case 5:
                return "Q";
            case 6:
                return "K";
            default:
                throw new IndexOutOfBoundsException();
        }
    }

    /**
     * Return the name of the given piece
     *
     * @return piece name
     * @throws IndexOutOfBoundsException if given int is not a piece
     */
    public static String getPieceName(int piece) {
        switch (abs(piece)) {
            case 1:
                return "pawn";
            case 2:
                return "knight";
            case 3:
                return "bishop";
            case 4:
                return "rook";
            case 5:
                return "queen";
            case 6:
                return "king";
            default:
                throw new IndexOutOfBoundsException();
        }
    }

    /**
     * Return true if the move is a legal pawn move, false otherwise
     */
    public static boolean isLegalPawnMove(Move move) {
        if (move.getMovedPiece() > 0) {
            return legalWhitePawnMove(move);
        }
        return legalBlackPawnMove(move);
    }

    private static boolean legalWhitePawnMove(Move move) {
        Board board = move.getBoard();

        int x1 = move.getStartX();
        int y1 = move.getStartY();
        int x2 = move.getEndX();
        int y2 = move.getEndY();

        if (move.getCapturedPiece() != 0) {
            if (move.getCapturedPiece() > 0 && move.getMovedPiece() > 0) {
                return false;
            }

            return legalWhitePawnCapture(move, x1, y1, x2, y2);
        }


        if (x1 != x2) {
            return false;
        }

        if (y1 == y2 - 1) {
            return board.getPiece(x2, y2) == 0;
        } else if (y1 == y2 - 2) {
            if (y1 == 2) {
                return board.getPiece(x2, y1 + 1) == 0 && board.getPiece(x2, y2) == 0;
            }
            return false;
        } else {
            return false;
        }
    }

    private static boolean legalBlackPawnMove(Move move) {
        Board board = move.getBoard();

        int x1 = move.getStartX();
        int y1 = move.getStartY();
        int x2 = move.getEndX();
        int y2 = move.getEndY();


        if (move.getCapturedPiece() != 0) {
            if (move.getCapturedPiece() < 0 && move.getMovedPiece() < 0) {
                return false;
            }

            return legalBlackPawnCapture(move, x1, y1, x2, y2);
        }

        if (x1 != x2) {
            return false;
        }

        if (y1 == y2 + 1) {
            return board.getPiece(x2, y2) == 0;
        } else if (y1 == y2 + 2) {
            if (y1 == 7) {
                return board.getPiece(x2, y1 - 1) == 0 && board.getPiece(x2, y2) == 0;
            }
            return false;
        } else {
            return false;
        }
    }

    private static boolean legalWhitePawnCapture(Move move, int x1, int y1, int x2, int y2) {
        if (x1 < x2 && y1 < y2) {
            return x2 - x1 == 1 && y2 - y1 == 1;
        } else {
            return x1 - x2 == 1 && y2 - y1 == 1;
        }
    }

    private static boolean legalBlackPawnCapture(Move move, int x1, int y1, int x2, int y2) {
        if (x2 > x1 && y2 < y1) {
            return x2 - x1 == 1 && y1 - y2 == 1;
        } else {
            return x1 - x2 == 1 && y1 - y2 == 1;
        }
    }
}

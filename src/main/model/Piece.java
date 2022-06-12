package model;

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
        return ""; // stub
    }

    /**
     * Return the name of the given piece
     *
     * @return piece name
     * @throws IndexOutOfBoundsException if given int is not a piece
     */
    public static String getPieceName(int piece) {
        return ""; // stub
    }
}

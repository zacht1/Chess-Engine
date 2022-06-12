package model;

import java.awt.*;

public class Board {
    private int[] board;

    /**
     * Construct a new chess board in the starting position
     */
    public Board() {
        // stub
    }

    /**
     * Perform the given move on this board
     * @param move a legal chess move
     */
    public void makeMove(Move move) {
        // stub
    }

    /**
     * Get the piece at the given (x,y) coordinate on this chess board
     */
    public int getPiece(int x, int y) {
        return 0; // stub
    }

    /**
     * Return the index of the given coordinates in the chess board integer array
     *
     * @throws IndexOutOfBoundsException if given x or y is larger than 7
     */
    public static int getSquareIndex(int x, int y) {
        return 0; // stub
    }

    /**
     * Return the coordinates of a chess board square given the squares index in the board integer array
     *
     * @throws IndexOutOfBoundsException if given index is larger than 63
     */
    public static Point getSquareCoordinates(int index) {
        return new Point(0,0); // stub
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
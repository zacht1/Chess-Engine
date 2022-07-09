package model.generation;

import model.Board;

import java.util.ArrayList;
import java.util.List;

// generates push and capture masks
public class MaskGenerator {
    private List<Integer> captureMask;
    private List<Integer> pushMask;

    private CheckGenerator checkGenerator;
    private int friendlyKingIndex;

    public MaskGenerator(CheckGenerator checkGenerator, int friendlyKingIndex) {
        this.checkGenerator = checkGenerator;
        this.friendlyKingIndex = friendlyKingIndex;
        captureMask = new ArrayList<>();
        pushMask = new ArrayList<>();

        for (int i = 0; i < 64; i++) {
            captureMask.add(i);
            pushMask.add(i);
        }
    }

    /**
     * Update the checkers and push maps
     */
    public void updateMasksInCheck() {
        List<Integer> checkers = checkGenerator.getCheckers();
        List<Integer> bishopCheckers = checkGenerator.getBishopCheckers();
        List<Integer> rookCheckers = checkGenerator.getRookCheckers();
        List<Integer> queenCheckers = checkGenerator.getQueenCheckers();

        captureMask.clear();
        pushMask.clear();

        if (checkers.size() == 1) {
            // if only one piece checking the king we can evade check my capturing that piece
            captureMask.addAll(checkers);

            // if the piece giving check is a sliding piece we can evade check by blocking
            if (!bishopCheckers.isEmpty()) {
                pushMask.addAll(diagonalSlidingRaysToSquare(friendlyKingIndex, bishopCheckers.get(0)));
            } else if (!rookCheckers.isEmpty()) {
                pushMask.addAll(straightSlidingRaysToSquares(friendlyKingIndex, rookCheckers.get(0)));
            } else if (!queenCheckers.isEmpty()) {
                pushMask.addAll(diagonalSlidingRaysToSquare(friendlyKingIndex, queenCheckers.get(0)));
                pushMask.addAll(straightSlidingRaysToSquares(friendlyKingIndex, queenCheckers.get(0)));
            }
            // otherwise, pushMask is empty because we can't block the check
        }
    }

    /**
     * Generate all squares in between the bishop, on the given index, and the friendly king
     * @param diagonalPieceIndex the index of the bishop on the current board
     */
    public List<Integer> diagonalSlidingRaysToSquare(int squareIndex, int diagonalPieceIndex) {
        List<Integer> slidingAttackRay = new ArrayList<>();

        int squareX = Board.getSquareCoordinates(squareIndex).x;
        int squareY = Board.getSquareCoordinates(squareIndex).y;

        int diagonalPieceX = Board.getSquareCoordinates(diagonalPieceIndex).x;
        int diagonalPieceY = Board.getSquareCoordinates(diagonalPieceIndex).y;

        if (diagonalPieceX < squareX && diagonalPieceY > squareY) {
            int y = squareY;
            for (int x = squareX; x > diagonalPieceX; x--) {
                slidingAttackRay.add(Board.getSquareIndex(x,y));
                y++;
            }

        } else if (diagonalPieceX > squareX && diagonalPieceY > squareY) {
            int y = squareY;
            for (int x = squareX; x < diagonalPieceX; x++) {
                slidingAttackRay.add(Board.getSquareIndex(x,y));
                y++;
            }

        } else if (diagonalPieceX < squareX && diagonalPieceY < squareY) {
            int y = squareY;
            for (int x = squareX; x > diagonalPieceX; x--) {
                slidingAttackRay.add(Board.getSquareIndex(x,y));
                y--;
            }

        } else if (diagonalPieceX > squareX && diagonalPieceY < squareY) {
            int y = squareY;
            for (int x = squareX; x < diagonalPieceX; x++) {
                slidingAttackRay.add(Board.getSquareIndex(x,y));
                y--;
            }
        }

        return slidingAttackRay;
    }

    /**
     * Generate all squares in between the rook, on the given index, and the friendly king
     * @param straightPieceIndex the index of the rook on the current board
     */
    public List<Integer> straightSlidingRaysToSquares(int squareIndex, int straightPieceIndex) {
        List<Integer> slidingAttackRay = new ArrayList<>();

        int kingX = Board.getSquareCoordinates(squareIndex).x;
        int kingY = Board.getSquareCoordinates(squareIndex).y;

        int rookX = Board.getSquareCoordinates(straightPieceIndex).x;
        int rookY = Board.getSquareCoordinates(straightPieceIndex).y;

        if (kingX == rookX && kingY < rookY) {
            for (int y = kingY; y < rookY; y++) {
                slidingAttackRay.add(Board.getSquareIndex(kingX, y));
            }
        } else if (kingX == rookX && kingY > rookY) {
            for (int y = kingY; y > rookY; y--) {
                slidingAttackRay.add(Board.getSquareIndex(kingX, y));
            }
        } else if (kingX > rookX && kingY == rookY) {
            for (int x = kingX; x > rookX; x--) {
                slidingAttackRay.add(Board.getSquareIndex(x, kingY));
            }
        } else if (kingX < rookX && kingY == rookY) {
            for (int x = kingX; x < rookX; x++) {
                slidingAttackRay.add(Board.getSquareIndex(x, kingY));
            }
        }

        return slidingAttackRay;
    }

    /**
     * Getters & Setters
     */
    public List<Integer> getCaptureMask() {
        return captureMask;
    }

    public List<Integer> getPushMask() {
        return pushMask;
    }
}

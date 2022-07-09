package model.generation;

import model.Board;
import model.Game;
import model.Move;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PinMoveGenerator {
    private Game game;
    private Board board;
    private boolean whiteToPlay;
    private int friendlyKingX;
    private int friendlyKingY;

    public PinMoveGenerator(Game game, boolean whiteToPlay, int friendlyKingIndex) {
        this.game = game;
        this.board = game.getBoard();
        this.friendlyKingX = Board.getSquareCoordinates(friendlyKingIndex).x;
        this.friendlyKingY = Board.getSquareCoordinates(friendlyKingIndex).y;
        this.whiteToPlay = whiteToPlay;
    }

    public List<Move> generateBlackPawnMoves(int pinnedPieceIndex, int pinningPieceIndex) {
        List<Move> legalMoves = new ArrayList<>();

        int pinnedPieceX = Board.getSquareCoordinates(pinnedPieceIndex).x;
        int pinnedPieceY = Board.getSquareCoordinates(pinnedPieceIndex).y;

        int pinningPieceX = Board.getSquareCoordinates(pinningPieceIndex).x;
        int pinningPieceY = Board.getSquareCoordinates(pinningPieceIndex).y;

        if (pinnedPieceX == pinningPieceX) { // piece is pinned on the vertical and piece can move
            // 1 square move
            if (pinnedPieceY - 1 >= 1 && board.getPiece(pinnedPieceX, pinnedPieceY - 1) == 0) {
                legalMoves.add(new Move(board, pinnedPieceX, pinnedPieceY, pinnedPieceX, pinnedPieceY - 1));
            }
            // 2 square move
            if (pinnedPieceY == 7 && board.getPiece(pinnedPieceX, pinnedPieceY - 1) == 0 && board.getPiece(pinnedPieceX, pinnedPieceY - 2) == 0) {
                legalMoves.add(new Move(board, pinnedPieceX, pinnedPieceY, pinnedPieceX, pinnedPieceY - 2));
            }

        } else { // piece is pinned on the diagonal
            if (pinnedPieceIndex - 9 == pinningPieceIndex) { // pinning piece is capturable to the left of pinned pawn
                legalMoves.add(new Move(board, pinnedPieceX, pinnedPieceY, pinnedPieceX - 1, pinnedPieceY - 1));
            } else if (pinnedPieceIndex - 7 == pinningPieceIndex) { // pinning piece is capturable to the right of pinned pawn
                legalMoves.add(new Move(board, pinnedPieceX, pinnedPieceY, pinnedPieceX + 1, pinnedPieceY + 1));
            }

            // en passant moves
        }

        return legalMoves;
    }

    public List<Move> generateWhitePawnMoves(int pinnedPieceIndex, int pinningPieceIndex) {
        List<Move> legalMoves = new ArrayList<>();

        int pinnedPieceX = Board.getSquareCoordinates(pinnedPieceIndex).x;
        int pinnedPieceY = Board.getSquareCoordinates(pinnedPieceIndex).y;

        int pinningPieceX = Board.getSquareCoordinates(pinningPieceIndex).x;
        int pinningPieceY = Board.getSquareCoordinates(pinningPieceIndex).y;

        if (pinnedPieceX == pinningPieceX) { // piece is pinned on the vertical and piece can move
            if (pinnedPieceY + 1 <= 8 && board.getPiece(pinnedPieceX, pinnedPieceY + 1) == 0) {
                legalMoves.add(new Move(board, pinnedPieceX, pinnedPieceY, pinnedPieceX, pinnedPieceY + 1));
            }

            // 2 square move
            if (pinnedPieceY == 2 && board.getPiece(pinnedPieceX, pinnedPieceY + 1) == 0 && board.getPiece(pinningPieceX, pinnedPieceY + 2) == 0) {
                legalMoves.add(new Move(board, pinnedPieceX, pinnedPieceY, pinnedPieceX, pinnedPieceY + 2));
            }
        } else { // piece is pinned on the diagonal
            if (pinnedPieceIndex + 9 == pinningPieceIndex) { // pinning piece is capturable to the right of pinned pawn
                legalMoves.add(new Move(board, pinnedPieceX, pinnedPieceY, pinnedPieceX + 1, pinnedPieceY + 1));
            } else if (pinnedPieceIndex + 7 == pinningPieceIndex) { // pinning piece is capturable to the left of pinned pawn
                legalMoves.add(new Move(board, pinnedPieceX, pinnedPieceY, pinnedPieceX - 1, pinnedPieceY + 1));
            }

            // en passant moves
        }

        return legalMoves;
    }

    public List<Move> generateBishopMoves(int pinnedPieceIndex, int pinningPieceIndex) {
        List<Move> legalMoves = new ArrayList<>();

        int pinnedPieceX = Board.getSquareCoordinates(pinnedPieceIndex).x;
        int pinnedPieceY = Board.getSquareCoordinates(pinnedPieceIndex).y;

        int pinningPieceX = Board.getSquareCoordinates(pinningPieceIndex).x;
        int pinningPieceY = Board.getSquareCoordinates(pinningPieceIndex).y;

        // step 1: where is pin coming from diagonal or vertical
        // step 2: if piece is pinned vertically, no legal moves, otherwise
        //         generate moves between pinned piece and pinning piece, those are the only legal moves
        //         (including capturing pinned piece)

        if (!(pinnedPieceX == pinningPieceX || pinnedPieceY == pinningPieceY)) {
            // pin is coming on the diagonal
            List<Point> possibleMoveEndSquareIndices = diagonalSlidingRaysToSquare(pinnedPieceX, pinnedPieceY, pinningPieceX, pinningPieceY);
            possibleMoveEndSquareIndices.addAll(diagonalSlidingRaysUpToSquare(pinnedPieceX, pinnedPieceY, friendlyKingX, friendlyKingY));

            for (Point square: possibleMoveEndSquareIndices) {
                legalMoves.add(new Move(board, pinnedPieceX, pinnedPieceY, square.x, square.y));
            }
        }

        return legalMoves;
    }

    public List<Move> generateRookMoves(int pinnedPieceIndex, int pinningPieceIndex) {
        List<Move> legalMoves = new ArrayList<>();

        int pinnedPieceX = Board.getSquareCoordinates(pinnedPieceIndex).x;
        int pinnedPieceY = Board.getSquareCoordinates(pinnedPieceIndex).y;

        int pinningPieceX = Board.getSquareCoordinates(pinningPieceIndex).x;
        int pinningPieceY = Board.getSquareCoordinates(pinningPieceIndex).y;

        // step 1: where is pin coming from diagonal or vertical
        // step 2: if piece is pinned diagonally, no legal moves, otherwise
        //         generate moves between pinned piece and pinning piece, those are the only legal moves
        //         (including capturing pinned piece)

        if (pinnedPieceX == pinningPieceX || pinnedPieceY == pinningPieceY) {
            // pin is coming on straight
            List<Point> possibleMoveEndSquareIndices = straightSlidingRaysToSquare(pinnedPieceX, pinnedPieceY, pinningPieceX, pinningPieceY);
            possibleMoveEndSquareIndices.addAll(straightSlidingRaysUpToSquare(pinnedPieceX, pinnedPieceY, friendlyKingX, friendlyKingY));

            for (Point square: possibleMoveEndSquareIndices) {
                legalMoves.add(new Move(board, pinnedPieceX, pinnedPieceY, square.x, square.y));
            }
        }

        return legalMoves;
    }

    public List<Move> generateQueenMoves(int pinnedPieceIndex, int pinningPieceIndex) {
        List<Move> legalMoves = new ArrayList<>();

        int pinnedPieceX = Board.getSquareCoordinates(pinnedPieceIndex).x;
        int pinnedPieceY = Board.getSquareCoordinates(pinnedPieceIndex).y;

        int pinningPieceX = Board.getSquareCoordinates(pinningPieceIndex).x;
        int pinningPieceY = Board.getSquareCoordinates(pinningPieceIndex).y;

        // step 1: check that the piece at startX and startY is pinned
        // step 2: where is pin coming from diagonal or vertical
        // step 3: generate moves between  pinned piece and pinning piece, those are the only legal moves
        //         (including capturing pinned piece)

        if (pinnedPieceX == pinningPieceX || pinnedPieceY == pinningPieceY) {
            // pin is coming on straight
            List<Point> possibleMoveEndSquareIndices = straightSlidingRaysToSquare(pinnedPieceX, pinnedPieceY, pinningPieceX, pinningPieceY);
            possibleMoveEndSquareIndices.addAll(straightSlidingRaysUpToSquare(pinnedPieceX, pinnedPieceY, friendlyKingX, friendlyKingY));

            for (Point square: possibleMoveEndSquareIndices) {
                legalMoves.add(new Move(board, pinnedPieceX, pinnedPieceY, square.x, square.y));
            }
        } else {
            // pin is coming on the diagonal
            List<Point> possibleMoveEndSquareIndices = diagonalSlidingRaysToSquare(pinnedPieceX, pinnedPieceY, pinningPieceX, pinningPieceY);
            possibleMoveEndSquareIndices.addAll(diagonalSlidingRaysUpToSquare(pinnedPieceX, pinnedPieceY, friendlyKingX, friendlyKingY));

            for (Point square: possibleMoveEndSquareIndices) {
                legalMoves.add(new Move(board, pinnedPieceX, pinnedPieceY, square.x, square.y));
            }
        }

        return legalMoves;
    }

    private List<Point> diagonalSlidingRaysToSquare(int pinnedPieceX, int pinnedPieceY, int pinningPieceX, int pinningPieceY) {
        List<Point> slidingAttackRay = new ArrayList<>();

        if (pinnedPieceX < pinningPieceX && pinnedPieceY < pinningPieceY) {
            int y = pinnedPieceY + 1;
            for (int x = pinnedPieceX + 1; x <= pinningPieceX; x++) {
                slidingAttackRay.add(new Point(x,y));
                y++;
            }

        } else if (pinnedPieceX < pinningPieceX && pinnedPieceY > pinningPieceY) {
            int y = pinnedPieceY - 1;
            for (int x = pinnedPieceX + 1; x <= pinningPieceX; x++) {
                slidingAttackRay.add(new Point(x,y));
                y--;
            }

        } else if (pinnedPieceX > pinningPieceX && pinnedPieceY < pinningPieceY) {
            int y = pinnedPieceY + 1;
            for (int x = pinnedPieceX - 1; x >= pinningPieceX; x--) {
                slidingAttackRay.add(new Point(x,y));
                y++;
            }

        } else if (pinnedPieceX > pinningPieceX && pinnedPieceY > pinningPieceY) {
            int y = pinnedPieceY - 1;
            for (int x = pinnedPieceX - 1; x >= pinningPieceX; x--) {
                slidingAttackRay.add(new Point(x,y));
                y--;
            }
        }

        return slidingAttackRay;
    }

    private List<Point> straightSlidingRaysToSquare(int pinnedPieceX, int pinnedPieceY, int pinningPieceX, int pinningPieceY) {
        List<Point> slidingAttackRay = new ArrayList<>();

        if (pinnedPieceX == pinningPieceX && pinnedPieceY < pinningPieceY) { // north
            for (int y = pinnedPieceY + 1; y <= pinningPieceY; y++) {
                slidingAttackRay.add(new Point(pinnedPieceX, y));
            }
        } else if (pinnedPieceX == pinningPieceX && pinnedPieceY > pinningPieceY) { // south
            for (int y = pinnedPieceY - 1; y >= pinningPieceY; y--) {
                slidingAttackRay.add(new Point(pinnedPieceX, y));
            }
        } else if (pinnedPieceX > pinningPieceX && pinnedPieceY == pinningPieceY) { // west
            for (int x = pinnedPieceX - 1; x >= pinningPieceX; x--) {
                slidingAttackRay.add(new Point(x, pinnedPieceY));
            }
        } else if (pinnedPieceX < pinningPieceX && pinnedPieceY == pinningPieceY) { // east
            for (int x = pinnedPieceX + 1; x <= pinningPieceX; x++) {
                slidingAttackRay.add(new Point(x, pinnedPieceY));
            }
        }

        return slidingAttackRay;
    }

    private List<Point> diagonalSlidingRaysUpToSquare(int pinnedPieceX, int pinnedPieceY, int pinningPieceX, int pinningPieceY) {
        List<Point> slidingAttackRay = new ArrayList<>();

        if (pinnedPieceX < pinningPieceX && pinnedPieceY < pinningPieceY) {
            int y = pinnedPieceY + 1;
            for (int x = pinnedPieceX + 1; x < pinningPieceX; x++) {
                slidingAttackRay.add(new Point(x,y));
                y++;
            }

        } else if (pinnedPieceX < pinningPieceX && pinnedPieceY > pinningPieceY) {
            int y = pinnedPieceY - 1;
            for (int x = pinnedPieceX + 1; x < pinningPieceX; x++) {
                slidingAttackRay.add(new Point(x,y));
                y--;
            }

        } else if (pinnedPieceX > pinningPieceX && pinnedPieceY < pinningPieceY) {
            int y = pinnedPieceY + 1;
            for (int x = pinnedPieceX - 1; x > pinningPieceX; x--) {
                slidingAttackRay.add(new Point(x,y));
                y++;
            }

        } else if (pinnedPieceX > pinningPieceX && pinnedPieceY > pinningPieceY) {
            int y = pinnedPieceY - 1;
            for (int x = pinnedPieceX - 1; x > pinningPieceX; x--) {
                slidingAttackRay.add(new Point(x,y));
                y--;
            }
        }

        return slidingAttackRay;
    }

    private List<Point> straightSlidingRaysUpToSquare(int pinnedPieceX, int pinnedPieceY, int pinningPieceX, int pinningPieceY) {
        List<Point> slidingAttackRay = new ArrayList<>();

        if (pinnedPieceX == pinningPieceX && pinnedPieceY < pinningPieceY) { // north
            for (int y = pinnedPieceY + 1; y < pinningPieceY; y++) {
                slidingAttackRay.add(new Point(pinnedPieceX, y));
            }
        } else if (pinnedPieceX == pinningPieceX && pinnedPieceY > pinningPieceY) { // south
            for (int y = pinnedPieceY - 1; y > pinningPieceY; y--) {
                slidingAttackRay.add(new Point(pinnedPieceX, y));
            }
        } else if (pinnedPieceX > pinningPieceX && pinnedPieceY == pinningPieceY) { // west
            for (int x = pinnedPieceX - 1; x > pinningPieceX; x--) {
                slidingAttackRay.add(new Point(x, pinnedPieceY));
            }
        } else if (pinnedPieceX < pinningPieceX && pinnedPieceY == pinningPieceY) { // east
            for (int x = pinnedPieceX + 1; x < pinningPieceX; x++) {
                slidingAttackRay.add(new Point(x, pinnedPieceY));
            }
        }

        return slidingAttackRay;
    }
}

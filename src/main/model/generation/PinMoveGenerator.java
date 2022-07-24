package model.generation;

import model.Board;
import model.Game;
import model.Move;
import model.Piece;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PinMoveGenerator {
    private Game game;
    private Board board;
    private boolean whiteToPlay;
    private int friendlyKingIndex;
    private int friendlyKingX;
    private int friendlyKingY;

    public PinMoveGenerator(Game game, boolean whiteToPlay, int friendlyKingIndex) {
        this.game = game;
        this.board = game.getBoard();
        this.friendlyKingIndex = friendlyKingIndex;
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
                legalMoves.add(new Move(game, pinnedPieceX, pinnedPieceY, pinnedPieceX, pinnedPieceY - 1));
            }
            // 2 square move
            if (pinnedPieceY == 7 && board.getPiece(pinnedPieceX, pinnedPieceY - 1) == 0 && board.getPiece(pinnedPieceX, pinnedPieceY - 2) == 0) {
                legalMoves.add(new Move(game, pinnedPieceX, pinnedPieceY, pinnedPieceX, pinnedPieceY - 2));
            }

        } else { // piece is pinned on the diagonal
            if (pinnedPieceIndex - 9 == pinningPieceIndex) { // pinning piece is capturable to the left of pinned pawn
                legalMoves.add(new Move(game, pinnedPieceX, pinnedPieceY, pinnedPieceX - 1, pinnedPieceY - 1));
            } else if (pinnedPieceIndex - 7 == pinningPieceIndex) { // pinning piece is capturable to the right of pinned pawn
                legalMoves.add(new Move(game, pinnedPieceX, pinnedPieceY, pinnedPieceX + 1, pinnedPieceY + 1));
            }

            // en passant moves
            if (!board.getMoveList().isEmpty()) {
                List<Move> moves = board.getMoveList();
                int mostRecent = moves.size() - 1;

                if ((pinnedPieceX < pinningPieceX && pinnedPieceY > pinningPieceY) ||
                        (pinnedPieceX > pinningPieceX && pinnedPieceY < pinningPieceY)) { // blue
                    if (pinnedPieceY == 4 && moves.get(mostRecent).getMovedPiece() == Piece.wPawn && moves.get(mostRecent).getEndY() == 4 &&
                            moves.get(mostRecent).getStartY() == 2 && moves.get(mostRecent).getEndX() == pinnedPieceX + 1 &&
                            legalBlackEnPassant(pinnedPieceX, pinnedPieceY, pinnedPieceX + 1, pinnedPieceY)) {
                        legalMoves.add(new Move(game, pinnedPieceX, pinnedPieceY, pinnedPieceX + 1, pinnedPieceY + 1));
                    }

                } else if ((pinnedPieceX < pinningPieceX && pinnedPieceY < pinningPieceY) ||
                        (pinnedPieceX > pinningPieceX && pinnedPieceY > pinningPieceY)) { // green
                    if (pinnedPieceY == 4 && moves.get(mostRecent).getMovedPiece() == Piece.wPawn && moves.get(mostRecent).getEndY() == 4 &&
                            moves.get(mostRecent).getStartY() == 2 && moves.get(mostRecent).getEndX() == pinnedPieceX - 1 &&
                            legalBlackEnPassant(pinnedPieceX, pinnedPieceY, pinnedPieceX - 1, pinnedPieceY)) {
                        legalMoves.add(new Move(game, pinnedPieceX, pinnedPieceY, pinnedPieceX - 1, pinnedPieceY + 1));
                    }
                }
            }
        }

        return legalMoves;
    }

    private boolean legalBlackEnPassant(int blackPawnX, int blackPawnY, int whitePawnX, int whitePawnY) {
        int[] testBoard = board.getBoard().clone();
        testBoard[Board.getSquareIndex(blackPawnX, blackPawnY)] = Piece.empty;
        testBoard[Board.getSquareIndex(whitePawnX, whitePawnY)] = Piece.empty;

        int kingX = Board.getSquareCoordinates(friendlyKingIndex).x;
        int kingY = Board.getSquareCoordinates(friendlyKingIndex).y;

        for (int x = kingX + 1; x <= 8; x++) {
            int index = Board.getSquareIndex(x, kingY);

            if (testBoard[index] < 0 || testBoard[index] == Piece.wPawn || testBoard[index] == Piece.wKnight ||
                    testBoard[index] == Piece.wBishop || testBoard[index] == Piece.wKing) {
                break;
            }

            if (testBoard[index] == Piece.wRook || testBoard[index] == Piece.wQueen) {
                return false;
            }
        }

        for (int x = kingX - 1; x >= 1; x--) {
            int index = Board.getSquareIndex(x, kingY);

            if (testBoard[index] < 0 || testBoard[index] == Piece.wPawn || testBoard[index] == Piece.wKnight ||
                    testBoard[index] == Piece.wBishop || testBoard[index] == Piece.wKing) {
                break;
            }

            if (testBoard[index] == Piece.wRook || testBoard[index] == Piece.wQueen) {
                return false;
            }
        }

        return true;
    }

    public List<Move> generateWhitePawnMoves(int pinnedPieceIndex, int pinningPieceIndex) {
        List<Move> legalMoves = new ArrayList<>();

        int pinnedPieceX = Board.getSquareCoordinates(pinnedPieceIndex).x;
        int pinnedPieceY = Board.getSquareCoordinates(pinnedPieceIndex).y;

        int pinningPieceX = Board.getSquareCoordinates(pinningPieceIndex).x;
        int pinningPieceY = Board.getSquareCoordinates(pinningPieceIndex).y;

        if (pinnedPieceX == pinningPieceX) { // piece is pinned on the vertical and piece can move
            if (pinnedPieceY + 1 <= 8 && board.getPiece(pinnedPieceX, pinnedPieceY + 1) == 0) {
                legalMoves.add(new Move(game, pinnedPieceX, pinnedPieceY, pinnedPieceX, pinnedPieceY + 1));
            }

            // 2 square move
            if (pinnedPieceY == 2 && board.getPiece(pinnedPieceX, pinnedPieceY + 1) == 0 && board.getPiece(pinningPieceX, pinnedPieceY + 2) == 0) {
                legalMoves.add(new Move(game, pinnedPieceX, pinnedPieceY, pinnedPieceX, pinnedPieceY + 2));
            }
        } else { // piece is pinned on the diagonal
            if (pinnedPieceIndex + 9 == pinningPieceIndex) { // pinning piece is capturable to the right of pinned pawn
                legalMoves.add(new Move(game, pinnedPieceX, pinnedPieceY, pinnedPieceX + 1, pinnedPieceY + 1));
            } else if (pinnedPieceIndex + 7 == pinningPieceIndex) { // pinning piece is capturable to the left of pinned pawn
                legalMoves.add(new Move(game, pinnedPieceX, pinnedPieceY, pinnedPieceX - 1, pinnedPieceY + 1));
            }

            // en passant moves

            if (!board.getMoveList().isEmpty()) {
                List<Move> moves = board.getMoveList();
                int mostRecent = moves.size() - 1;

                if ((pinnedPieceX < pinningPieceX && pinnedPieceY < pinningPieceY) ||
                        (pinnedPieceX > pinningPieceX && pinnedPieceY > pinningPieceY)) { // blue
                    if (pinnedPieceY == 5 && moves.get(mostRecent).getMovedPiece() == Piece.bPawn && moves.get(mostRecent).getEndY() == 5 &&
                            moves.get(mostRecent).getStartY() == 7 && moves.get(mostRecent).getEndX() == pinnedPieceX + 1 &&
                            legalWhiteEnPassant(pinnedPieceX, pinnedPieceY, pinnedPieceX + 1, pinnedPieceY)) {
                        legalMoves.add(new Move(game, pinnedPieceX, pinnedPieceY, pinnedPieceX + 1, pinnedPieceY + 1));
                    }

                } else if ((pinnedPieceX < pinningPieceX && pinnedPieceY > pinningPieceY) ||
                        (pinnedPieceX > pinningPieceX && pinnedPieceY < pinningPieceY)) { // green
                    if (pinnedPieceY == 5 && moves.get(mostRecent).getMovedPiece() == Piece.bPawn && moves.get(mostRecent).getEndY() == 5 &&
                            moves.get(mostRecent).getStartY() == 7 && moves.get(mostRecent).getEndX() == pinnedPieceX - 1 &&
                            legalWhiteEnPassant(pinnedPieceX, pinnedPieceY, pinnedPieceX - 1, pinnedPieceY)) {
                        legalMoves.add(new Move(game, pinnedPieceX, pinnedPieceY, pinnedPieceX - 1, pinnedPieceY + 1));
                    }
                }
            }
        }

        return legalMoves;
    }

    private boolean legalWhiteEnPassant(int whitePawnX, int whitePawnY, int blackPawnX, int blackPawnY) {
        int[] testBoard = board.getBoard().clone();
        testBoard[Board.getSquareIndex(whitePawnX, whitePawnY)] = Piece.empty;
        testBoard[Board.getSquareIndex(blackPawnX, blackPawnY)] = Piece.empty;

        int kingX = Board.getSquareCoordinates(friendlyKingIndex).x;
        int kingY = Board.getSquareCoordinates(friendlyKingIndex).y;

        for (int x = kingX + 1; x <= 8; x++) {
            int index = Board.getSquareIndex(x, kingY);

            if (testBoard[index] > 0 || testBoard[index] == Piece.bPawn || testBoard[index] == Piece.bKnight ||
                    testBoard[index] == Piece.bBishop || testBoard[index] == Piece.bKing) {
                break;
            }

            if (testBoard[index] == Piece.bRook || testBoard[index] == Piece.bQueen) {
                return false;
            }
        }

        for (int x = kingX - 1; x >= 1; x--) {
            int index = Board.getSquareIndex(x, kingY);

            if (testBoard[index] > 0 || testBoard[index] == Piece.bPawn || testBoard[index] == Piece.bKnight ||
                    testBoard[index] == Piece.bBishop || testBoard[index] == Piece.bKing) {
                break;
            }

            if (testBoard[index] == Piece.bRook || testBoard[index] == Piece.bQueen) {
                return false;
            }
        }

        return true;
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
                legalMoves.add(new Move(game, pinnedPieceX, pinnedPieceY, square.x, square.y));
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
                legalMoves.add(new Move(game, pinnedPieceX, pinnedPieceY, square.x, square.y));
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
                legalMoves.add(new Move(game, pinnedPieceX, pinnedPieceY, square.x, square.y));
            }
        } else {
            // pin is coming on the diagonal
            List<Point> possibleMoveEndSquareIndices = diagonalSlidingRaysToSquare(pinnedPieceX, pinnedPieceY, pinningPieceX, pinningPieceY);
            possibleMoveEndSquareIndices.addAll(diagonalSlidingRaysUpToSquare(pinnedPieceX, pinnedPieceY, friendlyKingX, friendlyKingY));

            for (Point square: possibleMoveEndSquareIndices) {
                legalMoves.add(new Move(game, pinnedPieceX, pinnedPieceY, square.x, square.y));
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

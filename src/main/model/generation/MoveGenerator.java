package model.generation;

import model.Board;
import model.Game;
import model.Move;
import model.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static java.lang.Math.abs;

// Represents a move generator which generates only legal chess moves
public class MoveGenerator {
    private Game game;
    private Board board;
    private boolean whiteToPlay;
    private boolean inCheck;
    private boolean inDoubleCheck;
    private int friendlyKingIndex;
    private ThreatMapGenerator threatMapGenerator;

    private List<Integer> checkers;
    private List<Integer> pawnCheckers;
    private List<Integer> knightCheckers;
    private List<Integer> bishopCheckers;
    private List<Integer> rookCheckers;
    private List<Integer> queenCheckers;

    private List<Integer> captureMask;
    private List<Integer> pushMask;

    private List<Integer> pinnedPieces; // pieces that are being pinned
    private List<Integer> pinningPieces; // pieces that are pinning another piece
    private boolean pinsExistInPosition;

    private List<Move> legalMoves;
    private Set<Integer> threatMap;

    /**
     * Generate all legal moves for the given player in the given game board position
     */
    public List<Move> generateMoves(Game game, Player player) {
        init(game, player);

        for (int i = 0; i < 64; i++) {
            captureMask.add(i);
            pushMask.add(i);
        }

        calculatePins();

        if (pinnedPieces.size() > 0) {
            pinsExistInPosition = true;
        }

        if (inCheck) {
            updateMasks();
            escapeCheck();
            return legalMoves;
        }

        int index = 0;
        for (int piece: game.getBoard().getBoard()) {

            if ((piece > 0 && player.isWhite()) || (piece < 0 && !player.isWhite())) {

                int startX = Board.getSquareCoordinates(index).x;
                int startY = Board.getSquareCoordinates(index).y;

                if (piece == -1) {
                    generateBlackPawnMoves(startX, startY);
                } else if (piece == 1) {
                    generateWhitePawnMoves(startX, startY);
                }

                switch (abs(piece)) {
                    case 2:
                        generateKnightMoves(startX, startY);
                        break;
                    case 3:
                        generateBishopMoves(startX, startY);
                        break;
                    case 4:
                        generateRookMoves(startX, startY);
                        break;
                    case 5:
                        generateQueenMoves(startX, startY);
                        break;
                    case 6:
                        generateKingMoves(startX, startY);
                        break;
                }
            }

            index++;
        }

        return legalMoves;
    }

    /**
     * Initialize this MoveGenerator object
     */
    private void init(Game game, Player player) {
        this.game = game;
        this.board = game.getBoard();
        this.whiteToPlay = player.isWhite();
        this.inCheck = false;
        this.inDoubleCheck = false;
        this.legalMoves = new ArrayList<>();
        this.threatMapGenerator = new ThreatMapGenerator();

        this.checkers = new ArrayList<>();
        this.pawnCheckers = new ArrayList<>();
        this.knightCheckers = new ArrayList<>();
        this.bishopCheckers = new ArrayList<>();
        this.rookCheckers = new ArrayList<>();
        this.queenCheckers = new ArrayList<>();
        this.pinnedPieces = new ArrayList<>();
        this.pinningPieces = new ArrayList<>();

        this.pinsExistInPosition = false;

        this.captureMask = new ArrayList<>();
        this.pushMask = new ArrayList<>();

        if (whiteToPlay) {
            threatMap = threatMapGenerator.generateBlackThreatMap(game);
        } else {
            threatMap = threatMapGenerator.generateWhiteThreatMap(game);
        }

        int index = 0;
        for (int piece: board.getBoard()) {
            if (whiteToPlay && piece == 6) {
                friendlyKingIndex = index;
            } else if (!whiteToPlay && piece == -6) {
                friendlyKingIndex = index;
            }
            index++;
        }

        isInCheck();
    }

    /**
     * Calculate the index all friendly pinned pieces within the current position
     */
    private void calculatePins() {
        // find all sliding enemy pieces

        // generate all possible attacks from that those pieces, stop when reach edge of board, same colour piece,
        // other colour king, more than one other colour piece

        int index = 0;
        for (int piece: board.getBoard()) {
            if (whiteToPlay) {
                switch (piece) {
                    case -3: // black bishop
                        findDiagonalPins(index);
                        break;
                    case -4: // black rook
                        findStraightPins(index);
                        break;
                    case -5: // black queen
                        findStraightPins(index);
                        findDiagonalPins(index);
                        break;
                }
            } else {
                switch (piece) {
                    case 3: // white bishop
                        findDiagonalPins(index);
                        break;
                    case 4: // white rook
                        findStraightPins(index);
                        break;
                    case 5: // white queen
                        findStraightPins(index);
                        findDiagonalPins(index);
                        break;
                }
            }

            index++;
        }
    }

    /**
     * Generate all legal moves that can escape from check
     */
    private void escapeCheck() {
        int index = 0;
        for (int piece: game.getBoard().getBoard()) {

            if ((piece > 0 && whiteToPlay) || (piece < 0 && !whiteToPlay)) {

                int startX = Board.getSquareCoordinates(index).x;
                int startY = Board.getSquareCoordinates(index).y;

                if (piece == -1) {
                    generateBlackPawnMovesInCheck(startX, startY);
                } else if (piece == 1) {
                    generateWhitePawnMovesInCheck(startX, startY);
                }

                switch (abs(piece)) {
                    case 2:
                        generateKnightMovesInCheck(startX, startY);
                        break;
                    case 3:
                        generateBishopMovesInCheck(startX, startY);
                        break;
                    case 4:
                        generateRookMovesInCheck(startX, startY);
                        break;
                    case 5:
                        generateQueenMovesInCheck(startX, startY);
                        break;
                    case 6:
                        generateKingMoves(startX, startY);
                        break;
                }
            }

            index++;
        }
    }

    /**
     * Update the checkers and push maps
     */
    private void updateMasks() {
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
            // otherwise pushMask is empty because we can't block the check
        }
    }

    /**
     * Generate all squares in between the bishop, on the given index, and the friendly king
     * @param diagonalPieceIndex the index of the bishop on the current board
     */
    private List<Integer> diagonalSlidingRaysToSquare(int squareIndex, int diagonalPieceIndex) {
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
    private List<Integer> straightSlidingRaysToSquares(int squareIndex, int straightPieceIndex) {
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
     * Generate all white pins on the board from black diagonal sliding pieces
     */
    private void findDiagonalPins(int pieceIndex) {
        int startX = Board.getSquareCoordinates(pieceIndex).x;
        int startY = Board.getSquareCoordinates(pieceIndex).y;

        this.pinnedPieces.addAll(findDiagonalPinsNorthWest(startX, startY));
        this.pinnedPieces.addAll(findDiagonalPinsNorthEast(startX, startY));
        this.pinnedPieces.addAll(findDiagonalPinsSouthWest(startX, startY));
        this.pinnedPieces.addAll(findDiagonalPinsSouthEast(startX, startY));
    }

    private List<Integer> findDiagonalPinsNorthWest(int startX, int startY) {
        List<Integer> pinnedPieces = new ArrayList<>();
        List<Integer> pinningPieces = new ArrayList<>();
        List<Integer> possiblePinnedPieces = new ArrayList<>();
        List<Integer> possiblePinningPieces = new ArrayList<>();

        int y = startY + 1;
        boolean attackKing = false;
        if (whiteToPlay) {
            for (int x = startX - 1; x >= 1; x--) {
                if (y > 8) {
                    break;
                }

                int piece = board.getPiece(x, y);

                if (piece < 0) {
                    break;
                }

                if (piece == 6) {
                    attackKing = true;
                    break;
                }

                if (piece > 0) {
                    possiblePinningPieces.add(Board.getSquareIndex(startX, startY));
                    possiblePinnedPieces.add(Board.getSquareIndex(x,y));
                }

                y++;
            }

        } else {
            for (int x = startX - 1; x >= 1; x--) {
                if (y > 8) {
                    break;
                }

                int piece = board.getPiece(x, y);

                if (piece > 0) {
                    break;
                }

                if (piece == -6) {
                    attackKing = true;
                    break;
                }

                if (piece < 0) {
                    possiblePinningPieces.add(Board.getSquareIndex(startX, startY));
                    possiblePinnedPieces.add(Board.getSquareIndex(x,y));
                }

                y++;
            }

        }
        if (attackKing && possiblePinnedPieces.size() == 1) {
            pinningPieces.add(possiblePinningPieces.get(0));
            pinnedPieces.add(possiblePinnedPieces.get(0));
        }

        this.pinningPieces.addAll(pinningPieces);
        return pinnedPieces;
    }

    private List<Integer> findDiagonalPinsNorthEast(int startX, int startY) {
        List<Integer> pinnedPieces = new ArrayList<>();
        List<Integer> pinningPieces = new ArrayList<>();
        List<Integer> possiblePinnedPieces = new ArrayList<>();
        List<Integer> possiblePinningPieces = new ArrayList<>();


        int y = startY + 1;
        boolean attackKing = false;
        if (whiteToPlay) {
            for (int x = startX + 1; x <= 8; x++) {
                if (y > 8) {
                    break;
                }

                int piece = board.getPiece(x, y);

                if (piece < 0) {
                    break;
                }

                if (piece == 6) {
                    attackKing = true;
                    break;
                }

                if (piece > 0) {
                    possiblePinnedPieces.add(Board.getSquareIndex(x,y));
                    possiblePinningPieces.add(Board.getSquareIndex(startX, startY));
                }

                y++;
            }
        } else {
            for (int x = startX + 1; x <= 8; x++) {
                if (y > 8) {
                    break;
                }

                int piece = board.getPiece(x, y);

                if (piece > 0) {
                    break;
                }

                if (piece == -6) {
                    attackKing = true;
                    break;
                }

                if (piece < 0) {
                    possiblePinnedPieces.add(Board.getSquareIndex(x,y));
                    possiblePinningPieces.add(Board.getSquareIndex(startX, startY));
                }

                y++;
            }
        }
        if (attackKing && possiblePinnedPieces.size() == 1) {
            pinnedPieces.add(possiblePinnedPieces.get(0));
            pinningPieces.add(possiblePinningPieces.get(0));
        }

        this.pinningPieces.addAll(pinningPieces);
        return pinnedPieces;
    }

    private List<Integer> findDiagonalPinsSouthWest(int startX, int startY) {
        List<Integer> pinnedPieces = new ArrayList<>();
        List<Integer> pinningPieces = new ArrayList<>();
        List<Integer> possiblePinnedPieces = new ArrayList<>();
        List<Integer> possiblePinningPieces = new ArrayList<>();

        int y = startY - 1;
        boolean attackKing = false;

        if (whiteToPlay) {
            for (int x = startX - 1; x >= 1; x--) {
                if (y < 1) {
                    break;
                }

                int piece = board.getPiece(x, y);

                if (piece < 0) {
                    break;
                }

                if (piece == 6) {
                    attackKing = true;
                    break;
                }

                if (piece > 0) {
                    possiblePinningPieces.add(Board.getSquareIndex(startX, startY));
                    possiblePinnedPieces.add(Board.getSquareIndex(x,y));
                }

                y--;
            }
        } else {
            for (int x = startX - 1; x >= 1; x--) {
                if (y < 1) {
                    break;
                }

                int piece = board.getPiece(x, y);

                if (piece > 0) {
                    break;
                }

                if (piece == -6) {
                    attackKing = true;
                    break;
                }

                if (piece < 0) {
                    possiblePinningPieces.add(Board.getSquareIndex(startX, startY));
                    possiblePinnedPieces.add(Board.getSquareIndex(x,y));
                }

                y--;
            }
        }

        if (attackKing && possiblePinnedPieces.size() == 1) {
            pinningPieces.add(possiblePinningPieces.get(0));
            pinnedPieces.add(possiblePinnedPieces.get(0));
        }

        this.pinningPieces.addAll(pinningPieces);
        return pinnedPieces;
    }

    private List<Integer> findDiagonalPinsSouthEast(int startX, int startY) {
        List<Integer> pinnedPieces = new ArrayList<>();
        List<Integer> pinningPieces = new ArrayList<>();
        List<Integer> possiblePinnedPieces = new ArrayList<>();
        List<Integer> possiblePinningPieces = new ArrayList<>();


        int y = startY - 1;
        boolean attackKing = false;

        if (whiteToPlay) {
            for (int x = startX + 1; x <= 8; x++) {
                if (y < 1) {
                    break;
                }

                int piece = board.getPiece(x, y);

                if (piece < 0) {
                    break;
                }

                if (piece == 6) {
                    attackKing = true;
                    break;
                }

                if (piece > 0) {
                    possiblePinningPieces.add(Board.getSquareIndex(startX, startY));
                    possiblePinnedPieces.add(Board.getSquareIndex(x,y));
                }

                y--;
            }
        } else {
            for (int x = startX + 1; x <= 8; x++) {
                if (y < 1) {
                    break;
                }

                int piece = board.getPiece(x, y);

                if (piece > 0) {
                    break;
                }

                if (piece == -6) {
                    attackKing = true;
                    break;
                }

                if (piece < 0) {
                    possiblePinningPieces.add(Board.getSquareIndex(startX, startY));
                    possiblePinnedPieces.add(Board.getSquareIndex(x,y));
                }

                y--;
            }
        }

        if (attackKing && possiblePinnedPieces.size() == 1) {
            pinnedPieces.add(possiblePinnedPieces.get(0));
            pinningPieces.add(possiblePinningPieces.get(0));
        }

        this.pinningPieces.addAll(pinningPieces);
        return pinnedPieces;
    }

    /**
     * Generate all white pins on the board from straight sliding pieces
     */
    private void findStraightPins(int pieceIndex) {
        int startX = Board.getSquareCoordinates(pieceIndex).x;
        int startY = Board.getSquareCoordinates(pieceIndex).y;

        this.pinnedPieces.addAll(findStraightPinsNorth(startX, startY));
        this.pinnedPieces.addAll(findStraightPinsSouth(startX, startY));
        this.pinnedPieces.addAll(findStraightPinsWest(startX, startY));
        this.pinnedPieces.addAll(findStraightPinsEast(startX, startY));
    }

    private List<Integer> findStraightPinsNorth(int startX, int startY) {
        List<Integer> pinnedPieces = new ArrayList<>();
        List<Integer> pinningPieces = new ArrayList<>();
        List<Integer> possiblePinnedPieces = new ArrayList<>();
        List<Integer> possiblePinningPieces = new ArrayList<>();

        boolean attackKing = false;

        if (whiteToPlay) {
            for (int y = startY + 1; y <= 8; y++) {
                int piece = board.getPiece(startX, y);

                if (piece < 0) {
                    break;
                }

                if (piece == 6) {
                    attackKing = true;
                    break;
                }

                if (piece > 0) {
                    possiblePinnedPieces.add(Board.getSquareIndex(startX,y));
                    possiblePinningPieces.add(Board.getSquareIndex(startX, startY));
                }
            }
        } else {
            for (int y = startY + 1; y <= 8; y++) {
                int piece = board.getPiece(startX, y);

                if (piece > 0) {
                    break;
                }

                if (piece == -6) {
                    attackKing = true;
                    break;
                }

                if (piece < 0) {
                    possiblePinnedPieces.add(Board.getSquareIndex(startX,y));
                    possiblePinningPieces.add(Board.getSquareIndex(startX, startY));
                }

            }
        }

        if (attackKing && possiblePinnedPieces.size() == 1) {
            pinnedPieces.add(possiblePinnedPieces.get(0));
            pinningPieces.add(possiblePinningPieces.get(0));
        }

        this.pinningPieces.addAll(pinningPieces);
        return pinnedPieces;
    }

    private List<Integer> findStraightPinsSouth(int startX, int startY) {
        List<Integer> pinnedPieces = new ArrayList<>();
        List<Integer> pinningPieces = new ArrayList<>();
        List<Integer> possiblePinnedPieces = new ArrayList<>();
        List<Integer> possiblePinningPieces = new ArrayList<>();


        boolean attackKing = false;

        if (whiteToPlay) {
            for (int y = startY - 1; y >= 8; y--) {
                int piece = board.getPiece(startX, y);

                if (piece < 0) {
                    break;
                }

                if (piece == 6) {
                    attackKing = true;
                    break;
                }

                if (piece > 0) {
                    possiblePinningPieces.add(Board.getSquareIndex(startX,startY));
                    possiblePinnedPieces.add(Board.getSquareIndex(startX,y));
                }
            }
        } else {
            for (int y = startY - 1; y >= 8; y--) {
                int piece = board.getPiece(startX, y);

                if (piece > 0) {
                    break;
                }

                if (piece == -6) {
                    attackKing = true;
                    break;
                }

                if (piece < 0) {
                    possiblePinningPieces.add(Board.getSquareIndex(startX,startY));
                    possiblePinnedPieces.add(Board.getSquareIndex(startX,y));
                }

            }
        }

        if (attackKing && possiblePinnedPieces.size() == 1) {
            pinningPieces.add(possiblePinningPieces.get(0));
            pinnedPieces.add(possiblePinnedPieces.get(0));
        }

        this.pinningPieces.addAll(pinningPieces);
        return pinnedPieces;
    }

    private List<Integer> findStraightPinsWest(int startX, int startY) {
        List<Integer> pinnedPieces = new ArrayList<>();
        List<Integer> pinningPieces = new ArrayList<>();
        List<Integer> possiblePinnedPieces = new ArrayList<>();
        List<Integer> possiblePinningPieces = new ArrayList<>();

        boolean attackKing = false;

        if (whiteToPlay) {
            for (int x = startX - 1; x >= 1; x--) {
                int piece = board.getPiece(x, startY);

                if (piece < 0) {
                    break;
                }

                if (piece == 6) {
                    attackKing = true;
                    break;
                }

                if (piece > 0) {
                    possiblePinningPieces.add(Board.getSquareIndex(startX,startY));
                    possiblePinnedPieces.add(Board.getSquareIndex(x, startY));
                }
            }
        } else {
            for (int x = startX - 1; x >= 1; x--) {
                int piece = board.getPiece(x, startY);

                if (piece > 0) {
                    break;
                }

                if (piece == -6) {
                    attackKing = true;
                    break;
                }

                if (piece < 0) {
                    possiblePinningPieces.add(Board.getSquareIndex(startX,startY));
                    possiblePinnedPieces.add(Board.getSquareIndex(x, startY));
                }
            }
        }

        if (attackKing && possiblePinnedPieces.size() == 1) {
            pinningPieces.add(possiblePinningPieces.get(0));
            pinnedPieces.add(possiblePinnedPieces.get(0));
        }

        this.pinningPieces.addAll(pinningPieces);
        return pinnedPieces;
    }

    private List<Integer> findStraightPinsEast(int startX, int startY) {
        List<Integer> pinnedPieces = new ArrayList<>();
        List<Integer> pinningPieces = new ArrayList<>();
        List<Integer> possiblePinnedPieces = new ArrayList<>();
        List<Integer> possiblePinningPieces = new ArrayList<>();

        boolean attackKing = false;

        if (whiteToPlay) {
            for (int x = startX + 1; x <= 8; x++) {
                int piece = board.getPiece(x, startY);

                if (piece < 0) {
                    break;
                }

                if (piece == 6) {
                    attackKing = true;
                    break;
                }

                if (piece > 0) {
                    possiblePinningPieces.add(Board.getSquareIndex(startX,startY));
                    possiblePinnedPieces.add(Board.getSquareIndex(x, startY));
                }
            }
        } else {
            for (int x = startX + 1; x <= 8; x++) {
                int piece = board.getPiece(x, startY);

                if (piece > 0) {
                    break;
                }

                if (piece == -6) {
                    attackKing = true;
                    break;
                }

                if (piece < 0) {
                    possiblePinningPieces.add(Board.getSquareIndex(startX,startY));
                    possiblePinnedPieces.add(Board.getSquareIndex(x, startY));
                }
            }
        }

        if (attackKing && possiblePinnedPieces.size() == 1) {
            pinningPieces.add(possiblePinningPieces.get(0));
            pinnedPieces.add(possiblePinnedPieces.get(0));
        }

        this.pinningPieces.addAll(pinningPieces);
        return pinnedPieces;
    }

    /**
     * Determine whether the current player is in check in the current position
     */
    private void isInCheck() {
        Set<Integer> possiblePawnChecks;
        if (whiteToPlay) {
            possiblePawnChecks = threatMapGenerator.generateWhitePawnThreatMap(game, friendlyKingIndex);
        } else {
            possiblePawnChecks = threatMapGenerator.generateBlackPawnThreatMap(game, friendlyKingIndex);
        }

        Set<Integer> possibleKnightChecks = threatMapGenerator.generateKnightThreatMap(game, friendlyKingIndex);
        Set<Integer> possibleBishopChecks = threatMapGenerator.generateBishopThreatMap(game, friendlyKingIndex);
        Set<Integer> possibleRookChecks = threatMapGenerator.generateRookThreatMap(game, friendlyKingIndex);
        Set<Integer> possibleQueenChecks = threatMapGenerator.generateQueenThreatMap(game, friendlyKingIndex);

        for (Integer index: possiblePawnChecks) {
            if (board.getPiece(Board.getSquareCoordinates(index).x, Board.getSquareCoordinates(index).y) == -1 && whiteToPlay ||
            board.getPiece(Board.getSquareCoordinates(index).x, Board.getSquareCoordinates(index).y) == 1 && !whiteToPlay) {
                inDoubleCheck = inCheck;
                inCheck = true;
                checkers.add(index);
                pawnCheckers.add(index);
            }
        }

        for (Integer index: possibleKnightChecks) {
            if (board.getPiece(Board.getSquareCoordinates(index).x, Board.getSquareCoordinates(index).y) == -2 && whiteToPlay ||
            board.getPiece(Board.getSquareCoordinates(index).x, Board.getSquareCoordinates(index).y) == 2 && !whiteToPlay) {
                inDoubleCheck = inCheck;
                inCheck = true;
                checkers.add(index);
                knightCheckers.add(index);
            }
        }

        for (Integer index: possibleBishopChecks) {
            if (board.getPiece(Board.getSquareCoordinates(index).x, Board.getSquareCoordinates(index).y) == -3 && whiteToPlay ||
                    board.getPiece(Board.getSquareCoordinates(index).x, Board.getSquareCoordinates(index).y) == 3 && !whiteToPlay) {
                inDoubleCheck = inCheck;
                inCheck = true;
                checkers.add(index);
                bishopCheckers.add(index);
            }
        }

        for (Integer index: possibleRookChecks) {
            if (board.getPiece(Board.getSquareCoordinates(index).x, Board.getSquareCoordinates(index).y) == -4 && whiteToPlay ||
                    board.getPiece(Board.getSquareCoordinates(index).x, Board.getSquareCoordinates(index).y) == 4 && !whiteToPlay) {
                inDoubleCheck = inCheck;
                inCheck = true;
                checkers.add(index);
                rookCheckers.add(index);
            }
        }

        for (Integer index: possibleQueenChecks) {
            if (board.getPiece(Board.getSquareCoordinates(index).x, Board.getSquareCoordinates(index).y) == -5 && whiteToPlay ||
                    board.getPiece(Board.getSquareCoordinates(index).x, Board.getSquareCoordinates(index).y) == 5 && !whiteToPlay) {
                inDoubleCheck = inCheck;
                inCheck = true;
                checkers.add(index);
                queenCheckers.add(index);
            }
        }
    }

    /**
     * Generate all pseudo-legal black pawn moves originating from the square at the given startX and startY
     */
    private void generateBlackPawnMoves(int startX, int startY) {
        if (pinsExistInPosition) {
            int pinningPieceIndex = 0;
            for (int pinnedPieceIndex: pinnedPieces) {
                if (pinnedPieceIndex == Board.getSquareIndex(startX, startY)) {
                    generateBlackPawnPinMoves(pinnedPieceIndex, pinningPieceIndex);
                }

                    pinningPieceIndex++;
            }
            return;
        }

        // 1 square move
        if (startY - 1 >= 1 && board.getPiece(startX, startY - 1) == 0) {
            legalMoves.add(new Move(board, startX, startY, startX, startY - 1));
        }

        // 2 square move
        if (startY == 7 && board.getPiece(startX, startY - 1) == 0 && board.getPiece(startX, startY - 2) == 0) {
            legalMoves.add(new Move(board, startX, startY, startX, startY - 2));
        }

        // captures
        if (startX - 1 >= 1 && startY - 1 >= 1 && board.getPiece(startX - 1, startY - 1) > 0) {
            legalMoves.add(new Move(board, startX, startY, startX - 1, startY - 1));
        }

        if (startX + 1 <= 8 && startY - 1 >= 1 && board.getPiece(startX + 1, startY - 1) > 0) {
            legalMoves.add(new Move(board, startX, startY, startX + 1, startY - 1));
        }

        // en passant
        if (startY == 4 && game.getMoveList().get(0).getEndY() == 4 && game.getMoveList().get(0).getStartY() == 2 && game.getMoveList().get(0).getEndX() == startX - 1) {
            legalMoves.add(new Move(board, startX, startY, startX - 1, startY - 1));
        }

        if (startY == 4 && game.getMoveList().get(0).getEndY() == 4 && game.getMoveList().get(0).getStartY() == 2 && game.getMoveList().get(0).getEndX() == startX + 1) {
            legalMoves.add(new Move(board, startX, startY, startX + 1, startY - 1));
        }
    }

    private void generateBlackPawnPinMoves(int pinnedPieceIndex, int pinningPieceIndex) {
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
            } // en passant moves
        }
    }

    /**
     * Generate all legal black pawn moves originating from the square at the given startX and startY, when in check
     */
    private void generateBlackPawnMovesInCheck(int startX, int startY) {
        if (pushMask.isEmpty() && captureMask.isEmpty()) {
            return;
        }

        if (pinsExistInPosition && pinnedPieces.contains(Board.getSquareIndex(startX, startY))) {
            return;
        }

        // 1 square move
        if (startY - 1 >= 1 && board.getPiece(startX, startY - 1) == 0) {
            int endIndex = Board.getSquareIndex(startX, startY - 1);

            if (!pushMask.isEmpty() && pushMask.contains(endIndex)) {
                legalMoves.add(new Move(board, startX, startY, startX, startY - 1));
            }
        }

        // 2 square move
        if (startY == 7 && board.getPiece(startX, startY - 1) == 0 && board.getPiece(startX, startY - 2) == 0) {
            int endIndex = Board.getSquareIndex(startX, startY - 2);

            if (!pushMask.isEmpty() && pushMask.contains(endIndex)) {
                legalMoves.add(new Move(board, startX, startY, startX, startY - 2));
            }
        }

        // captures
        if (startX - 1 >= 1 && startY - 1 >= 1 && board.getPiece(startX - 1, startY - 1) > 0) {
            int endIndex = Board.getSquareIndex(startX - 1, startY - 1);

            if (!captureMask.isEmpty() && captureMask.contains(endIndex)) {
                legalMoves.add(new Move(board, startX, startY, startX - 1, startY - 1));
            }

            if (!pushMask.isEmpty() && pushMask.contains(endIndex)) {
                legalMoves.add(new Move(board, startX, startY, startX - 1, startY - 1));
            }
        }

        if (startX + 1 <= 8 && startY - 1 >= 1 && board.getPiece(startX + 1, startY - 1) > 0) {
            int endIndex = Board.getSquareIndex(startX + 1, startY - 1);

            if (!captureMask.isEmpty() && captureMask.contains(endIndex)) {
                legalMoves.add(new Move(board, startX, startY, startX + 1, startY - 1));
            }

            if (!pushMask.isEmpty() && pushMask.contains(endIndex)) {
                legalMoves.add(new Move(board, startX, startY, startX + 1, startY - 1));
            }
        }

        // en passant
        if (!game.getMoveList().isEmpty() && startY == 4 && game.getMoveList().get(0).getEndY() == 4 && game.getMoveList().get(0).getStartY() == 2) {

            if (game.getMoveList().get(0).getEndX() == startX - 1) {
                int endIndex = Board.getSquareIndex(startX - 1, startY - 1);
                int captureIndex = Board.getSquareIndex(startX - 1, startY);

                if (!captureMask.isEmpty() && captureMask.contains(captureIndex)) {
                    legalMoves.add(new Move(board, startX, startY, startX - 1, startY - 1));
                }

                if (!pushMask.isEmpty() && pushMask.contains(endIndex)) {
                    legalMoves.add(new Move(board, startX, startY, startX - 1, startY - 1));
                }
            }

            if (game.getMoveList().get(0).getEndX() == startX + 1) {
                int endIndex = Board.getSquareIndex(startX + 1, startY - 1);
                int captureIndex = Board.getSquareIndex(startX + 1, startY);

                if (!captureMask.isEmpty() && captureMask.contains(captureIndex)) {
                    legalMoves.add(new Move(board, startX, startY, startX + 1, startY - 1));
                }

                if (!pushMask.isEmpty() && pushMask.contains(endIndex)) {
                    legalMoves.add(new Move(board, startX, startY, startX + 1, startY - 1));
                }
            }
        }
    }

    /**
     * Generate all pseudo-legal white pawn moves originating from the square at the given startX and startY
     */
    private void generateWhitePawnMoves(int startX, int startY) {
        if (pinsExistInPosition) {
            // can only move straight if the pieces is pinned vertically
            // can only capture if piece pinning the pawn is right next to it
        }

        // 1 square move
        if (startY + 1 <= 8 && board.getPiece(startX, startY + 1) == 0) {
            legalMoves.add(new Move(board, startX, startY, startX, startY + 1));
        }

        // 2 square move
        if (startY == 2 && board.getPiece(startX, startY + 1) == 0 && board.getPiece(startX, startY + 2) == 0) {
            legalMoves.add(new Move(board, startX, startY, startX, startY + 2));
        }

        // captures
        if (startX + 1 <= 8 && startY + 1 <= 8 && board.getPiece(startX + 1, startY + 1) < 0) {
            legalMoves.add(new Move(board, startX, startY, startX + 1, startY + 1));
        }

        if (startX - 1 >= 1 && startY + 1 <= 8 && board.getPiece(startX - 1, startY + 1) < 0) {
            legalMoves.add(new Move(board, startX, startY, startX - 1, startY + 1));
        }

        // en passant
        if (!game.getMoveList().isEmpty()) {
            if (startY == 5 && game.getMoveList().get(0).getEndY() == 5 && game.getMoveList().get(0).getStartY() == 7 && game.getMoveList().get(0).getEndX() == startX - 1) {
                legalMoves.add(new Move(board, startX, startY, startX - 1, startY + 1));
            }
            if (startY == 5 && game.getMoveList().get(0).getEndY() == 5 && game.getMoveList().get(0).getStartY() == 7 && game.getMoveList().get(0).getEndX() == startX + 1) {
                legalMoves.add(new Move(board, startX, startY, startX + 1, startY + 1));
            }
        }
    }

    /**
     * Generate all legal white pawn moves originating from the square at the given startX and startY, when in check
     */
    private void generateWhitePawnMovesInCheck(int startX, int startY) {
        if (captureMask.isEmpty() && pushMask.isEmpty()) {
            return;
        }

        if (pinsExistInPosition && pinnedPieces.contains(Board.getSquareIndex(startX, startY))) {
            return;
        }

        // 1 square move
        if (startY + 1 <= 8 && board.getPiece(startX, startY + 1) == 0) {
            int endIndex = Board.getSquareIndex(startX, startY + 1);

            if (!pushMask.isEmpty() && pushMask.contains(endIndex)) {
                legalMoves.add(new Move(board, startX, startY, startX, startY + 1));
            }
        }

        // 2 square move
        if (startY == 2 && board.getPiece(startX, startY + 1) == 0 && board.getPiece(startX, startY + 2) == 0) {
            int endIndex = Board.getSquareIndex(startX, startY + 2);

            if (!pushMask.isEmpty() && pushMask.contains(endIndex)) {
                legalMoves.add(new Move(board, startX, startY, startX, startY + 2));
            }
        }

        // captures
        if (startX + 1 <= 8 && startY + 1 <= 8 && board.getPiece(startX + 1, startY + 1) < 0) {
            int endIndex = Board.getSquareIndex(startX + 1, startY + 1);

            if (!captureMask.isEmpty() && captureMask.contains(endIndex)) {
                legalMoves.add(new Move(board, startX, startY, startX + 1, startY + 1));
            }

            if (!pushMask.isEmpty() && pushMask.contains(endIndex)) {
                legalMoves.add(new Move(board, startX, startY, startX + 1, startY + 1));
            }
        }

        if (startX - 1 >= 1 && startY + 1 <= 8 && board.getPiece(startX - 1, startY + 1) < 0) {
            int endIndex = Board.getSquareIndex(startX - 1, startY + 1);

            if (!captureMask.isEmpty() && captureMask.contains(endIndex)) {
                legalMoves.add(new Move(board, startX, startY, startX - 1, startY + 1));
            }

            if (!pushMask.isEmpty() && pushMask.contains(endIndex)) {
                legalMoves.add(new Move(board, startX, startY, startX - 1, startY + 1));
            }
        }

        // en passant
        if (!game.getMoveList().isEmpty() && startY == 5 && game.getMoveList().get(0).getEndY() == 5 && game.getMoveList().get(0).getStartY() == 7) {

            if (game.getMoveList().get(0).getEndX() == startX - 1) {
                int endIndex = Board.getSquareIndex(startX - 1, startY + 1);
                int captureIndex = Board.getSquareIndex(startX - 1, startY);

                if (!captureMask.isEmpty() && captureMask.contains(captureIndex)) {
                    legalMoves.add(new Move(board, startX, startY, startX - 1, startY + 1));
                }

                if (!pushMask.isEmpty() && pushMask.contains(endIndex)) {
                    legalMoves.add(new Move(board, startX, startY, startX - 1, startY + 1));
                }
            }

            if (game.getMoveList().get(0).getEndX() == startX + 1) {
                int endIndex = Board.getSquareIndex(startX + 1, startY + 1);
                int captureIndex = Board.getSquareIndex(startX + 1, startY);

                if (!captureMask.isEmpty() && captureMask.contains(captureIndex)) {
                    legalMoves.add(new Move(board, startX, startY, startX + 1, startY + 1));
                }

                if (!pushMask.isEmpty() && pushMask.contains(endIndex)) {
                    legalMoves.add(new Move(board, startX, startY, startX + 1, startY + 1));
                }
            }
        }
    }

    /**
     * Generate all pseudo-legal knight moves originating from the square at the given startX and startY
     */
    private void generateKnightMoves(int startX, int startY) {
        if (pinsExistInPosition && pinnedPieces.contains(Board.getSquareIndex(startX, startY))) {
            return;
        }

        if (startX + 1 <= 8 && startY + 2 <= 8) {
            int capturedPiece = board.getPiece(startX + 1, startY + 2);

            if (capturedPiece == 0 || capturedPiece < 0 && whiteToPlay || capturedPiece > 0 && !whiteToPlay) {
                legalMoves.add(new Move(board, startX, startY, startX + 1, startY + 2));
            }
        }

        if (startX + 2 <= 8 && startY + 1 <= 8) {
            int capturedPiece = board.getPiece(startX + 2, startY + 1);

            if (capturedPiece == 0 || capturedPiece < 0 && whiteToPlay || capturedPiece > 0 && !whiteToPlay) {
                legalMoves.add(new Move(board, startX, startY, startX + 2, startY + 1));
            }
        }

        if (startX - 1 >= 1 && startY + 2 <= 8) {
            int capturedPiece = board.getPiece(startX - 1, startY + 2);

            if (capturedPiece == 0 || capturedPiece < 0 && whiteToPlay || capturedPiece > 0 && !whiteToPlay) {
                legalMoves.add(new Move(board, startX, startY, startX - 1, startY + 2));
            }
        }

        if (startX - 2 >= 1 && startY + 1 <= 8) {
            int capturedPiece = board.getPiece(startX - 2, startY + 1);

            if (capturedPiece == 0 || capturedPiece < 0 && whiteToPlay || capturedPiece > 0 && !whiteToPlay) {
                legalMoves.add(new Move(board, startX, startY, startX - 2, startY + 1));
            }
        }

        if (startX - 2 >= 1 && startY - 1 >= 1) {
            int capturedPiece = board.getPiece(startX - 2, startY - 1);

            if (capturedPiece == 0 || capturedPiece < 0 && whiteToPlay || capturedPiece > 0 && !whiteToPlay) {
                legalMoves.add(new Move(board, startX, startY, startX - 2, startY - 1));
            }
        }

        if (startX - 1 >= 1 && startY - 2 >= 1) {
            int capturedPiece = board.getPiece(startX - 1, startY - 2);

            if (capturedPiece == 0 || capturedPiece < 0 && whiteToPlay || capturedPiece > 0 && !whiteToPlay) {
                legalMoves.add(new Move(board, startX, startY, startX - 1, startY - 2));
            }
        }

        if (startX + 1 <= 8 && startY - 2 >= 1) {
            int capturedPiece = board.getPiece(startX + 1, startY - 2);

            if (capturedPiece == 0 || capturedPiece < 0 && whiteToPlay || capturedPiece > 0 && !whiteToPlay) {
                legalMoves.add(new Move(board, startX, startY, startX + 1, startY - 2));
            }
        }

        if (startX + 2 <= 8 && startY - 1 >= 1) {
            int capturedPiece = board.getPiece(startX + 2, startY - 1);

            if (capturedPiece == 0 || capturedPiece < 0 && whiteToPlay || capturedPiece > 0 && !whiteToPlay) {
                legalMoves.add(new Move(board, startX, startY, startX + 2, startY - 1));
            }
        }
    }

    /**
     * Generate all legal knight moves originating from the square at the given startX and startY, when in check
     */
    private void generateKnightMovesInCheck(int startX, int startY) {
        if (pinsExistInPosition && pinnedPieces.contains(Board.getSquareIndex(startX, startY))) {
            return;
        }

        if (startX + 1 <= 8 && startY + 2 <= 8) {
            int capturedPiece = board.getPiece(startX + 1, startY + 2);

            if (capturedPiece == 0 || capturedPiece < 0 && whiteToPlay || capturedPiece > 0 && !whiteToPlay) {
                int endIndex = Board.getSquareIndex(startX + 1, startY + 2);

                if (!captureMask.isEmpty() && captureMask.contains(endIndex)) {
                    legalMoves.add(new Move(board, startX, startY, startX + 1, startY + 2));
                }

                if (!pushMask.isEmpty() && pushMask.contains(endIndex)) {
                    legalMoves.add(new Move(board, startX, startY, startX + 1, startY + 2));
                }
            }
        }

        if (startX + 2 <= 8 && startY + 1 <= 8) {
            int capturedPiece = board.getPiece(startX + 2, startY + 1);

            if (capturedPiece == 0 || capturedPiece < 0 && whiteToPlay || capturedPiece > 0 && !whiteToPlay) {
                int endIndex = Board.getSquareIndex(startX + 2, startY + 1);

                if (!captureMask.isEmpty() && captureMask.contains(endIndex)) {
                    legalMoves.add(new Move(board, startX, startY, startX + 2, startY + 1));
                }

                if (!pushMask.isEmpty() && pushMask.contains(endIndex)) {
                    legalMoves.add(new Move(board, startX, startY, startX + 2, startY + 1));
                }
            }
        }

        if (startX - 1 >= 1 && startY + 2 <= 8) {
            int capturedPiece = board.getPiece(startX - 1, startY + 2);

            if (capturedPiece == 0 || capturedPiece < 0 && whiteToPlay || capturedPiece > 0 && !whiteToPlay) {
                int endIndex = Board.getSquareIndex(startX - 1, startY + 2);

                if (!captureMask.isEmpty() && captureMask.contains(endIndex)) {
                    legalMoves.add(new Move(board, startX, startY, startX - 1, startY + 2));
                }

                if (!pushMask.isEmpty() && pushMask.contains(endIndex)) {
                    legalMoves.add(new Move(board, startX, startY, startX - 1, startY + 2));
                }
            }
        }

        if (startX - 2 >= 1 && startY + 1 <= 8) {
            int capturedPiece = board.getPiece(startX - 2, startY + 1);

            if (capturedPiece == 0 || capturedPiece < 0 && whiteToPlay || capturedPiece > 0 && !whiteToPlay) {
                int endIndex = Board.getSquareIndex(startX - 2, startY + 1);

                if (!captureMask.isEmpty() && captureMask.contains(endIndex)) {
                    legalMoves.add(new Move(board, startX, startY, startX - 2, startY + 1));
                }

                if (!pushMask.isEmpty() && pushMask.contains(endIndex)) {
                    legalMoves.add(new Move(board, startX, startY, startX - 2, startY + 1));
                }
            }
        }

        if (startX - 2 >= 1 && startY - 1 >= 1) {
            int capturedPiece = board.getPiece(startX - 2, startY - 1);

            if (capturedPiece == 0 || capturedPiece < 0 && whiteToPlay || capturedPiece > 0 && !whiteToPlay) {
                int endIndex = Board.getSquareIndex(startX - 2, startY - 1);

                if (!captureMask.isEmpty() && captureMask.contains(endIndex)) {
                    legalMoves.add(new Move(board, startX, startY, startX - 2, startY - 1));
                }

                if (!pushMask.isEmpty() && pushMask.contains(endIndex)) {
                    legalMoves.add(new Move(board, startX, startY, startX - 2, startY - 1));
                }
            }
        }

        if (startX - 1 >= 1 && startY - 2 >= 1) {
            int capturedPiece = board.getPiece(startX - 1, startY - 2);

            if (capturedPiece == 0 || capturedPiece < 0 && whiteToPlay || capturedPiece > 0 && !whiteToPlay) {
                int endIndex = Board.getSquareIndex(startX - 1, startY - 2);

                if (!captureMask.isEmpty() && captureMask.contains(endIndex)) {
                    legalMoves.add(new Move(board, startX, startY, startX - 1, startY - 2));
                }

                if (!pushMask.isEmpty() && pushMask.contains(endIndex)) {
                    legalMoves.add(new Move(board, startX, startY, startX - 1, startY - 2));
                }
            }
        }

        if (startX + 1 <= 8 && startY - 2 >= 1) {
            int capturedPiece = board.getPiece(startX + 1, startY - 2);

            if (capturedPiece == 0 || capturedPiece < 0 && whiteToPlay || capturedPiece > 0 && !whiteToPlay) {
                int endIndex = Board.getSquareIndex(startX + 1, startY - 2);

                if (!captureMask.isEmpty() && captureMask.contains(endIndex)) {
                    legalMoves.add(new Move(board, startX, startY, startX + 1, startY - 2));
                }

                if (!pushMask.isEmpty() && pushMask.contains(endIndex)) {
                    legalMoves.add(new Move(board, startX, startY, startX + 1, startY - 2));
                }
            }
        }

        if (startX + 2 <= 8 && startY - 1 >= 1) {
            int capturedPiece = board.getPiece(startX + 2, startY - 1);

            if (capturedPiece == 0 || capturedPiece < 0 && whiteToPlay || capturedPiece > 0 && !whiteToPlay) {
                int endIndex = Board.getSquareIndex(startX + 2, startY - 1);

                if (!captureMask.isEmpty() && captureMask.contains(endIndex)) {
                    legalMoves.add(new Move(board, startX, startY, startX + 2, startY - 1));
                }

                if (!pushMask.isEmpty() && pushMask.contains(endIndex)) {
                    legalMoves.add(new Move(board, startX, startY, startX + 2, startY - 1));
                }
            }
        }
    }

    /**
     * Generate all pseudo-legal bishop moves originating from the square at the given startX and startY
     */
    private void generateBishopMoves(int startX, int startY) {
        if (pinsExistInPosition) {
            // step 1: check that the piece at startX and startY is pinned
            // step 2: where is pin coming from diagonal or vertical
            // step 3: if piece is pinned vertically, no legal moves, otherwise
            //         generate moves between pinned piece and pinning piece, those are the only legal moves
            //         (including capturing pinned piece)
        }

        generateNorthWestMoves(startX, startY);
        generateNorthEastMoves(startX, startY);
        generateSouthWestMoves(startX, startY);
        generateSouthEastMoves(startX, startY);
    }

    /**
     * Generate all legal bishop moves originating from the square at the given startX and startY, when in check
     */
    private void generateBishopMovesInCheck(int startX, int startY) {
        if (pinsExistInPosition && pinnedPieces.contains(Board.getSquareIndex(startX, startY))) {
            return;
        }

        generateNorthWestMovesInCheck(startX, startY);
        generateNorthEastMovesInCheck(startX, startY);
        generateSouthWestMovesInCheck(startX, startY);
        generateSouthEastMovesInCheck(startX, startY);
    }

    /**
     * Generate all pseudo-legal rook moves originating from the square at the given startX and startY
     */
    private void generateRookMoves(int startX, int startY) {
        if (pinsExistInPosition) {
            // step 1: check that the piece at startX and startY is pinned
            // step 2: where is pin coming from diagonal or vertical
            // step 3: if piece is pinned diagonally, no legal moves, otherwise
            //         generate moves between pinned piece and pinning piece, those are the only legal moves
            //         (including capturing pinned piece)
        }

        generateNorthMoves(startX, startY);
        generateSouthMoves(startX, startY);
        generateWestMoves(startX, startY);
        generateEastMoves(startX, startY);
    }

    /**
     * Generate all legal rook moves originating from the square at the given startX and startY, when in check
     */
    private void generateRookMovesInCheck(int startX, int startY) {
        if (pinsExistInPosition && pinnedPieces.contains(Board.getSquareIndex(startX, startY))) {
            return;
        }

        generateNorthMovesInCheck(startX, startY);
        generateSouthMovesInCheck(startX, startY);
        generateWestMovesInCheck(startX, startY);
        generateEastMovesInCheck(startX, startY);
    }

    /**
     * Generate all pseudo-legal queen moves originating from the square at the given startX and startY
     */
    private void generateQueenMoves(int startX, int startY)  {
        if (pinsExistInPosition) {
            // step 1: check that the piece at startX and startY is pinned
            // step 2: where is pin coming from diagonal or vertical
            // step 3: generate moves between  pinned piece and pinning piece, those are the only legal moves
            //         (including capturing pinned piece)
        }

        // straight moves
        generateNorthMoves(startX, startY);
        generateSouthMoves(startX, startY);
        generateWestMoves(startX, startY);
        generateEastMoves(startX, startY);

        // diagonal moves
        generateNorthWestMoves(startX, startY);
        generateNorthEastMoves(startX, startY);
        generateSouthWestMoves(startX, startY);
        generateSouthEastMoves(startX, startY);
    }

    /**
     * Generate all legal rook moves originating from the square at the given startX and startY, when in check
     */
    private void generateQueenMovesInCheck(int startX, int startY) {
        if (pinsExistInPosition && pinnedPieces.contains(Board.getSquareIndex(startX, startY))) {
            return;
        }

        // straight moves
        generateNorthMovesInCheck(startX, startY);
        generateSouthMovesInCheck(startX, startY);
        generateWestMovesInCheck(startX, startY);
        generateEastMovesInCheck(startX, startY);

        // diagonal moves
        generateNorthWestMovesInCheck(startX, startY);
        generateNorthEastMovesInCheck(startX, startY);
        generateSouthWestMovesInCheck(startX, startY);
        generateSouthEastMovesInCheck(startX, startY);
    }

    /**
     * Generate all pseudo-legal king moves originating from the square at the given startX and startY
     */
    private void generateKingMoves(int startX, int startY) {
        // north
        if (startY + 1 <= 8) {
            int capturedPiece = board.getPiece(startX, startY + 1);

            if (!threatMap.contains(Board.getSquareIndex(startX, startY + 1))) {
                if (capturedPiece == 0 || capturedPiece < 0 && whiteToPlay || capturedPiece > 0 && !whiteToPlay) {
                    legalMoves.add(new Move(board, startX, startY, startX, startY + 1));
                }
            }
        }

        // south
        if (startY - 1 >= 1) {
            int capturedPiece = board.getPiece(startX, startY - 1);

            if (!threatMap.contains(Board.getSquareIndex(startX, startY - 1))) {
                if (capturedPiece == 0 || capturedPiece < 0 && whiteToPlay || capturedPiece > 0 && !whiteToPlay) {
                    legalMoves.add(new Move(board, startX, startY, startX, startY - 1));
                }
            }
        }

        // west
        if (startX - 1 >= 1) {
            int capturedPiece = board.getPiece(startX - 1, startY);

            if (!threatMap.contains(Board.getSquareIndex(startX - 1, startY))) {
                if (capturedPiece == 0 || capturedPiece < 0 && whiteToPlay || capturedPiece > 0 && !whiteToPlay) {
                    legalMoves.add(new Move(board, startX, startY, startX - 1, startY));
                }
            }
        }

        // east
        if (startX + 1 <= 8) {
            int capturedPiece = board.getPiece(startX + 1, startY);

            if (!threatMap.contains(Board.getSquareIndex(startX + 1, startY))) {
                if (capturedPiece == 0 || capturedPiece < 0 && whiteToPlay || capturedPiece > 0 && !whiteToPlay) {
                    legalMoves.add(new Move(board, startX, startY, startX + 1, startY));
                }
            }
        }

        // north-west
        if (startX - 1 >= 1 && startY + 1 <= 8) {
            int capturedPiece = board.getPiece(startX - 1, startY + 1);

            if (!threatMap.contains(Board.getSquareIndex(startX - 1, startY + 1))) {
                if (capturedPiece == 0 || capturedPiece < 0 && whiteToPlay || capturedPiece > 0 && !whiteToPlay) {
                    legalMoves.add(new Move(board, startX, startY, startX - 1, startY + 1));
                }
            }
        }

        // north-east
        if (startX + 1 <= 8 && startY + 1 <= 8) {
            int capturedPiece = board.getPiece(startX + 1, startY + 1);

            if (!threatMap.contains(Board.getSquareIndex(startX + 1, startY + 1))) {
                if (capturedPiece == 0 || capturedPiece < 0 && whiteToPlay || capturedPiece > 0 && !whiteToPlay) {
                    legalMoves.add(new Move(board, startX, startY, startX + 1, startY + 1));
                }
            }
        }

        // south-west
        if (startX - 1 >= 1 && startY - 1 >= 1) {
            int capturedPiece = board.getPiece(startX - 1, startY - 1);

            if (!threatMap.contains(Board.getSquareIndex(startX - 1, startY - 1))) {
                if (capturedPiece == 0 || capturedPiece < 0 && whiteToPlay || capturedPiece > 0 && !whiteToPlay) {
                    legalMoves.add(new Move(board, startX, startY, startX - 1, startY - 1));
                }
            }
        }

        // south-east
        if (startX + 1 <= 8 && startY - 1 >= 1) {
            int capturedPiece = board.getPiece(startX + 1, startY - 1);

            if (!threatMap.contains(Board.getSquareIndex(startX + 1, startY - 1))) {
                if (capturedPiece == 0 || capturedPiece < 0 && whiteToPlay || capturedPiece > 0 && !whiteToPlay) {
                    legalMoves.add(new Move(board, startX, startY, startX + 1, startY - 1));
                }
            }
        }
    }

    /**
     * Sliding piece move generators for different directions: north, south, west, east, north-west, north-east,
     * south-west and south-east
     */
    private void generateNorthMoves(int startX, int startY) {
        for (int y = startY + 1; y <= 8; y++) {
            if (board.getPiece(startX, y) == 0) {
                legalMoves.add(new Move(board, startX, startY, startX, y));
            } else if (board.getPiece(startX, y) < 0 && whiteToPlay || board.getPiece(startX, y) > 0 && !whiteToPlay) {
                legalMoves.add(new Move(board, startX, startY, startX, y));
                break;
            } else {
                break;
            }
        }
    }

    private void generateSouthMoves(int startX, int startY) {
        for (int y = startY - 1; y >= 1; y--) {
            if (board.getPiece(startX, y) == 0) {
                legalMoves.add(new Move(board, startX, startY, startX, y));
            } else if (board.getPiece(startX, y) < 0 && whiteToPlay || board.getPiece(startX, y) > 0 && !whiteToPlay) {
                legalMoves.add(new Move(board, startX, startY, startX, y));
                break;
            } else {
                break;
            }
        }
    }

    private void generateWestMoves(int startX, int startY) {
        for (int x = startX - 1; x >= 1; x--) {
            if (board.getPiece(x, startY) == 0) {
                legalMoves.add(new Move(board, startX, startY, x, startY));
            } else if (board.getPiece(x, startY) < 0 && whiteToPlay || board.getPiece(x, startY) > 0 && !whiteToPlay) {
                legalMoves.add(new Move(board, startX, startY, x, startY));
                break;
            }  else {
                break;
            }
        }
    }

    private void generateEastMoves(int startX, int startY) {
        for (int x = startX + 1; x <= 8; x++) {
            if (board.getPiece(x, startY) == 0) {
                legalMoves.add(new Move(board, startX, startY, x, startY));
            } else if (board.getPiece(x, startY) < 0 && whiteToPlay || board.getPiece(x, startY) > 0 && !whiteToPlay) {
                legalMoves.add(new Move(board, startX, startY, x, startY));
                break;
            }  else {
                break;
            }
        }
    }

    private void generateNorthEastMoves(int startX, int startY) {
        int y = startY + 1;

        for (int x = startX + 1; x <= 8; x++) {
            if (y > 8) {
                break;
            }

            if (board.getPiece(x, y) == 0) {
                legalMoves.add(new Move(board, startX, startY, x, y));
            } else if (board.getPiece(x, y) < 0 && whiteToPlay || board.getPiece(x,y) > 0 && !whiteToPlay) {
                legalMoves.add(new Move(board, startX, startY, x, y));
                break;
            } else {
                break;
            }

            y++;
        }
    }

    private void generateNorthWestMoves(int startX, int startY) {
        int y = startY + 1;

        for (int x = startX - 1; x >= 1; x--) {
            if (y > 8) {
                break;
            }

            if (board.getPiece(x, y) == 0) {
                legalMoves.add(new Move(board, startX, startY, x, y));
            } else if (board.getPiece(x, y) < 0 && whiteToPlay || board.getPiece(x,y) > 0 && !whiteToPlay) {
                legalMoves.add(new Move(board, startX, startY, x, y));
                break;
            } else {
                break;
            }

            y++;
        }
    }

    private void generateSouthWestMoves(int startX, int startY) {
        int y = startY - 1;

        for (int x = startX - 1; x >= 1; x--) {
            if (y < 1) {
                break;
            }

            if (board.getPiece(x, y) == 0) {
                legalMoves.add(new Move(board, startX, startY, x, y));
            } else if (board.getPiece(x, y) < 0 && whiteToPlay || board.getPiece(x,y) > 0 && !whiteToPlay) {
                legalMoves.add(new Move(board, startX, startY, x, y));
                break;
            } else {
                break;
            }

            y--;
        }
    }

    private void generateSouthEastMoves(int startX, int startY) {
        int y = startY - 1;

        for (int x = startX + 1; x <= 8; x++) {
            if (y < 1) {
                break;
            }

            if (board.getPiece(x, y) == 0) {
                legalMoves.add(new Move(board, startX, startY, x, y));
            } else if (board.getPiece(x, y) < 0 && whiteToPlay || board.getPiece(x,y) > 0 && !whiteToPlay) {
                legalMoves.add(new Move(board, startX, startY, x, y));
                break;
            } else {
                break;
            }

            y--;
        }
    }

    /**
     * Sliding piece move generators for different directions when in check: north, south, west, east, north-west,
     * north-east, south-west and south-east
     */
    private void generateNorthMovesInCheck(int startX, int startY) {
        for (int y = startY + 1; y <= 8; y++) {
            int endIndex = Board.getSquareIndex(startX, y);

            if (board.getPiece(startX, y) == 0) {
                if (!pushMask.isEmpty() && pushMask.contains(endIndex)) {
                    legalMoves.add(new Move(board, startX, startY, startX, y));
                }
            } else if (board.getPiece(startX, y) < 0 && whiteToPlay || board.getPiece(startX, y) > 0 && !whiteToPlay) {
                if (!captureMask.isEmpty() && captureMask.contains(endIndex)) {
                    legalMoves.add(new Move(board, startX, startY, startX, y));
                }
                break;
            } else {
                break;
            }
        }
    }

    private void generateSouthMovesInCheck(int startX, int startY) {
        for (int y = startY - 1; y >= 1; y--) {
            int endIndex = Board.getSquareIndex(startX, y);

            if (board.getPiece(startX, y) == 0) {
                if (!pushMask.isEmpty() && pushMask.contains(endIndex)) {
                    legalMoves.add(new Move(board, startX, startY, startX, y));
                }
            } else if (board.getPiece(startX, y) < 0 && whiteToPlay || board.getPiece(startX, y) > 0 && !whiteToPlay) {
                if (!captureMask.isEmpty() && captureMask.contains(endIndex)) {
                    legalMoves.add(new Move(board, startX, startY, startX, y));
                }
                break;
            } else {
                break;
            }
        }
    }

    private void generateWestMovesInCheck(int startX, int startY) {
        for (int x = startX - 1; x >= 1; x--) {
            int endIndex = Board.getSquareIndex(x, startY);
            if (board.getPiece(x, startY) == 0) {
                if (!pushMask.isEmpty() && pushMask.contains(endIndex)) {
                    legalMoves.add(new Move(board, startX, startY, x, startY));
                }
            } else if (board.getPiece(x, startY) < 0 && whiteToPlay || board.getPiece(x, startY) > 0 && !whiteToPlay) {
                if (!captureMask.isEmpty() && captureMask.contains(endIndex)) {
                    legalMoves.add(new Move(board, startX, startY, x, startY));
                }
                break;
            }  else {
                break;
            }
        }
    }

    private void generateEastMovesInCheck(int startX, int startY) {
        for (int x = startX + 1; x <= 8; x++) {
            int endIndex = Board.getSquareIndex(x, startY);
            if (board.getPiece(x, startY) == 0) {
                if (!pushMask.isEmpty() && pushMask.contains(endIndex)) {
                    legalMoves.add(new Move(board, startX, startY, x, startY));
                }
            } else if (board.getPiece(x, startY) < 0 && whiteToPlay || board.getPiece(x, startY) > 0 && !whiteToPlay) {
                if (!captureMask.isEmpty() && captureMask.contains(endIndex)) {
                    legalMoves.add(new Move(board, startX, startY, x, startY));
                }
                break;
            }  else {
                break;
            }
        }
    }

    private void generateNorthEastMovesInCheck(int startX, int startY) {
        int y = startY + 1;

        for (int x = startX + 1; x <= 8; x++) {
            if (y > 8) {
                break;
            }

            int endIndex = Board.getSquareIndex(x,y);
            if (board.getPiece(x, y) == 0) {
                if (!pushMask.isEmpty() && pushMask.contains(endIndex)) {
                    legalMoves.add(new Move(board, startX, startY, x, y));
                }
            } else if (board.getPiece(x, y) < 0 && whiteToPlay || board.getPiece(x,y) > 0 && !whiteToPlay) {
                if (!captureMask.isEmpty() && captureMask.contains(endIndex)) {
                    legalMoves.add(new Move(board, startX, startY, x, y));
                }
                break;
            } else {
                break;
            }

            y++;
        }
    }

    private void generateNorthWestMovesInCheck(int startX, int startY) {
        int y = startY + 1;

        for (int x = startX - 1; x >= 1; x--) {
            if (y > 8) {
                break;
            }

            int endIndex = Board.getSquareIndex(x,y);
            if (board.getPiece(x, y) == 0) {
                if (!pushMask.isEmpty() && pushMask.contains(endIndex)) {
                    legalMoves.add(new Move(board, startX, startY, x, y));
                }
            } else if (board.getPiece(x, y) < 0 && whiteToPlay || board.getPiece(x,y) > 0 && !whiteToPlay) {
                if (!captureMask.isEmpty() && captureMask.contains(endIndex)) {
                    legalMoves.add(new Move(board, startX, startY, x, y));
                }
                break;
            } else {
                break;
            }

            y++;
        }
    }

    private void generateSouthWestMovesInCheck(int startX, int startY) {
        int y = startY - 1;

        for (int x = startX - 1; x >= 1; x--) {
            if (y < 1) {
                break;
            }

            int endIndex = Board.getSquareIndex(x,y);
            if (board.getPiece(x, y) == 0) {
                if (!pushMask.isEmpty() && pushMask.contains(endIndex)) {
                    legalMoves.add(new Move(board, startX, startY, x, y));
                }
            } else if (board.getPiece(x, y) < 0 && whiteToPlay || board.getPiece(x,y) > 0 && !whiteToPlay) {
                if (!captureMask.isEmpty() && captureMask.contains(endIndex)) {
                    legalMoves.add(new Move(board, startX, startY, x, y));
                }
                break;
            } else {
                break;
            }

            y--;
        }
    }

    private void generateSouthEastMovesInCheck(int startX, int startY) {
        int y = startY - 1;

        for (int x = startX + 1; x <= 8; x++) {
            if (y < 1) {
                break;
            }

            int endIndex = Board.getSquareIndex(x,y);
            if (board.getPiece(x, y) == 0) {
                if (!pushMask.isEmpty() && pushMask.contains(endIndex)) {
                    legalMoves.add(new Move(board, startX, startY, x, y));
                }
            } else if (board.getPiece(x, y) < 0 && whiteToPlay || board.getPiece(x,y) > 0 && !whiteToPlay) {
                if (!captureMask.isEmpty() && captureMask.contains(endIndex)) {
                    legalMoves.add(new Move(board, startX, startY, x, y));
                }
                break;
            } else {
                break;
            }

            y--;
        }
    }

    /**
     * Getters & Setters
     */
    public boolean inCheck() {
        return inCheck;
    }

    public boolean isInDoubleCheck() {
        return inDoubleCheck;
    }

    public List<Integer> getCheckers() {
        return checkers;
    }

    public List<Integer> getPinnedPieces() {
        return pinnedPieces;
    }

    public boolean doPinsExistInPosition() {
        return pinsExistInPosition;
    }
}

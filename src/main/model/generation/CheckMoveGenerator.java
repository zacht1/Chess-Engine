package model.generation;

import model.Board;
import model.Game;
import model.Move;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static java.lang.Math.abs;

public class CheckMoveGenerator {
    private Game game;
    private Board board;
    private boolean whiteToPlay;
    private List<Integer> captureMask;
    private List<Integer> pushMask;

    private boolean inCheck;
    private boolean inDoubleCheck;

    private ThreatMapGenerator threatMapGenerator;
    private PinMoveGenerator pinMoveGenerator;
    private int friendlyKingIndex;

    private List<Integer> checkers;
    private List<Integer> pawnCheckers;
    private List<Integer> knightCheckers;
    private List<Integer> bishopCheckers;
    private List<Integer> rookCheckers;
    private List<Integer> queenCheckers;

    private Set<Integer> threatMap;

    private List<Move> legalMoves;

    /**
     * Initialize CheckMoveGenerator object
     *
     * @param game current chess game
     * @param whiteToPlay true if current turn is white
     */
    public CheckMoveGenerator(Game game, boolean whiteToPlay, List<Integer> captureMask,
                              List<Integer> pushMask, int friendlyKingIndex,
                              PinMoveGenerator pinMoveGenerator) {
        this.game = game;
        this.board = game.getBoard();
        this.whiteToPlay = whiteToPlay;
        this.captureMask = captureMask;
        this.pushMask = pushMask;
        this.pinMoveGenerator = pinMoveGenerator;

        this.threatMapGenerator = new ThreatMapGenerator();
        if (whiteToPlay) {
            threatMap = threatMapGenerator.generateBlackThreatMap(game);
        } else {
            threatMap = threatMapGenerator.generateWhiteThreatMap(game);
        }

        this.inCheck = false;
        this.inDoubleCheck = false;
        this.friendlyKingIndex = friendlyKingIndex;
        this.checkers = new ArrayList<>();
        this.pawnCheckers = new ArrayList<>();
        this.knightCheckers = new ArrayList<>();
        this.bishopCheckers = new ArrayList<>();
        this.rookCheckers = new ArrayList<>();
        this.queenCheckers = new ArrayList<>();
        this.legalMoves = new ArrayList<>();

        inCheck();
    }

    /**
     * Determine whether the current player is in check in the current position
     */
    private void inCheck() {
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
     * Generate all legal moves that can escape from check
     */
    private void generateCheckEscapingMoves() {
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
     * Generate all legal black pawn moves originating from the square at the given startX and startY, when in check
     */
    private void generateBlackPawnMovesInCheck(int startX, int startY) {
        if (pushMask.isEmpty() && captureMask.isEmpty()) {
            return;
        }

        if (pinMoveGenerator.doPinsExistInPosition() && pinMoveGenerator.getPinnedPieces().contains(Board.getSquareIndex(startX, startY))) {
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
     * Generate all legal white pawn moves originating from the square at the given startX and startY, when in check
     */
    private void generateWhitePawnMovesInCheck(int startX, int startY) {
        if (captureMask.isEmpty() && pushMask.isEmpty()) {
            return;
        }

        if (pinMoveGenerator.doPinsExistInPosition() && pinMoveGenerator.getPinnedPieces().contains(Board.getSquareIndex(startX, startY))) {
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
     * Generate all legal knight moves originating from the square at the given startX and startY, when in check
     */
    private void generateKnightMovesInCheck(int startX, int startY) {
        if (pinMoveGenerator.doPinsExistInPosition() && pinMoveGenerator.getPinnedPieces().contains(Board.getSquareIndex(startX, startY))) {
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
     * Generate all legal bishop moves originating from the square at the given startX and startY, when in check
     */
    private void generateBishopMovesInCheck(int startX, int startY) {
        if (pinMoveGenerator.doPinsExistInPosition() && pinMoveGenerator.getPinnedPieces().contains(Board.getSquareIndex(startX, startY))) {
            return;
        }

        generateNorthWestMovesInCheck(startX, startY);
        generateNorthEastMovesInCheck(startX, startY);
        generateSouthWestMovesInCheck(startX, startY);
        generateSouthEastMovesInCheck(startX, startY);
    }

    /**
     * Generate all legal rook moves originating from the square at the given startX and startY, when in check
     */
    private void generateRookMovesInCheck(int startX, int startY) {
        if (pinMoveGenerator.doPinsExistInPosition() && pinMoveGenerator.getPinnedPieces().contains(Board.getSquareIndex(startX, startY))) {
            return;
        }

        generateNorthMovesInCheck(startX, startY);
        generateSouthMovesInCheck(startX, startY);
        generateWestMovesInCheck(startX, startY);
        generateEastMovesInCheck(startX, startY);
    }

    /**
     * Generate all legal rook moves originating from the square at the given startX and startY, when in check
     */
    private void generateQueenMovesInCheck(int startX, int startY) {
        if (pinMoveGenerator.doPinsExistInPosition() && pinMoveGenerator.getPinnedPieces().contains(Board.getSquareIndex(startX, startY))) {
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
    public boolean isInCheck() {
        return inCheck;
    }

    public boolean isInDoubleCheck() {
        return inDoubleCheck;
    }
}

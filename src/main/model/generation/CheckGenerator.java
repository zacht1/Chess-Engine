package model.generation;

import model.Board;
import model.Game;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

// generates checks in the current position and all pieces that are giving check
public class CheckGenerator {
    private boolean inCheck;
    private boolean inDoubleCheck;

    private List<Integer> checkers;
    private List<Integer> pawnCheckers;
    private List<Integer> knightCheckers;
    private List<Integer> bishopCheckers;
    private List<Integer> rookCheckers;
    private List<Integer> queenCheckers;

    private Game game;
    private int friendlyKingIndex;
    private boolean whiteToPlay;
    private ThreatMapGenerator threatMapGenerator;

    // determines whether current player is in check in current board, and generates all pieces that are checking the
    // current players king
    public CheckGenerator(Game game, int friendlyKingIndex, boolean whiteToPlay, ThreatMapGenerator threatMapGenerator) {
        this.inCheck = false;
        this.inDoubleCheck = false;

        this.checkers = new ArrayList<>();
        this.pawnCheckers = new ArrayList<>();
        this.knightCheckers = new ArrayList<>();
        this.bishopCheckers = new ArrayList<>();
        this.rookCheckers = new ArrayList<>();
        this.queenCheckers = new ArrayList<>();

        this.game = game;
        this.friendlyKingIndex = friendlyKingIndex;
        this.whiteToPlay = whiteToPlay;
        this.threatMapGenerator = threatMapGenerator;

        inCheck();
    }

    /**
     * Determine whether the current player is in check in the current position
     */
    private void inCheck() {
        Board board = game.getBoard();

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
     * Getters & Setters
     */
    public List<Integer> getCheckers() {
        return checkers;
    }

    public List<Integer> getPawnCheckers() {
        return pawnCheckers;
    }

    public List<Integer> getKnightCheckers() {
        return knightCheckers;
    }

    public List<Integer> getBishopCheckers() {
        return bishopCheckers;
    }

    public List<Integer> getRookCheckers() {
        return rookCheckers;
    }

    public List<Integer> getQueenCheckers() {
        return queenCheckers;
    }

    public boolean isInDoubleCheck() {
        return inDoubleCheck;
    }

    public boolean isInCheck() {
        return inCheck;
    }
}

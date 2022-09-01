package model.search;

import model.Board;
import model.Game;
import model.Move;
import model.Player;

import java.util.List;

public class Evaluation {
    private Game game;
    private Board board;
    private boolean whiteToPlay;

    /**
     * Evaluates the current position from the perspective of the player whose turn it is
     * Large positive score means position is very favourable for current player, large negative score means position is
     * very unfavourable for current player
     */
    public double evaluatePosition(Game game) {
        this.game = game;
        this.board = game.getBoard();
        this.whiteToPlay = game.getCurrentTurn().isWhite();

        return countMaterial() + countMobility() + countPawnErrors();
    }

    /**
     * Count the number of possible moves for the current player and return that value multiplied by 0.1
     */
    public double countMobility() {
        List<Move> moveList = game.getMoveGenerator().generateLegalMoves(game, game.getCurrentTurn());

        List<Move> opponentMoveList = game.getMoveGenerator().generateLegalMoves(game, new Player(!whiteToPlay));

        return 0.1 * (moveList.size() - opponentMoveList.size());
    }

    /**
     * Count the material for the current player minus the material for the opponent
     * queen = 9
     * rook = 5
     * bishop & knight = 3
     * pawn = 1
     * bishop pair = 0.2
     */
    public double countMaterial() {
        int wPawns = 0;
        int wKnights = 0;
        int wBishops = 0;
        int wRooks = 0;
        int wQueen = 0;
        double wBishopPair = 0;

        int bPawns = 0;
        int bKnights = 0;
        int bBishops = 0;
        int bRooks = 0;
        int bQueen = 0;
        double bBishopPair = 0;

        for (int piece : board.getBoard()) {
            switch (piece) {
                case 1: wPawns++;
                    break;
                case 2: wKnights++;
                    break;
                case 3: wBishops++;
                    break;
                case 4: wRooks++;
                    break;
                case 5: wQueen++;
                    break;
                case -1: bPawns++;
                    break;
                case -2: bKnights++;
                    break;
                case -3: bBishops++;
                    break;
                case -4: bRooks++;
                    break;
                case -5: bQueen++;
                    break;
            }
        }

        if (wBishops == 2) {
            wBishopPair = 0.2;
        }

        if (bBishops == 2) {
            bBishopPair = 0.2;
        }

        double material = (9*(wQueen - bQueen)) + (5*(wRooks - bRooks)) + (3*((wBishops - bBishops) +
                (wKnights - bKnights))) + (wPawns - bPawns) + (wBishopPair - bBishopPair);

        if (whiteToPlay) {
            return material;
        } else {
            return -1 * material;
        }
    }

    /**
     * Count the number of doubled pawns and isolated pawns in the given position and
     * return that value multiplied by -0.5
     */
    public double countPawnErrors() {
        int[] whitePawns = new int[8];
        int[] blackPawns = new int[8];

        int index = 0;
        for (int piece: board.getBoard()) {
            int file = index % 8; // 0-7

            if (piece == 1) {
                whitePawns[file]++;
            } else if (piece == -1) {
                blackPawns[file]++;
            }

            index++;
        }

        double whitePawnErrors = -0.5 * (countDoubledPawns(whitePawns) + countIsolatedPawns(whitePawns));
        double blackPawnErrors = -0.5 * (countDoubledPawns(blackPawns) + countIsolatedPawns(blackPawns));

        double pawnErrors = whitePawnErrors - blackPawnErrors;

        if (whiteToPlay) {
            return pawnErrors;
        } else {
            return -1 * pawnErrors;
        }
    }

    public int countDoubledPawns(int[] pawns) {
        int doubledPawns = 0;

        for (int p: pawns) {
            if (p > 1) {
                doubledPawns++;
            }
        }

        return doubledPawns;
    }

    public int countIsolatedPawns(int[] pawns) {
        int isolatedPawns = 0;

        int index = 0;
        for (int p: pawns) {
            if (p > 0) {
                if (index == 0) {
                    if (pawns[1] == 0) {
                        isolatedPawns++;
                    }
                } else if (index == 7) {
                    if (pawns[6] == 0) {
                        isolatedPawns++;
                    }
                } else if (pawns[index - 1] == 0 && pawns[index + 1] == 0) {
                    isolatedPawns++;
                }
            }

            index++;
        }

        return isolatedPawns;
    }
}

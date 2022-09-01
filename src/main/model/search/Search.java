package model.search;

import model.Game;
import model.Move;
import model.generation.MoveGenerator;

import java.util.List;

public class Search {
    private Evaluation evaluation;
    private MoveGenerator moveGenerator;
    private Game game;


    public Move search(Game game, int depth) {
        System.out.println("\033[H\033[2J");

        this.evaluation = new Evaluation();
        this.moveGenerator = new MoveGenerator();
        this.game = game;

        Move bestMove = null;
        double bestEvaluation = Double.POSITIVE_INFINITY;

        List<Move> moveList = moveGenerator.generateLegalMoves(game, game.getCurrentTurn());

        for (Move move: moveList) {
            game.playMove(move);

            double eval = negaMax(game, depth);

            System.out.println(move.formatMove() + " : " + eval);

            if (eval <= bestEvaluation) {
                bestEvaluation = eval;
                bestMove = move;
            }

            game.undoMove(move);
        }

        return bestMove;
    }

    private double negaMax(Game game, int depth) {
        if (depth == 0) {
            return evaluation.evaluatePosition(this.game);
        }

        double max = Double.NEGATIVE_INFINITY;
        double score;

        for (Move move: moveGenerator.generateLegalMoves(game, game.getCurrentTurn())) {
            game.playMove(move);

            score = negaMax(game, depth - 1) * -1;

            //System.out.println(move.formatMove() + " : " + score);

            if (score > max) {
                max = score;
            }

            game.undoMove(move);
        }

        return max;
    }
}

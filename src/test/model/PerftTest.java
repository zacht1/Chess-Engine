package model;

import model.generation.MoveGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class PerftTest {
    private MoveGenerator moveGenerator;
    private Game game;
    private Board board;
    private List<String> moves;

    @BeforeEach
    public void init() {
        moveGenerator = new MoveGenerator();
        game = new Game();
        board = game.getBoard();
        this.moves = new ArrayList<>();
    }

    @Test
    public void perft() {
        game.setBoardFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
        //System.out.println(perft(2));
        System.out.println(divide(5));
    }

    private int perft(int depth) {
        if (depth == 0) {
            return 1;
        }

        List<Move> moveList = moveGenerator.generateLegalMoves(game, game.getCurrentTurn());
        int numPositions = 0;

        for (Move move: moveList) {
            game.playMove(move);
            numPositions += perft(depth - 1);
            game.undoMove(move);
        }

        return numPositions;
    }

    private long divide(int depth) {
        List<Move> moveList;
        int nMoves, i;
        long nodes = 0;

        moveList = moveGenerator.generateLegalMoves(game, game.getCurrentTurn());
        nMoves = moveList.size();

        for (i = 0; i < nMoves; i++) {
            Move move = moveList.get(i);
            game.playMove(move);
            long divideCounter = perft(depth - 1);
            nodes += divideCounter;
            game.undoMove(move);
            System.out.println(move.formatPerftMove() + ":" + divideCounter);
        }

        return nodes;
    }
}

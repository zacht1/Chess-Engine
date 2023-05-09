package model;

import model.generation.MoveGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

// NOTE : Results of PERFT testing compared against results of stockfish chess engine

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
    public void perftStartingPosTest() {
        game.setBoardFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
        assertEquals(20, perft(1));
        assertEquals(400, perft(2));
        assertEquals(8902, perft(3));
        assertEquals(197281, perft(4));
        // assertEquals(4865609, perft(5));
        // assertEquals(119060324, perft(6));
    }

    @Test
    public void perftKiwipetePosTest() {
        game.setBoardFEN("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - ");
        assertEquals(48, perft(1));
        assertEquals(2039, perft(2));
        assertEquals(97862, perft(3));
    }

    @Test
    public void perftEndgamePosTest() {
        game.setBoardFEN("8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - -");
        assertEquals(14, perft(1));
        assertEquals(191, perft(2));
        assertEquals(2812, perft(3));
        assertEquals(43238, perft(4));
    }

    /**
     * Returns the number of leaf nodes in a move generation tree of legal moves with given depth. The returned number
     * can then be compared to pre-determined values.
     *
     * REQUIRE : depth >= 1
     */
    private int perft(int depth) {
        List<Move> moveList = moveGenerator.generateLegalMoves(game, game.getCurrentTurn());
        int numPositions = 0;

        if (depth == 0) {
            return moveList.size();
        }

        for (Move move: moveList) {
            game.playMove(move);
            numPositions += perft(depth - 1);
            game.undoMove(move);
        }

        return numPositions;
    }

    /**
     * Variation of PERFT algorithm, which also returns number of leaf nodes in move generation tree with given depth.
     * Additionally, prints to console all moves and the number of leaf nodes in that moves sub-tree.
     */
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

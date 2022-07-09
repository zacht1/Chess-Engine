package model;

import model.generation.MoveGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MoveGeneratorTest {
    private Game testGame;
    private Player whitePlayer;
    private Player blackPlayer;
    private MoveGenerator testMoveGenerator;

    @BeforeEach
    public void init() {
        this.testGame = new Game();
        this.whitePlayer = new Player(true);
        this.blackPlayer = new Player(false);
        this.testMoveGenerator = new MoveGenerator();
    }

    @Test
    public void kingMovingIntoCheckTest() {
        testGame.setBoardFEN("rnbq1rk1/ppp1pppp/8/1B6/4P3/5P2/PBP3PP/RN2K2R w KQq - 0 1");
        List<Move> whiteMoveList = testMoveGenerator.generateMoves(testGame, whitePlayer);
        assertEquals(34, whiteMoveList.size());

        testGame.setBoardFEN("r2qk2r/pppbp1bp/3p2p1/8/2B5/8/PPPPP2P/RNBQK1NR b KQkq - 0 1");
        List<Move> blackMoveList = testMoveGenerator.generateMoves(testGame, blackPlayer);

        assertEquals(34, blackMoveList.size());
    }

    @Test
    public void kingInCheckTest() {
        testGame.setBoardFEN("4k3/8/2r5/8/2K5/8/8/8 w - - 0 1");
        List<Move> whiteMoveList = testMoveGenerator.generateMoves(testGame, whitePlayer);
        assertEquals(6, whiteMoveList.size());
        assertTrue(testMoveGenerator.inCheck());

        testGame.setBoardFEN("8/7R/4k3/8/3N4/8/5R2/2K5 b - - 0 1");
        List<Move> blackMoveList = testMoveGenerator.generateMoves(testGame, blackPlayer);
        assertEquals(3, blackMoveList.size());
        assertTrue(testMoveGenerator.inCheck());
    }

    @Test
    public void blockCheck() {
        testGame.setBoardFEN("r1bqk1nr/pppp1ppp/2n5/4p3/1b2P3/3P1N2/PPP2PPP/RNBQKB1R w KQkq - 1 4");
        List<Move> whiteMoveList = testMoveGenerator.generateMoves(testGame, whitePlayer);

        List<String> moveList = new ArrayList<>();
        for (Move move: whiteMoveList) {
            moveList.add(move.formatMove());
        }

        assertEquals(7, whiteMoveList.size());

        testGame.setBoardFEN("8/5p2/3bk3/5p2/8/1P6/P7/2K1R3 b - - 0 1");
        List<Move> blackMoveList = testMoveGenerator.generateMoves(testGame, blackPlayer);
        assertEquals(4, blackMoveList.size());
    }

    @Test
    public void pins() {
        testGame.setBoardFEN("rnbqk1nr/pp3ppp/8/3p4/1bpP4/2N1PN2/PP3PPP/R1BQKB1R w KQkq - 1 7");
        List<Move> whiteMoveList = testMoveGenerator.generateMoves(testGame, whitePlayer);
        assertEquals(18, testMoveGenerator.getPinnedPieces().get(0));
        assertTrue(testMoveGenerator.doPinsExistInPosition());
        assertEquals(27, whiteMoveList.size());

        testGame.setBoardFEN("8/pr5p/3k1n1r/3b4/8/8/PP3PPP/R2R2K1 b Q - 1 7");
        List<Move> blackMoveList = testMoveGenerator.generateMoves(testGame, blackPlayer);
        assertTrue(testMoveGenerator.doPinsExistInPosition());
        assertEquals(35, testMoveGenerator.getPinnedPieces().get(0));
        assertEquals(31, blackMoveList.size());
    }
}

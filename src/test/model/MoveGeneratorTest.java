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
        List<Move> whiteMoveList = testMoveGenerator.generateLegalMoves(testGame, whitePlayer);
        assertEquals(35, whiteMoveList.size());

        testGame.setBoardFEN("r2qk2r/pppbp1bp/3p2p1/8/2B5/8/PPPPP2P/RNBQK1NR b KQkq - 0 1");
        List<Move> blackMoveList = testMoveGenerator.generateLegalMoves(testGame, blackPlayer);

        assertEquals(34, blackMoveList.size());
    }

    @Test
    public void kingInCheckTest() {
        testGame.setBoardFEN("4k3/8/2r5/8/2K5/8/8/8 w - - 0 1");
        List<Move> whiteMoveList = testMoveGenerator.generateLegalMoves(testGame, whitePlayer);
        assertEquals(6, whiteMoveList.size());
        assertTrue(testMoveGenerator.inCheck());

        testGame.setBoardFEN("8/7R/4k3/8/3N4/8/5R2/2K5 b - - 0 1");
        List<Move> blackMoveList = testMoveGenerator.generateLegalMoves(testGame, blackPlayer);
        assertEquals(3, blackMoveList.size());
        assertTrue(testMoveGenerator.inCheck());

        testGame.setBoardFEN("8/8/8/2k5/3Pp3/8/8/4K3 b - d3 0 2");
        List<Move> blackMoveList2 = testMoveGenerator.generateLegalMoves(testGame, blackPlayer);

        List<String> moveList = new ArrayList<>();
        for (Move move: blackMoveList2) {
            moveList.add(move.formatMove());
        }
        System.out.println(moveList);

        assertTrue(testMoveGenerator.inCheck());
        assertEquals(9, blackMoveList2.size());
    }

    @Test
    public void blockCheck() {
        testGame.setBoardFEN("r1bqk1nr/pppp1ppp/2n5/4p3/1b2P3/3P1N2/PPP2PPP/RNBQKB1R w KQkq - 1 4");
        List<Move> whiteMoveList = testMoveGenerator.generateLegalMoves(testGame, whitePlayer);
        assertEquals(7, whiteMoveList.size());

        testGame.setBoardFEN("8/5p2/3bk3/5p2/8/1P6/P7/2K1R3 b - - 0 1");
        List<Move> blackMoveList = testMoveGenerator.generateLegalMoves(testGame, blackPlayer);
        assertEquals(4, blackMoveList.size());
    }

    @Test
    public void pins() {
        testGame.setBoardFEN("rnbqk1nr/pp3ppp/8/3p4/1bpP4/2N1PN2/PP3PPP/R1BQKB1R w KQkq - 1 7");
        List<Move> whiteMoveList = testMoveGenerator.generateLegalMoves(testGame, whitePlayer);
        assertEquals(18, testMoveGenerator.getPinnedPieces().get(0));
        assertTrue(testMoveGenerator.doPinsExistInPosition());
        assertEquals(27, whiteMoveList.size());

        testGame.setBoardFEN("8/pr5p/3k1n1r/3b4/8/8/PP3PPP/R2R2K1 b Q - 1 7");
        List<Move> blackMoveList = testMoveGenerator.generateLegalMoves(testGame, blackPlayer);
        assertTrue(testMoveGenerator.doPinsExistInPosition());
        assertEquals(35, testMoveGenerator.getPinnedPieces().get(0));
        assertEquals(31, blackMoveList.size());

        testGame.setBoardFEN("4k3/8/4r3/8/8/4Q3/8/2K5 b - - 0 1");
        List<Move> blackMoveList2 = testMoveGenerator.generateLegalMoves(testGame, blackPlayer);
        assertTrue(testMoveGenerator.doPinsExistInPosition());
        assertEquals(9, blackMoveList2.size());
    }

    @Test
    public void testCastling() {
        testGame.setBoardFEN("r3k1nr/8/5q2/2b5/8/8/8/R3K2R w KQkq - 0 1");
        List<Move> whiteMoveList = testMoveGenerator.generateLegalMoves(testGame, whitePlayer);
        assertEquals(23, whiteMoveList.size());

        testGame.setBoardFEN("r3k2r/pbpq1pbp/1pnppnp1/8/8/1PNPPNP1/PBPQ1PBP/R4RK1 b kq - 3 10");
        List<Move> blackMoveList = testMoveGenerator.generateLegalMoves(testGame, blackPlayer);
        assertEquals(37, blackMoveList.size());
    }

    @Test
    public void testPromotionMoves() {
        testGame.setBoardFEN("4n3/P3PP2/8/3K4/5k2/8/2pp3p/3N4 w - - 0 1");
        List<Move> whiteMoveList = testMoveGenerator.generateLegalMoves(testGame, whitePlayer);
        assertEquals(21, whiteMoveList.size());

        testGame.setBoardFEN("4n3/P3PP2/8/3K4/5k2/8/2pp3p/3N4 b - - 0 1");
        List<Move> blackMoveList = testMoveGenerator.generateLegalMoves(testGame, blackPlayer);
        assertEquals(21, blackMoveList.size());
    }

    @Test
    public void enPassantDiscoveredCheck() {
        testGame.setBoardFEN("8/8/8/8/k2Pp2Q/8/8/3K4 b - - 0 2");
        List<Move> blackMoveList = testMoveGenerator.generateLegalMoves(testGame, blackPlayer);
        assertEquals(6, blackMoveList.size());
    }

    @Test
    public void otherPositions() {
        testGame.setBoardFEN("r3k2r/p1pp1pb1/bn2Qnp1/2qPN3/1p2P3/2N5/PPPBBPPP/R3K2R b KQkq - 3 2");
        List<Move> moveList = testMoveGenerator.generateLegalMoves(testGame, testGame.getCurrentTurn());
        assertEquals(5, moveList.size());

        testGame.setBoardFEN("k5b1/8/8/3Pp3/2K5/8/8/8 w - e6 0 2");
        List<Move> moveList1 = testMoveGenerator.generateLegalMoves(testGame, testGame.getCurrentTurn());
        assertEquals(7, moveList1.size());

        testGame.setBoardFEN("K6b/8/8/2pP4/8/3r4/6b1/k7 w - c6 0 3");
        List<Move> moveList2 = testMoveGenerator.generateLegalMoves(testGame, testGame.getCurrentTurn());
        assertEquals(4, moveList2.size());

        testGame.setBoardFEN("8/1k6/8/8/4pP1K/8/8/7B b - f3 0 2");
        List<Move> moveList3 = testMoveGenerator.generateLegalMoves(testGame, testGame.getCurrentTurn());
        assertEquals(9, moveList3.size());

        testGame.setBoardFEN("1Q5k/8/8/8/8/8/6p1/K7 b - - 0 6");
        List<Move> moveList4 = testMoveGenerator.generateLegalMoves(testGame, testGame.getCurrentTurn());
        assertEquals(2, moveList4.size());

        testGame.setBoardFEN("8/2p5/3p4/KP2r3/7k/5p2/4P1P1/R7 w - - 2 3");
        List<Move> moveList5 = testMoveGenerator.generateLegalMoves(testGame, testGame.getCurrentTurn());
        assertEquals(19, moveList5.size());

        testGame.setBoardFEN("8/2p5/3p4/KP5r/4P2k/8/6p1/7R b - - 1 3");
        List<Move> moveList6 = testMoveGenerator.generateLegalMoves(testGame, testGame.getCurrentTurn());

        List<String> moves = new ArrayList<>();
        for (Move m: moveList6) {
            moves.add(m.formatMove());
        }
        System.out.println(moves);

        assertEquals(7, moveList6.size());
    }
}

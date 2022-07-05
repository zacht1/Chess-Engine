package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PseudoLegalMoveGeneratorTest {
    private PseudoLegalMoveGenerator testPseudoLegalMoveGenerator;
    private Player testWhitePlayer;
    private Player testBlackPlayer;
    private Game testGame;

    @BeforeEach
    public void init() {
        testPseudoLegalMoveGenerator = new PseudoLegalMoveGenerator();
        testWhitePlayer = new Player(true);
        testBlackPlayer = new Player(false);
        testGame = new Game();
    }

    @Test
    public void generatePseudoLegalMovesOnStartingBoardTest() {
        List<Move> whiteMoveList = testPseudoLegalMoveGenerator.generatePseudoLegalMoves(testGame, testWhitePlayer);
        List<Move> blackMoveList = testPseudoLegalMoveGenerator.generatePseudoLegalMoves(testGame, testBlackPlayer);

        assertEquals(20, whiteMoveList.size());
        assertEquals(20, blackMoveList.size());
    }

    @Test
    public void generatePseudoLegalMovesTest() {
        testGame.setBoardFEN("4q3/p3r1k1/1p2B1p1/2pQ2P1/2P5/4P1P1/PP6/3R2K1 b - - 0 30");

        List<Move> whiteMoveList = testPseudoLegalMoveGenerator.generatePseudoLegalMoves(testGame, testWhitePlayer);
        List<Move> blackMoveList = testPseudoLegalMoveGenerator.generatePseudoLegalMoves(testGame, testBlackPlayer);

        List<String> moveList = new ArrayList<>();
        for (Move move: whiteMoveList) {
            moveList.add(move.formatMove());
        }
        System.out.println(moveList);

        assertEquals(42, whiteMoveList.size());
        assertEquals(27, blackMoveList.size());
    }
}

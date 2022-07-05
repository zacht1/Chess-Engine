package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PseudoLegalMoveGeneratorTest {
    private Player testWhitePlayer;
    private Player testBlackPlayer;
    private Board testBoard;

    @BeforeEach
    public void init() {
        testWhitePlayer = new Player(true);
        testBlackPlayer = new Player(false);
        testBoard = new Board();
    }

    @Test
    public void generatePseudoLegalMovesOnStartingBoardTest() {
        List<Move> whiteMoveList = PseudoLegalMoveGenerator.generatePseudoLegalMoves(testWhitePlayer, testBoard);
        List<Move> blackMoveList = PseudoLegalMoveGenerator.generatePseudoLegalMoves(testBlackPlayer, testBoard);

        assertEquals(20, whiteMoveList.size());
        assertEquals(20, blackMoveList.size());
    }

    @Test
    public void generatePseudoLegalMovesTest() {
        testBoard.setBoardFEN("4q3/p3r1k1/1p2B1p1/2pQ2P1/2P5/4P1P1/PP6/3R2K1 b - - 0 30");

        List<Move> whiteMoveList = PseudoLegalMoveGenerator.generatePseudoLegalMoves(testWhitePlayer, testBoard);
        List<Move> blackMoveList = PseudoLegalMoveGenerator.generatePseudoLegalMoves(testBlackPlayer, testBoard);

        assertEquals(42, whiteMoveList.size());
        assertEquals(27, blackMoveList.size());
    }
}

package model;

import model.search.Evaluation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.text.DecimalFormat;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EvaluationTest {
    private Game testGame;
    private Evaluation testEvaluation;

    @BeforeEach
    public void init() {
        testGame = new Game();
        testEvaluation = new Evaluation();
    }

    @Test
    public void evaluatePositionTest() {
        DecimalFormat decimalFormat = new DecimalFormat("#.#");

        assertEquals(0, testEvaluation.evaluatePosition(testGame));

        testGame.setBoardFEN("r1bnkb1r/1p3p1p/p3pp2/8/P1P2P2/1Bp2N2/1PP3PP/R1B1K2R w KQkq - 0 13");
        assertEquals(1, testEvaluation.evaluatePosition(testGame));

        testGame.setBoardFEN("r3k2r/p4p1p/2Rp1p2/q7/4P3/8/P1Q2PPP/1R4K1 b kq - 1 20");
        assertEquals(-2.9, testEvaluation.evaluatePosition(testGame));

        testGame.setBoardFEN("rn2k2r/pbpp1ppp/1p2p3/4P1N1/3PB3/8/P1PB1P1R/R2nK3 w Qkq - 0 15");
        assertEquals(-2.1, Double.parseDouble(decimalFormat.format(testEvaluation.evaluatePosition(testGame))));
    }

    @Test
    public void countMaterialTest() {
        testEvaluation.evaluatePosition(testGame);
        assertEquals(0, testEvaluation.countMaterial());

        testGame.setBoardFEN("rnbqk2r/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
        testEvaluation.evaluatePosition(testGame);
        assertEquals(6.2, testEvaluation.countMaterial());

        testGame.setBoardFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPP1/RNBQKBNR b KQkq - 0 1");
        testEvaluation.evaluatePosition(testGame);
        assertEquals(1, testEvaluation.countMaterial());

        testGame.setBoardFEN("7k/7p/8/5p2/8/1N5P/5PPK/q1r5 w - - 0 38");
        testEvaluation.evaluatePosition(testGame);
        assertEquals(-10, testEvaluation.countMaterial());

        testGame.setBoardFEN("rn1qkbnr/ppp2ppp/3p4/4p3/3PP3/5N2/PPP2PPP/R1BQKB1R b KQkq - 0 5");
        testEvaluation.evaluatePosition(testGame);
        assertEquals(-0.2, testEvaluation.countMaterial());
    }

    @Test
    public void countMobilityTest() {
        DecimalFormat decimalFormat = new DecimalFormat("#.#");

        testEvaluation.evaluatePosition(testGame);
        assertEquals(0, testEvaluation.countMobility());

        testGame.setBoardFEN("1r3rk1/p1p3pp/3q1p2/3p4/3P2PN/4P2P/Pn1K1P2/1R5R w - - 0 24");
        testEvaluation.evaluatePosition(testGame);
        assertEquals(-1.4, Double.parseDouble(decimalFormat.format(testEvaluation.countMobility())));
    }

    @Test
    public void countPawnErrorsTest() {
        DecimalFormat decimalFormat = new DecimalFormat("#.#");

        testEvaluation.evaluatePosition(testGame);
        assertEquals(0, testEvaluation.countPawnErrors() + 0);

        testGame.setBoardFEN("1r3rk1/p1p3pp/2nq1p2/3p4/3P2PN/4P2P/PpQ2P2/R3K2R w KQ - 0 21");
        testEvaluation.evaluatePosition(testGame);
        assertEquals(-0.5, Double.parseDouble(decimalFormat.format(testEvaluation.countPawnErrors())));

        testGame.setBoardFEN("1r3rk1/p1p3pp/2pq4/8/3P2PN/4P2P/Pn1K1P2/1R5R b - - 0 24");
        testEvaluation.evaluatePosition(testGame);
        assertEquals(-1, Double.parseDouble(decimalFormat.format(testEvaluation.countPawnErrors())));
    }
}

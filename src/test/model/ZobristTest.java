package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class ZobristTest {
    private Zobrist testZobrist;

    @BeforeEach
    public void init() {
        testZobrist = new Zobrist();
    }

    @Test
    public void constructorTest() {
        long[][] pieceTable = testZobrist.getPieceTable();
        long[] enPassantFiles = testZobrist.getEnPassantFiles();
        long[] castlingRights = testZobrist.getCastlingRights();

        assertEquals(768, pieceTable.length);
        assertEquals(8, enPassantFiles.length);
        assertEquals(4, castlingRights.length);
    }

    @Test
    public void randomLongGeneratorTest() {
        long num1 = testZobrist.randomLongGenerator();
        long num2 = testZobrist.randomLongGenerator();

        assertNotEquals(num1, num2);

        long num3 = testZobrist.randomLongGenerator();
        long num4 = testZobrist.randomLongGenerator();

        assertNotEquals(num3, num4);
        assertNotEquals(num1, num3);
        assertNotEquals(num1, num4);
        assertNotEquals(num2, num3);
        assertNotEquals(num2, num4);
    }

    @Test
    public void calculateHashTest() {
        Game testGame = new Game();

        long startingHash = testZobrist.calculateHash(testGame);

        testGame.playMove(new Move(testGame, 1, 1, 1, 3));

        long secondHash = testZobrist.calculateHash(testGame);

        assertNotEquals(startingHash, secondHash);

        long pieceNum = testZobrist.getPieceTable()[16][1];
        assertEquals(startingHash ^ pieceNum ^ testZobrist.getWhiteToPlay(), secondHash);
    }
}

package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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

        assertEquals(64, pieceTable.length);
        assertEquals(12, pieceTable[0].length);
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

        assertTrue(num1 >= 0);
        assertTrue(num2 >= 0);
        assertTrue(num3 >= 0);
        assertTrue(num4 >= 0);

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
        System.out.println(startingHash);

        testGame.playMove(new Move(testGame, 1, 2, 1, 3));

        long secondHash = testZobrist.calculateHash(testGame);
        System.out.println(secondHash);

        assertNotEquals(startingHash, secondHash);

        long pieceNum = testZobrist.getPieceTable()[16][0];
        long spaceNum = testZobrist.getPieceTable()[8][0];
        assertEquals(startingHash ^ pieceNum ^ spaceNum ^ testZobrist.getWhiteToPlay(), secondHash);
    }

    @Test
    public void calculateEnPassantHashTest() {
        Game testGame = new Game();
        testGame.setBoardFEN("rnbqkb1r/pppppppp/5n2/8/2P5/8/PP1PPPPP/RNBQKBNR w KQkq - 1 2");
        testGame.playMove(new Move(testGame, 3,4,3,5));
        testGame.playMove(new Move(testGame, 2, 7, 2, 5));

        long firstHash = testZobrist.calculateHash(testGame);

        testGame.playMove(new Move(testGame, 3,5,2,6));

        long secondHash = testZobrist.calculateHash(testGame);

        assertNotEquals(firstHash, secondHash);

        long pieceNum = testZobrist.getPieceTable()[34][0];
        long num = testZobrist.getPieceTable()[33][6];
        long spaceNum = testZobrist.getPieceTable()[41][0];

        assertEquals(secondHash ^ pieceNum ^ num ^ spaceNum ^ testZobrist.getWhiteToPlay() ^ testZobrist.getEnPassantFiles()[1], firstHash);
    }

    @Test
    public void calculateBlackEnPassantHashTest() {
        Game testGame = new Game();
        testGame.setBoardFEN("rnbqkbnr/ppp1pppp/8/3p4/8/5NP1/PPPPPP1P/RNBQKB1R b KQkq - 0 2");
        testGame.playMove(new Move(testGame,4,5,4,4));
        testGame.playMove(new Move(testGame,3,2,3,4));

        long firstHash = testZobrist.calculateHash(testGame);

        testGame.playMove(new Move(testGame,4,4,3,3));

        long secondHash = testZobrist.calculateHash(testGame);

        assertNotEquals(firstHash, secondHash);

        long startNum = testZobrist.getPieceTable()[27][6];
        long captureNum = testZobrist.getPieceTable()[26][0];
        long endNum = testZobrist.getPieceTable()[18][6];

        assertEquals(secondHash ^ startNum ^ captureNum ^ endNum ^ testZobrist.getWhiteToPlay() ^ testZobrist.getEnPassantFiles()[2], firstHash);
    }

    @Test
    public void calculateWhiteKingMoveHashTest() {
        Game testGame = new Game();
        testGame.setBoardFEN("r3k2r/pppppppp/8/8/8/8/PPPPPPPP/R3K2R w KQkq - 0 1");

        long firstHash = testZobrist.calculateHash(testGame);

        testGame.playMove(new Move(testGame, 5,1,6,1));

        long secondHash = testZobrist.calculateHash(testGame);

        assertNotEquals(firstHash, secondHash);

        long startNum = testZobrist.getPieceTable()[4][5];
        long endNum = testZobrist.getPieceTable()[5][5];

        assertEquals(secondHash ^ startNum ^ endNum ^ testZobrist.getWhiteToPlay() ^
                testZobrist.getCastlingRights()[Zobrist.WHITE_KING_SIDE] ^ testZobrist.getCastlingRights()[Zobrist.WHITE_QUEEN_SIDE], firstHash);
    }

    @Test
    public void calculateBlackKingMoveHashTest() {
        Game testGame = new Game();
        testGame.setBoardFEN("r3k2r/pppppppp/8/8/8/8/PPPPPPPP/R3K2R b KQkq - 0 1");

        long firstHash = testZobrist.calculateHash(testGame);

        testGame.playMove(new Move(testGame, 5,8,4,8));

        long secondHash = testZobrist.calculateHash(testGame);

        assertNotEquals(firstHash, secondHash);

        long startNum = testZobrist.getPieceTable()[60][11];
        long endNum = testZobrist.getPieceTable()[59][11];

        assertEquals(secondHash ^ startNum ^ endNum ^ testZobrist.getWhiteToPlay() ^
                testZobrist.getCastlingRights()[Zobrist.BLACK_KING_SIDE] ^ testZobrist.getCastlingRights()[Zobrist.BLACK_QUEEN_SIDE], firstHash);
    }

    @Test
    public void calculateWhiteRookMoveHashTest() {
        Game testGame = new Game();
        testGame.setBoardFEN("r3k2r/pppppppp/8/8/8/8/PPPPPPPP/R3K2R w KQkq - 0 1");

        long firstHash = testZobrist.calculateHash(testGame);

        testGame.playMove(new Move(testGame, 1,1,3,1));

        long secondHash = testZobrist.calculateHash(testGame);

        assertNotEquals(firstHash, secondHash);

        long startNum = testZobrist.getPieceTable()[0][3];
        long endNum = testZobrist.getPieceTable()[2][3];

        assertEquals(secondHash ^ startNum ^ endNum ^ testZobrist.getWhiteToPlay() ^
                testZobrist.getCastlingRights()[Zobrist.WHITE_QUEEN_SIDE], firstHash);
    }

    @Test
    public void calculateBlackRookMoveHashTest() {
        Game testGame = new Game();
        testGame.setBoardFEN("r3k2r/pppppppp/8/8/8/8/PPPPPPPP/R3K2R b KQkq - 0 1");

        long firstHash = testZobrist.calculateHash(testGame);

        testGame.playMove(new Move(testGame, 8,8,6,8));

        long secondHash = testZobrist.calculateHash(testGame);

        assertNotEquals(firstHash, secondHash);

        long startNum = testZobrist.getPieceTable()[63][9];
        long endNum = testZobrist.getPieceTable()[61][9];

        assertEquals(secondHash ^ startNum ^ endNum ^ testZobrist.getWhiteToPlay() ^
                testZobrist.getCastlingRights()[Zobrist.BLACK_KING_SIDE], firstHash);
    }
}
package model;

import enumerations.MoveType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MoveTest {
    private Game testGame;
    private Board testBoard;

    @BeforeEach
    public void init() {
        testGame = new Game();
        testBoard = testGame.getBoard();
    }

    @Test
    public void constructorTestBasicMove() {
        Move testMove = new Move(testGame, 7,2,7,4);

        assertEquals(7, testMove.getStartX());
        assertEquals(2, testMove.getStartY());
        assertEquals(7, testMove.getEndX());
        assertEquals(4, testMove.getEndY());

        assertEquals(Piece.wPawn, testMove.getMovedPiece());
        assertEquals(Piece.empty, testMove.getCapturedPiece());

        assertEquals(MoveType.NORMAL, testMove.getMoveType());
    }

    @Test
    public void constructorTestCapture() {
        testGame.setBoardFEN("rnbqkbnr/ppp2ppp/4p3/3p4/4P3/2N5/PPPP1PPP/R1BQKBNR w KQkq - 0 3");

        Move testMove = new Move(testGame, 3,3,4,5);

        assertEquals(3, testMove.getStartX());
        assertEquals(3, testMove.getStartY());
        assertEquals(4, testMove.getEndX());
        assertEquals(5, testMove.getEndY());

        assertEquals(Piece.wKnight, testMove.getMovedPiece());
        assertEquals(Piece.bPawn, testMove.getCapturedPiece());

        assertEquals(MoveType.CAPTURE, testMove.getMoveType());
    }

    @Test
    public void constructorTestEnPassant() {
        testGame.setBoardFEN("rnbqkbnr/ppp1pppp/8/8/3pP2P/8/PPPP1PP1/RNBQKBNR b KQkq e3 0 3");

        Move testMove = new Move(testGame, 4, 4, 5, 3);

        assertEquals(4, testMove.getStartX());
        assertEquals(4, testMove.getStartY());
        assertEquals(5, testMove.getEndX());
        assertEquals(3, testMove.getEndY());

        assertEquals(Piece.bPawn, testMove.getMovedPiece());
        assertEquals(Piece.wPawn, testMove.getCapturedPiece());

        assertEquals(MoveType.EN_PASSANT, testMove.getMoveType());
    }

    @Test
    public void constructorTestPromotionMove() {
        testGame.setBoardFEN("rnbqkb2/1pp1pprP/p4n2/6P1/8/4P3/PPPP4/RNBQKBNR w KQq - 0 11");

        Move testMove = new Move(testGame, 8,7,8,8);

        assertEquals(8, testMove.getStartX());
        assertEquals(7, testMove.getStartY());
        assertEquals(8, testMove.getEndX());
        assertEquals(8, testMove.getEndY());

        assertEquals(Piece.wPawn, testMove.getMovedPiece());
        assertEquals(Piece.empty, testMove.getCapturedPiece());

        assertEquals(MoveType.NORMAL, testMove.getMoveType());
    }

    @Test
    public void constructorTestKingSideCastle() {
        testGame.setBoardFEN("rnbqk2r/pppp1ppp/4pn2/2b5/2B5/4PN2/PPPP1PPP/RNBQK2R w KQkq - 4 4");

        Move testMove = new Move(testGame, 5,1,7,1);

        assertEquals(5, testMove.getStartX());
        assertEquals(1, testMove.getStartY());
        assertEquals(7, testMove.getEndX());
        assertEquals(1, testMove.getEndY());

        assertEquals(Piece.wKing, testMove.getMovedPiece());
        assertEquals(Piece.empty, testMove.getCapturedPiece());

        assertEquals(MoveType.KING_SIDE_CASTLE, testMove.getMoveType());
    }

    @Test
    public void constructorTestQueenSideCastle() {
        testGame.setBoardFEN("r3kbnr/pbppqppp/1pn1p3/8/2B1P3/P1N2N2/1PPP1PPP/R1BQ1RK1 b kq - 3 6");

        Move testMove = new Move(testGame, 5,8,3,8);

        assertEquals(5, testMove.getStartX());
        assertEquals(8, testMove.getStartY());
        assertEquals(3, testMove.getEndX());
        assertEquals(8, testMove.getEndY());

        assertEquals(Piece.bKing, testMove.getMovedPiece());
        assertEquals(Piece.empty, testMove.getCapturedPiece());

        assertEquals(MoveType.QUEEN_SIDE_CASTLE, testMove.getMoveType());
    }

    @Test
    public void formatMoveTest() {
        Move testMove = new Move(testGame, 8,2,8,3);
        assertEquals("h3", testMove.formatMove());

        Move testKnightMove = new Move(testGame, 7,8,6,6);
        assertEquals("Nf6", testKnightMove.formatMove());

        testGame.setBoardFEN("rnbqkb1r/pppppppp/5n2/8/4P3/7P/PPPP1PP1/RNBQKBNR b KQkq - 0 2");
        Move testCapture = new Move(testGame, 6,6,5,4);
        assertEquals("Nxe4", testCapture.formatMove());

        testGame.setBoardFEN("r2qkbnr/pbpppppp/1pn5/1B6/4P3/5N2/PPPP1PPP/RNBQK2R w KQkq - 4 4");
        Move testKingSideCastle = new Move(testGame, 5,1,7,1);
        assertEquals("O-O", testKingSideCastle.formatMove());

        testGame.setBoardFEN("r3kbnr/pbppqppp/1pn1p3/1B6/4P3/P1N2N2/1PPP1PPP/R1BQ1RK1 b kq - 2 6");
        Move testQueenSideCastle = new Move(testGame, 5,8,3,8);
        assertEquals("O-O-O", testQueenSideCastle.formatMove());
    }
}

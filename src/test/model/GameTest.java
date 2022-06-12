package model;

import enumerations.CheckStatus;
import enumerations.GameStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static model.Piece.*;
import static org.junit.jupiter.api.Assertions.*;

public class GameTest {
    private Game testGame;

    @BeforeEach
    public void init() {
        testGame = new Game();
    }

    @Test
    public void constructorTest() {
        int[] startingBoard = new int[]{
                wRook, wKnight, wBishop, wQueen, wKing, wBishop, wKnight, wRook,
                wPawn, wPawn, wPawn, wPawn, wPawn, wPawn, wPawn, wPawn,
                empty, empty, empty, empty, empty, empty, empty, empty,
                empty, empty, empty, empty, empty, empty, empty, empty,
                empty, empty, empty, empty, empty, empty, empty, empty,
                empty, empty, empty, empty, empty, empty, empty, empty,
                bPawn, bPawn, bPawn, bPawn, bPawn, bPawn, bPawn, bPawn,
                bRook, bKnight, bBishop, bQueen, bKing, bBishop, bKnight, bRook
        };

        for (int i = 0; i < 64; i++) {
            assertEquals(testGame.getBoard().getBoard()[i], startingBoard[i]);
        }

        assertEquals(GameStatus.ACTIVE, testGame.getGameStatus());

        assertEquals(2, testGame.getPlayers().length);
        assertTrue(testGame.getPlayers()[0].isWhite());
        assertFalse(testGame.getPlayers()[1].isWhite());

        assertTrue(testGame.getCurrentTurn().isWhite());

        assertEquals(0, testGame.getMoveList().size());

        assertTrue(testGame.isWhiteQueenSideCastling());
        assertTrue(testGame.isWhiteKingSideCastling());
        assertTrue(testGame.isBlackQueenSideCastling());
        assertTrue(testGame.isBlackKingSideCastling());
    }

    @Test
    public void playMoveTest() {
        Move testMove1 = new Move(testGame.getBoard(), 5,2,5,4);
        assertTrue(testGame.playMove(testMove1));
        assertEquals(empty, testGame.getBoard().getPiece(5,2));
        assertEquals(wPawn, testGame.getBoard().getPiece(5,4));
        assertEquals(testGame.getPlayers()[1], testGame.getCurrentTurn());
        assertEquals(testMove1, testGame.getMoveList().get(0));

        Move testMove2 = new Move(testGame.getBoard(), 1,8, 1, 4);
        assertFalse(testGame.playMove(testMove2));
        assertEquals(bRook, testGame.getBoard().getPiece(1,8));
        assertEquals(empty, testGame.getBoard().getPiece(1,4));
        assertEquals(testGame.getPlayers()[1], testGame.getCurrentTurn());
        assertEquals(1, testGame.getMoveList().size());

        testGame.setBoardFEN("rnbqkbnr/pppp1ppp/8/4p3/3P4/5N2/PPP1PPPP/RNBQKB1R b KQkq - 1 2");

        Move testMove3 = new Move(testGame.getBoard(), 6,8, 2, 4);
        assertTrue(testGame.playMove(testMove3));
        assertEquals(empty, testGame.getBoard().getPiece(6,8));
        assertEquals(bBishop, testGame.getBoard().getPiece(2,4));
        assertEquals(testGame.getPlayers()[0], testGame.getCurrentTurn());
        assertEquals(CheckStatus.WHITE_IN_CHECK, testGame.getCheckStatus());

        testGame.setBoardFEN("rnbqkbnr/1pp1ppp1/p2p3p/7Q/2B5/4P3/PPPP1PPP/RNB1K1NR w KQkq - 0 4");

        Move testMove4 = new Move(testGame.getBoard(), 8, 5, 6,7);
        assertTrue(testGame.playMove(testMove4));
        assertEquals(CheckStatus.BLACK_IN_CHECK, testGame.getCheckStatus());
        assertEquals(GameStatus.WHITE_CHECKMATE, testGame.getGameStatus());
    }

    @Test
    public void nextTurnTest() {
        testGame.nextTurn();
        assertFalse(testGame.getCurrentTurn().isWhite());

        testGame.nextTurn();
        assertTrue(testGame.getCurrentTurn().isWhite());

        testGame.nextTurn();
        assertFalse(testGame.getCurrentTurn().isWhite());
    }
}
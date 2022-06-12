package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static model.Piece.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FenUtilityTest {
    Board testBoard;
    Game testGame;
    FenUtility testFENUtility;

    @BeforeEach
    public void init() {
        testBoard = new Board();
        testGame = new Game();
        testFENUtility = new FenUtility();
    }

    @Test
    public void loadGameFromFENTest() {
        int[] testChessBoard = new int[]{
                empty, empty, empty, empty, bKnight, empty, wKing, empty,
                empty, wPawn, empty, empty, wRook, wPawn, wQueen, empty,
                empty, empty, wPawn, empty, empty, bRook, wPawn, empty,
                wPawn, empty, empty, empty, wPawn, empty, empty, empty,
                empty, empty, bPawn, empty, empty, wBishop, bQueen, empty,
                bPawn, empty, empty, bPawn, empty, empty, bPawn, empty,
                empty, bPawn, empty, empty, empty, empty, bKing, empty,
                empty, empty, empty, empty, empty, bRook, empty, empty
        };

        testFENUtility.loadGameFromFEN(testGame, "5r2/1p4k1/p2p2p1/2p2Bq1/P3P3/2P2rP1/1P2RPQ1/4n1K1 w - - 0 29");

        for (int i = 0; i < 64; i++) {
            assertEquals(testChessBoard[i], testGame.getBoard().getBoard()[i]);
        }

        // TODO: finish other aspects of FEN
    }

    @Test
    public void getFENFromPosition() {
        int[] testChessBoard = new int[]{
                empty, empty, empty, empty, empty, empty, empty, empty,
                empty, empty, wPawn, bRook, empty, empty, wKing, empty,
                wPawn, empty, empty, empty, empty, empty, empty, empty,
                bPawn, empty, wPawn, empty, wRook, empty, empty, empty,
                empty, empty, bKing, empty, empty, empty, wPawn, empty,
                empty, empty, bPawn, empty, empty, empty, empty, empty,
                empty, empty, empty, empty, empty, empty, empty, empty,
                empty, empty, empty, empty, empty, empty, empty, empty
        };

        testGame.getBoard().setBoard(testChessBoard);
        // TODO: add additional FEN elements to the testBoard

        assertEquals("8/8/2p5/2k3P1/p1P1R3/P7/2Pr2K1/8 w - - 1 49", testFENUtility.getFENFromGame(testGame));
    }

    @Test
    public void loadPositionFromFENTest() {
        int[] testChessBoard = new int[]{
                empty, empty, empty, wQueen, empty, wRook, wKing, empty,
                wPawn, wPawn, empty, empty, empty, wPawn, wPawn, wPawn,
                empty, wKnight, empty, empty, empty, wKnight, empty, empty,
                empty, empty, empty, empty, empty, empty, empty, empty,
                empty, bPawn, wRook, wPawn, empty, empty, empty, empty,
                bPawn, empty, empty, empty, empty, empty, bKnight, empty,
                empty, bBishop, empty, bQueen, empty, bPawn, bPawn, bPawn,
                empty, bRook, empty, empty, empty, bRook, bKing, empty
        };

        testFENUtility.loadPositionFromFEN(testBoard, "1r3rk1/1b1q1ppp/p5n1/1pRP4/8/1N3N2/PP3PPP/3Q1RK1");

        for (int i = 0; i < 64; i++) {
            assertEquals(testChessBoard[i], testBoard.getBoard()[i]);
        }
    }
}

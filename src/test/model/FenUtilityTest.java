package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static model.Piece.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FenUtilityTest {
    Game testGame;
    Board testBoard;
    FenUtility testFENUtility;

    @BeforeEach
    public void init() {
        testGame = new Game();
        testBoard = testGame.getBoard();
        testFENUtility = new FenUtility();
    }

    @Test
    public void loadPositionFromFENTest() {
        int[] testChessBoard = new int[]{
                empty, empty, empty, empty, bKnight, empty, wKing, empty,
                empty, wPawn, empty, empty, wRook, wPawn, wQueen, empty,
                empty, empty, wPawn, empty, empty, bRook, wPawn, empty,
                wPawn, empty, empty, empty, wPawn, empty, empty, empty,
                empty, empty, bPawn, empty, empty, wBishop, bQueen, empty,
                bPawn, empty, empty, bPawn, empty, empty, bPawn, empty,
                empty, bPawn, empty, empty, empty, empty, bKnight, empty,
                empty, empty, empty, empty, empty, bRook, empty, empty
        };

        testFENUtility.loadPositionFromFEN(testGame, "5r2/1p4k1/p2p2p1/2p2Bq1/P3P3/2P2rP1/1P2RPQ1/4n1K1 w - - 0 29");

        for (int i = 0; i < 64; i++) {
            assertEquals(testChessBoard[i], testBoard.getBoard()[i]);
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

        testBoard.setBoard(testChessBoard);
        // TODO: add additional FEN elements to the testBoard

        assertEquals("8/8/2p5/2k3P1/p1P1R3/P7/2Pr2K1/8 w - - 1 49", testFENUtility.getFENFromPosition(testGame));
    }
}

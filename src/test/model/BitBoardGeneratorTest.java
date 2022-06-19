package model;

import org.junit.jupiter.api.Test;

import static model.Piece.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class BitBoardGeneratorTest {
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

    long[] startingBitBoards = new long[]{
            65280L, 66L, 36L, 129L, 8L, 16L,
            71776119061217280L, 4755801206503243776L, 2594073385365405696L,
            -9151314442816847872L, 576460752303423488L, 1152921504606846976L
    };

    int[] middleGameBoard = new int[]{
            empty, empty, empty, wQueen, empty, wRook, wKing, empty,
            wPawn, wPawn, empty, empty, empty, wPawn, wPawn, wPawn,
            empty, wKnight, empty, empty, empty, wKnight, empty, empty,
            empty, empty, empty, empty, empty, empty, empty, empty,
            empty, bPawn, wRook, wPawn, empty, empty, empty, empty,
            bPawn, empty, empty, empty, empty, empty, bKnight, empty,
            empty, bBishop, empty, bQueen, empty, bPawn, bPawn, bPawn,
            empty, bRook, empty, empty, empty, bRook, bKing, empty
    };

    long[] middleGameBitBoards = new long[]{
            34359796480L, 2228224L, 0L, 17179869216L, 8L, 64L,
            63051502884749312L, 70368744177664L, 562949953421312L,
            2449958197289549824L, 2251799813685248L, 4611686018427387904L

};

    @Test
    public void arrayToBitBoardsTest() {
        // starting game board
        long[] startingGameBitboards = BitBoardGenerator.arrayToBitBoards(startingBoard);

        String whitePawnBoard = Long.toBinaryString(startingGameBitboards[0]);
        assertEquals(whitePawnBoard, "1111111100000000");

        for (int i = 0; i < 12; i++) {
            assertEquals(startingBitBoards[i], startingGameBitboards[i]);
        }

        String blackPawnBoard = Long.toBinaryString(startingGameBitboards[6]);
        assertEquals(blackPawnBoard, "11111111000000000000000000000000000000000000000000000000");


        // middle game board
        long[] middleGameBitboards = BitBoardGenerator.arrayToBitBoards(middleGameBoard);

        String whiteKingBoard = Long.toBinaryString(middleGameBitboards[5]);
        assertEquals(whiteKingBoard, "1000000");

        for (int n = 0; n < 12; n++) {
            assertEquals(middleGameBitBoards[n], middleGameBitboards[n]);
        }

        String blackRookBoard = Long.toBinaryString(middleGameBitboards[9]);
        assertEquals(blackRookBoard, "10001000000000000000000000000000000000000000000000000000000000");
    }

    @Test
    public void bitboardsToArrayTest() {
        int[] startingGameBoard = BitBoardGenerator.bitboardsToArray(startingBitBoards);
        for (int i = 0; i < 64; i++) {
            assertEquals(startingBoard[i], startingGameBoard[i]);
        }

        int [] middleBoard = BitBoardGenerator.bitboardsToArray(middleGameBitBoards);
        for (int i = 0; i < 64; i++) {
            assertEquals(middleGameBoard[i], middleBoard[i]);
        }
    }
}

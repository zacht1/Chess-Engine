package model;

import org.junit.jupiter.api.Test;

import static model.Piece.*;
import static model.Piece.bRook;

public class BitBoardGeneratorTest {

    @Test
    public void arrayToBitBoardsTest() {
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

        BitBoardGenerator.arrayToBitBoards(startingBoard);
    }
}

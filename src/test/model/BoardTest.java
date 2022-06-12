package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;

import static model.Piece.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class BoardTest {
    private Board testBoard;
    private Game testGame;

    @BeforeEach
    public void init() {
        testGame = new Game();
        testBoard = testGame.getBoard();
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

        assertEquals(testBoard.getBoard().length, startingBoard.length);

        for (int i = 0; i < 64; i++) {
            assertEquals(testBoard.getBoard()[i], startingBoard[i]);
        }
    }

    @Test
    public void makeBasicMoveTest() {
        Move testPawnMove = new Move(testBoard,2,2,2,4);
        testBoard.makeMove(testPawnMove);
        assertEquals(0, testBoard.getPiece(2,2));
        assertEquals(wPawn, testBoard.getPiece(2,4));

        Move testKnightMove = new Move(testBoard,7,8,6,6);
        testBoard.makeMove(testKnightMove);
        assertEquals(0, testBoard.getPiece(7,8));
        assertEquals(bKnight, testBoard.getPiece(6,6));
    }

    @Test
    public void makeCaptureTest() {
        testGame.setBoardFEN("2B2r2/8/8/3r4/2P5/7n/4p3/3N2Q1 w - - 0 1");

        Move testPawnCapture = new Move(testBoard,3,4,4,5);
        testBoard.makeMove(testPawnCapture);
        assertEquals(0, testBoard.getPiece(3,4));
        assertEquals(wPawn, testBoard.getPiece(4,5));

        Move testRookCapture = new Move(testBoard,6,8,3,8);
        testBoard.makeMove(testRookCapture);
        assertEquals(0, testBoard.getPiece(6,8));
        assertEquals(bRook, testBoard.getPiece(3,8));
    }

    @Test
    public void makeCastlingMoveTest() {
        testGame.setBoardFEN("r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 1");

        Move testKingSideCastle = new Move(testBoard,5,1,7,1);
        testKingSideCastle.setKingSideCastleMove();
        testBoard.makeMove(testKingSideCastle);
        assertEquals(0, testBoard.getPiece(5,1));
        assertEquals(wKing, testBoard.getPiece(7,1));
        assertEquals(0, testBoard.getPiece(8,1));
        assertEquals(wRook, testBoard.getPiece(6,1));

        Move testQueenSideCastle = new Move(testBoard,5,8,3,8);
        testQueenSideCastle.setQueenSideCastleMove();
        testBoard.makeMove(testQueenSideCastle);
        assertEquals(0, testBoard.getPiece(5,8));
        assertEquals(bKing, testBoard.getPiece(3,8));
        assertEquals(0, testBoard.getPiece(1,8));
        assertEquals(bRook, testBoard.getPiece(4,8));
    }

    @Test
    public void makePromotionMoveTest() {
        testGame.setBoardFEN("8/1P1P1P1P/8/8/8/8/1p1p1p1p/8 w - - 0 1");

        Move testQueenPromotionMove = new Move(testBoard,2,7,2,8);
        testQueenPromotionMove.setQueenPromotionMove();
        testBoard.makeMove(testQueenPromotionMove);
        assertEquals(0, testBoard.getPiece(2,7));
        assertEquals(wQueen, testBoard.getPiece(2,8));

        Move testKnightPromotionMove = new Move(testBoard,4,2,4,1);
        testKnightPromotionMove.setKnightPromotionMove();
        testBoard.makeMove(testKnightPromotionMove);
        assertEquals(0, testBoard.getPiece(4,2));
        assertEquals(bKnight, testBoard.getPiece(4,1));

        Move testRookPromotionCapture = new Move(testBoard,8,7,7,8);
        testRookPromotionCapture.setRookPromotionMove();
        testBoard.makeMove(testRookPromotionCapture);
        assertEquals(0, testBoard.getPiece(8,7));
        assertEquals(wRook, testBoard.getPiece(7,8));

        Move testBishopPromotionCapture = new Move(testBoard,2,2,1,1);
        testBishopPromotionCapture.setBishopPromotionMove();
        testBoard.makeMove(testBishopPromotionCapture);
        assertEquals(0, testBoard.getPiece(2,2));
        assertEquals(bBishop, testBoard.getPiece(1,1));
    }

    @Test
    public void makeEnPassantMoveTest() {
        testGame.setBoardFEN("7k/8/8/1Pp5/8/8/8/7K w - c6 0 2");

        Move testWhiteEnPassantMove = new Move(testBoard,2,5,3,6);
        testWhiteEnPassantMove.setEnPassantMove();
        testBoard.makeMove(testWhiteEnPassantMove);
        assertEquals(0, testBoard.getPiece(2,5));
        assertEquals(0, testBoard.getPiece(3,5));
        assertEquals(wPawn, testBoard.getPiece(3,6));

        testGame.setBoardFEN("7k/8/8/8/4pP2/8/8/7K b - f3 0 2");

        Move testBlackEnPassantMove = new Move(testBoard,5,4,4,3);
        testBlackEnPassantMove.setEnPassantMove();
        testBoard.makeMove(testBlackEnPassantMove);
        assertEquals(0, testBoard.getPiece(5,4));
        assertEquals(0, testBoard.getPiece(4,4));
        assertEquals(bPawn, testBoard.getPiece(4,3));
    }

    @Test
    public void getPieceTest() {
        try {
            assertEquals(wRook, testBoard.getPiece(0,0));
            assertEquals(bPawn, testBoard.getPiece(4, 7));
            assertEquals(wKing, testBoard.getPiece(1, 5));
        } catch (IndexOutOfBoundsException e) {
            fail("Unexpected IndexOutOfBoundsException");
        }

        try {
            int piece = testBoard.getPiece(9,3);
            fail("Expected an IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) {
            // pass
        }
    }

    @Test
    public void getSquareIndexTest() {
        try {
            assertEquals(0, Board.getSquareIndex(1,1));
            assertEquals(63, Board.getSquareIndex(8,8));
            assertEquals(19, Board.getSquareIndex(4,3));
            assertEquals(42, Board.getSquareIndex(3,6));
        } catch (IndexOutOfBoundsException e) {
            fail("Unexpected IndexOutOfBoundsException");
        }

        try {
            int i = Board.getSquareIndex(2,10);
            fail("Expected an IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) {
            // pass
        }
    }

    @Test
    public void getSquareCoordinateTest() {
        try {
            assertEquals(2, Board.getSquareCoordinates(57).x);
            assertEquals(8, Board.getSquareCoordinates(57).y);

            assertEquals(7, Board.getSquareCoordinates(46).x);
            assertEquals(6, Board.getSquareCoordinates(46).y);

            assertEquals(2, Board.getSquareCoordinates(25).x);
            assertEquals(4, Board.getSquareCoordinates(25).y);

            assertEquals(5, Board.getSquareCoordinates(12).x);
            assertEquals(2, Board.getSquareCoordinates(12).y);
        } catch (IndexOutOfBoundsException e) {
            fail("Unexpected IndexOutOfBoundsException");
        }

        try {
            Point p = Board.getSquareCoordinates(31);
            fail("Expected an IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) {
            // pass
        }
    }
}

package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PieceTest {

    @Test
    public void getPieceCharTest() {
        try {
            assertEquals("", Piece.getPieceChar(Piece.bPawn));
            assertEquals("N", Piece.getPieceChar(Piece.wKnight));
            assertEquals("B", Piece.getPieceChar(Piece.bBishop));
            assertEquals("R", Piece.getPieceChar(Piece.wRook));
            assertEquals("Q", Piece.getPieceChar(Piece.bQueen));
            assertEquals("K", Piece.getPieceChar(Piece.wKing));
        } catch (IndexOutOfBoundsException e) {
            fail("Unexpected IndexOutOfBoundsException");
        }

        try {
            String s = Piece.getPieceChar(9);
            fail("Expected an IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) {
            // pass
        }
    }

    @Test
    public void getPieceNameTest() {
        try {
            assertEquals("pawn", Piece.getPieceName(Piece.wPawn));
            assertEquals("knight", Piece.getPieceName(Piece.bKnight));
            assertEquals("bishop", Piece.getPieceName(Piece.wBishop));
            assertEquals("rook", Piece.getPieceName(Piece.bRook));
            assertEquals("queen", Piece.getPieceName(Piece.wQueen));
            assertEquals("king", Piece.getPieceName(Piece.bKing));
        } catch (IndexOutOfBoundsException e) {
            fail("Unexpected IndexOutOfBoundsException");
        }

        try {
            String s = Piece.getPieceName(13);
            fail("Expected an IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) {
            // pass
        }
    }
}

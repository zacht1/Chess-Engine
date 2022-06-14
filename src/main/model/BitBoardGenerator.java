package model;

import java.util.Arrays;

public class BitBoardGenerator {

    /**
     * Generate a list of bitboards from the given integer array of the current chess board
     *
     * @return list a bitboards in order of white then black, pawn through king
     */
    public static long[] arrayToBitBoards(int[] board) {
        long whitePawn = 0L, whiteKnight = 0L, whiteBishop = 0L, whiteRook = 0L, whiteQueen = 0L, whiteKing = 0L,
                blackPawn = 0L, blackKnight = 0L, blackBishop = 0L, blackRook = 0L, blackQueen = 0L, blackKing = 0L;
        String binaryString;

        for (int i = 0; i < 64; i++) {
            binaryString = "0000000000000000000000000000000000000000000000000000000000000000";
            binaryString = binaryString.substring(i + 1) + "1" + binaryString.substring(0, i);

            switch (board[i]) {
                case 1 : whitePawn += stringToBitBoard(binaryString);
                    break;
                case 2: whiteKnight += stringToBitBoard(binaryString);
                    break;
                case 3: whiteBishop += stringToBitBoard(binaryString);
                    break;
                case 4: whiteRook += stringToBitBoard(binaryString);
                    break;
                case 5: whiteQueen += stringToBitBoard(binaryString);
                    break;
                case 6: whiteKing += stringToBitBoard(binaryString);
                    break;
                case -1: blackPawn += stringToBitBoard(binaryString);
                    break;
                case -2: blackKnight += stringToBitBoard(binaryString);
                    break;
                case -3: blackBishop += stringToBitBoard(binaryString);
                    break;
                case -4: blackRook += stringToBitBoard(binaryString);
                    break;
                case -5: blackQueen += stringToBitBoard(binaryString);
                    break;
                case -6: blackKing += stringToBitBoard(binaryString);
                    break;
            }
        }

        long[] bitboards = new long[]{
                whitePawn, whiteKnight, whiteBishop, whiteRook, whiteQueen, whiteKing,
                blackPawn, blackKnight, blackBishop, blackRook, blackQueen, blackKing
        };

        System.out.println(Arrays.toString(bitboards));

        return bitboards;
    }

    private static long stringToBitBoard(String binaryString) {
        if (binaryString.charAt(0) == '0') {
            // not a negative number
            return Long.parseLong(binaryString, 2);
        } else {
            return Long.parseLong("1" + binaryString.substring(2), 2) * 2;
        }
    }
}

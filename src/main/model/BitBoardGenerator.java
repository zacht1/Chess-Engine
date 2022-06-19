package model;

public class BitBoardGenerator {

    /**
     * Generate a list of bitboards from the given integer array of the current chess board
     *
     * @param board integer array of the chess board
     * @return array of bitboards in order of white then black: pawn, knight, bishop, rook, queen king
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

    /**
     * Return an integer array of the chess board in the position given by the given bitboards
     *
     * @param bitboards array of bitboards in order of white then black: pawn, knight, bishop, rook, queen, king
     * @return integer array of the chess board
     */
    public static int[] bitboardsToArray(long[] bitboards) {
        int[] board = new int[64];
        // bitboards indices: 0=whitePawns, 1=whiteKnights, 2=whiteBishops, 3=whiteRooks, 4=whiteQueen, 5=whiteKing,
        //                    6=blackPawns, 7=blackKnights, 8=blackBishops, 9=blackRooks, 10=blackQueen, 11=blackKing

        for (int n = 0; n < 12; n++) {
            String binary = Long.toBinaryString(bitboards[n]);
            int bitsToBeAddedToFront = 64 - binary.length();
            String zeros = new String(new char[bitsToBeAddedToFront]).replace("\0", "0");
            StringBuilder binaryStringBuilder = new StringBuilder();
            binaryStringBuilder.append(zeros).append(binary);
            binaryStringBuilder.reverse();

            String binary64Bit = binaryStringBuilder.toString();

            int piece = 0;
            switch (n) {
                case 0: piece = 1;
                    break;
                case 1: piece = 2;
                    break;
                case 2: piece = 3;
                    break;
                case 3: piece = 4;
                    break;
                case 4: piece = 5;
                    break;
                case 5: piece = 6;
                    break;
                case 6: piece = -1;
                    break;
                case 7: piece = -2;
                    break;
                case 8: piece = -3;
                    break;
                case 9: piece = -4;
                    break;
                case 10: piece = -5;
                    break;
                case 11: piece = -6;
                    break;
            }

            for (int i = 0; i < 64; i++) {
                if (binary64Bit.toCharArray()[i] == '1') {
                    board[i] = piece;
                }
            }
        }

        return board;
    }
}

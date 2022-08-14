package model;

import java.util.List;
import java.util.Random;

import static java.lang.Math.abs;

public class Zobrist {
    public static final int WHITE_KING_SIDE = 0;
    public static final int WHITE_QUEEN_SIDE = 1;
    public static final int BLACK_KING_SIDE = 2;
    public static final int BLACK_QUEEN_SIDE = 3;

    private final long[][] pieceTable;
    private final long[] enPassantFiles;
    private final long[] castlingRights;
    private long whiteToPlay;

    /**
     * Initializes the Zobrist class
     */
    public Zobrist() {
        this.pieceTable = new long[64][12];
        this.enPassantFiles = new long[8];
        this.castlingRights = new long[4];

        initTables();
    }

    /**
     * Initializes the Zobrist hash table
     */
    private void initTables() {
        for (int x = 0; x < 64; x++) {
            for (int y = 0; y < 12; y++) {
                this.pieceTable[x][y] = randomLongGenerator();
            }
        }

        for (int i = 0; i < 8; i++) {
            this.enPassantFiles[i] = randomLongGenerator();
        }

        for (int i = 0; i < 4; i++) {
            this.castlingRights[i] = randomLongGenerator();
        }

        this.whiteToPlay = randomLongGenerator();
    }

    /**
     * Generates a random number from 0 to 2^64-1
     */
    public long randomLongGenerator() {
        Random random = new Random();
        return abs(random.nextLong());
    }

    /**
     * Compute the hash value of the given board
     */
    public long calculateHash(Game game) {
        Board board = game.getBoard();
        long hash = 0L;

        // pieces
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                int piece = board.getPiece(x + 1, y + 1);

                if (piece != 0) {
                    int pieceNum = blackPieceNumber(piece);
                    hash ^= pieceTable[Board.getSquareIndex(x + 1,y + 1)][pieceNum - 1];
                }
            }
        }

        // en passant file
        List<Move> moveList = board.getMoveList();
        if (moveList.size() >= 2) {
            Move currentMove = board.getMoveList().get(board.getMoveList().size() - 2);
            Move lastPlayedMove = board.getMoveList().get(board.getMoveList().size() - 1);

            if (lastPlayedMove.getMovedPiece() == Piece.wPawn && lastPlayedMove.getStartY() == 2 && lastPlayedMove.getEndY() == 4 &&
                    currentMove.getMovedPiece() == Piece.bPawn && currentMove.getEndY() == 4) {

                if (currentMove.getEndX() == lastPlayedMove.getEndX() + 1) {
                    hash ^= enPassantFiles[currentMove.getEndX() - 2];
                } else if (currentMove.getEndX() == lastPlayedMove.getEndX() - 1) {
                    hash ^= enPassantFiles[currentMove.getEndX()];
                }

            } else if (lastPlayedMove.getMovedPiece() == Piece.bPawn && lastPlayedMove.getStartY() == 7 && lastPlayedMove.getEndY() == 5 &&
                    currentMove.getMovedPiece() == Piece.wPawn && currentMove.getEndY() == 5) {

                if (currentMove.getEndX() == lastPlayedMove.getEndX() + 1) {
                    hash ^= enPassantFiles[currentMove.getEndX() - 2];
                } else if (currentMove.getEndX() == lastPlayedMove.getEndX() - 1) {
                    hash ^= enPassantFiles[currentMove.getEndX()];
                }

            }
        }


        // castling rights
        if (!game.canWhiteKingSideCastle()) {
            hash ^= castlingRights[WHITE_KING_SIDE];
        }

        if (!game.canWhiteQueenSideCastle()) {
            hash ^= castlingRights[WHITE_QUEEN_SIDE];
        }

        if (!game.canBlackKingSideCastle()) {
            hash ^= castlingRights[BLACK_KING_SIDE];
        }

        if (!game.canBlackQueenSideCastle()) {
            hash ^= castlingRights[BLACK_QUEEN_SIDE];
        }

        // white to play
        if (game.getCurrentTurn().isWhite()) {
            hash ^= whiteToPlay;
        }

        return hash;
    }

    private int blackPieceNumber(int piece) {
        if (piece < 0) {
            switch (piece) {
                case -1:
                    return 7;
                case -2:
                    return 8;
                case -3:
                    return 9;
                case -4:
                    return 10;
                case -5:
                    return 11;
                case -6:
                    return 12;
                default:
                    throw new IndexOutOfBoundsException();
            }
        } else {
            return piece;
        }
    }

    /**
     * Getters & Setters
     */
    public long[][] getPieceTable() {
        return pieceTable;
    }

    public long[] getEnPassantFiles() {
        return enPassantFiles;
    }

    public long[] getCastlingRights() {
        return castlingRights;
    }

    public long getWhiteToPlay() {
        return whiteToPlay;
    }
}

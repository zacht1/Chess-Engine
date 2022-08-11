package model;

public class Zobrist {
    private long[][] pieceTable;
    private long[] enPassantFiles;
    private long[] castlingRights;
    private long whiteToPlay;

    /**
     * Initializes the Zobrist class
     */
    public Zobrist() {
        // stub
    }

    /**
     * Initializes the Zobrist hash table
     */
    private void initTable() {
        // stub
    }

    /**
     * Generates a random number from 0 to 2^64-1
     */
    public long randomLongGenerator() {
        return 0L; // stub
    }

    /**
     * Compute the hash value of the given board
     */
    public long calculateHash(Game game) {
        return 0L; // stub
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

package model;

import enumerations.CheckStatus;
import enumerations.GameStatus;

import java.util.List;

public class Game {
    private Board board;
    private GameStatus gameStatus;
    private CheckStatus checkStatus;
    private Player[] players;
    private Player currentTurn;
    private List<Move> moveList;

    // is castling still legal
    private boolean whiteQueenSideCastling = true;
    private boolean whiteKingSideCastling = true;
    private boolean blackQueenSideCastling = true;
    private boolean blackKingSideCastling = true;

    /**
     * Constructs a new game with a new board, a gameStatus of ACTIVE, a checkStatus of NONE, a players list of one
     * black player and one white player, a current turn of white + human player, and an empty moveList
     */
    public Game() {
        // stub
    }

    /**
     * If move is legal return true, update the board, change the currentTurn, add the move to moveList,
     * and update the gameStatus + checkStatus if necessary.  If move is false only return false
     *
     * @return true if move is legal, false otherwise
     */
    public boolean playMove(Move move) {
        return true; // stub
    }

    /**
     * Change the currentTurn to the opposite of what it currently is
     */
    public void nextTurn() {
        // stub
    }

    /**
     * Set this board to the position in the given Forsyth–Edwards Notation (FEN) string
     */
    public void setBoardFEN(String fen) {
        // stub
    }

    /**
     * Getters & Setters
     */
    public Board getBoard() {
        return board;
    }

    public Player[] getPlayers() {
        return players;
    }

    public Player getCurrentTurn() {
        return currentTurn;
    }

    public List<Move> getMoveList() {
        return moveList;
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
    }

    public CheckStatus getCheckStatus() {
        return checkStatus;
    }

    public void setCheckStatus(CheckStatus checkStatus) {
        this.checkStatus = checkStatus;
    }

    public boolean isWhiteQueenSideCastling() {
        return whiteQueenSideCastling;
    }

    public void setWhiteQueenSideCastling(boolean whiteQueenSideCastling) {
        this.whiteQueenSideCastling = whiteQueenSideCastling;
    }

    public boolean isWhiteKingSideCastling() {
        return whiteKingSideCastling;
    }

    public void setWhiteKingSideCastling(boolean whiteKingSideCastling) {
        this.whiteKingSideCastling = whiteKingSideCastling;
    }

    public boolean isBlackQueenSideCastling() {
        return blackQueenSideCastling;
    }

    public void setBlackQueenSideCastling(boolean blackQueenSideCastling) {
        this.blackQueenSideCastling = blackQueenSideCastling;
    }

    public boolean isBlackKingSideCastling() {
        return blackKingSideCastling;
    }

    public void setBlackKingSideCastling(boolean blackKingSideCastling) {
        this.blackKingSideCastling = blackKingSideCastling;
    }
}
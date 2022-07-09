package model;

import enumerations.CheckStatus;
import enumerations.GameStatus;

import java.util.ArrayList;
import java.util.List;

public class Game {
    private Board board;
    private GameStatus gameStatus;
    private CheckStatus checkStatus;
    private Player[] players;
    private Player currentTurn;
    private List<Move> moveList;

    // is castling still legal
    private boolean whiteCastling = true;
    private boolean whiteQueenSideCastling = true;
    private boolean whiteKingSideCastling = true;
    private boolean blackCastling = true;
    private boolean blackQueenSideCastling = true;
    private boolean blackKingSideCastling = true;

    /**
     * Constructs a new game with a new board, a gameStatus of ACTIVE, a checkStatus of NONE, a players list of one
     * black player and one white player, a current turn of white + human player, and an empty moveList
     */
    public Game() {
        this.board = new Board();
        this.gameStatus = GameStatus.ACTIVE;
        this.checkStatus = CheckStatus.NONE;
        this.players = new Player[2];
        players[0] = new Player(true);
        players[1] = new Player(false);
        this.currentTurn = players[0];
        this.moveList = new ArrayList<>();
    }

    /**
     * If move is legal return true, update the board, change the currentTurn, add the move to moveList,
     * and update the gameStatus + checkStatus if necessary.  If move is false only return false
     *
     * @return true if move is legal, false otherwise
     */
    public boolean playMove(Move move) {
        if (!(move.isWhiteMove() == currentTurn.isWhite())) {
            return false;
        }

        if (!isLegal(move)) {
            return false;
        }

        this.board.makeMove(move);

        if (move.isWhiteRookMove()) {
            whiteCastlingRights(move);
        }

        if (move.isBlackRookMove()) {
            blackCastlingRights(move);
        }

        this.moveList.add(move);

        nextTurn();

        // TODO: checks & checkmates
        return true;
    }

    private boolean isLegal(Move move) {
        return true; // stub
    }

    private void whiteCastlingRights(Move move) {
        if (move.getStartX() == 1 && move.getStartY() == 1) {
            whiteQueenSideCastling = false;
        } else if (move.getStartX() == 8 && move.getStartY() == 1) {
            whiteKingSideCastling = false;
        }
    }

    private void blackCastlingRights(Move move) {
        if (move.getStartX() == 1 && move.getStartY() == 8) {
            blackQueenSideCastling = false;
        } else if (move.getStartX() == 8 && move.getStartY() == 8) {
            blackKingSideCastling = false;
        }
    }

    /**
     * Change the currentTurn to the opposite of what it currently is
     */
    public void nextTurn() {
        if (currentTurn == players[0]) {
            currentTurn = players[1];
        } else {
            currentTurn = players[0];
        }
    }

    /**
     * Set this board to the position in the given Forsyth–Edwards Notation (FEN) string
     */
    public void setBoardFEN(String fen) {
        FenUtility fenUtility = new FenUtility();
        fenUtility.loadGameFromFEN(this, fen);
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

    public boolean canWhiteCastle() {
        return whiteCastling;
    }

    public boolean canWhiteQueenSideCastle() {
        return whiteQueenSideCastling;
    }

    public void setWhiteQueenSideCastling(boolean whiteQueenSideCastling) {
        this.whiteQueenSideCastling = whiteQueenSideCastling;
    }

    public boolean canWhiteKingSideCastle() {
        return whiteKingSideCastling;
    }

    public void setWhiteKingSideCastling(boolean whiteKingSideCastling) {
        this.whiteKingSideCastling = whiteKingSideCastling;
    }

    public boolean canBlackCastle() {
        return blackCastling;
    }

    public boolean canBlackQueenSideCastle() {
        return blackQueenSideCastling;
    }

    public void setBlackQueenSideCastling(boolean blackQueenSideCastling) {
        this.blackQueenSideCastling = blackQueenSideCastling;
    }

    public boolean canBlackKingSideCastle() {
        return blackKingSideCastling;
    }

    public void setBlackKingSideCastling(boolean blackKingSideCastling) {
        this.blackKingSideCastling = blackKingSideCastling;
    }
}

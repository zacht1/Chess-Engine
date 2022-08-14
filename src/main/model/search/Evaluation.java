package model.search;

import model.Game;

public class Evaluation {

    /**
     * Evaluates the current position from the perspective of the player whose turn it is
     * Large positive score means position is very favourable for current player, large negative score means position is
     * very unfavourable for current player
     */
    public int evaluatePosition(Game game) {
        if (game.getCurrentTurn().isWhite()) {
            // white current turn
            return countWhiteMaterial();
        } else {
            // black current turn
            return countBlackMaterial();
        }
    }

    private int countWhiteMaterial() {
        return 0; // stub
    }

    private int countBlackMaterial() {
        return 0; // stub
    }
}

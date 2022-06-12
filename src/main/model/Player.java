package model;

public class Player {
    private boolean isWhite;
    private boolean isHuman;

    /**
     * Construct a new player with the given team colour and human by default
     *
     * @param isWhite whether the player is white or black
     */
    public Player(boolean isWhite) {
        this.isWhite = isWhite;
        this.isHuman = true;
    }

    /**
     * Getters & Setters
     */
    public boolean isWhite() {
        return isWhite;
    }

    public void setWhite(boolean white) {
        isWhite = white;
    }

    public boolean isHuman() {
        return isHuman;
    }

    public void setHuman(boolean human) {
        isHuman = human;
    }
}

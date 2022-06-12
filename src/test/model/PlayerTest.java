package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PlayerTest {

    @Test
    public void constructorTest() {
        Player whitePlayer = new Player(true);
        assertTrue(whitePlayer.isWhite());
        assertTrue(whitePlayer.isHuman());

        Player blackPlayer = new Player(false);
        assertFalse(blackPlayer.isWhite());
        assertTrue(whitePlayer.isHuman());
    }
}

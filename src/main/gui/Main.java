package gui;

import com.formdev.flatlaf.intellijthemes.FlatSolarizedLightIJTheme;
import model.Game;

public class Main {

    public static void main(String[] args) {
        FlatSolarizedLightIJTheme.setup();

        Game game = new Game();
        game.setBoardFEN("k7/8/7R/8/pppppR2/8/8/2K5 w - - 0 1");

        new GameWindow(game);
    }
}

package gui;

import com.formdev.flatlaf.intellijthemes.FlatSolarizedLightIJTheme;
import model.Game;

public class Main {

    public static void main(String[] args) {
        FlatSolarizedLightIJTheme.setup();

        Game game = new Game();
        //game.setBoardFEN("7k/R7/1K4R1/8/8/8/p2pp2p/r7 w - - 0 1");

        new GameWindow(game);
    }
}

package gui;

import com.formdev.flatlaf.intellijthemes.FlatSolarizedLightIJTheme;
import model.Game;

public class Main {

    public static void main(String[] args) {
        FlatSolarizedLightIJTheme.setup();

        Game game = new Game();
        //game.setBoardFEN("8/1PPPB1P1/8/6k1/1K6/7r/1p2ppp1/8 b - - 0 1");

        new GameWindow(game);
    }
}

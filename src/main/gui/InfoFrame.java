package gui;

import model.Game;

import javax.swing.*;
import java.awt.*;

public class InfoFrame extends JPanel {

    public InfoFrame(Game game) {
        this.setPreferredSize(new Dimension(200, 500));
        this.setBackground(GameWindow.LIGHT_SQUARE);
        this.setLayout(new BorderLayout());

        JPanel leftGap = new JPanel();
        leftGap.setPreferredSize(new Dimension(5, 500));
        leftGap.setBackground(GameWindow.DARK_SQUARE);
        this.add(leftGap, BorderLayout.EAST);
    }
}

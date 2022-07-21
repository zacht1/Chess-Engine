package gui;

import model.Game;

import javax.swing.*;
import java.awt.*;

public class GameWindow extends JFrame {
    public static final Color DARK_SQUARE = new Color(180, 136, 98);
    public static final Color LIGHT_SQUARE = new Color(240, 216, 181);

    private final Game game;

    /**
     * Initialize the game window: top, bottom, and side gaps as well as the board panel
     */
    public GameWindow(Game game) {
        this.game = game;

        this.setSize(650, 678);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setLayout(new BorderLayout());
        this.setResizable(false);

        initPanels();

        this.setVisible(true);
    }

    private void initPanels() {
        JPanel topGap = new JPanel();
        topGap.setPreferredSize(new Dimension(810, 5));
        topGap.setBackground(DARK_SQUARE);

        JPanel leftGap = new JPanel();
        leftGap.setPreferredSize(new Dimension(5, 500));
        leftGap.setBackground(DARK_SQUARE);

        JPanel rightGap = new JPanel();
        rightGap.setPreferredSize(new Dimension(5, 500));
        rightGap.setBackground(DARK_SQUARE);

        JPanel bottomGap = new JPanel();
        bottomGap.setPreferredSize(new Dimension(810, 5));
        bottomGap.setBackground(DARK_SQUARE);

        JPanel infoPanel = new InfoFrame(this.game);

        JPanel boardPanel = new BoardFrame(this.game, this);

        this.add(topGap, BorderLayout.NORTH);
        this.add(leftGap, BorderLayout.WEST);
        this.add(bottomGap, BorderLayout.SOUTH);
        this.add(boardPanel, BorderLayout.CENTER);
        this.add(rightGap, BorderLayout.EAST);
    }
}

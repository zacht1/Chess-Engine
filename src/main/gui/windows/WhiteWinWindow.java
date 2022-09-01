package gui.windows;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import enumerations.GameStatus;
import gui.GameWindow;
import model.Game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WhiteWinWindow extends JFrame implements ActionListener {

    private final Game game;
    private JLabel winLabel;
    private JLabel methodLabel;
    private JButton closeButton;
    private JButton newGameButton;

    public WhiteWinWindow(Game game) {
        this.game = game;
        setupLabels();
        setupButtons();
        setupFrame();
    }

    private void setupFrame() {
        this.setSize(230,130);
        this.setUndecorated(true);
        this.setLayout(null);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setResizable(false);

        this.add(winLabel);
        this.add(methodLabel);
        this.add(closeButton);
        this.add(newGameButton);

        this.setVisible(true);
    }

    private void setupButtons() {
        closeButton = new JButton();
        closeButton.setIcon(new FlatSVGIcon("images/close.svg"));
        closeButton.setSelectedIcon(new FlatSVGIcon("images/close_selected.svg"));
        closeButton.setBounds(211,2,17, 17);
        closeButton.addActionListener(this);
        closeButton.setFocusable(false);
        closeButton.setBorderPainted(false);
        closeButton.setContentAreaFilled(false);

        newGameButton = new JButton("New Game");
        newGameButton.setFont(new Font("Helvetica Neue", Font.PLAIN, 13));
        newGameButton.setBounds(15,80, 200,30);
        newGameButton.setBackground(new Color(135, 150, 107));
        newGameButton.setForeground(new Color(42, 51, 33));
        newGameButton.addActionListener(this);
        newGameButton.setFocusable(false);
        newGameButton.setBorderPainted(false);
    }

    private void setupLabels() {
        this.winLabel = new JLabel("White Won");
        this.winLabel.setForeground(new Color(0, 0, 0));
        this.winLabel.setFont(new Font("Helvetica Neue", Font.BOLD, 20));
        this.winLabel.setBounds(63,20,103,25);

        this.methodLabel = new JLabel();
        this.methodLabel.setForeground(new Color(84, 79, 77));
        this.methodLabel.setFont(new Font("Helvetica Neue", Font.BOLD, 13));

        if (game.getGameStatus() == GameStatus.WHITE_CHECKMATE) {
            this.methodLabel.setBounds(71, 48, 86, 13);
            this.methodLabel.setText("by checkmate");
        } else if (game.getGameStatus() == GameStatus.BLACK_TIMEOUT) {
            this.methodLabel.setBounds(81,48,67,13);
            this.methodLabel.setText("by timeout");
        } else if (game.getGameStatus() == GameStatus.BLACK_RESIGNATION) {
            this.methodLabel.setBounds(71, 48, 88, 13);
            this.methodLabel.setText("by resignation");
        }
    }

    /**
     * Invoked when an action occurs.
     *
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == closeButton) {
            this.dispose();
        } else if (e.getSource() == newGameButton) {
            //System.exit(0);
            Game newGame = new Game();
            new GameWindow(newGame);
        }
    }
}

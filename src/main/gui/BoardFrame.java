package gui;

import model.Game;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class BoardFrame extends JPanel {
    private static final Color DARK_SELECTED_SQUARE_COLOUR = new Color(107,111,64);
    private static final Color LIGHT_SELECTED_SQUARE_COLOUR = new Color(135, 150, 107);

    private static final Color DARK_SQUARE_PAST_MOVE_COLOUR = new Color(171,161,68);
    private static final Color LIGHT_SQUARE_PAST_MOVE_COLOUR = new Color(204,209,118);

    private static final Color DARK_SQUARE_COLOUR = new Color(180, 136, 98);
    private static final Color LIGHT_SQUARE_COLOUR = new Color(240, 216, 181);

    public static final int SQUARE_DIMENSION = 80;

    private JPanel boardPanel;
    private JLayeredPane boardLayeredPane;
    private List<JPanel> boardSquaresList;
    private Map<JPanel, Integer> pieceMap;
    private Map<Integer, JPanel> kingPanelMap;

    private Game game;
    private JFrame gameFrame;

    public BoardFrame(Game game, JFrame gameFrame) {
        super(new BorderLayout());

        this.game = game;
        this.gameFrame = gameFrame;
        this.setBackground(DARK_SQUARE_COLOUR);

        initializeBoardLayeredPane();
        initializeSquares();
        initializePieces();
    }

    private void initializeBoardLayeredPane() {
        // stub
    }

    private void initializeSquares() {
        // stub
    }

    private void initializePieces() {
        // stub
    }
}

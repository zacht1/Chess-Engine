package gui.windows;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import model.Move;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WhitePromotionWindow implements ActionListener {
    private final Move move;

    private JDialog dialogBox;

    private JButton queenButton;
    private JButton knightButton;
    private JButton rookButton;
    private  JButton bishopButton;
    private JButton exitButton;

    private boolean cancelled = false;

    public WhitePromotionWindow(Move move, JFrame frame, int x, int y) {
        this.move = move;
        setUpDialogBox(frame, x,y);
    }

    private void setUpDialogBox(JFrame frame, int x, int y) {
        dialogBox = new JDialog(frame, true);
        dialogBox.setLayout(null);
        dialogBox.setUndecorated(true);
        dialogBox.setSize(80,360);
        dialogBox.setResizable(false);
        dialogBox.setLocation(new Point(x,y));
        setUpButtons();
        dialogBox.add(queenButton);
        dialogBox.add(knightButton);
        dialogBox.add(rookButton);
        dialogBox.add(bishopButton);
        dialogBox.add(exitButton);
        dialogBox.setVisible(true);
    }

    private void setUpButtons() {
        queenButton = new JButton();
        queenButton.putClientProperty("JButton.buttonType", "borderless");
        queenButton.setBorderPainted(false);
        queenButton.setIcon(new FlatSVGIcon("images/white_queen.svg"));
        queenButton.setBounds(0,0,80,80);
        queenButton.addActionListener(this);

        knightButton = new JButton();
        knightButton.putClientProperty("JButton.buttonType", "borderless");
        knightButton.setBounds(0,80,80,80);
        knightButton.setBorderPainted(false);
        knightButton.setIcon(new FlatSVGIcon("images/white_knight.svg"));
        knightButton.addActionListener(this);

        rookButton = new JButton();
        rookButton.putClientProperty("JButton.buttonType", "borderless");
        rookButton.setBounds(0,160,80,80);
        rookButton.setBorderPainted(false);
        rookButton.setIcon(new FlatSVGIcon("images/white_rook.svg"));
        rookButton.addActionListener(this);

        bishopButton = new JButton();
        bishopButton.putClientProperty("JButton.buttonType", "borderless");
        bishopButton.setBounds(0,240,80,80);
        bishopButton.setBorderPainted(false);
        bishopButton.setIcon(new FlatSVGIcon("images/white_bishop.svg"));
        bishopButton.addActionListener(this);

        exitButton = new JButton();
        exitButton.putClientProperty("JButton.buttonType", "borderless");
        exitButton.setBounds(0,320,80,40);
        exitButton.setBorderPainted(false);
        exitButton.setIcon(new FlatSVGIcon("images/close.svg"));
        exitButton.setBackground(new Color(229, 224, 206));
        exitButton.setFocusable(false);
        exitButton.addActionListener(this);
    }

    /**
     * Invoked when an action occurs.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == exitButton) {
            cancelled = true;
            dialogBox.dispose();
        }
        if (e.getSource() == queenButton) {
            move.setQueenPromotionMove();
            dialogBox.dispose();
        }
        if (e.getSource() == knightButton) {
            move.setKnightPromotionMove();
            dialogBox.dispose();
        }
        if (e.getSource() == rookButton) {
            move.setRookPromotionMove();
            dialogBox.dispose();
        }
        if (e.getSource() == bishopButton) {
            move.setBishopPromotionMove();
            dialogBox.dispose();
        }
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}

package gui;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import enumerations.CheckStatus;
import enumerations.GameStatus;
import gui.windows.BlackPromotionWindow;
import gui.windows.BlackWinWindow;
import gui.windows.WhitePromotionWindow;
import gui.windows.WhiteWinWindow;
import model.Board;
import model.Game;
import model.Move;
import model.Piece;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Math.abs;

public class BoardFrame extends JPanel {
    private static final Color DARK_SELECTED_SQUARE_COLOUR = new Color(107,111,64);
    private static final Color LIGHT_SELECTED_SQUARE_COLOUR = new Color(135, 150, 107);

    private static final Color DARK_SQUARE_PAST_MOVE_COLOUR = new Color(171,161,68);
    private static final Color LIGHT_SQUARE_PAST_MOVE_COLOUR = new Color(204,209,118);

    private static final Color DARK_SQUARE_COLOUR = GameWindow.DARK_SQUARE;
    private static final Color LIGHT_SQUARE_COLOUR = GameWindow.LIGHT_SQUARE;

    public static final int SQUARE_DIMENSION = 80;

    private JPanel boardPanel;
    private JLayeredPane boardLayeredPane;
    private List<JPanel> boardSquaresList;
    private Map<JPanel, Integer> pieceMap;
    private Map<Integer, JPanel> kingPanelMap;

    private final Game game;
    private final JFrame gameFrame;

    /**
     * Initialize the BoardFrame of the given game and with the given game frame
     */
    public BoardFrame(Game game, JFrame gameFrame) {
        super(new BorderLayout());

        this.game = game;
        this.gameFrame = gameFrame;
        this.setBackground(DARK_SQUARE_COLOUR);

        initBoardLayeredPane();

        if (!game.isFlippedBoard()) {
            initSquares();
            initPieces();
        } else {
            initFlippedSquares();
            initFlippedPieces();
        }
    }

    /**
     * Initialize the boardLayeredFrame
     */
    private void initBoardLayeredPane() {
        boardPanel = new JPanel(new GridLayout(8,8));
        boardPanel.setBounds(0,0,640,640);

        boardLayeredPane = new JLayeredPane();
        boardLayeredPane.setPreferredSize(new Dimension(640,640));
        boardLayeredPane.add(boardPanel, JLayeredPane.DEFAULT_LAYER);

        PieceMover pieceMover = new PieceMover(this);

        boardLayeredPane.addMouseListener(pieceMover);
        boardLayeredPane.addMouseMotionListener(pieceMover);

        boardLayeredPane.setVisible(true);
        this.add(boardLayeredPane, BorderLayout.CENTER);
    }

    /**
     * Initialize all square panels on the chess board
     */
    private void initSquares() {
        boardSquaresList = new ArrayList<>();
        pieceMap = new HashMap<>();
        kingPanelMap = new HashMap<>();
        List<JPanel> temporaryBoardSquaresList = new ArrayList<>();

        for (int i = 63; i >= 0; i--) {
            JPanel panel = new JPanel();
            temporaryBoardSquaresList.add(panel);

            if (i >= 56) {
                initializeEvenSquares(panel, i);
            } else if (i >= 48) {
                initializeOddSquares(panel, i);
            } else if (i >= 40) {
                initializeEvenSquares(panel, i);
            } else if (i >= 32) {
                initializeOddSquares(panel, i);
            } else if (i >= 24) {
                initializeEvenSquares(panel, i);
            } else if (i >= 16) {
                initializeOddSquares(panel, i);
            } else if (i >= 8) {
                initializeEvenSquares(panel, i);
            } else {
                initializeOddSquares(panel, i);
            }

            boardPanel.add(panel);
        }

        initBoardSquaresList(temporaryBoardSquaresList);
    }

    private void initBoardSquaresList(List<JPanel> temporaryBoardSquaresList) {
        for (int i = 56; i < 64; i++) {
            boardSquaresList.add(temporaryBoardSquaresList.get(i));
        }

        for (int i = 48; i < 56; i++) {
            boardSquaresList.add(temporaryBoardSquaresList.get(i));

        }

        for (int i = 40; i < 48; i++) {
            boardSquaresList.add(temporaryBoardSquaresList.get(i));
        }

        for (int i = 32; i < 40; i++) {
            boardSquaresList.add(temporaryBoardSquaresList.get(i));
        }

        for (int i = 24; i < 32; i++) {
            boardSquaresList.add(temporaryBoardSquaresList.get(i));

        }

        for (int i = 16; i < 24; i++) {
            boardSquaresList.add(temporaryBoardSquaresList.get(i));
        }

        for (int i = 8; i < 16; i++) {
            boardSquaresList.add(temporaryBoardSquaresList.get(i));

        }

        for (int i = 0; i < 8; i++) {
            boardSquaresList.add(temporaryBoardSquaresList.get(i));
        }
    }

    private void initializeEvenSquares(JPanel panel, int num) {
        if (num % 2 == 0) {
            panel.setBackground(DARK_SQUARE_COLOUR);
        } else {
            panel.setBackground(LIGHT_SQUARE_COLOUR);
        }
        panel.setLayout(new GridBagLayout());
    }

    private void initializeOddSquares(JPanel panel, int num) {
        if (num % 2 == 0) {
            panel.setBackground(LIGHT_SQUARE_COLOUR);
        } else {
            panel.setBackground(DARK_SQUARE_COLOUR);
        }
        panel.setLayout(new GridBagLayout());
    }

    /**
     * Initialize all pieces on the chess board
     */
    private void initPieces() {
        int[] chessBoard = game.getBoard().getBoard();

        for (int index = 0; index < 64; index++) {
            JPanel panel = boardSquaresList.get(index);
            int piece = chessBoard[index];
            panel.setLayout(new GridBagLayout());
            pieceMap.put(panel, piece);

            if (piece == Piece.wKing) {
                kingPanelMap.put(piece, panel);
            } else if (piece == Piece.bKing) {
                kingPanelMap.put(piece, panel);
            }

            if (piece != 0) {
                initPiece(panel, piece);
            }
        }

        if (game.getCheckStatus() == CheckStatus.BLACK_IN_CHECK) {
            highlightBlackCheck();
        } else if (game.getCheckStatus() == CheckStatus.WHITE_IN_CHECK) {
            highlightWhiteCheck();
        }
    }

    private void initPiece(JPanel panel, int piece) {
        JLabel label = new JLabel();

        switch (piece) {
            case -6: label.setIcon(new FlatSVGIcon("images/black_king.svg"));
                break;
            case -5: label.setIcon(new FlatSVGIcon("images/black_queen.svg"));
                break;
            case -4: label.setIcon(new FlatSVGIcon("images/black_rook.svg"));
                break;
            case -3: label.setIcon(new FlatSVGIcon("images/black_bishop.svg"));
                break;
            case -2: label.setIcon(new FlatSVGIcon("images/black_knight.svg"));
                break;
            case -1: label.setIcon(new FlatSVGIcon("images/black_pawn.svg"));
                break;
            case 1: label.setIcon(new FlatSVGIcon("images/white_pawn.svg"));
                break;
            case 2: label.setIcon(new FlatSVGIcon("images/white_knight.svg"));
                break;
            case 3: label.setIcon(new FlatSVGIcon("images/white_bishop.svg"));
                break;
            case 4: label.setIcon(new FlatSVGIcon("images/white_rook.svg"));
                break;
            case 5: label.setIcon(new FlatSVGIcon("images/white_queen.svg"));
                break;
            case 6: label.setIcon(new FlatSVGIcon("images/white_king.svg"));
                break;
        }

        panel.add(label);
    }

    /**
     * Initialize all square panels on the flipped chess board
     */
    private void initFlippedSquares() {
        // TODO: implement
    }

    /**
     * Initialize all pieces on the flipped chess board
     */
    private void initFlippedPieces() {
        // TODO: implement
    }

    /**
     * If the move with the given originPoint and destinationPoint is legal on the current board make the move
     *
     * @param originPoint point on the board where starting square of the move is
     * @param destinationPoint point on the board where destination square of the move is
     * @return true if the move is legal, false otherwise
     */
    public boolean requestMove(Point originPoint, Point destinationPoint) {
        JPanel originPanel = getPanel(originPoint);
        JPanel destinationPanel = getPanel(destinationPoint);

        int startPanelIndex = boardSquaresList.indexOf(originPanel);
        int endPanelIndex = boardSquaresList.indexOf(destinationPanel);

        int x1 = Board.getSquareCoordinates(startPanelIndex).x;
        int y1 = Board.getSquareCoordinates(startPanelIndex).y;

        int x2 = Board.getSquareCoordinates(endPanelIndex).x;
        int y2 = Board.getSquareCoordinates(endPanelIndex).y;

        Move move = new Move(game, x1, y1, x2, y2);

        if (move.isPromotionMove() && Piece.isLegalPawnMove(move)) {
            return requestPromotionMove(move, originPanel, destinationPanel);
        }

        if (game.playMove(move)) {
            executeMove(move, originPanel, destinationPanel);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Perform the given move on the board whose ending square is given by pressedPanel
     *
     * @param originPanel starting panel of the given move
     * @param endPanel destination panel of the given move
     */
    private void executeMove(Move move, JPanel originPanel, JPanel endPanel) {
        if (move.isQueenSideCastleMove()) {
            makeQueenSideCastleMove(move, originPanel, endPanel, move.getStartY());
        } else if (move.isKingSideCastleMove()) {
            makeKingSideCastleMove(move, originPanel, endPanel, move.getStartY());
        } else if (move.isEnPassantMove()) {
            makeEnPassantMove(move, originPanel, endPanel);
        } else {
            JLabel label = getPieceLabel(move.getMovedPiece());
            originPanel.removeAll();
            originPanel.revalidate();
            originPanel.repaint();

            endPanel.removeAll();
            endPanel.add(label);
            endPanel.repaint();
            endPanel.revalidate();

            pieceMap.remove(originPanel, move.getMovedPiece());
            pieceMap.put(originPanel, 0);
            pieceMap.remove(endPanel, move.getCapturedPiece());

            pieceMap.put(endPanel, move.getMovedPiece());

            if (abs(move.getMovedPiece()) == Piece.wKing) {
                kingPanelMap.remove(move.getMovedPiece(), originPanel);
                kingPanelMap.put(move.getMovedPiece(), endPanel);
            }
        }

        if (game.getCheckStatus() == CheckStatus.BLACK_IN_CHECK) {
            highlightBlackCheck();
        } else if (game.getCheckStatus() == CheckStatus.WHITE_IN_CHECK) {
            highlightWhiteCheck();
        } else {
            resetBlackKingPanelBackground();
            resetWhiteKingPanelBackground();
        }

        if (game.getGameStatus() == GameStatus.WHITE_CHECKMATE) {
            new WhiteWinWindow(game);
        } else if (game.getGameStatus() == GameStatus.BLACK_CHECKMATE) {
            new BlackWinWindow(game);
        }

        SwingWorker swingWorker = new SwingWorker() {
            @Override
            protected Move doInBackground() {
                sleep(500);
                return playComputerMove();
            }
        };

        if (!game.getCurrentTurn().isHuman()) {
            swingWorker.execute();
        }

        this.revalidate();
        this.repaint();
        this.setVisible(true);
    }

    /**
     * Sleep the current thread for the given amount of milliseconds
     */
    private void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    private boolean requestPromotionMove(Move move, JPanel originPanel, JPanel endPanel) {
        WhitePromotionWindow wpw = null;
        BlackPromotionWindow bpw = null;

        originPanel.removeAll();
        originPanel.repaint();
        originPanel.revalidate();

        if (game.getCurrentTurn().isWhite()) {
            wpw = new WhitePromotionWindow(move, gameFrame, endPanel.getLocationOnScreen().x,
                    endPanel.getLocationOnScreen().y);
        } else {
            bpw =  new BlackPromotionWindow(move, gameFrame, endPanel.getLocationOnScreen().x,
                    endPanel.getLocationOnScreen().y);
        }

        if (wpw != null) {
            if (!wpw.isCancelled()) {
                game.playMove(move);
                makePromotionMove(move, originPanel, endPanel);
            } else if (wpw.isCancelled()) {
                initPiece(originPanel, Piece.wPawn);
                setOriginalPanelColour(getCoordinates(originPanel));
                setOriginalPanelColour(getCoordinates(endPanel));
                return false;
            }
        } else {
            if (!bpw.isCancelled()) {
                game.playMove(move);
                makePromotionMove(move, originPanel, endPanel);
            } else if (bpw.isCancelled()) {
                initPiece(originPanel, Piece.bPawn);
                setOriginalPanelColour(getCoordinates(originPanel));
                setOriginalPanelColour(getCoordinates(endPanel));
                return false;
            }
        }
        return true;
    }

    private void makeEnPassantMove(Move move, JPanel originPanel, JPanel endPanel) {
        Board board = this.game.getBoard();
        int indexEndPanel = boardSquaresList.indexOf(endPanel);
        int indexOriginPanel = boardSquaresList.indexOf(originPanel);
        JPanel capturedPawnPanel = boardSquaresList.get(Board.getSquareIndex(Board.getSquareCoordinates(indexEndPanel).x, Board.getSquareCoordinates(indexOriginPanel).y));

        JLabel pawnLabel = (JLabel) originPanel.getComponent(0);
        originPanel.removeAll();
        originPanel.revalidate();

        capturedPawnPanel.removeAll();
        capturedPawnPanel.revalidate();
        capturedPawnPanel.repaint();

        endPanel.removeAll();
        endPanel.add(pawnLabel);
        endPanel.repaint();
        endPanel.revalidate();

        pieceMap.remove(originPanel, move.getMovedPiece());
        pieceMap.put(originPanel, 0);

        pieceMap.remove(capturedPawnPanel, game.getBoard().getPiece(Board.getSquareCoordinates(indexEndPanel).x, Board.getSquareCoordinates(indexOriginPanel).y));
        pieceMap.put(capturedPawnPanel, 0);

        pieceMap.remove(endPanel, move.getCapturedPiece());
        pieceMap.put(endPanel, move.getMovedPiece());
    }

    private void makePromotionMove(Move move, JPanel originPanel, JPanel endPanel) {
        originPanel.removeAll();
        originPanel.repaint();
        originPanel.revalidate();

        endPanel.removeAll();

        JLabel label = new JLabel();

        setPromotionMoveLabel(move, label);

        endPanel.add(label);
        endPanel.revalidate();
        endPanel.repaint();

        pieceMap.remove(originPanel, move.getMovedPiece());
        pieceMap.put(originPanel, 0);
        pieceMap.remove(endPanel, move.getCapturedPiece());

        if (game.getCheckStatus() == CheckStatus.BLACK_IN_CHECK) {
            highlightBlackCheck();
        } else if (game.getCheckStatus() == CheckStatus.WHITE_IN_CHECK) {
            highlightWhiteCheck();
        } else {
            resetBlackKingPanelBackground();
            resetWhiteKingPanelBackground();
        }

        updatePromotionMovePieceMap(move, endPanel);
    }

    private void setPromotionMoveLabel(Move move, JLabel label) {
        if (move.isQueenPromotionMove()) {
            if (move.getMovedPiece() > 0) {
                label.setIcon(new FlatSVGIcon("images/white_queen.svg"));
            } else {
                label.setIcon(new FlatSVGIcon("images/black_queen.svg"));
            }
        } else if (move.isKnightPromotionMove()) {
            if (move.getMovedPiece() > 0) {
                label.setIcon(new FlatSVGIcon("images/white_knight.svg"));
            } else {
                label.setIcon(new FlatSVGIcon("images/black_knight.svg"));
            }
        } else if (move.isRookPromotionMove()) {
            if (move.getMovedPiece() > 0) {
                label.setIcon(new FlatSVGIcon("images/white_rook.svg"));
            } else {
                label.setIcon(new FlatSVGIcon("images/black_rook.svg"));
            }
        } else if (move.isBishopPromotionMove()) {
            if (move.getMovedPiece() > 0) {
                label.setIcon(new FlatSVGIcon("images/white_bishop.svg"));
            } else {
                label.setIcon(new FlatSVGIcon("images/black_bishop.svg"));
            }
        }
    }

    private void updatePromotionMovePieceMap(Move move, JPanel endPanel) {
        if (move.isQueenPromotionMove()) {
            if (move.getMovedPiece() > 0) {
                pieceMap.put(endPanel, Piece.wQueen);
            } else {
                pieceMap.put(endPanel, Piece.bQueen);
            }
        } else if (move.isKnightPromotionMove()) {
            if (move.getMovedPiece() > 0) {
                pieceMap.put(endPanel, Piece.wKnight);
            } else {
                pieceMap.put(endPanel, Piece.bKnight);
            }
        } else if (move.isRookPromotionMove()) {
            if (move.getMovedPiece() > 0) {
                pieceMap.put(endPanel, Piece.wRook);
            } else {
                pieceMap.put(endPanel, Piece.bRook);
            }
        } else if (move.isBishopPromotionMove()) {
            if (move.getMovedPiece() > 0) {
                pieceMap.put(endPanel, Piece.wBishop);
            } else {
                pieceMap.put(endPanel, Piece.bBishop);
            }
        }
    }

    private void makeKingSideCastleMove(Move move, JPanel originPanel, JPanel endPanel, int y) {
        JPanel rookPanel = this.boardSquaresList.get(Board.getSquareIndex(8, y));

        JPanel endKingPanel = this.boardSquaresList.get(Board.getSquareIndex(7, y));
        JPanel endRookPanel = this.boardSquaresList.get(Board.getSquareIndex(6, y));

        JLabel kingLabel = (JLabel) originPanel.getComponent(0);
        JLabel rookLabel = (JLabel) rookPanel.getComponent(0);

        originPanel.removeAll();
        originPanel.revalidate();

        rookPanel.removeAll();
        rookPanel.revalidate();
        rookPanel.repaint();

        endKingPanel.removeAll();
        endKingPanel.add(kingLabel);
        endKingPanel.repaint();
        endKingPanel.revalidate();

        endRookPanel.removeAll();
        endRookPanel.add(rookLabel);
        endRookPanel.repaint();
        endRookPanel.revalidate();

        int rook;
        if (move.getMovedPiece() < 0) {
            rook = -4;
        } else {
            rook = 4;
        }

        pieceMap.remove(originPanel, move.getMovedPiece());
        pieceMap.put(originPanel, 0);

        kingPanelMap.remove(move.getMovedPiece(), originPanel);

        pieceMap.remove(endPanel, 0);
        pieceMap.put(endPanel, move.getMovedPiece());

        kingPanelMap.put(move.getMovedPiece(), endPanel);

        pieceMap.remove(rookPanel,rook);
        pieceMap.put(rookPanel, 0);

        pieceMap.remove(endRookPanel, 0);
        pieceMap.put(endRookPanel, rook);
    }

    private void makeQueenSideCastleMove(Move move, JPanel originPanel, JPanel endPanel, int y) {
        JPanel rookPanel = this.boardSquaresList.get(Board.getSquareIndex(1, y));

        JPanel endKingPanel = this.boardSquaresList.get(Board.getSquareIndex(3, y));
        JPanel endRookPanel = this.boardSquaresList.get(Board.getSquareIndex(4, y));

        JLabel kingLabel = (JLabel) originPanel.getComponent(0);
        JLabel rookLabel = (JLabel) rookPanel.getComponent(0);

        originPanel.removeAll();
        originPanel.revalidate();

        rookPanel.removeAll();
        rookPanel.revalidate();
        rookPanel.repaint();

        endKingPanel.removeAll();
        endKingPanel.add(kingLabel);
        endKingPanel.repaint();
        endKingPanel.revalidate();

        endRookPanel.removeAll();
        endRookPanel.add(rookLabel);
        endRookPanel.repaint();
        endRookPanel.revalidate();

        int rook;
        if (move.getMovedPiece() < 0) {
            rook = -4;
        } else {
            rook = 4;
        }

        pieceMap.remove(originPanel, move.getMovedPiece());
        pieceMap.put(originPanel, 0);

        kingPanelMap.remove(move.getMovedPiece(), originPanel);

        pieceMap.remove(endPanel, 0);
        pieceMap.put(endPanel, move.getMovedPiece());

        kingPanelMap.put(move.getMovedPiece(), endPanel);

        pieceMap.remove(rookPanel, rook);
        pieceMap.put(rookPanel, 0);

        pieceMap.remove(endRookPanel, 0);
        pieceMap.put(endRookPanel, rook);
    }

    /**
     * Generate a computer move and play it on the board
     */
    public Move playComputerMove() {
        Move move = game.playComputerMove();
        JPanel originPanel = getPanel(move.getStartPoint());
        JPanel endPanel = getPanel(move.getEndPoint());

        if (move.isPromotionMove()) {
            makePromotionMove(move, originPanel, endPanel);
        } else {
            executeMove(move, originPanel, endPanel);
        }

        this.repaint();
        this.revalidate();
        this.setVisible(true);

        JPanel ePanel = getPanel(move.getEndPoint());
        JPanel sPanel = getPanel(move.getStartPoint());

        ePanel.repaint();
        ePanel.revalidate();
        ePanel.setVisible(true);

        sPanel.repaint();
        sPanel.revalidate();
        sPanel.setVisible(true);

        JLabel l = (JLabel) ePanel.getComponent(0);

        l.revalidate();
        l.repaint();
        l.setVisible(true);

        return move;
    }

    /**
     * Add the image label of the dragged piece to the drag layer
     */
    public void preDrag(Point originPoint, int dragX, int dragY) {
        int draggedPiece = game.getBoard().getPiece(originPoint.x, originPoint.y);

        if (draggedPiece != 0) {
            getPanel(originPoint).setVisible(true);

            JLabel draggedPieceLabel = getPieceLabel(draggedPiece);
            draggedPieceLabel.setLocation(dragX, dragY);
            draggedPieceLabel.setSize(SQUARE_DIMENSION, SQUARE_DIMENSION);

            boardLayeredPane.add(draggedPieceLabel, JLayeredPane.DRAG_LAYER);
        }
    }

    /**
     * Execute a drag effect by moving the dragged piece's image label, called by the PieceDragAndDropListener
     */
    public void executeDrag(int dragX, int dragY) {
        JLabel draggedPieceImageLabel = null;
        Component[] components = boardLayeredPane.getComponentsInLayer(JLayeredPane.DRAG_LAYER);
        if (components.length != 0) {
            draggedPieceImageLabel = (JLabel) components[0];
        }
        if (draggedPieceImageLabel != null) {
            draggedPieceImageLabel.setLocation(dragX, dragY);
        }
    }

    /**
     * Remove the dragged piece's image label from the drag layer
     */
    public void postDrag() {
        JLabel draggedPieceImageLabel = null;
        Component[] components = boardLayeredPane.getComponentsInLayer(JLayeredPane.DRAG_LAYER);
        if (components.length != 0) {
            draggedPieceImageLabel = (JLabel) components[0];
        }
        if (draggedPieceImageLabel != null) {
            boardLayeredPane.remove(draggedPieceImageLabel);
        }
        boardLayeredPane.repaint();
    }

    /**
     * Return the correct image label for the given piece
     */
    public JLabel getPieceLabel(int piece) {
        JLabel label = new JLabel();

        if (piece > 0) {
            label.setIcon(new FlatSVGIcon("images/white_" + Piece.getPieceName(piece) + ".svg"));
        } else {
            label.setIcon(new FlatSVGIcon("images/black_" + Piece.getPieceName(piece) + ".svg"));
        }

        return label;
    }

    /**
     * Return the panel at the given point
     */
    public JPanel getPanel(Point point) {
        int index = Board.getSquareIndex(point.x, point.y);
        return boardSquaresList.get(index);
    }

    /**
     * Return the coordinate of the square corresponding to the given panel
     */
    public Point getCoordinates(JPanel panel) {
        int index = boardSquaresList.indexOf(panel);
        return new Point(Board.getSquareCoordinates(index).x, Board.getSquareCoordinates(index).y);
    }

    /**
     * Set image label at the given point to the corresponding translucent piece image
     */
    public void removeImage(Point point) {
        JPanel panel = getPanel(point);

        panel.removeAll();
        panel.repaint();
        panel.revalidate();
    }

    /**
     * Set image label at the given point to the corresponding piece image
     */
    public void setPieceImage(Point point) {
        JPanel panel = getPanel(point);
        int piece = game.getBoard().getPiece(point.x, point.y);

        panel.removeAll();

        if (game.getCheckStatus() == CheckStatus.BLACK_IN_CHECK && piece == Piece.bKing) {
            highlightBlackCheck();
        } else if (game.getCheckStatus() == CheckStatus.WHITE_IN_CHECK && piece == Piece.wKing) {
            highlightWhiteCheck();
        } else {
            panel.add(getPieceLabel(piece));
        }

        panel.repaint();
        panel.revalidate();
    }

    /**
     * Set the colour of the panel at the given point to the correct selected colour
     */
    public void setSelectedPanelColour(Point point) {
        int sum = point.x + point.y;

        if ((sum % 2) == 0) {
            getPanel(point).setBackground(DARK_SELECTED_SQUARE_COLOUR);
        } else {
            getPanel(point).setBackground(LIGHT_SELECTED_SQUARE_COLOUR);
        }

        getPanel(point).repaint();
        getPanel(point).revalidate();
    }

    public void setOriginalPanelColour(Point point) {
        int sum = point.x + point.y;

        if ((sum % 2) == 0) {
            getPanel(point).setBackground(DARK_SQUARE_COLOUR);
        } else {
            getPanel(point).setBackground(LIGHT_SQUARE_COLOUR);
        }

        getPanel(point).repaint();
        getPanel(point).revalidate();
    }

    public void setRecentlyMovedPanelColour(Point point) {
        int sum = point.x + point.y;

        if ((sum % 2) == 0) {
            getPanel(point).setBackground(DARK_SQUARE_PAST_MOVE_COLOUR);
        } else {
            getPanel(point).setBackground(LIGHT_SQUARE_PAST_MOVE_COLOUR);
        }

        getPanel(point).repaint();
        getPanel(point).revalidate();
    }

    private void highlightBlackCheck() {
        JPanel kingPanel = kingPanelMap.get(Piece.bKing);
        kingPanel.removeAll();

        JLabel label = new JLabel(new FlatSVGIcon("images/black_king_check.svg"));

        kingPanel.add(label);
        kingPanel.repaint();
        kingPanel.revalidate();
    }

    private void highlightWhiteCheck() {
        JPanel kingPanel = kingPanelMap.get(Piece.wKing);
        kingPanel.removeAll();

        JLabel label = new JLabel(new FlatSVGIcon("images/white_king_check.svg"));

        kingPanel.add(label);
        kingPanel.repaint();
        kingPanel.revalidate();
    }

    private void resetWhiteKingPanelBackground() {
        JPanel kingPanel = kingPanelMap.get(Piece.wKing);
        kingPanel.removeAll();

        initPiece(kingPanel, Piece.wKing);
    }

    private void resetBlackKingPanelBackground() {
        JPanel kingPanel = kingPanelMap.get(Piece.bKing);
        kingPanel.removeAll();

        initPiece(kingPanel, Piece.bKing);
    }

    /**
     * Return true if the square at the given point is a dark square
     */
    public Color getSquareColour(Point point) {
        int sum = point.x + point.y;

        if ((sum % 2) == 0) {
            return DARK_SQUARE_COLOUR;
        } else {
            return LIGHT_SQUARE_COLOUR;
        }
    }

    /**
     * Getters & Setters
     */
    public Game getGame() {
        return game;
    }
}

package gui;

import model.Player;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class PieceMover implements MouseListener, MouseMotionListener {
    private final BoardFrame boardPanel;

    private Point originPoint;
    private int dragOffsetX;
    private int dragOffsetY;
    private boolean dragging;

    private Point previousOriginPoint = null;
    private Point previousEndPoint = null;
    private Point selectedPanel = null;

    private Point clickedPoint = null;

    public PieceMover(BoardFrame boardPanel) {
        this.boardPanel = boardPanel;
    }


    /**
     * Invoked when a mouse button has been pressed on a component.
     */
    @Override
    public void mousePressed(MouseEvent e) {
        originPoint = getCoordinate(e);
        dragOffsetX = e.getPoint().x - BoardFrame.SQUARE_DIMENSION * ((int) getCoordinate(e).getX() - 1);
        dragOffsetY = e.getPoint().y - BoardFrame.SQUARE_DIMENSION * ( 8 - (int) getCoordinate(e).getY());
        highlightSquare(getCoordinate(e));
    }

    /**
     * Invoked when a mouse button has been released on a component.
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        if (dragging && (boardPanel.getGame().getBoard().getPiece(originPoint.x, originPoint.y) != 0)) {
            boardPanel.setPieceImage(originPoint);
            boardPanel.postDrag();
            if (boardPanel.requestMove(originPoint, getCoordinate(e))) {
                highlightMove(originPoint, getCoordinate(e));
                selectedPanel = null;
            }
            clickedPoint = null;
        }
        dragging = false;
    }

    /**
     * Invoked when a mouse button is pressed on a component and then
     * dragged.  <code>MOUSE_DRAGGED</code> events will continue to be
     * delivered to the component where the drag originated until the
     * mouse button is released (regardless of whether the mouse position
     * is within the bounds of the component).
     * <p>
     * Due to platform-dependent Drag&amp;Drop implementations,
     * <code>MOUSE_DRAGGED</code> events may not be delivered during a native
     * Drag&amp;Drop operation.
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        if (dragging) {
            boardPanel.executeDrag(e.getPoint().x - dragOffsetX, e.getPoint().y - dragOffsetY);
        } else {
            if (boardPanel.getGame().getBoard().getPiece(getCoordinate(e).x, getCoordinate(e).y) != 0) {
                boardPanel.preDrag(originPoint, e.getPoint().x - dragOffsetX, e.getPoint().y - dragOffsetY);
                boardPanel.removeImage(originPoint);
                dragging = true;
            }
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // not used
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // not used
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        // not used
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (clickedPoint != null) {
            boardPanel.requestMove(clickedPoint, getCoordinate(e));
            clickedPoint = null;
        } else {
            if (boardPanel.getGame().getBoard().getPiece(getCoordinate(e).x, getCoordinate(e).y) != 0) {
                clickedPoint = getCoordinate(e);
            }
        }
    }

    // return coordinates on board from point on panel
    public Point getCoordinate(MouseEvent e) {
        int x = 1 + e.getPoint().x / BoardFrame.SQUARE_DIMENSION;
        int y = 8 - e.getPoint().y / BoardFrame.SQUARE_DIMENSION;

        return new Point(x,y);
    }

    // highlight the square if it should be highlighted
    private void highlightSquare(Point pressedPoint) {
        int piece = boardPanel.getGame().getBoard().getPiece(pressedPoint.x, pressedPoint.y);
        Player currentTurn = boardPanel.getGame().getCurrentTurn();

        if ((piece > 0 && currentTurn.isWhite()) || (piece < 0 && !currentTurn.isWhite())) {
            if (selectedPanel != null) {
                if (selectedPanel.x == pressedPoint.x  && selectedPanel.y == pressedPoint.y) {
                    boardPanel.setOriginalPanelColour(pressedPoint);
                    selectedPanel = null;
                } else {
                    boardPanel.setOriginalPanelColour(selectedPanel);
                    boardPanel.setSelectedPanelColour(pressedPoint);
                    selectedPanel = pressedPoint;
                }
            } else {
                boardPanel.setSelectedPanelColour(pressedPoint);
                selectedPanel = pressedPoint;
            }
        }
    }

    // highlight the origin and destination square of the move that was just played
    private void highlightMove(Point originPoint, Point endPoint) {
        if (previousOriginPoint != null && previousEndPoint != null) {
            // remove highlighting of the previous move
            boardPanel.setOriginalPanelColour(previousOriginPoint);
            boardPanel.setOriginalPanelColour(previousEndPoint);
        }
        // highlight the current move
        boardPanel.setRecentlyMovedPanelColour(originPoint);
        boardPanel.setRecentlyMovedPanelColour(endPoint);

        previousOriginPoint = originPoint;
        previousEndPoint = endPoint;
    }
}

package model.generation;

import model.Board;

import java.util.ArrayList;
import java.util.List;

// generates all friendly pinned pieces and all enemy pinning pieces
public class PinGenerator {
    private Board board;
    private boolean whiteToPlay;
    private boolean pinsExistInPosition;

    private List<Integer> pinnedPieces; // pieces that are being pinned
    private List<Integer> pinningPieces; // pieces that are pinning an opposite colour piece

    /**
     * Initialize pin move generator object
     * @param board current chess board
     * @param whiteToPlay true if current turn is white
     */
    public PinGenerator(Board board, boolean whiteToPlay) {
        this.pinnedPieces = new ArrayList<>();
        this.pinningPieces = new ArrayList<>();
        this.board = board;
        this.whiteToPlay = whiteToPlay;

        calculatePins();

        pinsExistInPosition = pinnedPieces.size() > 0;
    }

    /**
     * Calculate the index all friendly pinned pieces within the current position
     */
    private void calculatePins() {
        // find all sliding enemy pieces

        // generate all possible attacks from that those pieces, stop when reach edge of board, same colour piece,
        // other colour king, more than one other colour piece

        int index = 0;
        for (int piece: board.getBoard()) {
            if (whiteToPlay) {
                switch (piece) {
                    case -3: // black bishop
                        findDiagonalPins(index);
                        break;
                    case -4: // black rook
                        findStraightPins(index);
                        break;
                    case -5: // black queen
                        findStraightPins(index);
                        findDiagonalPins(index);
                        break;
                }
            } else {
                switch (piece) {
                    case 3: // white bishop
                        findDiagonalPins(index);
                        break;
                    case 4: // white rook
                        findStraightPins(index);
                        break;
                    case 5: // white queen
                        findStraightPins(index);
                        findDiagonalPins(index);
                        break;
                }
            }

            index++;
        }
    }

    /**
     * Calculate whether the piece at the given startX and startY is pinning any friendly pieces in diagonal directions
     *
     * @param index the board index of a possible pinning piece
     */
    private void findDiagonalPins(int index) {
        int startX = Board.getSquareCoordinates(index).x;
        int startY = Board.getSquareCoordinates(index).y;

        findDiagonalPinsNorthWest(startX, startY);
        findDiagonalPinsNorthEast(startX, startY);
        findDiagonalPinsSouthWest(startX, startY);
        findDiagonalPinsSouthEast(startX, startY);
    }

    private void findDiagonalPinsNorthWest(int startX, int startY) {
        List<Integer> pinnedPieces = new ArrayList<>();
        List<Integer> pinningPieces = new ArrayList<>();
        List<Integer> possiblePinnedPieces = new ArrayList<>();
        List<Integer> possiblePinningPieces = new ArrayList<>();

        int y = startY + 1;
        boolean attackKing = false;
        if (whiteToPlay) {
            for (int x = startX - 1; x >= 1; x--) {
                if (y > 8) {
                    break;
                }

                int piece = board.getPiece(x, y);

                if (piece < 0) {
                    break;
                }

                if (piece == 6) {
                    attackKing = true;
                    break;
                }

                if (piece > 0) {
                    possiblePinningPieces.add(Board.getSquareIndex(startX, startY));
                    possiblePinnedPieces.add(Board.getSquareIndex(x,y));
                }

                y++;
            }

        } else {
            for (int x = startX - 1; x >= 1; x--) {
                if (y > 8) {
                    break;
                }

                int piece = board.getPiece(x, y);

                if (piece > 0) {
                    break;
                }

                if (piece == -6) {
                    attackKing = true;
                    break;
                }

                if (piece < 0) {
                    possiblePinningPieces.add(Board.getSquareIndex(startX, startY));
                    possiblePinnedPieces.add(Board.getSquareIndex(x,y));
                }

                y++;
            }

        }
        if (attackKing && possiblePinnedPieces.size() == 1) {
            pinningPieces.add(possiblePinningPieces.get(0));
            pinnedPieces.add(possiblePinnedPieces.get(0));
        }

        this.pinningPieces.addAll(pinningPieces);
        this.pinnedPieces.addAll(pinnedPieces);
    }

    private void findDiagonalPinsNorthEast(int startX, int startY) {
        List<Integer> pinnedPieces = new ArrayList<>();
        List<Integer> pinningPieces = new ArrayList<>();
        List<Integer> possiblePinnedPieces = new ArrayList<>();
        List<Integer> possiblePinningPieces = new ArrayList<>();


        int y = startY + 1;
        boolean attackKing = false;
        if (whiteToPlay) {
            for (int x = startX + 1; x <= 8; x++) {
                if (y > 8) {
                    break;
                }

                int piece = board.getPiece(x, y);

                if (piece < 0) {
                    break;
                }

                if (piece == 6) {
                    attackKing = true;
                    break;
                }

                if (piece > 0) {
                    possiblePinnedPieces.add(Board.getSquareIndex(x,y));
                    possiblePinningPieces.add(Board.getSquareIndex(startX, startY));
                }

                y++;
            }
        } else {
            for (int x = startX + 1; x <= 8; x++) {
                if (y > 8) {
                    break;
                }

                int piece = board.getPiece(x, y);

                if (piece > 0) {
                    break;
                }

                if (piece == -6) {
                    attackKing = true;
                    break;
                }

                if (piece < 0) {
                    possiblePinnedPieces.add(Board.getSquareIndex(x,y));
                    possiblePinningPieces.add(Board.getSquareIndex(startX, startY));
                }

                y++;
            }
        }
        if (attackKing && possiblePinnedPieces.size() == 1) {
            pinnedPieces.add(possiblePinnedPieces.get(0));
            pinningPieces.add(possiblePinningPieces.get(0));
        }

        this.pinningPieces.addAll(pinningPieces);
        this.pinnedPieces.addAll(pinnedPieces);
    }

    private void findDiagonalPinsSouthWest(int startX, int startY) {
        List<Integer> pinnedPieces = new ArrayList<>();
        List<Integer> pinningPieces = new ArrayList<>();
        List<Integer> possiblePinnedPieces = new ArrayList<>();
        List<Integer> possiblePinningPieces = new ArrayList<>();

        int y = startY - 1;
        boolean attackKing = false;

        if (whiteToPlay) {
            for (int x = startX - 1; x >= 1; x--) {
                if (y < 1) {
                    break;
                }

                int piece = board.getPiece(x, y);

                if (piece < 0) {
                    break;
                }

                if (piece == 6) {
                    attackKing = true;
                    break;
                }

                if (piece > 0) {
                    possiblePinningPieces.add(Board.getSquareIndex(startX, startY));
                    possiblePinnedPieces.add(Board.getSquareIndex(x,y));
                }

                y--;
            }
        } else {
            for (int x = startX - 1; x >= 1; x--) {
                if (y < 1) {
                    break;
                }

                int piece = board.getPiece(x, y);

                if (piece > 0) {
                    break;
                }

                if (piece == -6) {
                    attackKing = true;
                    break;
                }

                if (piece < 0) {
                    possiblePinningPieces.add(Board.getSquareIndex(startX, startY));
                    possiblePinnedPieces.add(Board.getSquareIndex(x,y));
                }

                y--;
            }
        }

        if (attackKing && possiblePinnedPieces.size() == 1) {
            pinningPieces.add(possiblePinningPieces.get(0));
            pinnedPieces.add(possiblePinnedPieces.get(0));
        }

        this.pinningPieces.addAll(pinningPieces);
        this.pinnedPieces.addAll(pinnedPieces);
    }

    private void findDiagonalPinsSouthEast(int startX, int startY) {
        List<Integer> pinnedPieces = new ArrayList<>();
        List<Integer> pinningPieces = new ArrayList<>();
        List<Integer> possiblePinnedPieces = new ArrayList<>();
        List<Integer> possiblePinningPieces = new ArrayList<>();


        int y = startY - 1;
        boolean attackKing = false;

        if (whiteToPlay) {
            for (int x = startX + 1; x <= 8; x++) {
                if (y < 1) {
                    break;
                }

                int piece = board.getPiece(x, y);

                if (piece < 0) {
                    break;
                }

                if (piece == 6) {
                    attackKing = true;
                    break;
                }

                if (piece > 0) {
                    possiblePinningPieces.add(Board.getSquareIndex(startX, startY));
                    possiblePinnedPieces.add(Board.getSquareIndex(x,y));
                }

                y--;
            }
        } else {
            for (int x = startX + 1; x <= 8; x++) {
                if (y < 1) {
                    break;
                }

                int piece = board.getPiece(x, y);

                if (piece > 0) {
                    break;
                }

                if (piece == -6) {
                    attackKing = true;
                    break;
                }

                if (piece < 0) {
                    possiblePinningPieces.add(Board.getSquareIndex(startX, startY));
                    possiblePinnedPieces.add(Board.getSquareIndex(x,y));
                }

                y--;
            }
        }

        if (attackKing && possiblePinnedPieces.size() == 1) {
            pinnedPieces.add(possiblePinnedPieces.get(0));
            pinningPieces.add(possiblePinningPieces.get(0));
        }

        this.pinningPieces.addAll(pinningPieces);
        this.pinnedPieces.addAll(pinnedPieces);
    }

    /**
     * Calculate whether the piece at the given startX and startY is pinning any friendly pieces in the vertical or
     * horizontal directions
     *
     * @param index the board index of a possible pinning piece
     */
    private void findStraightPins(int index) {
        int startX = Board.getSquareCoordinates(index).x;
        int startY = Board.getSquareCoordinates(index).y;

        findStraightPinsNorth(startX, startY);
        findStraightPinsSouth(startX, startY);
        findStraightPinsWest(startX, startY);
        findStraightPinsEast(startX, startY);
    }

    private void findStraightPinsNorth(int startX, int startY) {
        List<Integer> pinnedPieces = new ArrayList<>();
        List<Integer> pinningPieces = new ArrayList<>();
        List<Integer> possiblePinnedPieces = new ArrayList<>();
        List<Integer> possiblePinningPieces = new ArrayList<>();

        boolean attackKing = false;

        if (whiteToPlay) {
            for (int y = startY + 1; y <= 8; y++) {
                int piece = board.getPiece(startX, y);

                if (piece < 0) {
                    break;
                }

                if (piece == 6) {
                    attackKing = true;
                    break;
                }

                if (piece > 0) {
                    possiblePinnedPieces.add(Board.getSquareIndex(startX,y));
                    possiblePinningPieces.add(Board.getSquareIndex(startX, startY));
                }
            }
        } else {
            for (int y = startY + 1; y <= 8; y++) {
                int piece = board.getPiece(startX, y);

                if (piece > 0) {
                    break;
                }

                if (piece == -6) {
                    attackKing = true;
                    break;
                }

                if (piece < 0) {
                    possiblePinnedPieces.add(Board.getSquareIndex(startX,y));
                    possiblePinningPieces.add(Board.getSquareIndex(startX, startY));
                }

            }
        }

        if (attackKing && possiblePinnedPieces.size() == 1) {
            pinnedPieces.add(possiblePinnedPieces.get(0));
            pinningPieces.add(possiblePinningPieces.get(0));
        }

        this.pinningPieces.addAll(pinningPieces);
        this.pinnedPieces.addAll(pinnedPieces);
    }

    private void findStraightPinsSouth(int startX, int startY) {
        List<Integer> pinnedPieces = new ArrayList<>();
        List<Integer> pinningPieces = new ArrayList<>();
        List<Integer> possiblePinnedPieces = new ArrayList<>();
        List<Integer> possiblePinningPieces = new ArrayList<>();


        boolean attackKing = false;

        if (whiteToPlay) {
            for (int y = startY - 1; y >= 8; y--) {
                int piece = board.getPiece(startX, y);

                if (piece < 0) {
                    break;
                }

                if (piece == 6) {
                    attackKing = true;
                    break;
                }

                if (piece > 0) {
                    possiblePinningPieces.add(Board.getSquareIndex(startX,startY));
                    possiblePinnedPieces.add(Board.getSquareIndex(startX,y));
                }
            }
        } else {
            for (int y = startY - 1; y >= 8; y--) {
                int piece = board.getPiece(startX, y);

                if (piece > 0) {
                    break;
                }

                if (piece == -6) {
                    attackKing = true;
                    break;
                }

                if (piece < 0) {
                    possiblePinningPieces.add(Board.getSquareIndex(startX,startY));
                    possiblePinnedPieces.add(Board.getSquareIndex(startX,y));
                }

            }
        }

        if (attackKing && possiblePinnedPieces.size() == 1) {
            pinningPieces.add(possiblePinningPieces.get(0));
            pinnedPieces.add(possiblePinnedPieces.get(0));
        }

        this.pinningPieces.addAll(pinningPieces);
        this.pinnedPieces.addAll(pinnedPieces);
    }

    private void findStraightPinsWest(int startX, int startY) {
        List<Integer> pinnedPieces = new ArrayList<>();
        List<Integer> pinningPieces = new ArrayList<>();
        List<Integer> possiblePinnedPieces = new ArrayList<>();
        List<Integer> possiblePinningPieces = new ArrayList<>();

        boolean attackKing = false;

        if (whiteToPlay) {
            for (int x = startX - 1; x >= 1; x--) {
                int piece = board.getPiece(x, startY);

                if (piece < 0) {
                    break;
                }

                if (piece == 6) {
                    attackKing = true;
                    break;
                }

                if (piece > 0) {
                    possiblePinningPieces.add(Board.getSquareIndex(startX,startY));
                    possiblePinnedPieces.add(Board.getSquareIndex(x, startY));
                }
            }
        } else {
            for (int x = startX - 1; x >= 1; x--) {
                int piece = board.getPiece(x, startY);

                if (piece > 0) {
                    break;
                }

                if (piece == -6) {
                    attackKing = true;
                    break;
                }

                if (piece < 0) {
                    possiblePinningPieces.add(Board.getSquareIndex(startX,startY));
                    possiblePinnedPieces.add(Board.getSquareIndex(x, startY));
                }
            }
        }

        if (attackKing && possiblePinnedPieces.size() == 1) {
            pinningPieces.add(possiblePinningPieces.get(0));
            pinnedPieces.add(possiblePinnedPieces.get(0));
        }

        this.pinningPieces.addAll(pinningPieces);
        this.pinnedPieces.addAll(pinnedPieces);
    }

    private void findStraightPinsEast(int startX, int startY) {
        List<Integer> pinnedPieces = new ArrayList<>();
        List<Integer> pinningPieces = new ArrayList<>();
        List<Integer> possiblePinnedPieces = new ArrayList<>();
        List<Integer> possiblePinningPieces = new ArrayList<>();

        boolean attackKing = false;

        if (whiteToPlay) {
            for (int x = startX + 1; x <= 8; x++) {
                int piece = board.getPiece(x, startY);

                if (piece < 0) {
                    break;
                }

                if (piece == 6) {
                    attackKing = true;
                    break;
                }

                if (piece > 0) {
                    possiblePinningPieces.add(Board.getSquareIndex(startX,startY));
                    possiblePinnedPieces.add(Board.getSquareIndex(x, startY));
                }
            }
        } else {
            for (int x = startX + 1; x <= 8; x++) {
                int piece = board.getPiece(x, startY);

                if (piece > 0) {
                    break;
                }

                if (piece == -6) {
                    attackKing = true;
                    break;
                }

                if (piece < 0) {
                    possiblePinningPieces.add(Board.getSquareIndex(startX,startY));
                    possiblePinnedPieces.add(Board.getSquareIndex(x, startY));
                }
            }
        }

        if (attackKing && possiblePinnedPieces.size() == 1) {
            pinningPieces.add(possiblePinningPieces.get(0));
            pinnedPieces.add(possiblePinnedPieces.get(0));
        }

        this.pinningPieces.addAll(pinningPieces);
        this.pinnedPieces.addAll(pinnedPieces);
    }

    /**
     * Getters & Setters
     */
    public List<Integer> getPinnedPieces() {
        return pinnedPieces;
    }

    public List<Integer> getPinningPieces() {
        return pinningPieces;
    }

    public boolean doPinsExistInPosition() {
        return pinsExistInPosition;
    }
}

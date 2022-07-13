package model.generation;

import model.Board;
import model.Game;

import java.util.LinkedHashSet;
import java.util.Set;

// Represents a move generator which generates only pseudo-legal chess moves
public class ThreatMapGenerator {
    private Game game;
    private Board board;
    private Set<Integer> threatMap;

    /**
     * Generate black's threat map, e.g. all squares that are either attacked or defended by at least one black piece
     *
     * @return Integer list of indexes of each square in the threat map
     */
    public Set<Integer> generateWhiteThreatMap(Game game) {
        init(game);

        int i = 0;
        for (int piece: board.getBoard()) {
            if (piece == -6) {
                board.getBoard()[i] = 0;
                break;
            }
            i++;
        }

        int index = 0;
        for (int piece: board.getBoard()) {

            if (piece > 0) {
                int startX = Board.getSquareCoordinates(index).x;
                int startY = Board.getSquareCoordinates(index).y;

                switch (piece) {
                    case 1: whitePawnThreatMap(startX, startY);
                        break;
                    case 2: knightThreatMap(index, startX, startY);
                        break;
                    case 3: bishopThreatMap(startX, startY);
                        break;
                    case 4: rookThreatMap(startX, startY);
                        break;
                    case 5: queenThreatMap(startX, startY);
                        break;
                    case 6: kingThreatMap(index, startX, startY);
                        break;
                }
            }

            index++;
        }

        return threatMap;
    }

    /**
     * Generate white's threat map, e.g. all squares that are either attacked or defended by at least one white piece
     *
     * @return Integer list of indexes of each square in the threat map
     */
    public Set<Integer> generateBlackThreatMap(Game game) {
        init(game);

        int i = 0;
        for (int piece: board.getBoard()) {
            if (piece == 6) {
                board.getBoard()[i] = 0;
                break;
            }
            i++;
        }

        int index = 0;
        for (int piece: board.getBoard()) {

            if (piece < 0) {
                int startX = Board.getSquareCoordinates(index).x;
                int startY = Board.getSquareCoordinates(index).y;

                switch (piece) {
                    case -1: blackPawnThreatMap(startX, startY);
                        break;
                    case -2: knightThreatMap(index, startX, startY);
                        break;
                    case -3: bishopThreatMap(startX, startY);
                        break;
                    case -4: rookThreatMap(startX, startY);
                        break;
                    case -5: queenThreatMap(startX, startY);
                        break;
                    case -6: kingThreatMap(index, startX, startY);
                        break;
                }
            }

            index++;
        }

        return threatMap;
    }

    /**
     * Generate a white pawn threat map from the given index
     */
    public Set<Integer> generateWhitePawnThreatMap(Game game, int index) {
        init(game);

        whitePawnThreatMap(Board.getSquareCoordinates(index).x, Board.getSquareCoordinates(index).y);

        return threatMap;
    }

    /**
     * Generate a black pawn threat map from the given index
     */
    public Set<Integer> generateBlackPawnThreatMap(Game game, int index) {
        init(game);

        blackPawnThreatMap(Board.getSquareCoordinates(index).x, Board.getSquareCoordinates(index).y);

        return threatMap;
    }

    /**
     * Generate a knight threat map from the given index
     */
    public Set<Integer> generateKnightThreatMap(Game game, int index) {
        init(game);

        knightThreatMap(index, Board.getSquareCoordinates(index).x, Board.getSquareCoordinates(index).y);

        return threatMap;
    }

    /**
     * Generate a bishop threat map from the given index
     */
    public Set<Integer> generateBishopThreatMap(Game game, int index) {
        init(game);

        bishopThreatMap(Board.getSquareCoordinates(index).x, Board.getSquareCoordinates(index).y);

        return threatMap;
    }

    /**
     * Generate a rook threat map from the given index
     */
    public Set<Integer> generateRookThreatMap(Game game, int index) {
        init(game);

        rookThreatMap(Board.getSquareCoordinates(index).x, Board.getSquareCoordinates(index).y);

        return threatMap;
    }

    /**
     * Generate a queen threat map from the given index
     */
    public Set<Integer> generateQueenThreatMap(Game game, int index) {
        init(game);

        queenThreatMap(Board.getSquareCoordinates(index).x, Board.getSquareCoordinates(index).y);

        return threatMap;
    }

    /**
     * Initialize the current ThreatMapGenerator object
     */
    private void init(Game game) {
        this.game = game;
        this.board = new Board();
        board.setBoard(game.getBoard().getBoard().clone());
        this.threatMap = new LinkedHashSet<>();
    }

    /**
     * Generate a threat map for the white pawn on the square (startX, startY)
     */
    private void whitePawnThreatMap(int startX, int startY) {
        // captures
        if (startX + 1 <= 8 && startY + 1 <= 8) {
            threatMap.add(Board.getSquareIndex(startX + 1, startY + 1));
        }

        if (startX - 1 >= 1 && startY + 1 <= 8) {
            threatMap.add(Board.getSquareIndex(startX - 1, startY + 1));
        }
    }

    /**
     * Generate a threat map for the black pawn on the square (startX, startY)
     */
    private void blackPawnThreatMap(int startX, int startY) {
        // captures
        if (startX + 1 <= 8 && startY - 1 >= 1) {
            threatMap.add(Board.getSquareIndex(startX + 1, startY - 1));
        }

        if (startX - 1 >= 1 && startY - 1 >= 1) {
            threatMap.add(Board.getSquareIndex(startX - 1, startY - 1));
        }
    }

    /**
     * Generate a threat map for the knight on the square with the given index
     */
    private void knightThreatMap(int index, int startX, int startY) {
        if (startX + 1 <= 8 && startY + 2 <= 8) {
            threatMap.add(index + 17);
        }

        if (startX + 2 <= 8 && startY + 1 <= 8) {
            threatMap.add(index + 10);
        }

        if (startX - 1 >= 1 && startY + 2 <= 8) {
            threatMap.add(index + 15);
        }

        if (startX - 2 >= 1 && startY + 1 <= 8) {
            threatMap.add(index + 6);
        }

        if (startX - 2 >= 1 && startY - 1 >= 1) {
            threatMap.add(index - 10);
        }

        if (startX - 1 >= 1 && startY - 2 >= 1) {
            threatMap.add(index - 17);
        }

        if (startX + 1 <= 8 && startY - 2 >= 1) {
            threatMap.add(index - 15);
        }

        if (startX + 2 <= 8 && startY - 1 >= 1) {
            threatMap.add(index - 6);
        }
    }

    /**
     * Generate a threat map for the bishop on the square (startX, startY)
     */
    private void bishopThreatMap(int startX, int startY) {
        northEastSquares(startX, startY);
        northWestSquares(startX, startY);
        southEastSquares(startX, startY);
        southWestSquares(startX, startY);
    }

    /**
     * Generate a threat map for the rook on the square (startX, startY)
     */
    private void rookThreatMap(int startX, int startY) {
        northSquares(startX, startY);
        southSquares(startX, startY);
        westSquares(startX, startY);
        eastSquares(startX, startY);
    }

    /**
     * Generate a threat map for the queen on the square (startX, startY)
     */
    private void queenThreatMap(int startX, int startY) {
        northSquares(startX, startY);
        southSquares(startX, startY);
        westSquares(startX, startY);
        eastSquares(startX, startY);
        northEastSquares(startX, startY);
        northWestSquares(startX, startY);
        southEastSquares(startX, startY);
        southWestSquares(startX, startY);
    }

    /**
     * Generate a threat map for the king on the square (startX, startY)
     */
    private void kingThreatMap(int index, int startX, int startY) {
        if (startY + 1 <= 8) {
            threatMap.add(index + 8);
        }

        if (startY - 1 >= 1) {
            threatMap.add(index - 8);
        }

        if (startX + 1 <= 8) {
            threatMap.add(index + 1);
        }

        if (startX - 1 >= 1) {
            threatMap.add(index - 1);
        }

        if (startX - 1 >= 1 && startY + 1 <= 8) {
            threatMap.add(index + 7);
        }

        if (startX + 1 <= 8 && startY - 1 >= 1) {
            threatMap.add(index - 7);
        }

        if (startX + 1 <= 8 && startY + 1 <= 8) {
            threatMap.add(index + 9);
        }

        if (startX - 1 >= 1 && startY - 1 >= 1) {
            threatMap.add(index - 9);
        }
    }

    /**
     * Sliding piece threat map generators for different directions: north, south, west, east, north-west, north-east,
     * south-west and south-east
     */
    private void northEastSquares(int startX, int startY) {
        int y = startY + 1;

        for (int x = startX + 1; x <= 8; x++) {
            if (y > 8) {
                break;
            }

            threatMap.add(Board.getSquareIndex(x,y));

            if (board.getPiece(x,y) != 0) {
                break;
            }

            y++;
        }
    }

    private void northWestSquares(int startX, int startY) {
        int y = startY + 1;

        for (int x = startX - 1; x >= 1; x--) {
            if (y > 8) {
                break;
            }

            threatMap.add(Board.getSquareIndex(x,y));

            if (board.getPiece(x,y) != 0) {
                break;
            }

            y++;
        }
    }

    private void southWestSquares(int startX, int startY) {
        int y = startY - 1;

        for (int x = startX - 1; x >= 1; x--) {
            if (y < 1) {
                break;
            }

            threatMap.add(Board.getSquareIndex(x,y));

            if (board.getPiece(x,y) != 0) {
                break;
            }

            y--;
        }
    }

    private void southEastSquares(int startX, int startY) {
        int y = startY - 1;

        for (int x = startX + 1; x <= 8; x++) {
            if (y < 1) {
                break;
            }

            threatMap.add(Board.getSquareIndex(x,y));

            if (board.getPiece(x,y) != 0) {
                break;
            }

            y--;
        }
    }

    private void northSquares(int startX, int startY) {
        for (int y = startY + 1; y <= 8; y++) {
            threatMap.add(Board.getSquareIndex(startX,y));

            if (board.getPiece(startX,y) != 0) {
                break;
            }
        }
    }

    private void southSquares(int startX, int startY) {
        for (int y = startY - 1; y >= 1; y--) {
            threatMap.add(Board.getSquareIndex(startX,y));

            if (board.getPiece(startX,y) != 0) {
                break;
            }
        }
    }

    private void westSquares(int startX, int startY) {
        for (int x = startX - 1; x >= 1; x--) {
            threatMap.add(Board.getSquareIndex(x,startY));

            if (board.getPiece(x,startY) != 0) {
                break;
            }
        }
    }

    private void eastSquares(int startX, int startY) {
        for (int x = startX + 1; x <= 8; x++) {
            threatMap.add(Board.getSquareIndex(x,startY));

            if (board.getPiece(x,startY) != 0) {
                break;
            }
        }
    }
}
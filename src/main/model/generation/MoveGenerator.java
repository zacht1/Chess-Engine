package model.generation;

import model.Board;
import model.Game;
import model.Move;
import model.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static java.lang.Math.abs;

// Represents a move generator which generates only legal chess moves
public class MoveGenerator {
    private Game game;
    private Board board;
    private boolean whiteToPlay;
    private int friendlyKingIndex;

    private ThreatMapGenerator threatMapGenerator;
    private CheckGenerator checkGenerator;
    private CheckMoveGenerator checkMoveGenerator;
    private PinGenerator pinGenerator;
    private MaskGenerator maskGenerator;

    private boolean pinsExistInPosition;
    private List<Integer> pinnedPieces;
    private Set<Integer> threatMap;
    private List<Move> legalMoves;

    /**
     * Generate all legal moves for the given player in the given game board position
     */
    public List<Move> generateMoves(Game game, Player player) {
        init(game, player);

        if (checkGenerator.isInCheck()) {
            legalMoves.addAll(checkMoveGenerator.generateCheckEscapingMoves());
            return legalMoves;
        }

        int index = 0;
        for (int piece: game.getBoard().getBoard()) {

            if ((piece > 0 && player.isWhite()) || (piece < 0 && !player.isWhite())) {

                int startX = Board.getSquareCoordinates(index).x;
                int startY = Board.getSquareCoordinates(index).y;

                if (piece == -1) {
                    generateBlackPawnMoves(startX, startY);
                } else if (piece == 1) {
                    generateWhitePawnMoves(startX, startY);
                }

                switch (abs(piece)) {
                    case 2:
                        generateKnightMoves(startX, startY);
                        break;
                    case 3:
                        generateBishopMoves(startX, startY);
                        break;
                    case 4:
                        generateRookMoves(startX, startY);
                        break;
                    case 5:
                        generateQueenMoves(startX, startY);
                        break;
                    case 6:
                        generateKingMoves(startX, startY);
                        break;
                }
            }

            index++;
        }

        return legalMoves;
    }

    /**
     * Initialize this MoveGenerator object
     */
    private void init(Game game, Player player) {
        this.game = game;
        this.board = game.getBoard();
        this.whiteToPlay = player.isWhite();
        this.legalMoves = new ArrayList<>();

        int index = 0;
        for (int piece: board.getBoard()) {
            if (whiteToPlay && piece == 6) {
                friendlyKingIndex = index;
            } else if (!whiteToPlay && piece == -6) {
                friendlyKingIndex = index;
            }
            index++;
        }

        this.threatMapGenerator = new ThreatMapGenerator();

        if (whiteToPlay) {
            threatMap = threatMapGenerator.generateBlackThreatMap(game);
        } else {
            threatMap = threatMapGenerator.generateWhiteThreatMap(game);
        }

        this.checkGenerator = new CheckGenerator(game, friendlyKingIndex, whiteToPlay, threatMapGenerator);
        this.maskGenerator = new MaskGenerator(checkGenerator, friendlyKingIndex);
        this.pinGenerator = new PinGenerator(board, whiteToPlay);
        this.checkMoveGenerator = new CheckMoveGenerator(game, whiteToPlay, maskGenerator, threatMap, pinGenerator);

        this.pinsExistInPosition = pinGenerator.doPinsExistInPosition();
        this.pinnedPieces = pinGenerator.getPinnedPieces();
    }

    /**
     * Generate all pseudo-legal black pawn moves originating from the square at the given startX and startY
     */
    private void generateBlackPawnMoves(int startX, int startY) {
        if (pinsExistInPosition) {
            int pinningPieceIndex = 0;
            for (int pinnedPieceIndex: pinnedPieces) {
                if (pinnedPieceIndex == Board.getSquareIndex(startX, startY)) {
                    generateBlackPawnPinMoves(pinnedPieceIndex, pinningPieceIndex);
                }

                    pinningPieceIndex++;
            }
            return;
        }

        // 1 square move
        if (startY - 1 >= 1 && board.getPiece(startX, startY - 1) == 0) {
            legalMoves.add(new Move(board, startX, startY, startX, startY - 1));
        }

        // 2 square move
        if (startY == 7 && board.getPiece(startX, startY - 1) == 0 && board.getPiece(startX, startY - 2) == 0) {
            legalMoves.add(new Move(board, startX, startY, startX, startY - 2));
        }

        // captures
        if (startX - 1 >= 1 && startY - 1 >= 1 && board.getPiece(startX - 1, startY - 1) > 0) {
            legalMoves.add(new Move(board, startX, startY, startX - 1, startY - 1));
        }

        if (startX + 1 <= 8 && startY - 1 >= 1 && board.getPiece(startX + 1, startY - 1) > 0) {
            legalMoves.add(new Move(board, startX, startY, startX + 1, startY - 1));
        }

        // en passant
        if (startY == 4 && game.getMoveList().get(0).getEndY() == 4 && game.getMoveList().get(0).getStartY() == 2 && game.getMoveList().get(0).getEndX() == startX - 1) {
            legalMoves.add(new Move(board, startX, startY, startX - 1, startY - 1));
        }

        if (startY == 4 && game.getMoveList().get(0).getEndY() == 4 && game.getMoveList().get(0).getStartY() == 2 && game.getMoveList().get(0).getEndX() == startX + 1) {
            legalMoves.add(new Move(board, startX, startY, startX + 1, startY - 1));
        }
    }

    private void generateBlackPawnPinMoves(int pinnedPieceIndex, int pinningPieceIndex) {
        int pinnedPieceX = Board.getSquareCoordinates(pinnedPieceIndex).x;
        int pinnedPieceY = Board.getSquareCoordinates(pinnedPieceIndex).y;

        int pinningPieceX = Board.getSquareCoordinates(pinningPieceIndex).x;
        int pinningPieceY = Board.getSquareCoordinates(pinningPieceIndex).y;

        if (pinnedPieceX == pinningPieceX) { // piece is pinned on the vertical and piece can move
            // 1 square move
            if (pinnedPieceY - 1 >= 1 && board.getPiece(pinnedPieceX, pinnedPieceY - 1) == 0) {
                legalMoves.add(new Move(board, pinnedPieceX, pinnedPieceY, pinnedPieceX, pinnedPieceY - 1));
            }

            // 2 square move
            if (pinnedPieceY == 7 && board.getPiece(pinnedPieceX, pinnedPieceY - 1) == 0 && board.getPiece(pinnedPieceX, pinnedPieceY - 2) == 0) {
                legalMoves.add(new Move(board, pinnedPieceX, pinnedPieceY, pinnedPieceX, pinnedPieceY - 2));
            }
        } else { // piece is pinned on the diagonal
            if (pinnedPieceIndex - 9 == pinningPieceIndex) { // pinning piece is capturable to the left of pinned pawn
                legalMoves.add(new Move(board, pinnedPieceX, pinnedPieceY, pinnedPieceX - 1, pinnedPieceY - 1));
            } else if (pinnedPieceIndex - 7 == pinningPieceIndex) { // pinning piece is capturable to the right of pinned pawn
                legalMoves.add(new Move(board, pinnedPieceX, pinnedPieceY, pinnedPieceX + 1, pinnedPieceY + 1));
            } // en passant moves
        }
    }

    /**
     * Generate all pseudo-legal white pawn moves originating from the square at the given startX and startY
     */
    private void generateWhitePawnMoves(int startX, int startY) {
        if (pinsExistInPosition) {
            // can only move straight if the pieces is pinned vertically
            // can only capture if piece pinning the pawn is right next to it
        }

        // 1 square move
        if (startY + 1 <= 8 && board.getPiece(startX, startY + 1) == 0) {
            legalMoves.add(new Move(board, startX, startY, startX, startY + 1));
        }

        // 2 square move
        if (startY == 2 && board.getPiece(startX, startY + 1) == 0 && board.getPiece(startX, startY + 2) == 0) {
            legalMoves.add(new Move(board, startX, startY, startX, startY + 2));
        }

        // captures
        if (startX + 1 <= 8 && startY + 1 <= 8 && board.getPiece(startX + 1, startY + 1) < 0) {
            legalMoves.add(new Move(board, startX, startY, startX + 1, startY + 1));
        }

        if (startX - 1 >= 1 && startY + 1 <= 8 && board.getPiece(startX - 1, startY + 1) < 0) {
            legalMoves.add(new Move(board, startX, startY, startX - 1, startY + 1));
        }

        // en passant
        if (!game.getMoveList().isEmpty()) {
            if (startY == 5 && game.getMoveList().get(0).getEndY() == 5 && game.getMoveList().get(0).getStartY() == 7 && game.getMoveList().get(0).getEndX() == startX - 1) {
                legalMoves.add(new Move(board, startX, startY, startX - 1, startY + 1));
            }
            if (startY == 5 && game.getMoveList().get(0).getEndY() == 5 && game.getMoveList().get(0).getStartY() == 7 && game.getMoveList().get(0).getEndX() == startX + 1) {
                legalMoves.add(new Move(board, startX, startY, startX + 1, startY + 1));
            }
        }
    }

    /**
     * Generate all pseudo-legal knight moves originating from the square at the given startX and startY
     */
    private void generateKnightMoves(int startX, int startY) {
        if (pinsExistInPosition && pinnedPieces.contains(Board.getSquareIndex(startX, startY))) {
            return;
        }

        if (startX + 1 <= 8 && startY + 2 <= 8) {
            int capturedPiece = board.getPiece(startX + 1, startY + 2);

            if (capturedPiece == 0 || capturedPiece < 0 && whiteToPlay || capturedPiece > 0 && !whiteToPlay) {
                legalMoves.add(new Move(board, startX, startY, startX + 1, startY + 2));
            }
        }

        if (startX + 2 <= 8 && startY + 1 <= 8) {
            int capturedPiece = board.getPiece(startX + 2, startY + 1);

            if (capturedPiece == 0 || capturedPiece < 0 && whiteToPlay || capturedPiece > 0 && !whiteToPlay) {
                legalMoves.add(new Move(board, startX, startY, startX + 2, startY + 1));
            }
        }

        if (startX - 1 >= 1 && startY + 2 <= 8) {
            int capturedPiece = board.getPiece(startX - 1, startY + 2);

            if (capturedPiece == 0 || capturedPiece < 0 && whiteToPlay || capturedPiece > 0 && !whiteToPlay) {
                legalMoves.add(new Move(board, startX, startY, startX - 1, startY + 2));
            }
        }

        if (startX - 2 >= 1 && startY + 1 <= 8) {
            int capturedPiece = board.getPiece(startX - 2, startY + 1);

            if (capturedPiece == 0 || capturedPiece < 0 && whiteToPlay || capturedPiece > 0 && !whiteToPlay) {
                legalMoves.add(new Move(board, startX, startY, startX - 2, startY + 1));
            }
        }

        if (startX - 2 >= 1 && startY - 1 >= 1) {
            int capturedPiece = board.getPiece(startX - 2, startY - 1);

            if (capturedPiece == 0 || capturedPiece < 0 && whiteToPlay || capturedPiece > 0 && !whiteToPlay) {
                legalMoves.add(new Move(board, startX, startY, startX - 2, startY - 1));
            }
        }

        if (startX - 1 >= 1 && startY - 2 >= 1) {
            int capturedPiece = board.getPiece(startX - 1, startY - 2);

            if (capturedPiece == 0 || capturedPiece < 0 && whiteToPlay || capturedPiece > 0 && !whiteToPlay) {
                legalMoves.add(new Move(board, startX, startY, startX - 1, startY - 2));
            }
        }

        if (startX + 1 <= 8 && startY - 2 >= 1) {
            int capturedPiece = board.getPiece(startX + 1, startY - 2);

            if (capturedPiece == 0 || capturedPiece < 0 && whiteToPlay || capturedPiece > 0 && !whiteToPlay) {
                legalMoves.add(new Move(board, startX, startY, startX + 1, startY - 2));
            }
        }

        if (startX + 2 <= 8 && startY - 1 >= 1) {
            int capturedPiece = board.getPiece(startX + 2, startY - 1);

            if (capturedPiece == 0 || capturedPiece < 0 && whiteToPlay || capturedPiece > 0 && !whiteToPlay) {
                legalMoves.add(new Move(board, startX, startY, startX + 2, startY - 1));
            }
        }
    }

    /**
     * Generate all pseudo-legal bishop moves originating from the square at the given startX and startY
     */
    private void generateBishopMoves(int startX, int startY) {
        if (pinsExistInPosition) {
            // step 1: check that the piece at startX and startY is pinned
            // step 2: where is pin coming from diagonal or vertical
            // step 3: if piece is pinned vertically, no legal moves, otherwise
            //         generate moves between pinned piece and pinning piece, those are the only legal moves
            //         (including capturing pinned piece)
        }

        generateNorthWestMoves(startX, startY);
        generateNorthEastMoves(startX, startY);
        generateSouthWestMoves(startX, startY);
        generateSouthEastMoves(startX, startY);
    }

    /**
     * Generate all pseudo-legal rook moves originating from the square at the given startX and startY
     */
    private void generateRookMoves(int startX, int startY) {
        if (pinsExistInPosition) {
            // step 1: check that the piece at startX and startY is pinned
            // step 2: where is pin coming from diagonal or vertical
            // step 3: if piece is pinned diagonally, no legal moves, otherwise
            //         generate moves between pinned piece and pinning piece, those are the only legal moves
            //         (including capturing pinned piece)
        }

        generateNorthMoves(startX, startY);
        generateSouthMoves(startX, startY);
        generateWestMoves(startX, startY);
        generateEastMoves(startX, startY);
    }

    /**
     * Generate all pseudo-legal queen moves originating from the square at the given startX and startY
     */
    private void generateQueenMoves(int startX, int startY)  {
        if (pinsExistInPosition) {
            // step 1: check that the piece at startX and startY is pinned
            // step 2: where is pin coming from diagonal or vertical
            // step 3: generate moves between  pinned piece and pinning piece, those are the only legal moves
            //         (including capturing pinned piece)
        }

        // straight moves
        generateNorthMoves(startX, startY);
        generateSouthMoves(startX, startY);
        generateWestMoves(startX, startY);
        generateEastMoves(startX, startY);

        // diagonal moves
        generateNorthWestMoves(startX, startY);
        generateNorthEastMoves(startX, startY);
        generateSouthWestMoves(startX, startY);
        generateSouthEastMoves(startX, startY);
    }

    /**
     * Generate all pseudo-legal king moves originating from the square at the given startX and startY
     */
    private void generateKingMoves(int startX, int startY) {
        // north
        if (startY + 1 <= 8) {
            int capturedPiece = board.getPiece(startX, startY + 1);

            if (!threatMap.contains(Board.getSquareIndex(startX, startY + 1))) {
                if (capturedPiece == 0 || capturedPiece < 0 && whiteToPlay || capturedPiece > 0 && !whiteToPlay) {
                    legalMoves.add(new Move(board, startX, startY, startX, startY + 1));
                }
            }
        }

        // south
        if (startY - 1 >= 1) {
            int capturedPiece = board.getPiece(startX, startY - 1);

            if (!threatMap.contains(Board.getSquareIndex(startX, startY - 1))) {
                if (capturedPiece == 0 || capturedPiece < 0 && whiteToPlay || capturedPiece > 0 && !whiteToPlay) {
                    legalMoves.add(new Move(board, startX, startY, startX, startY - 1));
                }
            }
        }

        // west
        if (startX - 1 >= 1) {
            int capturedPiece = board.getPiece(startX - 1, startY);

            if (!threatMap.contains(Board.getSquareIndex(startX - 1, startY))) {
                if (capturedPiece == 0 || capturedPiece < 0 && whiteToPlay || capturedPiece > 0 && !whiteToPlay) {
                    legalMoves.add(new Move(board, startX, startY, startX - 1, startY));
                }
            }
        }

        // east
        if (startX + 1 <= 8) {
            int capturedPiece = board.getPiece(startX + 1, startY);

            if (!threatMap.contains(Board.getSquareIndex(startX + 1, startY))) {
                if (capturedPiece == 0 || capturedPiece < 0 && whiteToPlay || capturedPiece > 0 && !whiteToPlay) {
                    legalMoves.add(new Move(board, startX, startY, startX + 1, startY));
                }
            }
        }

        // north-west
        if (startX - 1 >= 1 && startY + 1 <= 8) {
            int capturedPiece = board.getPiece(startX - 1, startY + 1);

            if (!threatMap.contains(Board.getSquareIndex(startX - 1, startY + 1))) {
                if (capturedPiece == 0 || capturedPiece < 0 && whiteToPlay || capturedPiece > 0 && !whiteToPlay) {
                    legalMoves.add(new Move(board, startX, startY, startX - 1, startY + 1));
                }
            }
        }

        // north-east
        if (startX + 1 <= 8 && startY + 1 <= 8) {
            int capturedPiece = board.getPiece(startX + 1, startY + 1);

            if (!threatMap.contains(Board.getSquareIndex(startX + 1, startY + 1))) {
                if (capturedPiece == 0 || capturedPiece < 0 && whiteToPlay || capturedPiece > 0 && !whiteToPlay) {
                    legalMoves.add(new Move(board, startX, startY, startX + 1, startY + 1));
                }
            }
        }

        // south-west
        if (startX - 1 >= 1 && startY - 1 >= 1) {
            int capturedPiece = board.getPiece(startX - 1, startY - 1);

            if (!threatMap.contains(Board.getSquareIndex(startX - 1, startY - 1))) {
                if (capturedPiece == 0 || capturedPiece < 0 && whiteToPlay || capturedPiece > 0 && !whiteToPlay) {
                    legalMoves.add(new Move(board, startX, startY, startX - 1, startY - 1));
                }
            }
        }

        // south-east
        if (startX + 1 <= 8 && startY - 1 >= 1) {
            int capturedPiece = board.getPiece(startX + 1, startY - 1);

            if (!threatMap.contains(Board.getSquareIndex(startX + 1, startY - 1))) {
                if (capturedPiece == 0 || capturedPiece < 0 && whiteToPlay || capturedPiece > 0 && !whiteToPlay) {
                    legalMoves.add(new Move(board, startX, startY, startX + 1, startY - 1));
                }
            }
        }
    }

    /**
     * Sliding piece move generators for different directions: north, south, west, east, north-west, north-east,
     * south-west and south-east
     */
    private void generateNorthMoves(int startX, int startY) {
        for (int y = startY + 1; y <= 8; y++) {
            if (board.getPiece(startX, y) == 0) {
                legalMoves.add(new Move(board, startX, startY, startX, y));
            } else if (board.getPiece(startX, y) < 0 && whiteToPlay || board.getPiece(startX, y) > 0 && !whiteToPlay) {
                legalMoves.add(new Move(board, startX, startY, startX, y));
                break;
            } else {
                break;
            }
        }
    }

    private void generateSouthMoves(int startX, int startY) {
        for (int y = startY - 1; y >= 1; y--) {
            if (board.getPiece(startX, y) == 0) {
                legalMoves.add(new Move(board, startX, startY, startX, y));
            } else if (board.getPiece(startX, y) < 0 && whiteToPlay || board.getPiece(startX, y) > 0 && !whiteToPlay) {
                legalMoves.add(new Move(board, startX, startY, startX, y));
                break;
            } else {
                break;
            }
        }
    }

    private void generateWestMoves(int startX, int startY) {
        for (int x = startX - 1; x >= 1; x--) {
            if (board.getPiece(x, startY) == 0) {
                legalMoves.add(new Move(board, startX, startY, x, startY));
            } else if (board.getPiece(x, startY) < 0 && whiteToPlay || board.getPiece(x, startY) > 0 && !whiteToPlay) {
                legalMoves.add(new Move(board, startX, startY, x, startY));
                break;
            }  else {
                break;
            }
        }
    }

    private void generateEastMoves(int startX, int startY) {
        for (int x = startX + 1; x <= 8; x++) {
            if (board.getPiece(x, startY) == 0) {
                legalMoves.add(new Move(board, startX, startY, x, startY));
            } else if (board.getPiece(x, startY) < 0 && whiteToPlay || board.getPiece(x, startY) > 0 && !whiteToPlay) {
                legalMoves.add(new Move(board, startX, startY, x, startY));
                break;
            }  else {
                break;
            }
        }
    }

    private void generateNorthEastMoves(int startX, int startY) {
        int y = startY + 1;

        for (int x = startX + 1; x <= 8; x++) {
            if (y > 8) {
                break;
            }

            if (board.getPiece(x, y) == 0) {
                legalMoves.add(new Move(board, startX, startY, x, y));
            } else if (board.getPiece(x, y) < 0 && whiteToPlay || board.getPiece(x,y) > 0 && !whiteToPlay) {
                legalMoves.add(new Move(board, startX, startY, x, y));
                break;
            } else {
                break;
            }

            y++;
        }
    }

    private void generateNorthWestMoves(int startX, int startY) {
        int y = startY + 1;

        for (int x = startX - 1; x >= 1; x--) {
            if (y > 8) {
                break;
            }

            if (board.getPiece(x, y) == 0) {
                legalMoves.add(new Move(board, startX, startY, x, y));
            } else if (board.getPiece(x, y) < 0 && whiteToPlay || board.getPiece(x,y) > 0 && !whiteToPlay) {
                legalMoves.add(new Move(board, startX, startY, x, y));
                break;
            } else {
                break;
            }

            y++;
        }
    }

    private void generateSouthWestMoves(int startX, int startY) {
        int y = startY - 1;

        for (int x = startX - 1; x >= 1; x--) {
            if (y < 1) {
                break;
            }

            if (board.getPiece(x, y) == 0) {
                legalMoves.add(new Move(board, startX, startY, x, y));
            } else if (board.getPiece(x, y) < 0 && whiteToPlay || board.getPiece(x,y) > 0 && !whiteToPlay) {
                legalMoves.add(new Move(board, startX, startY, x, y));
                break;
            } else {
                break;
            }

            y--;
        }
    }

    private void generateSouthEastMoves(int startX, int startY) {
        int y = startY - 1;

        for (int x = startX + 1; x <= 8; x++) {
            if (y < 1) {
                break;
            }

            if (board.getPiece(x, y) == 0) {
                legalMoves.add(new Move(board, startX, startY, x, y));
            } else if (board.getPiece(x, y) < 0 && whiteToPlay || board.getPiece(x,y) > 0 && !whiteToPlay) {
                legalMoves.add(new Move(board, startX, startY, x, y));
                break;
            } else {
                break;
            }

            y--;
        }
    }

    /**
     * Getters & Setters
     */
    public boolean inCheck() {
        return checkGenerator.isInCheck();
    }

    public boolean isInDoubleCheck() {
        return checkGenerator.isInDoubleCheck();
    }

    public List<Integer> getCheckers() {
        return checkGenerator.getCheckers();
    }

    public List<Integer> getPinnedPieces() {
        return pinnedPieces;
    }

    public boolean doPinsExistInPosition() {
        return pinsExistInPosition;
    }
}

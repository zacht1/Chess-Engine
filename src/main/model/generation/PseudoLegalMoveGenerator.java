package model.generation;

import model.Board;
import model.Game;
import model.Move;
import model.Player;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;

public class PseudoLegalMoveGenerator {
    private Game game;
    private Board board;
    private boolean whiteToPlay;
    private List<Move> pseudoLegalMoves;

    /**
     * Generate all pseudo legal moves for the given colour on the given chess board
     * Pseudo legal moves are all legal moves without considering checks or pins
     */
    public List<Move> generatePseudoLegalMoves(Game game, Player player) {
        init(game, player);

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

        return pseudoLegalMoves;
    }

    /**
     * Initialize the pseudo-legal move generator for the given game and current player
     */
    private void init(Game game, Player player) {
        this.game = game;
        this.board = game.getBoard();
        this.whiteToPlay = player.isWhite();
        this.pseudoLegalMoves = new ArrayList<>();
    }

    /**
     * Generate all pseudo-legal black pawn moves originating from the square at the given startX and startY
     */
    private void generateBlackPawnMoves(int startX, int startY) {
        // 1 square move
        if (startY - 1 >= 1 && board.getPiece(startX, startY - 1) == 0) {
            pseudoLegalMoves.add(new Move(board, startX, startY, startX, startY - 1));
        }

        // 2 square move
        if (startY == 7 && board.getPiece(startX, startY - 1) == 0 && board.getPiece(startX, startY - 2) == 0) {
            pseudoLegalMoves.add(new Move(board, startX, startY, startX, startY - 2));
        }

        // captures
        if (startX - 1 >= 1 && startY - 1 >= 1 && board.getPiece(startX - 1, startY - 1) > 0) {
            pseudoLegalMoves.add(new Move(board, startX, startY, startX - 1, startY - 1));
        }

        if (startX + 1 <= 8 && startY - 1 >= 1 && board.getPiece(startX + 1, startY - 1) > 0) {
            pseudoLegalMoves.add(new Move(board, startX, startY, startX + 1, startY - 1));
        }

        // en passant
        if (startY == 4 && board.getMoveList().get(0).getEndY() == 4 && board.getMoveList().get(0).getStartY() == 2 && board.getMoveList().get(0).getEndX() == startX - 1) {
            pseudoLegalMoves.add(new Move(board, startX, startY, startX - 1, startY - 1));
        }

        if (startY == 4 && board.getMoveList().get(0).getEndY() == 4 && board.getMoveList().get(0).getStartY() == 2 && board.getMoveList().get(0).getEndX() == startX + 1) {
            pseudoLegalMoves.add(new Move(board, startX, startY, startX + 1, startY - 1));
        }
    }

    /**
     * Generate all pseudo-legal white pawn moves originating from the square at the given startX and startY
     */
    private void generateWhitePawnMoves(int startX, int startY) {
        // 1 square move
        if (startY + 1 <= 8 && board.getPiece(startX, startY + 1) == 0) {
            pseudoLegalMoves.add(new Move(board, startX, startY, startX, startY + 1));
        }

        // 2 square move
        if (startY == 2 && board.getPiece(startX, startY + 1) == 0 && board.getPiece(startX, startY + 2) == 0) {
            pseudoLegalMoves.add(new Move(board, startX, startY, startX, startY + 2));
        }

        // captures
        if (startX + 1 <= 8 && startY + 1 <= 8 && board.getPiece(startX + 1, startY + 1) < 0) {
            pseudoLegalMoves.add(new Move(board, startX, startY, startX + 1, startY + 1));
        }

        if (startX - 1 >= 1 && startY + 1 <= 8 && board.getPiece(startX - 1, startY + 1) < 0) {
            pseudoLegalMoves.add(new Move(board, startX, startY, startX - 1, startY + 1));
        }

        // en passant
        if (!board.getMoveList().isEmpty()) {
            if (startY == 5 && board.getMoveList().get(0).getEndY() == 5 && board.getMoveList().get(0).getStartY() == 7 && board.getMoveList().get(0).getEndX() == startX - 1) {
                pseudoLegalMoves.add(new Move(board, startX, startY, startX - 1, startY + 1));
            }

            if (startY == 5 && board.getMoveList().get(0).getEndY() == 5 && board.getMoveList().get(0).getStartY() == 7 && board.getMoveList().get(0).getEndX() == startX + 1) {
                pseudoLegalMoves.add(new Move(board, startX, startY, startX + 1, startY + 1));
            }
        }
    }

    /**
     * Generate all pseudo-legal knight moves originating from the square at the given startX and startY
     */
    private void generateKnightMoves(int startX, int startY) {

        if (startX + 1 <= 8 && startY + 2 <= 8) {
            int capturedPiece = board.getPiece(startX + 1, startY + 2);

            if (capturedPiece == 0 || capturedPiece < 0 && whiteToPlay || capturedPiece > 0 && !whiteToPlay) {
                pseudoLegalMoves.add(new Move(board, startX, startY, startX + 1, startY + 2));
            }
        }

        if (startX + 2 <= 8 && startY + 1 <= 8) {
            int capturedPiece = board.getPiece(startX + 2, startY + 1);

            if (capturedPiece == 0 || capturedPiece < 0 && whiteToPlay || capturedPiece > 0 && !whiteToPlay) {
                pseudoLegalMoves.add(new Move(board, startX, startY, startX + 2, startY + 1));
            }
        }

        if (startX - 1 >= 1 && startY + 2 <= 8) {
            int capturedPiece = board.getPiece(startX - 1, startY + 2);

            if (capturedPiece == 0 || capturedPiece < 0 && whiteToPlay || capturedPiece > 0 && !whiteToPlay) {
                pseudoLegalMoves.add(new Move(board, startX, startY, startX - 1, startY + 2));
            }
        }

        if (startX - 2 >= 1 && startY + 1 <= 8) {
            int capturedPiece = board.getPiece(startX - 2, startY + 1);

            if (capturedPiece == 0 || capturedPiece < 0 && whiteToPlay || capturedPiece > 0 && !whiteToPlay) {
                pseudoLegalMoves.add(new Move(board, startX, startY, startX - 2, startY + 1));
            }
        }

        if (startX - 2 >= 1 && startY - 1 >= 1) {
            int capturedPiece = board.getPiece(startX - 2, startY - 1);

            if (capturedPiece == 0 || capturedPiece < 0 && whiteToPlay || capturedPiece > 0 && !whiteToPlay) {
                pseudoLegalMoves.add(new Move(board, startX, startY, startX - 2, startY - 1));
            }
        }

        if (startX - 1 >= 1 && startY - 2 >= 1) {
            int capturedPiece = board.getPiece(startX - 1, startY - 2);

            if (capturedPiece == 0 || capturedPiece < 0 && whiteToPlay || capturedPiece > 0 && !whiteToPlay) {
                pseudoLegalMoves.add(new Move(board, startX, startY, startX - 1, startY - 2));
            }
        }

        if (startX + 1 <= 8 && startY - 2 >= 1) {
            int capturedPiece = board.getPiece(startX + 1, startY - 2);

            if (capturedPiece == 0 || capturedPiece < 0 && whiteToPlay || capturedPiece > 0 && !whiteToPlay) {
                pseudoLegalMoves.add(new Move(board, startX, startY, startX + 1, startY - 2));
            }
        }

        if (startX + 2 <= 8 && startY - 1 >= 1) {
            int capturedPiece = board.getPiece(startX + 2, startY - 1);

            if (capturedPiece == 0 || capturedPiece < 0 && whiteToPlay || capturedPiece > 0 && !whiteToPlay) {
                pseudoLegalMoves.add(new Move(board, startX, startY, startX + 2, startY - 1));
            }
        }
    }

    /**
     * Generate all pseudo-legal bishop moves originating from the square at the given startX and startY
     */
    private void generateBishopMoves(int startX, int startY) {
        generateNorthWestMoves(startX, startY);
        generateNorthEastMoves(startX, startY);
        generateSouthWestMoves(startX, startY);
        generateSouthEastMoves(startX, startY);
    }

    /**
     * Generate all pseudo-legal rook moves originating from the square at the given startX and startY
     */
    private void generateRookMoves(int startX, int startY) {
        generateNorthMoves(startX, startY);
        generateSouthMoves(startX, startY);
        generateWestMoves(startX, startY);
        generateEastMoves(startX, startY);
    }

    /**
     * Generate all pseudo-legal queen moves originating from the square at the given startX and startY
     */
    private void generateQueenMoves(int startX, int startY) {
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

            if (capturedPiece == 0 || capturedPiece < 0 && whiteToPlay || capturedPiece > 0 && !whiteToPlay) {
                pseudoLegalMoves.add(new Move(board, startX, startY, startX, startY + 1));
            }
        }

        // south
        if (startY - 1 >= 1) {
            int capturedPiece = board.getPiece(startX, startY - 1);

            if (capturedPiece == 0 || capturedPiece < 0 && whiteToPlay || capturedPiece > 0 && !whiteToPlay) {
                pseudoLegalMoves.add(new Move(board, startX, startY, startX, startY - 1));
            }
        }

        // west
        if (startX - 1 >= 1) {
            int capturedPiece = board.getPiece(startX - 1, startY);

            if (capturedPiece == 0 || capturedPiece < 0 && whiteToPlay || capturedPiece > 0 && !whiteToPlay) {
                pseudoLegalMoves.add(new Move(board, startX, startY, startX - 1, startY));
            }
        }

        // east
        if (startX + 1 <= 8) {
            int capturedPiece = board.getPiece(startX + 1, startY);

            if (capturedPiece == 0 || capturedPiece < 0 && whiteToPlay || capturedPiece > 0 && !whiteToPlay) {
                pseudoLegalMoves.add(new Move(board, startX, startY, startX + 1, startY));
            }
        }

        // north-west
        if (startX - 1 >= 1 && startY + 1 <= 8) {
            int capturedPiece = board.getPiece(startX - 1, startY + 1);

            if (capturedPiece == 0 || capturedPiece < 0 && whiteToPlay || capturedPiece > 0 && !whiteToPlay) {
                pseudoLegalMoves.add(new Move(board, startX, startY, startX - 1, startY + 1));
            }
        }

        // north-east
        if (startX + 1 <= 8 && startY + 1 <= 8) {
            int capturedPiece = board.getPiece(startX + 1, startY + 1);

            if (capturedPiece == 0 || capturedPiece < 0 && whiteToPlay || capturedPiece > 0 && !whiteToPlay) {
                pseudoLegalMoves.add(new Move(board, startX, startY, startX + 1, startY + 1));
            }
        }

        // south-west
        if (startX - 1 >= 1 && startY - 1 >= 1) {
            int capturedPiece = board.getPiece(startX - 1, startY - 1);

            if (capturedPiece == 0 || capturedPiece < 0 && whiteToPlay || capturedPiece > 0 && !whiteToPlay) {
                pseudoLegalMoves.add(new Move(board, startX, startY, startX - 1, startY - 1));
            }
        }

        // south-east
        if (startX + 1 <= 8 && startY - 1 >= 1) {
            int capturedPiece = board.getPiece(startX + 1, startY - 1);

            if (capturedPiece == 0 || capturedPiece < 0 && whiteToPlay || capturedPiece > 0 && !whiteToPlay) {
                pseudoLegalMoves.add(new Move(board, startX, startY, startX + 1, startY - 1));
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
                pseudoLegalMoves.add(new Move(board, startX, startY, startX, y));
            } else if (board.getPiece(startX, y) < 0 && whiteToPlay || board.getPiece(startX, y) > 0 && !whiteToPlay) {
                pseudoLegalMoves.add(new Move(board, startX, startY, startX, y));
                break;
            } else {
                break;
            }
        }
    }

    private void generateSouthMoves(int startX, int startY) {
        for (int y = startY - 1; y >= 1; y--) {
            if (board.getPiece(startX, y) == 0) {
                pseudoLegalMoves.add(new Move(board, startX, startY, startX, y));
            } else if (board.getPiece(startX, y) < 0 && whiteToPlay || board.getPiece(startX, y) > 0 && !whiteToPlay) {
                pseudoLegalMoves.add(new Move(board, startX, startY, startX, y));
                break;
            } else {
                break;
            }
        }
    }

    private void generateWestMoves(int startX, int startY) {
        for (int x = startX - 1; x >= 1; x--) {
            if (board.getPiece(x, startY) == 0) {
                pseudoLegalMoves.add(new Move(board, startX, startY, x, startY));
            } else if (board.getPiece(x, startY) < 0 && whiteToPlay || board.getPiece(x, startY) > 0 && !whiteToPlay) {
                pseudoLegalMoves.add(new Move(board, startX, startY, x, startY));
                break;
            }  else {
                break;
            }
        }
    }

    private void generateEastMoves(int startX, int startY) {
        for (int x = startX + 1; x <= 8; x++) {
            if (board.getPiece(x, startY) == 0) {
                pseudoLegalMoves.add(new Move(board, startX, startY, x, startY));
            } else if (board.getPiece(x, startY) < 0 && whiteToPlay || board.getPiece(x, startY) > 0 && !whiteToPlay) {
                pseudoLegalMoves.add(new Move(board, startX, startY, x, startY));
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
                pseudoLegalMoves.add(new Move(board, startX, startY, x, y));
            } else if (board.getPiece(x, y) < 0 && whiteToPlay || board.getPiece(x,y) > 0 && !whiteToPlay) {
                pseudoLegalMoves.add(new Move(board, startX, startY, x, y));
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
                pseudoLegalMoves.add(new Move(board, startX, startY, x, y));
            } else if (board.getPiece(x, y) < 0 && whiteToPlay || board.getPiece(x,y) > 0 && !whiteToPlay) {
                pseudoLegalMoves.add(new Move(board, startX, startY, x, y));
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
                pseudoLegalMoves.add(new Move(board, startX, startY, x, y));
            } else if (board.getPiece(x, y) < 0 && whiteToPlay || board.getPiece(x,y) > 0 && !whiteToPlay) {
                pseudoLegalMoves.add(new Move(board, startX, startY, x, y));
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
                pseudoLegalMoves.add(new Move(board, startX, startY, x, y));
            } else if (board.getPiece(x, y) < 0 && whiteToPlay || board.getPiece(x,y) > 0 && !whiteToPlay) {
                pseudoLegalMoves.add(new Move(board, startX, startY, x, y));
                break;
            } else {
                break;
            }

            y--;
        }
    }
}

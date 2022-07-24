package model.generation;

import enumerations.MoveType;
import model.*;

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
    private PinMoveGenerator pinMoveGenerator;

    private boolean pinsExistInPosition;
    private List<Integer> pinnedPieces;
    private List<Integer> pinningPieces;
    private Set<Integer> threatMap;
    private List<Move> legalMoves;

    /**
     * Generate all legal moves for the given player in the given game board position
     */
    public List<Move> generateLegalMoves(Game game, Player player) {
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

        for (Move move: legalMoves) {
            move.setComputerMove(true);
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
                break;
            } else if (!whiteToPlay && piece == -6) {
                friendlyKingIndex = index;
                break;
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
        this.pinMoveGenerator = new PinMoveGenerator(game, whiteToPlay, friendlyKingIndex);

        this.pinsExistInPosition = pinGenerator.doPinsExistInPosition();
        this.pinnedPieces = pinGenerator.getPinnedPieces();
        this.pinningPieces = pinGenerator.getPinningPieces();
    }

    /**
     * Generate all pseudo-legal black pawn moves originating from the square at the given startX and startY
     */
    private void generateBlackPawnMoves(int startX, int startY) {
        if (pinsExistInPosition) {
            int pinningPieceIndex = 0;
            for (int pinnedPieceIndex: pinnedPieces) {
                if (pinnedPieceIndex == Board.getSquareIndex(startX, startY)) {
                    legalMoves.addAll(pinMoveGenerator.generateBlackPawnMoves(pinnedPieceIndex, pinningPieces.get(pinningPieceIndex)));
                    return;
                }
                pinningPieceIndex++;
            }
        }

        // 1 square move
        if (startY - 1 >= 1 && board.getPiece(startX, startY - 1) == 0) {
            if (startY - 1 == 1) {
                Move queenPromotion = new Move(game, startX, startY, startX, 1);
                Move knightPromotion = new Move(game, startX, startY, startX, 1);
                Move bishopPromotion = new Move(game, startX, startY, startX, 1);
                Move rookPromotion = new Move(game, startX, startY, startX, 1);
                queenPromotion.setMoveType(MoveType.QUEEN_PROMOTION);
                knightPromotion.setMoveType(MoveType.KNIGHT_PROMOTION);
                bishopPromotion.setMoveType(MoveType.BISHOP_PROMOTION);
                rookPromotion.setMoveType(MoveType.ROOK_PROMOTION);

                legalMoves.add(queenPromotion);
                legalMoves.add(knightPromotion);
                legalMoves.add(bishopPromotion);
                legalMoves.add(rookPromotion);
            } else {
                legalMoves.add(new Move(game, startX, startY, startX, startY - 1));
            }
        }

        // 2 square move
        if (startY == 7 && board.getPiece(startX, startY - 1) == 0 && board.getPiece(startX, startY - 2) == 0) {
            legalMoves.add(new Move(game, startX, startY, startX, startY - 2));
        }

        // captures
        if (startX - 1 >= 1 && startY - 1 >= 1 && board.getPiece(startX - 1, startY - 1) > 0) {
            if (startY - 1 == 1) {
                Move queenPromotion = new Move(game, startX, startY, startX - 1, 1);
                Move knightPromotion = new Move(game, startX, startY, startX - 1, 1);
                Move bishopPromotion = new Move(game, startX, startY, startX - 1, 1);
                Move rookPromotion = new Move(game, startX, startY, startX - 1, 1);
                queenPromotion.setMoveType(MoveType.QUEEN_PROMOTION);
                knightPromotion.setMoveType(MoveType.KNIGHT_PROMOTION);
                bishopPromotion.setMoveType(MoveType.BISHOP_PROMOTION);
                rookPromotion.setMoveType(MoveType.ROOK_PROMOTION);

                legalMoves.add(queenPromotion);
                legalMoves.add(knightPromotion);
                legalMoves.add(bishopPromotion);
                legalMoves.add(rookPromotion);
            } else {
                legalMoves.add(new Move(game, startX, startY, startX - 1, startY - 1));
            }
        }

        if (startX + 1 <= 8 && startY - 1 >= 1 && board.getPiece(startX + 1, startY - 1) > 0) {
            if (startY - 1 == 1) {
                Move queenPromotion = new Move(game, startX, startY, startX + 1, 1);
                Move knightPromotion = new Move(game, startX, startY, startX + 1, 1);
                Move bishopPromotion = new Move(game, startX, startY, startX + 1, 1);
                Move rookPromotion = new Move(game, startX, startY, startX + 1, 1);
                queenPromotion.setMoveType(MoveType.QUEEN_PROMOTION);
                knightPromotion.setMoveType(MoveType.KNIGHT_PROMOTION);
                bishopPromotion.setMoveType(MoveType.BISHOP_PROMOTION);
                rookPromotion.setMoveType(MoveType.ROOK_PROMOTION);

                legalMoves.add(queenPromotion);
                legalMoves.add(knightPromotion);
                legalMoves.add(bishopPromotion);
                legalMoves.add(rookPromotion);
            } else {
                legalMoves.add(new Move(game, startX, startY, startX + 1, startY - 1));
            }
        }

        // en passant
        if (!board.getMoveList().isEmpty()) {
            List<Move> moves = board.getMoveList();
            int mostRecent = moves.size() - 1;

            if (startY == 4 && moves.get(mostRecent).getEndY() == 4 && moves.get(mostRecent).getStartY() == 2 &&
                    moves.get(mostRecent).getEndX() == startX - 1 && board.getPiece(startX - 1, startY) == Piece.wPawn &&
                    legalBlackEnPassant(startX, startY, startX - 1, startY))  {
                legalMoves.add(new Move(game, startX, startY, startX - 1, startY - 1));
            }

            if (startY == 4 && moves.get(mostRecent).getEndY() == 4 && moves.get(mostRecent).getStartY() == 2 &&
                    moves.get(mostRecent).getEndX() == startX + 1 && board.getPiece(startX + 1, startY) == Piece.wPawn &&
                    legalBlackEnPassant(startX, startY, startX + 1, startY)) {
                legalMoves.add(new Move(game, startX, startY, startX + 1, startY - 1));
            }
        }
    }

    private boolean legalBlackEnPassant(int blackPawnX, int blackPawnY, int whitePawnX, int whitePawnY) {
        int[] testBoard = board.getBoard().clone();
        testBoard[Board.getSquareIndex(blackPawnX, blackPawnY)] = Piece.empty;
        testBoard[Board.getSquareIndex(whitePawnX, whitePawnY)] = Piece.empty;

        int kingX = Board.getSquareCoordinates(friendlyKingIndex).x;
        int kingY = Board.getSquareCoordinates(friendlyKingIndex).y;

        for (int x = kingX + 1; x <= 8; x++) {
            int index = Board.getSquareIndex(x, kingY);

            if (testBoard[index] < 0 || testBoard[index] == Piece.wPawn || testBoard[index] == Piece.wKnight ||
                    testBoard[index] == Piece.wBishop || testBoard[index] == Piece.wKing) {
                break;
            }

            if (testBoard[index] == Piece.wRook || testBoard[index] == Piece.wQueen) {
                return false;
            }
        }

        for (int x = kingX - 1; x >= 1; x--) {
            int index = Board.getSquareIndex(x, kingY);

            if (testBoard[index] < 0 || testBoard[index] == Piece.wPawn || testBoard[index] == Piece.wKnight ||
                    testBoard[index] == Piece.wBishop || testBoard[index] == Piece.wKing) {
                break;
            }

            if (testBoard[index] == Piece.wRook || testBoard[index] == Piece.wQueen) {
                return false;
            }
        }

        return true;
    }

    /**
     * Generate all pseudo-legal white pawn moves originating from the square at the given startX and startY
     */
    private void generateWhitePawnMoves(int startX, int startY) {
        if (pinsExistInPosition) {
            int pinningPieceIndex = 0;
            for (int pinnedPieceIndex: pinnedPieces) {
                if (pinnedPieceIndex == Board.getSquareIndex(startX, startY)) {
                    legalMoves.addAll(pinMoveGenerator.generateWhitePawnMoves(pinnedPieceIndex, pinningPieces.get(pinningPieceIndex)));
                    return;
                }
                pinningPieceIndex++;
            }
        }

        // 1 square move
        if (startY + 1 <= 8 && board.getPiece(startX, startY + 1) == 0) {
            if (startY + 1 == 8) {
                Move queenPromotion = new Move(game, startX, startY, startX, 8);
                Move knightPromotion = new Move(game, startX, startY, startX, 8);
                Move bishopPromotion = new Move(game, startX, startY, startX, 8);
                Move rookPromotion = new Move(game, startX, startY, startX, 8);
                queenPromotion.setMoveType(MoveType.QUEEN_PROMOTION);
                knightPromotion.setMoveType(MoveType.KNIGHT_PROMOTION);
                bishopPromotion.setMoveType(MoveType.BISHOP_PROMOTION);
                rookPromotion.setMoveType(MoveType.ROOK_PROMOTION);

                legalMoves.add(queenPromotion);
                legalMoves.add(knightPromotion);
                legalMoves.add(bishopPromotion);
                legalMoves.add(rookPromotion);
            } else {
                legalMoves.add(new Move(game, startX, startY, startX, startY + 1));
            }
        }

        // 2 square move
        if (startY == 2 && board.getPiece(startX, startY + 1) == 0 && board.getPiece(startX, startY + 2) == 0) {
            legalMoves.add(new Move(game, startX, startY, startX, startY + 2));
        }

        // captures
        if (startX + 1 <= 8 && startY + 1 <= 8 && board.getPiece(startX + 1, startY + 1) < 0) {
            if (startY + 1 == 8) {
                Move queenPromotion = new Move(game, startX, startY, startX + 1, 8);
                Move knightPromotion = new Move(game, startX, startY, startX + 1, 8);
                Move bishopPromotion = new Move(game, startX, startY, startX + 1, 8);
                Move rookPromotion = new Move(game, startX, startY, startX + 1, 8);
                queenPromotion.setMoveType(MoveType.QUEEN_PROMOTION);
                knightPromotion.setMoveType(MoveType.KNIGHT_PROMOTION);
                bishopPromotion.setMoveType(MoveType.BISHOP_PROMOTION);
                rookPromotion.setMoveType(MoveType.ROOK_PROMOTION);

                legalMoves.add(queenPromotion);
                legalMoves.add(knightPromotion);
                legalMoves.add(bishopPromotion);
                legalMoves.add(rookPromotion);
            } else {
                legalMoves.add(new Move(game, startX, startY, startX + 1, startY + 1));
            }
        }

        if (startX - 1 >= 1 && startY + 1 <= 8 && board.getPiece(startX - 1, startY + 1) < 0) {
            if (startY + 1 == 8) {
                Move queenPromotion = new Move(game, startX, startY, startX - 1, 8);
                Move knightPromotion = new Move(game, startX, startY, startX - 1, 8);
                Move bishopPromotion = new Move(game, startX, startY, startX - 1, 8);
                Move rookPromotion = new Move(game, startX, startY, startX - 1, 8);
                queenPromotion.setMoveType(MoveType.QUEEN_PROMOTION);
                knightPromotion.setMoveType(MoveType.KNIGHT_PROMOTION);
                bishopPromotion.setMoveType(MoveType.BISHOP_PROMOTION);
                rookPromotion.setMoveType(MoveType.ROOK_PROMOTION);

                legalMoves.add(queenPromotion);
                legalMoves.add(knightPromotion);
                legalMoves.add(bishopPromotion);
                legalMoves.add(rookPromotion);
            } else {
                legalMoves.add(new Move(game, startX, startY, startX - 1, startY + 1));
            }
        }

        // en passant
        if (!board.getMoveList().isEmpty()) {
            List<Move> moves = board.getMoveList();
            int mostRecent = moves.size() - 1;

            if (startY == 5 && moves.get(mostRecent).getEndY() == 5 && moves.get(mostRecent).getStartY() == 7 &&
                    moves.get(mostRecent).getEndX() == startX - 1 && board.getPiece(startX - 1, startY) == Piece.bPawn &&
                    legalWhiteEnPassant(startX, startY, startX - 1, startY)) {
                legalMoves.add(new Move(game, startX, startY, startX - 1, startY + 1));
            }
            if (startY == 5 && moves.get(mostRecent).getEndY() == 5 && moves.get(mostRecent).getStartY() == 7 &&
                    moves.get(mostRecent).getEndX() == startX + 1 && board.getPiece(startX + 1, startY) == Piece.bPawn &&
                    legalWhiteEnPassant(startX, startY, startX + 1, startY)) {
                legalMoves.add(new Move(game, startX, startY, startX + 1, startY + 1));
            }
        }
    }

    private boolean legalWhiteEnPassant(int whitePawnX, int whitePawnY, int blackPawnX, int blackPawnY) {
        int[] testBoard = board.getBoard().clone();
        testBoard[Board.getSquareIndex(whitePawnX, whitePawnY)] = Piece.empty;
        testBoard[Board.getSquareIndex(blackPawnX, blackPawnY)] = Piece.empty;

        int kingX = Board.getSquareCoordinates(friendlyKingIndex).x;
        int kingY = Board.getSquareCoordinates(friendlyKingIndex).y;

        for (int x = kingX + 1; x <= 8; x++) {
            int index = Board.getSquareIndex(x, kingY);

            if (testBoard[index] > 0 || testBoard[index] == Piece.bPawn || testBoard[index] == Piece.bKnight ||
                    testBoard[index] == Piece.bBishop || testBoard[index] == Piece.bKing) {
                break;
            }

            if (testBoard[index] == Piece.bRook || testBoard[index] == Piece.bQueen) {
                return false;
            }
        }

        for (int x = kingX - 1; x >= 1; x--) {
            int index = Board.getSquareIndex(x, kingY);

            if (testBoard[index] > 0 || testBoard[index] == Piece.bPawn || testBoard[index] == Piece.bKnight ||
                    testBoard[index] == Piece.bBishop || testBoard[index] == Piece.bKing) {
                break;
            }

            if (testBoard[index] == Piece.bRook || testBoard[index] == Piece.bQueen) {
                return false;
            }
        }

        return true;
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
                legalMoves.add(new Move(game, startX, startY, startX + 1, startY + 2));
            }
        }

        if (startX + 2 <= 8 && startY + 1 <= 8) {
            int capturedPiece = board.getPiece(startX + 2, startY + 1);

            if (capturedPiece == 0 || capturedPiece < 0 && whiteToPlay || capturedPiece > 0 && !whiteToPlay) {
                legalMoves.add(new Move(game, startX, startY, startX + 2, startY + 1));
            }
        }

        if (startX - 1 >= 1 && startY + 2 <= 8) {
            int capturedPiece = board.getPiece(startX - 1, startY + 2);

            if (capturedPiece == 0 || capturedPiece < 0 && whiteToPlay || capturedPiece > 0 && !whiteToPlay) {
                legalMoves.add(new Move(game, startX, startY, startX - 1, startY + 2));
            }
        }

        if (startX - 2 >= 1 && startY + 1 <= 8) {
            int capturedPiece = board.getPiece(startX - 2, startY + 1);

            if (capturedPiece == 0 || capturedPiece < 0 && whiteToPlay || capturedPiece > 0 && !whiteToPlay) {
                legalMoves.add(new Move(game, startX, startY, startX - 2, startY + 1));
            }
        }

        if (startX - 2 >= 1 && startY - 1 >= 1) {
            int capturedPiece = board.getPiece(startX - 2, startY - 1);

            if (capturedPiece == 0 || capturedPiece < 0 && whiteToPlay || capturedPiece > 0 && !whiteToPlay) {
                legalMoves.add(new Move(game, startX, startY, startX - 2, startY - 1));
            }
        }

        if (startX - 1 >= 1 && startY - 2 >= 1) {
            int capturedPiece = board.getPiece(startX - 1, startY - 2);

            if (capturedPiece == 0 || capturedPiece < 0 && whiteToPlay || capturedPiece > 0 && !whiteToPlay) {
                legalMoves.add(new Move(game, startX, startY, startX - 1, startY - 2));
            }
        }

        if (startX + 1 <= 8 && startY - 2 >= 1) {
            int capturedPiece = board.getPiece(startX + 1, startY - 2);

            if (capturedPiece == 0 || capturedPiece < 0 && whiteToPlay || capturedPiece > 0 && !whiteToPlay) {
                legalMoves.add(new Move(game, startX, startY, startX + 1, startY - 2));
            }
        }

        if (startX + 2 <= 8 && startY - 1 >= 1) {
            int capturedPiece = board.getPiece(startX + 2, startY - 1);

            if (capturedPiece == 0 || capturedPiece < 0 && whiteToPlay || capturedPiece > 0 && !whiteToPlay) {
                legalMoves.add(new Move(game, startX, startY, startX + 2, startY - 1));
            }
        }
    }

    /**
     * Generate all pseudo-legal bishop moves originating from the square at the given startX and startY
     */
    private void generateBishopMoves(int startX, int startY) {
        if (pinsExistInPosition) {

            int pinningPieceIndex = 0;
            for (int pinnedPieceIndex: pinnedPieces) {
                if (pinnedPieceIndex == Board.getSquareIndex(startX, startY)) {
                    legalMoves.addAll(pinMoveGenerator.generateBishopMoves(pinnedPieceIndex, pinningPieces.get(pinningPieceIndex)));
                    return;
                }
                pinningPieceIndex++;
            }
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
            int pinningPieceIndex = 0;
            for (int pinnedPieceIndex: pinnedPieces) {
                if (pinnedPieceIndex == Board.getSquareIndex(startX, startY)) {
                    legalMoves.addAll(pinMoveGenerator.generateRookMoves(pinnedPieceIndex, pinningPieces.get(pinningPieceIndex)));
                    return;
                }
                pinningPieceIndex++;
            }
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
            int pinningPieceIndex = 0;
            for (int pinnedPieceIndex: pinnedPieces) {
                if (pinnedPieceIndex == Board.getSquareIndex(startX, startY)) {
                    legalMoves.addAll(pinMoveGenerator.generateQueenMoves(pinnedPieceIndex, pinningPieces.get(pinningPieceIndex)));
                    return;
                }
                pinningPieceIndex++;
            }
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
                    legalMoves.add(new Move(game, startX, startY, startX, startY + 1));
                }
            }
        }

        // south
        if (startY - 1 >= 1) {
            int capturedPiece = board.getPiece(startX, startY - 1);

            if (!threatMap.contains(Board.getSquareIndex(startX, startY - 1))) {
                if (capturedPiece == 0 || capturedPiece < 0 && whiteToPlay || capturedPiece > 0 && !whiteToPlay) {
                    legalMoves.add(new Move(game, startX, startY, startX, startY - 1));
                }
            }
        }

        // west
        if (startX - 1 >= 1) {
            int capturedPiece = board.getPiece(startX - 1, startY);

            if (!threatMap.contains(Board.getSquareIndex(startX - 1, startY))) {
                if (capturedPiece == 0 || capturedPiece < 0 && whiteToPlay || capturedPiece > 0 && !whiteToPlay) {
                    legalMoves.add(new Move(game, startX, startY, startX - 1, startY));
                }
            }
        }

        // east
        if (startX + 1 <= 8) {
            int capturedPiece = board.getPiece(startX + 1, startY);

            if (!threatMap.contains(Board.getSquareIndex(startX + 1, startY))) {
                if (capturedPiece == 0 || capturedPiece < 0 && whiteToPlay || capturedPiece > 0 && !whiteToPlay) {
                    legalMoves.add(new Move(game, startX, startY, startX + 1, startY));
                }
            }
        }

        // north-west
        if (startX - 1 >= 1 && startY + 1 <= 8) {
            int capturedPiece = board.getPiece(startX - 1, startY + 1);

            if (!threatMap.contains(Board.getSquareIndex(startX - 1, startY + 1))) {
                if (capturedPiece == 0 || capturedPiece < 0 && whiteToPlay || capturedPiece > 0 && !whiteToPlay) {
                    legalMoves.add(new Move(game, startX, startY, startX - 1, startY + 1));
                }
            }
        }

        // north-east
        if (startX + 1 <= 8 && startY + 1 <= 8) {
            int capturedPiece = board.getPiece(startX + 1, startY + 1);

            if (!threatMap.contains(Board.getSquareIndex(startX + 1, startY + 1))) {
                if (capturedPiece == 0 || capturedPiece < 0 && whiteToPlay || capturedPiece > 0 && !whiteToPlay) {
                    legalMoves.add(new Move(game, startX, startY, startX + 1, startY + 1));
                }
            }
        }

        // south-west
        if (startX - 1 >= 1 && startY - 1 >= 1) {
            int capturedPiece = board.getPiece(startX - 1, startY - 1);

            if (!threatMap.contains(Board.getSquareIndex(startX - 1, startY - 1))) {
                if (capturedPiece == 0 || capturedPiece < 0 && whiteToPlay || capturedPiece > 0 && !whiteToPlay) {
                    legalMoves.add(new Move(game, startX, startY, startX - 1, startY - 1));
                }
            }
        }

        // south-east
        if (startX + 1 <= 8 && startY - 1 >= 1) {
            int capturedPiece = board.getPiece(startX + 1, startY - 1);

            if (!threatMap.contains(Board.getSquareIndex(startX + 1, startY - 1))) {
                if (capturedPiece == 0 || capturedPiece < 0 && whiteToPlay || capturedPiece > 0 && !whiteToPlay) {
                    legalMoves.add(new Move(game, startX, startY, startX + 1, startY - 1));
                }
            }
        }

        if (whiteToPlay) {
            generateWhiteCastleMoves(startX, startY);
        } else {
            generateBlackCastleMoves(startX, startY);
        }
    }

    private void generateWhiteCastleMoves(int startX, int startY) {
        if (game.canWhiteKingSideCastle() && board.getPiece(6, 1) == Piece.empty &&
                board.getPiece(7, 1) == Piece.empty && board.getPiece(8, 1) == Piece.wRook) { // white king side castle
            if (!threatMap.contains(5) && !threatMap.contains(6)) {
                legalMoves.add(new Move(game, startX, startY, 7, 1));
            }
        }

        if (game.canWhiteQueenSideCastle() && board.getPiece(4, 1) == Piece.empty &&
                board.getPiece(3, 1) == Piece.empty && board.getPiece(2, 1) == Piece.empty && board.getPiece(1,1) == Piece.wRook) { // white queen side castle
            if (!threatMap.contains(3) && !threatMap.contains(2)) {
                legalMoves.add(new Move(game, startX, startY, 3, 1));
            }
        }
    }

    private void generateBlackCastleMoves(int startX, int startY) {
        if (game.canBlackKingSideCastle() && board.getPiece(6, 8) == Piece.empty &&
                board.getPiece(7, 8) == Piece.empty && board.getPiece(8, 8) == Piece.bRook) { // white king side castle
            if (!threatMap.contains(61) && !threatMap.contains(62)) {
                legalMoves.add(new Move(game, startX, startY, 7, 8));
            }
        }

        if (game.canBlackQueenSideCastle() && board.getPiece(4, 8) == Piece.empty &&
                board.getPiece(3, 8) == Piece.empty && board.getPiece(2, 8) == Piece.empty && board.getPiece(1,8) == Piece.bRook) { // white queen side castle
            if (!threatMap.contains(58) && !threatMap.contains(59)) {
                legalMoves.add(new Move(game, startX, startY, 3, 8));
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
                legalMoves.add(new Move(game, startX, startY, startX, y));
            } else if (board.getPiece(startX, y) < 0 && whiteToPlay || board.getPiece(startX, y) > 0 && !whiteToPlay) {
                legalMoves.add(new Move(game, startX, startY, startX, y));
                break;
            } else {
                break;
            }
        }
    }

    private void generateSouthMoves(int startX, int startY) {
        for (int y = startY - 1; y >= 1; y--) {
            if (board.getPiece(startX, y) == 0) {
                legalMoves.add(new Move(game, startX, startY, startX, y));
            } else if (board.getPiece(startX, y) < 0 && whiteToPlay || board.getPiece(startX, y) > 0 && !whiteToPlay) {
                legalMoves.add(new Move(game, startX, startY, startX, y));
                break;
            } else {
                break;
            }
        }
    }

    private void generateWestMoves(int startX, int startY) {
        for (int x = startX - 1; x >= 1; x--) {
            if (board.getPiece(x, startY) == 0) {
                legalMoves.add(new Move(game, startX, startY, x, startY));
            } else if (board.getPiece(x, startY) < 0 && whiteToPlay || board.getPiece(x, startY) > 0 && !whiteToPlay) {
                legalMoves.add(new Move(game, startX, startY, x, startY));
                break;
            }  else {
                break;
            }
        }
    }

    private void generateEastMoves(int startX, int startY) {
        for (int x = startX + 1; x <= 8; x++) {
            if (board.getPiece(x, startY) == 0) {
                legalMoves.add(new Move(game, startX, startY, x, startY));
            } else if (board.getPiece(x, startY) < 0 && whiteToPlay || board.getPiece(x, startY) > 0 && !whiteToPlay) {
                legalMoves.add(new Move(game, startX, startY, x, startY));
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
                legalMoves.add(new Move(game, startX, startY, x, y));
            } else if (board.getPiece(x, y) < 0 && whiteToPlay || board.getPiece(x,y) > 0 && !whiteToPlay) {
                legalMoves.add(new Move(game, startX, startY, x, y));
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
                legalMoves.add(new Move(game, startX, startY, x, y));
            } else if (board.getPiece(x, y) < 0 && whiteToPlay || board.getPiece(x,y) > 0 && !whiteToPlay) {
                legalMoves.add(new Move(game, startX, startY, x, y));
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
                legalMoves.add(new Move(game, startX, startY, x, y));
            } else if (board.getPiece(x, y) < 0 && whiteToPlay || board.getPiece(x,y) > 0 && !whiteToPlay) {
                legalMoves.add(new Move(game, startX, startY, x, y));
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
                legalMoves.add(new Move(game, startX, startY, x, y));
            } else if (board.getPiece(x, y) < 0 && whiteToPlay || board.getPiece(x,y) > 0 && !whiteToPlay) {
                legalMoves.add(new Move(game, startX, startY, x, y));
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

    public List<Move> getLegalMoves() {
        return legalMoves;
    }
}
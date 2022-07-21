package model;

import enumerations.CheckStatus;
import model.generation.MoveGenerator;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FenUtility {
    public static final String START_POS_FEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR";
    public static final String START_GAME_FEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    private final Map<Character, Integer> pieceType;
    private final Map<Integer, Character> pieceChar;

    /**
     * Instantiate a new FenUtility object and set up piece hash map
     */
    public FenUtility() {
        pieceType = new HashMap<>();
        pieceChar = new HashMap<>();

        pieceType.put('P', Piece.wPawn);
        pieceType.put('N', Piece.wKnight);
        pieceType.put('B', Piece.wBishop);
        pieceType.put('R', Piece.wRook);
        pieceType.put('Q', Piece.wQueen);
        pieceType.put('K', Piece.wKing);

        pieceType.put('p', Piece.bPawn);
        pieceType.put('n', Piece.bKnight);
        pieceType.put('b', Piece.bBishop);
        pieceType.put('r', Piece.bRook);
        pieceType.put('q', Piece.bQueen);
        pieceType.put('k', Piece.bKing);

        pieceChar.put(Piece.wPawn, 'P');
        pieceChar.put(Piece.wKnight, 'N');
        pieceChar.put(Piece.wBishop, 'B');
        pieceChar.put(Piece.wRook, 'R');
        pieceChar.put(Piece.wQueen, 'Q');
        pieceChar.put(Piece.wKing, 'K');

        pieceChar.put(Piece.bPawn, 'p');
        pieceChar.put(Piece.bKnight, 'n');
        pieceChar.put(Piece.bBishop, 'b');
        pieceChar.put(Piece.bRook, 'r');
        pieceChar.put(Piece.bQueen, 'q');
        pieceChar.put(Piece.bKing, 'k');
    }

    /**
     * Set the given game to the given fen string game
     * Requires a valid FEN string
     *
     * @param game current chess game
     * @param fen desired position in fen notation
     */
    public void loadGameFromFEN(Game game, String fen) {
        String[] splitFEN = fen.split(" ");

        int[] newBoard = new int[64];

        int x = 1;
        int y = 8;

        for (char c: splitFEN[0].toCharArray()) {
            if (c == '/') {
                x = 1;
                y--;
            } else if (Character.isDigit(c)) {
                x += Character.getNumericValue(c);
            } else {
                newBoard[Board.getSquareIndex(x,y)] = pieceType.get(c);
                x++;
            }
        }

        game.getBoard().setBoard(newBoard);

        String currentTurn = splitFEN[1];
        if (Objects.equals(currentTurn, "w")) {
            game.setCurrentTurn(game.getPlayers()[Game.WHITE_PLAYER_INDEX]);
        } else if (Objects.equals(currentTurn, "b")) {
            game.setCurrentTurn(game.getPlayers()[Game.BLACK_PLAYER_INDEX]);

        }

        String castlingAvailability = splitFEN[2];
        game.setWhiteKingSideCastling(false);
        game.setWhiteQueenSideCastling(false);
        game.setBlackKingSideCastling(false);
        game.setBlackQueenSideCastling(false);
        for (char c: castlingAvailability.toCharArray()) {
            switch (c) {
                case 'K':
                    game.setWhiteKingSideCastling(true);
                    break;
                case 'Q':
                    game.setWhiteQueenSideCastling(true);
                    break;
                case 'k':
                    game.setBlackKingSideCastling(true);
                    break;
                case 'q':
                    game.setBlackQueenSideCastling(true);
                    break;
            }
        }

        String enPassantMove = splitFEN[3];
        if (!Objects.equals(enPassantMove, "-")) {
            int possibleEnPassantEndSquare = Board.getSquareIndexFromString(enPassantMove);
            int xEnPassant = Board.getSquareCoordinates(possibleEnPassantEndSquare).x;
            int yEnPassant = Board.getSquareCoordinates(possibleEnPassantEndSquare).y;

            if (yEnPassant == 3) {
                Move blackPawnMove = new Move(game.getBoard(), xEnPassant, yEnPassant - 1, xEnPassant, yEnPassant + 1);
                blackPawnMove.setMovedPiece(Piece.wPawn);
                blackPawnMove.setCapturedPiece(0);
                game.getBoard().getMoveList().add(blackPawnMove);
            } else if (yEnPassant == 6) {
                Move whitePawnMove = new Move(game.getBoard(),  xEnPassant, yEnPassant + 1, xEnPassant, yEnPassant - 1);
                whitePawnMove.setMovedPiece(Piece.bPawn);
                whitePawnMove.setCapturedPiece(0);
                game.getBoard().getMoveList().add(whitePawnMove);
            }
        }

        MoveGenerator moveGenerator = new MoveGenerator();
        moveGenerator.generateLegalMoves(game, game.getCurrentTurn());
        if (moveGenerator.inCheck()) {
            if (game.getCurrentTurn().isWhite()) {
                game.setCheckStatus(CheckStatus.WHITE_IN_CHECK);
            } else {
                game.setCheckStatus(CheckStatus.BLACK_IN_CHECK);
            }
        }

        // TODO: implement for rest of the fen string
    }

    /**
     * Return the FEN string of the position from the given board
     */
    public String getFENFromGame(Game game) {
        return ""; // stub
    }

    /**
     * Set the given board to have the same position as the given FEN string position
     * Requires a valid position FEN string (only the position part of a normal FEN string)
     */
    public void loadPositionFromFEN(Board board, String fen) {
        int[] newBoard = new int[64];

        int x = 1;
        int y = 8;

        for (char c: fen.toCharArray()) {
            if (c == '/') {
                x = 1;
                y--;
            } else if (Character.isDigit(c)) {
                x += Character.getNumericValue(c);
            } else {
                newBoard[Board.getSquareIndex(x,y)] = pieceType.get(c);
                x++;
            }
        }

        board.setBoard(newBoard);
    }
}
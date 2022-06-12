package model;

import java.util.HashMap;
import java.util.Map;

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
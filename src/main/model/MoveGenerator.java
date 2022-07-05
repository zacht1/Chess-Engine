package model;

import java.util.ArrayList;
import java.util.List;

// Represents a move generator which generates only legal chess moves
public class MoveGenerator {
    private Game game;
    private Board board;
    private boolean whiteToPlay;
    private ThreatMapGenerator threatMapGenerator;
    private PseudoLegalMoveGenerator pseudoLegalMoveGenerator;

    private List<Move> legalMoves;
    private List<Integer> threatMap;

    /**
     * Generate all legal moves for the given player in the given game board position
     */
    public List<Move> generateMoves(Game game, Player player) {
        init(game, player);
        return pseudoLegalMoveGenerator.generatePseudoLegalMoves(game, player);
    }

    /**
     * Initialize this MoveGenerator object
     */
    private void init(Game game, Player player) {
        this.game = game;
        this.board = game.getBoard();
        this.whiteToPlay = player.isWhite();
        this.legalMoves = new ArrayList<>();
        this.threatMapGenerator = new ThreatMapGenerator();
        this.pseudoLegalMoveGenerator = new PseudoLegalMoveGenerator();

        if (whiteToPlay) {
            threatMapGenerator.generateBlackThreatMap(game);
        } else {
            threatMapGenerator.generateWhiteThreatMap(game);
        }
    }
}

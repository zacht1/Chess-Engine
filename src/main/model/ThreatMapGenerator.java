package model;

import java.util.ArrayList;
import java.util.List;

public class ThreatMapGenerator {
    private Game game;
    private Board board;
    private List<Integer> threatMap;

    /**
     * Generate black's threat map, e.g. all squares that are either attacked or defended by at least one black piece
     *
     * @return Integer list of indexes of each square in the threat map
     */
    public List<Integer> generateWhiteThreatMap(Game game) {
        return new ArrayList<>(); // stub
    }

    /**
     * Generate white's threat map, e.g. all squares that are either attacked or defended by at least one white piece
     *
     * @return Integer list of indexes of each square in the threat map
     */
    public List<Integer> generateBlackThreatMap(Game game) {
        return new ArrayList<>(); // stub
    }
}
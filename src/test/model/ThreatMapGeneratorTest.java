package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ThreatMapGeneratorTest {
    private Game testGame;
    private ThreatMapGenerator testThreatMapGenerator;

    @BeforeEach
    public void init() {
        testGame = new Game();
        testThreatMapGenerator = new ThreatMapGenerator();
    }

    @Test
    public void whiteThreatMapGeneratorTest() {
        testGame.setBoardFEN("rnbqkbnr/pppppppp/8/8/1P1Q4/5N2/PBPPPPPP/RN2KB1R w KQkq - 0 1");

        Set<Integer> whiteThreatMap = testThreatMapGenerator.generateWhiteThreatMap(testGame);

        assertEquals(40, whiteThreatMap.size());
    }

    @Test
    public void blackThreatMapGeneratorTest() {
        testGame.setBoardFEN("rnb1kbnr/pppppppp/8/2q5/8/8/PPPPPPPP/RNBQKBNR b KQkq - 0 1");

        Set<Integer> blackThreatMap = testThreatMapGenerator.generateBlackThreatMap(testGame);

        List<String> coordinateList = new ArrayList<>();
        for (Integer index: blackThreatMap) {
            coordinateList.add(Board.getChessNotation(index));
        }
        System.out.println(coordinateList);

        assertEquals(35, blackThreatMap.size());
    }
}
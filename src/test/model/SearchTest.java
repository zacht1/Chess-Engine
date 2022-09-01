package model;

import model.search.Search;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SearchTest {
    private Search testSearch;
    private Game testGame;

    @BeforeEach
    public void init() {
        testSearch = new Search();
        testGame = new Game();
    }

    @Test
    public void searchTest() {
        //testGame.setBoardFEN("k7/8/6pq/6P1/8/8/8/K7 w - - 1 1");
        System.out.println(testSearch.search(testGame, 1).formatMove());
    }

}

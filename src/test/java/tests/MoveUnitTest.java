package tests;

import model.Board;
import model.Tile;
import model.Unit;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

public class MoveUnitTest {
    Board board;

    @Before
    public void initBoard() {
        board = new Board(true);
    }

    @Test
    public void moveUnit() {
        Unit currentUnit = board.getUnit(0);
        Tile oldTile = currentUnit.getTile();
        Tile newTile = board.getTile(currentUnit.getCol() + 1, currentUnit.getRow());

        currentUnit.moveTo(newTile);

        assertNull(oldTile.getUnit());
        assertSame(currentUnit, newTile.getUnit());
        assertSame(newTile, currentUnit.getTile());
    }
}

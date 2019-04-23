package tests;

import model.Board;
import model.Cultist;
import model.Tile;
import model.Unit;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class BulletPathTest {
    Board board;

    @Before
    public void initBoard() {
        board = new Board(true);
    }


    @Test
    public void ifShootNoObstacle_thenHitTarget() {
        board.allUnits.add(new Cultist(2, board.getTile(2, 3), 0));
        board.allUnits.add(new Cultist(3, board.getTile(6, 5), 1));
        Unit unit = board.getUnit(2);
        Unit target = board.getUnit(3);

        Tile hitTile = board.checkBulletPath(unit.getTile(), target.getTile());

        assertEquals(target.getTile(), hitTile);
    }

    @Test
    public void ifShootNeutralOnPath_thenHitNeutral() {
        board.allUnits.add(new Cultist(2, board.getTile(3, 2), 0));
        board.allUnits.add(new Cultist(8, board.getTile(8, 2), 1));
        board.allUnits.add(new Cultist(12, board.getTile(6, 2), 2));
        board.getUnit(1).moveTo(board.getTile(9, 2));
        Unit unit = board.getUnit(2);
        Unit target = board.getUnit(1);

        Tile hitTile = board.checkBulletPath(unit.getTile(), target.getTile());

        assertEquals(board.getTile(6, 2), hitTile);
    }

    @Test
    public void ifShootNeutralOnPathOtherDirection_thenHitNeutral() {
        board.allUnits.add(new Cultist(2, board.getTile(3, 2), 0));
        board.allUnits.add(new Cultist(3, board.getTile(8, 2), 1));
        board.allUnits.add(new Cultist(4, board.getTile(6, 2), 2));
        board.getUnit(0).moveTo(board.getTile(2, 2));
        board.getUnit(1).moveTo(board.getTile(9, 2));
        Unit unit = board.getUnit(3);
        Unit target = board.getUnit(0);

        Tile hitTile = board.checkBulletPath(unit.getTile(), target.getTile());

        assertEquals(board.getTile(6, 2), hitTile);
    }

    @Test
    public void ifShootNeutralOnDiagonalPath_thenHitNeutral() {
        board.allUnits.add(new Cultist(2, board.getTile(1, 1), 0));
        board.allUnits.add(new Cultist(3, board.getTile(4, 2), 1));
        board.allUnits.add(new Cultist(4, board.getTile(3, 2), 2));
        board.getUnit(0).moveTo(board.getTile(0, 1));
        board.getUnit(1).moveTo(board.getTile(5, 3));
        Unit unit = board.getUnit(2);
        Unit target = board.getUnit(1);

        Tile hitTile = board.checkBulletPath(unit.getTile(), target.getTile());

        assertEquals(board.getTile(3, 2), hitTile);
    }

    @Test
    public void ifShootNeutralOnDiagonalPathReverse_thenHitNeutral() {
        board.allUnits.add(new Cultist(2, board.getTile(2, 1), 0));
        board.allUnits.add(new Cultist(3, board.getTile(5, 3), 1));
        board.allUnits.add(new Cultist(4, board.getTile(3, 2), 2));
        board.getUnit(0).moveTo(board.getTile(1, 1));
        board.getUnit(1).moveTo(board.getTile(4, 2));
        Unit unit = board.getUnit(3);
        Unit target = board.getUnit(0);

        Tile hitTile = board.checkBulletPath(unit.getTile(), target.getTile());

        assertEquals(board.getTile(4, 2), hitTile);
    }

}

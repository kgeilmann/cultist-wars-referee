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

    // TODO: fix tests
    @Test
    public void ifShootNoObstacle_thenHitTarget() {
        board.allUnits.add(new Cultist(2, board.getTile(2, 3), 0));
        board.allUnits.add(new Cultist(3, board.getTile(6, 5), 1));
        Unit unit = board.getUnit(2);
        Unit target = board.getUnit(3);

        Tile hitTile = board.checkBulletPath(unit.getTile(), target.getTile());

        assertEquals(target.getTile(), hitTile);
    }

//    @Test
//    public void ifShootOwnUnitOnPath_thenFriendlyFire() {
//        Unit currentUnit = board.getUnit(4);
//        Tile unitTile = board.getTile(0, 4);
//        currentUnit.moveTo(unitTile);
//        Unit targetUnit = board.getUnit(5);
//        Tile targetTile = board.getTile(4, 6);
//        targetUnit.moveTo(targetTile);
//        Tile friendTile = board.getTile(3, 5);
//        Unit friendUnit = board.getUnit(2);
//        friendUnit.moveTo(friendTile);
//
//        Tile hitTile = board.checkBulletPath(unitTile, targetTile);
//
//        assertEquals(friendTile, hitTile);
//    }
//
//    @Test
//    public void ifShootObstacleOnPath_thenFriendlyFire() {
//        Unit currentUnit = board.getUnit(4);
//        Tile unitTile = board.getTile(0, 4);
//        currentUnit.moveTo(unitTile);
//        Unit targetUnit = board.getUnit(5);
//        Tile targetTile = board.getTile(4, 6);
//        targetUnit.moveTo(targetTile);
//        Tile obstacleTile = board.getTile(3, 5);
//        obstacleTile.setType(Tile.Type.OBSTACLE);
//
//        Tile hitTile = board.checkBulletPath(unitTile, targetTile);
//
//        assertEquals(obstacleTile, hitTile);
//    }
}

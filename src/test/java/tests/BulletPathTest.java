package tests;

import model.Board;
import org.junit.Before;

public class BulletPathTest {
    Board board;

    @Before
    public void initBoard() {
        board = new Board();
    }

    // TODO: fix tests
//    @Test
//    public void ifShootNoObstacle_thenHitTarget() {
//        Unit currentUnit = board.getUnit(4);
//        Tile unitTile = board.getTile(0, 4);
//        currentUnit.setTile(unitTile);
//        Unit targetUnit = board.getUnit(5);
//        Tile targetTile = board.getTile(4, 6);
//        targetUnit.setTile(targetTile);
//
//        Tile hitTile = board.checkBulletPath(unitTile, targetTile);
//
//        assertEquals(targetTile, hitTile);
//    }

//    @Test
//    public void ifShootOwnUnitOnPath_thenFriendlyFire() {
//        Unit currentUnit = board.getUnit(4);
//        Tile unitTile = board.getTile(0, 4);
//        currentUnit.setTile(unitTile);
//        Unit targetUnit = board.getUnit(5);
//        Tile targetTile = board.getTile(4, 6);
//        targetUnit.setTile(targetTile);
//        Tile friendTile = board.getTile(3, 5);
//        Unit friendUnit = board.getUnit(2);
//        friendUnit.setTile(friendTile);
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
//        currentUnit.setTile(unitTile);
//        Unit targetUnit = board.getUnit(5);
//        Tile targetTile = board.getTile(4, 6);
//        targetUnit.setTile(targetTile);
//        Tile obstacleTile = board.getTile(3, 5);
//        obstacleTile.setType(Tile.Type.OBSTACLE);
//
//        Tile hitTile = board.checkBulletPath(unitTile, targetTile);
//
//        assertEquals(obstacleTile, hitTile);
//    }
}

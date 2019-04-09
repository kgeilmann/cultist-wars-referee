package tests;

import model.Action;
import model.Board;
import model.Cultist;
import model.Tile;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class getFinalActionTest {
    Board board;

    @Before
    public void initBoard() {
        board = new Board(true);
    }

    @Test
    public void ifWaiting_thenJustWait() {
        Action action = new Action(Action.Command.WAIT);

        action = board.getFinalAction(action);

        assertEquals(new Action(Action.Command.WAIT), action);
    }

    @Test
    public void ifMoveIsFreeAdjacentCell_thenJustMoveToCell() {
        Action action = Action.parseAction("0 MOVE 0 2".split(" "));

        action = board.getFinalAction(action);

        assertEquals(Action.parseAction("0 MOVE 0 2".split(" ")), action);
    }

    @Test
    public void ifMoveAdjacentCellWithObstacle_thenJustWait() {
        Action action = Action.parseAction("0 MOVE 0 2".split(" "));
        board.tiles[0][2].setType(Tile.Type.OBSTACLE);

        action = board.getFinalAction(action);

        assertEquals(Action.parseAction("0 WAIT".split(" ")), action);
    }

    @Test
    public void ifMoveToOccupiedAdjacentCell_thenJustWait() {
        Action action = Action.parseAction("0 MOVE 0 2".split(" "));
        board.allUnits.add(new Cultist(2, board.tiles[0][2], 0));


        action = board.getFinalAction(action);

        assertEquals(Action.parseAction("0 WAIT".split(" ")), action);
    }

    @Test
    public void ifMoveIsNotAdjacentCell_thenFindPath() {
        for (int row = 0; row < Board.HEIGHT -1; row++) {
            board.tiles[1][row].setType(Tile.Type.OBSTACLE);
        }
        System.err.println(board);
        Action action = Action.parseAction("0 MOVE 2 0".split(" "));

        action = board.getFinalAction(action);

        assertEquals(Action.parseAction("0 MOVE 0 4".split(" ")), action);
    }

    @Test
    public void ifMoveIsNotAdjacentCellButPathIsBlocked_thenMoveInDirection() {
        for (int row = 0; row < Board.HEIGHT -1; row++) {
            board.tiles[1][row].setType(Tile.Type.OBSTACLE);
        }
        System.err.println(board);
        Action action = Action.parseAction("0 MOVE 2 0".split(" "));
        board.allUnits.add(new Cultist(2, board.tiles[1][Board.HEIGHT - 1], 0));

        action = board.getFinalAction(action);

        assertEquals(Action.parseAction("0 MOVE 0 4".split(" ")), action);
    }

    @Test
    public void ifMoveIsNotAdjacentCellButPathIsBlockedAndCantMoveCloser_thenWait() {
        for (int row = 0; row < Board.HEIGHT -1; row++) {
            board.tiles[1][row].setType(Tile.Type.OBSTACLE);
        }
        System.err.println(board);
        Action action = Action.parseAction("0 MOVE 2 0".split(" "));
        board.allUnits.add(new Cultist(2, board.tiles[1][Board.HEIGHT - 1], 0));
        board.getUnit(0).setTile(board.getTile(0, Board.HEIGHT - 1));

        action = board.getFinalAction(action);

        assertEquals(Action.parseAction("0 WAIT".split(" ")), action);
    }

    @Test
    public void ifMoveIsNotAdjacentCellButPathIsBlockedByObstacle_thenMoveInDirection() {
        for (int row = 0; row < Board.HEIGHT; row++) {
            board.tiles[1][row].setType(Tile.Type.OBSTACLE);
        }
        System.err.println(board);
        Action action = Action.parseAction("0 MOVE 2 0".split(" "));

        action = board.getFinalAction(action);

        assertEquals(Action.parseAction("0 WAIT".split(" ")), action);
    }

    public void ifInRange_thenShoot() {

    }

    @Test
    public void ifNotInRange_thenMoveTowardsTarget() {
        board.allUnits.add(new Cultist(2, board.tiles[0][0], 0));
        Action action = Action.parseAction("2 SHOOT 1".split(" "));

        action = board.getFinalAction(action);

        assertEquals(Action.parseAction("2 MOVE 1 0".split(" ")), action);
    }

    // TODO: Convert tests
    @Test
    public void ifNeutralOnAdjacent_thenConvert() {
        board.allUnits.add(new Cultist(2, board.tiles[1][3], 2));
        Action action = Action.parseAction("0 CONVERT 2".split(" "));

        action = board.getFinalAction(action);

        assertEquals(Action.parseAction("0 CONVERT 2".split(" ")), action);
    }

}

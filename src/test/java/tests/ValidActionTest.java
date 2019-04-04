package tests;

import model.Action;
import model.Board;
import model.E;
import model.Unit;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ValidActionTest {
    // TODO: fix tests

    Board board;

    @Before
    public void initBoard() {
        board = new Board();
    }

    @Test
    public void ifSurroundedByEmptyTiles_includeMoveToAllDirections() {
        Unit currentUnit = board.getUnit(0);

        List<Action> validActions = board.getValidActions(0);

        assertTrue(validActions.contains(new Action(0,"MOVE", "UP")));
        assertTrue(validActions.contains(new Action(0, "MOVE", "RIGHT")));
        assertTrue(validActions.contains(new Action(0,"MOVE", "DOWN")));
        assertTrue(validActions.contains(new Action(0,"MOVE", "LEFT")));
        assertTrue(validActions.contains(new Action(0,"WAIT", "0")));
    }

    @Test
    public void ifNextToWall_dontIncludeMoveToThatDirection() {
        Unit currentUnit = board.getUnit(1);
        currentUnit.setTile(board.getTile(Board.WIDTH - 1, currentUnit.getRow()));

        List<Action> validActions = board.getValidActions(1);

        assertTrue(validActions.contains(new Action(1,"MOVE", "UP")));
        assertFalse(validActions.contains(new Action(1,"MOVE", "RIGHT")));
        assertTrue(validActions.contains(new Action(1,"MOVE", "DOWN")));
        assertTrue(validActions.contains(new Action(1,"MOVE", "LEFT")));
        assertTrue(validActions.contains(new Action(1,"WAIT", "0")));
    }

    @Test
    public void ifNextToObstacle_dontIncludeMoveToThatDirection() {
        Unit currentUnit = board.getUnit(2);
        currentUnit.setTile(board.getTile(7, 2));

        List<Action> validActions = board.getValidActions(0);

        assertTrue(validActions.contains(new Action(2,"MOVE", "UP")));
        assertTrue(validActions.contains(new Action(2,"MOVE", "RIGHT")));
        assertFalse(validActions.contains(new Action(2,"MOVE", "DOWN")));
        assertTrue(validActions.contains(new Action(2,"MOVE", "LEFT")));
        assertTrue(validActions.contains(new Action(2,"WAIT", "0")));
    }

    @Test
    public void ifInCorner_dontIncludeMoveToThoseDirections() {
        Unit currentUnit = board.getUnit(5);
        currentUnit.setTile(board.getTile(Board.WIDTH - 1, Board.HEIGHT - 1));

        List<Action> validActions = board.getValidActions(1);

        assertTrue(validActions.contains(new Action(5,"MOVE", "UP")));
        assertFalse(validActions.contains(new Action(5,"MOVE", "RIGHT")));
        assertFalse(validActions.contains(new Action(5,"MOVE", "DOWN")));
        assertTrue(validActions.contains(new Action(5,"MOVE", "LEFT")));
        assertTrue(validActions.contains(new Action(5,"WAIT", "0")));
    }

    @Test
    public void ifNextToAUnit_dontIncludeMoveToThoseDirections() {
        Unit currentUnit = board.getUnit(0);
        currentUnit.setTile(board.getTile(currentUnit.getCol(), 2));

        List<Action> validActions = board.getValidActions(0);

        assertTrue(validActions.contains(new Action(0,"MOVE", "UP")));
        assertTrue(validActions.contains(new Action(0,"MOVE", "RIGHT")));
        assertFalse(validActions.contains(new Action(0,"MOVE", "DOWN")));
        assertTrue(validActions.contains(new Action(0,"MOVE", "LEFT")));
        assertTrue(validActions.contains(new Action(0,"WAIT", "0")));
    }

    @Test
    public void ifNextToTwoUnits_dontIncludeMoveToThoseDirections() {
        Unit currentUnit = board.getUnit(1);
        currentUnit.setTile(board.getTile(currentUnit.getCol(), 4));

        List<Action> validActions = board.getValidActions(1);

        assertFalse(validActions.contains(new Action(1,"MOVE", "UP")));
        assertTrue(validActions.contains(new Action(1,"MOVE", "RIGHT")));
        assertFalse(validActions.contains(new Action(1,"MOVE", "DOWN")));
        assertTrue(validActions.contains(new Action(1,"MOVE", "LEFT")));
        assertTrue(validActions.contains(new Action(1,"WAIT", "0")));
    }

    @Test
    public void ifCompletelySurrounded_validActionsIncludeNoPossibleMoves() {
        Unit currentUnit = board.getUnit(2);
        currentUnit.setTile(board.getTile(1, 0));

        currentUnit = board.getUnit(4);
        currentUnit.setTile(board.getTile(0, 1));

        currentUnit = board.getUnit(0);
        currentUnit.setTile(board.getTile(0, 0));

        List<Action> validActions = board.getValidActions(1);

        assertFalse(validActions.contains(new Action(2,"MOVE", "UP")));
        assertFalse(validActions.contains(new Action(2,"MOVE", "RIGHT")));
        assertFalse(validActions.contains(new Action(2, "MOVE", "DOWN")));
        assertFalse(validActions.contains(new Action(2,"MOVE", "LEFT")));
        assertTrue(validActions.contains(new Action(2,"WAIT", "0")));
    }

    @Test
    public void ifNextToTileWithDeadEnemy_thenCanMoveThere() {
        Unit currentUnit = board.getUnit(2);
        currentUnit.setTile(board.getTile(1, 0));

        currentUnit = board.getUnit(4);
        currentUnit.setTile(board.getTile(0, 1));
        currentUnit.takeDamage(10);

        currentUnit = board.getUnit(0);
        currentUnit.setTile(board.getTile(0, 0));

        List<Action> validActions = board.getValidActions(1);

        assertFalse(validActions.contains(new Action(2,"MOVE", "UP")));
        assertFalse(validActions.contains(new Action(2,"MOVE", "RIGHT")));
        assertTrue(validActions.contains(new Action(2,"MOVE", "DOWN")));
        assertFalse(validActions.contains(new Action(2,"MOVE", "LEFT")));
        assertTrue(validActions.contains(new Action(2,"WAIT", "0")));
    }

    @Test
    public void ifNoEnemyInRange_thenShootIsNotPossible() {
        Unit currentUnit = board.getUnit(0);

        List<Action> validActions = board.getValidActions(0);

        assertFalse(validActions.contains(new Action(0, E.SHOOT, "1")));
    }

    @Test
    public void ifAnEnemyIsInRange_thenShootIsPossible() {
        Unit currentUnit = board.getUnit(0);
        currentUnit.setTile(board.getTile(6, 3));

        List<Action> validActions = board.getValidActions(0);

        assertTrue(validActions.contains(new Action(0, E.SHOOT, "1")));
        assertTrue(validActions.contains(new Action(0, E.SHOOT, "3")));
        assertTrue(validActions.contains(new Action(0, E.SHOOT, "5")));
    }

    @Test
    public void ifDeadEnemyIsInRange_thenCannotShootIt() {
        Unit currentUnit = board.getUnit(0);
        currentUnit.setTile(board.getTile(6, 3));
        Unit targetUnit = board.getUnit(3);
        targetUnit.takeDamage(10);

        List<Action> validActions = board.getValidActions(0);

        assertTrue(validActions.contains(new Action(0, E.SHOOT, "1")));
        assertFalse(validActions.contains(new Action(0, E.SHOOT, "3")));
        assertTrue(validActions.contains(new Action(0, E.SHOOT, "5")));
    }
}

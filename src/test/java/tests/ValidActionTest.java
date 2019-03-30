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
    Board board;

    @Before
    public void initBoard() {
        board = new Board();
    }

    @Test
    public void ifSurroundedByEmptyTiles_includeMoveToAllDirections() {
        Unit currentUnit = board.getUnit(0);

        List<Action> validActions = board.getValidActions(currentUnit);

        assertTrue(validActions.contains(new Action("MOVE", "UP")));
        assertTrue(validActions.contains(new Action("MOVE", "RIGHT")));
        assertTrue(validActions.contains(new Action("MOVE", "DOWN")));
        assertTrue(validActions.contains(new Action("MOVE", "LEFT")));
        assertTrue(validActions.contains(new Action("WAIT", "0")));
    }

    @Test
    public void ifNextToWall_dontIncludeMoveToThatDirection() {
        Unit currentUnit = board.getUnit(1);
        currentUnit.setCol(Board.WIDTH - 1);

        List<Action> validActions = board.getValidActions(currentUnit);

        assertTrue(validActions.contains(new Action("MOVE", "UP")));
        assertFalse(validActions.contains(new Action("MOVE", "RIGHT")));
        assertTrue(validActions.contains(new Action("MOVE", "DOWN")));
        assertTrue(validActions.contains(new Action("MOVE", "LEFT")));
        assertTrue(validActions.contains(new Action("WAIT", "0")));
    }

    @Test
    public void ifNextToObstacle_dontIncludeMoveToThatDirection() {
        Unit currentUnit = board.getUnit(2);
        currentUnit.setCol(7);
        currentUnit.setRow(2);

        List<Action> validActions = board.getValidActions(currentUnit);

        assertTrue(validActions.contains(new Action("MOVE", "UP")));
        assertTrue(validActions.contains(new Action("MOVE", "RIGHT")));
        assertFalse(validActions.contains(new Action("MOVE", "DOWN")));
        assertTrue(validActions.contains(new Action("MOVE", "LEFT")));
        assertTrue(validActions.contains(new Action("WAIT", "0")));
    }

    @Test
    public void ifInCorner_dontIncludeMoveToThoseDirections() {
        Unit currentUnit = board.getUnit(5);
        currentUnit.setCol(Board.WIDTH - 1);
        currentUnit.setRow(Board.HEIGHT - 1);

        List<Action> validActions = board.getValidActions(currentUnit);

        assertTrue(validActions.contains(new Action("MOVE", "UP")));
        assertFalse(validActions.contains(new Action("MOVE", "RIGHT")));
        assertFalse(validActions.contains(new Action("MOVE", "DOWN")));
        assertTrue(validActions.contains(new Action("MOVE", "LEFT")));
        assertTrue(validActions.contains(new Action("WAIT", "0")));
    }

    @Test
    public void ifNextToAUnit_dontIncludeMoveToThoseDirections() {
        Unit currentUnit = board.getUnit(0);
        currentUnit.setRow(2);

        List<Action> validActions = board.getValidActions(currentUnit);

        assertTrue(validActions.contains(new Action("MOVE", "UP")));
        assertTrue(validActions.contains(new Action("MOVE", "RIGHT")));
        assertFalse(validActions.contains(new Action("MOVE", "DOWN")));
        assertTrue(validActions.contains(new Action("MOVE", "LEFT")));
        assertTrue(validActions.contains(new Action("WAIT", "0")));
    }

    @Test
    public void ifNextToTwoUnits_dontIncludeMoveToThoseDirections() {
        Unit currentUnit = board.getUnit(1);
        currentUnit.setRow(4);

        List<Action> validActions = board.getValidActions(currentUnit);

        assertFalse(validActions.contains(new Action("MOVE", "UP")));
        assertTrue(validActions.contains(new Action("MOVE", "RIGHT")));
        assertFalse(validActions.contains(new Action("MOVE", "DOWN")));
        assertTrue(validActions.contains(new Action("MOVE", "LEFT")));
        assertTrue(validActions.contains(new Action("WAIT", "0")));
    }

    @Test
    public void ifCompletelySurrounded_validActionsIncludeNoPossibleMoves() {
        Unit currentUnit = board.getUnit(2);
        currentUnit.setCol(1);
        currentUnit.setRow(0);

        currentUnit = board.getUnit(4);
        currentUnit.setCol(0);
        currentUnit.setRow(1);

        currentUnit = board.getUnit(0);
        currentUnit.setCol(0);
        currentUnit.setRow(0);

        List<Action> validActions = board.getValidActions(currentUnit);

        assertFalse(validActions.contains(new Action("MOVE", "UP")));
        assertFalse(validActions.contains(new Action("MOVE", "RIGHT")));
        assertFalse(validActions.contains(new Action("MOVE", "DOWN")));
        assertFalse(validActions.contains(new Action("MOVE", "LEFT")));
        assertTrue(validActions.contains(new Action("WAIT", "0")));
    }

    @Test
    public void ifNextToTileWithDeadEnemy_thenCanMoveThere() {
        Unit currentUnit = board.getUnit(2);
        currentUnit.setCol(1);
        currentUnit.setRow(0);

        currentUnit = board.getUnit(4);
        currentUnit.setCol(0);
        currentUnit.setRow(1);
        currentUnit.takeDamage(10.0);

        currentUnit = board.getUnit(0);
        currentUnit.setCol(0);
        currentUnit.setRow(0);

        List<Action> validActions = board.getValidActions(currentUnit);

        assertFalse(validActions.contains(new Action("MOVE", "UP")));
        assertFalse(validActions.contains(new Action("MOVE", "RIGHT")));
        assertTrue(validActions.contains(new Action("MOVE", "DOWN")));
        assertFalse(validActions.contains(new Action("MOVE", "LEFT")));
        assertTrue(validActions.contains(new Action("WAIT", "0")));
    }

    @Test
    public void ifNoEnemyInRange_thenShootIsNotPossible() {
        Unit currentUnit = board.getUnit(0);

        List<Action> validActions = board.getValidActions(currentUnit);

        assertFalse(validActions.contains(new Action(E.SHOOT, "1")));
    }

    @Test
    public void ifAnEnemyIsInRange_thenShootIsPossible() {
        Unit currentUnit = board.getUnit(0);
        currentUnit.setCol(6);
        currentUnit.setRow(3);

        List<Action> validActions = board.getValidActions(currentUnit);

        assertTrue(validActions.contains(new Action(E.SHOOT, "1")));
        assertTrue(validActions.contains(new Action(E.SHOOT, "3")));
        assertTrue(validActions.contains(new Action(E.SHOOT, "5")));
    }

    @Test
    public void ifDeadEnemyIsInRange_thenCannotShootIt() {
        Unit currentUnit = board.getUnit(0);
        currentUnit.setCol(6);
        currentUnit.setRow(3);
        Unit targetUnit = board.getUnit(3);
        targetUnit.takeDamage(10.0);

        List<Action> validActions = board.getValidActions(currentUnit);

        assertTrue(validActions.contains(new Action(E.SHOOT, "1")));
        assertFalse(validActions.contains(new Action(E.SHOOT, "3")));
        assertTrue(validActions.contains(new Action(E.SHOOT, "5")));
    }
}

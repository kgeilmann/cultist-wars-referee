package tests;

import model.Action;
import model.Board;
import model.Cultist;
import org.junit.Before;
import org.junit.Test;

public class ValidateActionTest {
    Board board;

    @Before
    public void init() {
        board = new Board(true);
        board.allUnits.add(new Cultist(2, board.tiles[0][0], 0));
    }

    @Test
    public void validAction() {
        Action action = Action.parseAction("0 MOVE 0 1".split(" "));

        board.checkActionValidity(action, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void ifUnitIdIsOutOfRange_invalidAction() {
        Action action = Action.parseAction("15 MOVE 0 1".split(" "));

        board.checkActionValidity(action, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void ifMoveCoordinateIsOutOfRange_invalidAction() {
        Action action = Action.parseAction("1 MOVE 0 -1".split(" "));

        board.checkActionValidity(action, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void ifUnitIsNotPlayersUnit_invalidAction() {
        Action action = Action.parseAction("0 MOVE 0 1".split(" "));

        board.checkActionValidity(action, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void ifShootTargetIdIsOutOfRange_invalidAction() {
        Action action = Action.parseAction("2 SHOOT 15".split(" "));

        board.checkActionValidity(action, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void ifShootTargetIsShootingUnit_invalidAction() {
        Action action = Action.parseAction("2 SHOOT 2".split(" "));

        board.checkActionValidity(action, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void ifUnitIsCultLeaderButAsketToShoot_invalidAction() {
        Action action = Action.parseAction("0 SHOOT 2".split(" "));

        board.checkActionValidity(action, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void ifUnitIsCultistButAskedToConvert_invalidAction() {
        Action action = Action.parseAction("2 COVNVERT 3".split(" "));

        board.checkActionValidity(action, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void ifConvertTargetIdIsOutOfRange_invalidAction() {
        Action action = Action.parseAction("0 COVNVERT -1".split(" "));

        board.checkActionValidity(action, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void ifConvertTargetIsOwnUnit_invalidAction() {
        Action action = Action.parseAction("0 COVNVERT 2".split(" "));

        board.checkActionValidity(action, 0);
    }
}

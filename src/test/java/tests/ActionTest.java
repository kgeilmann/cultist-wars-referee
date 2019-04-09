package tests;

import model.Action;
import model.MoveAction;
import model.SpecialAction;
import org.junit.Test;

import static org.junit.Assert.*;

public class ActionTest {
    @Test
    public void properActionCreated() {
        Action action = new MoveAction(0, Action.Command.MOVE, 1, 0);
        System.err.println(action);
    }

    @Test(expected = IllegalArgumentException.class)
    public void improperActionCreated() {
        Action action = Action.parseAction("0 BLA 1".split(" "));
    }

    @Test
    public void equalActions() {
        Action action1 = new SpecialAction(0, Action.Command.SHOOT, 0);
        Action action2 = new SpecialAction(0, Action.Command.SHOOT, 0);

        assertEquals(action1, action2);
    }

    @Test
    public void notEqualActions() {
        Action action1 = new SpecialAction(0, Action.Command.CONVERT, 0);
        Action action2 = new SpecialAction(0, Action.Command.SHOOT, 0);

        assertNotEquals(action1, action2);
    }
}

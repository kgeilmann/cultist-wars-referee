package tests;

import model.Action;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class ActionTest {
    @Test
    public void properActionCreated() {
        Action action = new Action("MOVE", "UP");
        System.err.println(action);
    }

    @Test(expected = IllegalArgumentException.class)
    public void improperActionCreated() {
        Action action = new Action("BLA", "1");
    }

    @Test
    public void equalActions() {
        Action action1 = new Action("SHOOT", "0");
        Action action2 = new Action("SHOOT", "0");

        assertEquals(action1, action2);
    }

    @Test
    public void notEqualActions() {
        Action action1 = new Action("SHOOT", "0");
        Action action2 = new Action("MOVE", "UP");

        assertFalse(action1.equals(action2));
    }

}

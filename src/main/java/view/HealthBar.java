package view;

import com.codingame.gameengine.module.entities.Rectangle;

public class HealthBar {
    public static final int HEALTH_BAR_LENGTH = 50;

    private final Rectangle greenRectangle;
    private final Rectangle redRectangle;

    public HealthBar(Rectangle greenRectangle, Rectangle redRectangle) {
        this.greenRectangle = greenRectangle;
        this.redRectangle = redRectangle;
    }

    public void update(int hp) {
        greenRectangle.setWidth(hp * 5);
    }
}

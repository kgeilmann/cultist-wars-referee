package view;

import com.codingame.game.Player;
import com.codingame.gameengine.core.MultiplayerGameManager;
import com.codingame.gameengine.module.entities.*;
import model.*;

import java.util.ArrayList;
import java.util.List;

public class ViewController {
    public static int ENTITY_SIZE = 90;

    public static int BOARD_OFFSET_X = (1920 - Board.WIDTH * ENTITY_SIZE) / 2;
    public static int BOARD_OFFSET_Y = (1080 - Board.HEIGHT * ENTITY_SIZE) / 2;
    public static int UNIT_ELEVATION = -20;

    public static final int TILE_Z = -3;
    public static final int SHADOW_Z = -2;
    public static final int SELECTED_SHADOW_Z = -1;
    public static final int UNIT_Z = 0;

    private BufferedGroup tileGroup;
    private GraphicEntityModule graphicEntityModule;
    private MultiplayerGameManager<Player> gameManager;
    private Board board;
    private List<Group> unitSpriteGroups;


    public ViewController(GraphicEntityModule graphicEntityModule, MultiplayerGameManager<Player> gameManager, Board board) {
        this.graphicEntityModule = graphicEntityModule;
        this.gameManager = gameManager;
        this.board = board;
    }

    public void createTilesView() {
        // TODO: switch to spritesheet

        tileGroup = graphicEntityModule.createBufferedGroup()
                .setX(BOARD_OFFSET_X - ENTITY_SIZE)
                .setY(BOARD_OFFSET_Y - ENTITY_SIZE).setZIndex(TILE_Z);

        int obstacleCounter = 0;
        for (int x = 1; x < Board.WIDTH + 1; x++) {
            for (int y = 1; y < Board.HEIGHT + 1; y++) {
                if (board.getTile(x - 1, y - 1).getType().equals(Tile.Type.OBSTACLE)) {
                    tileGroup.add(graphicEntityModule.createSprite()
                            .setImage("obstacle_" + E.random.nextInt(6) + ".png")
                            .setX(x * ENTITY_SIZE)
                            .setY(y * ENTITY_SIZE));
                } else {
                    tileGroup.add(graphicEntityModule.createSprite()
                            .setImage("floor_1.png")
                            .setX(x * ENTITY_SIZE)
                            .setY(y * ENTITY_SIZE));
                }
            }
        }

        // corners
        tileGroup.add(graphicEntityModule.createSprite()
                .setImage("wall_upper_left_corner.png")
                .setX(0)
                .setY(0));

        tileGroup.add(graphicEntityModule.createSprite()
                .setImage("wall_bottom_left_corner.png")
                .setX(0)
                .setY((Board.HEIGHT + 1) * ENTITY_SIZE));

        tileGroup.add(graphicEntityModule.createSprite()
                .setImage("wall_upper_right_corner.png")
                .setX((Board.WIDTH + 1) * ENTITY_SIZE)
                .setY(0));

        tileGroup.add(graphicEntityModule.createSprite()
                .setImage("wall_bottom_right_corner.png")
                .setX((Board.WIDTH + 1) * ENTITY_SIZE)
                .setY((Board.HEIGHT + 1) * ENTITY_SIZE));

        // walls
        for (int x = 1; x < Board.WIDTH + 1; x++) {
            tileGroup.add(graphicEntityModule.createSprite()
                    .setImage("wall_horizontal.png")
                    .setX(x * ENTITY_SIZE)
                    .setY(0));
            tileGroup.add(graphicEntityModule.createSprite()
                    .setImage("wall_horizontal.png")
                    .setX(x * ENTITY_SIZE)
                    .setY((Board.HEIGHT + 1) * ENTITY_SIZE));
        }

        for (int y = 1; y < Board.HEIGHT + 1; y++) {
            tileGroup.add(graphicEntityModule.createSprite()
                    .setImage("wall_vertical.png")
                    .setX(0)
                    .setY(y * ENTITY_SIZE));
            tileGroup.add(graphicEntityModule.createSprite()
                    .setImage("wall_vertical.png")
                    .setX((Board.WIDTH + 1) * ENTITY_SIZE)
                    .setY(y * ENTITY_SIZE));
        }
    }

    public void createUnitsView() {
        unitSpriteGroups = new ArrayList<>();

        for (Unit unit : board.getUnits()) {
            if (unit.getPlayerId() == E.PLAYER_ONE_ID) {
                if (unit.getClass().equals(Mage.class)) {
                    createUnitView("green_mage_1.png", unit);
                } else {
                    createUnitView("green_gunman_1.png", unit);
                }
            } else {
                if (unit.getClass().equals(Mage.class)) {
                    createUnitView("red_mage_1.png", unit);
                } else {
                    createUnitView("red_gunman_1.png", unit);
                }
            }
        }
    }

    private void createUnitView(String sprite, Unit unit) {
        Sprite unitSprite = graphicEntityModule.createSprite()
                .setImage(sprite)
                .setZIndex(UNIT_Z + unit.getRow());
        
        Sprite shadowSprite = graphicEntityModule.createSprite()
                .setImage("shadow.png")
                .setZIndex(SHADOW_Z)
                .setY(20);
        Group unitGroup = graphicEntityModule.createGroup(unitSprite, shadowSprite);
        placeUnitViewOnTile(unitGroup, unit.getCol(), unit.getRow());
        unitSpriteGroups.add(unitGroup);
    }

    public void updateView(Unit currentUnit, Action action, Tile affectedTile) {
        // TODO: update view
        switch (action.getCommand()) {
            case WAIT:
                break;
            case MOVE:

                int unitId = currentUnit.getUnitId();
                placeUnitViewOnTile(
                        unitSpriteGroups.get(unitId),
                        currentUnit.getCol(),
                        currentUnit.getRow());

                // TODO: turn when moving opposite direction
                break;
            case SHOOT:
                // TODO: add shoot animation
                int targetUnitId = Integer.parseInt(action.getTarget());
                Unit targetUnit = board.getUnit(targetUnitId);
                if (!targetUnit.isInGame()) {
                    unitSpriteGroups.get(targetUnitId).setVisible(false);
                }
        }

        // TODO: create animations
    }

    private void placeUnitViewOnTile(Entity unitAnimation, int col, int row) {
        unitAnimation.setX(BOARD_OFFSET_X + col * ENTITY_SIZE)
                .setY(BOARD_OFFSET_Y + row * ENTITY_SIZE + UNIT_ELEVATION);
    }
}

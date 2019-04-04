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
    public static final int FX_Z = 100;

    private BufferedGroup tileGroup;
    private GraphicEntityModule graphicEntityModule;
    private MultiplayerGameManager<Player> gameManager;
    private Board board;
    private List<Group> unitSpriteGroups;
    private List<Sprite> unitSprites;
    private Sprite bulletSprite;
    private SpriteAnimation cutAnimation;
    private SpriteAnimation convertAnimation;
    private SpriteAnimation puffAnimation;


    public ViewController(
            GraphicEntityModule graphicEntityModule,
            MultiplayerGameManager<Player> gameManager,
            Board board) {
        this.graphicEntityModule = graphicEntityModule;
        this.gameManager = gameManager;
        this.board = board;
    }

    public void createTilesView() {
        // TODO: switch to spritesheet

        tileGroup = graphicEntityModule.createBufferedGroup()
                .setX(BOARD_OFFSET_X - ENTITY_SIZE)
                .setY(BOARD_OFFSET_Y - ENTITY_SIZE).setZIndex(TILE_Z);

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
        unitSprites = new ArrayList<>();

        for (Unit unit : board.getUnits()) {
            // TODO: create laymen views
            switch (unit.getPlayerId()) {
                case E.PLAYER_ONE_ID:
                    createUnitView("red_cultleader.png", unit);
                    break;
                case E.PLAYER_TWO_ID:
                    createUnitView("blue_cultleader.png", unit);
                    break;
                default:
                    createUnitView("layman.png", unit);
            }
        }


        // TODO: add health bar
    }

    public void createFxView() {
        bulletSprite = graphicEntityModule.createSprite()
                .setImage("bullet.png")
                .setX(BOARD_OFFSET_X)
                .setY(BOARD_OFFSET_Y)
                .setZIndex(FX_Z)
                .setVisible(false);

        cutAnimation = graphicEntityModule.createSpriteAnimation()
                .setImages("cut_1.png", "cut_2.png")
                .setX(BOARD_OFFSET_X)
                .setY(BOARD_OFFSET_Y)
                .setZIndex(FX_Z)
                .setVisible(false);

        // TODO: add unit death animation
        // TODO: add convert animation
        convertAnimation = graphicEntityModule.createSpriteAnimation()
                .setImages("convert_1.png", "convert_2.png")
                .setX(BOARD_OFFSET_X)
                .setY(BOARD_OFFSET_Y)
                .setZIndex(FX_Z)
                .setVisible(false)
                .setDuration(400);

        puffAnimation = graphicEntityModule.createSpriteAnimation()
                .setImages("puff_1.png", "puff_2.png")
                .setX(BOARD_OFFSET_X)
                .setY(BOARD_OFFSET_Y)
                .setZIndex(FX_Z)
                .setVisible(false)
                .setDuration(400);
    }

    private void createUnitView(String sprite, Unit unit) {
        Sprite unitSprite = graphicEntityModule.createSprite()
                .setImage(sprite)
                .setZIndex(UNIT_Z);
        unitSprites.add(unitSprite);
        Sprite shadowSprite = graphicEntityModule.createSprite()
                .setImage("shadow.png")
                .setZIndex(SHADOW_Z)
                .setY(10);
        Group unitGroup = graphicEntityModule.createGroup(unitSprite, shadowSprite);
        placeUnitViewOnTile(unitGroup, unit.getCol(), unit.getRow());
        unitSpriteGroups.add(unitGroup);
    }

    public void updateView(Unit currentUnit, Action action, Tile affectedTile) {
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
                Unit hitUnit = affectedTile.getUnit();
                if (hitUnit != null && !hitUnit.isInGame()) {
                    unitSpriteGroups.get(hitUnit.getUnitId()).setVisible(false);
                    playFx(affectedTile, puffAnimation);
                }
                bulletAnimation(currentUnit.getTile(), affectedTile);
                unitHitAnimation(affectedTile);
                break;
            case CONVERT:
                int affectedUnitId = Integer.parseInt(action.getTarget());
                Sprite affectedSprite = unitSprites.get(affectedUnitId);
                if (currentUnit.getPlayerId() == E.PLAYER_ONE_ID) {
                    affectedSprite.setImage("red_cultist.png");
                } else {
                    affectedSprite.setImage("blue_cultist.png");
                }
                playFx(affectedTile, convertAnimation);
                break;
        }

        // TODO: create animations
    }

    private void playFx(Tile affectedTile, SpriteAnimation animation) {
        animation
                .setVisible(true)
                .setX(BOARD_OFFSET_X + affectedTile.getX() * ENTITY_SIZE)
                .setY(BOARD_OFFSET_Y + affectedTile.getY() * ENTITY_SIZE)
                .setPlaying(true);
        graphicEntityModule.commitEntityState(0.2, animation);
        animation.setVisible(false);
        graphicEntityModule.commitEntityState(1, animation);
    }

    private void unitHitAnimation(Tile affectedTile) {
        cutAnimation
                .setVisible(true)
                .setX(BOARD_OFFSET_X + affectedTile.getX() * ENTITY_SIZE)
                .setY(BOARD_OFFSET_Y + affectedTile.getY() * ENTITY_SIZE)
                .setPlaying(true);
        graphicEntityModule.commitEntityState(0.6, cutAnimation);
        cutAnimation.setVisible(false);
        graphicEntityModule.commitEntityState(1, cutAnimation);
    }

    private void bulletAnimation(Tile startTile, Tile endTile) {
        int distance = startTile.distanceFrom(endTile);
        bulletSprite
                .setVisible(true)
                .setX(BOARD_OFFSET_X + startTile.getX() * ENTITY_SIZE)
                .setY(BOARD_OFFSET_Y + startTile.getY() * ENTITY_SIZE);
        graphicEntityModule.commitEntityState(0, bulletSprite);
        bulletSprite
                .setX(BOARD_OFFSET_X + endTile.getX() * ENTITY_SIZE)
                .setY(BOARD_OFFSET_Y + endTile.getY() * ENTITY_SIZE);
        graphicEntityModule.commitEntityState(distance * 0.1, bulletSprite);
        bulletSprite.setVisible(false);
        graphicEntityModule.commitEntityState((distance + 1) * 0.1, bulletSprite);
    }

    private void placeUnitViewOnTile(Entity unit, int col, int row) {
        unit.setX(BOARD_OFFSET_X + col * ENTITY_SIZE)
                .setY(BOARD_OFFSET_Y + row * ENTITY_SIZE + UNIT_ELEVATION)
                .setZIndex(UNIT_Z + row);
    }
}

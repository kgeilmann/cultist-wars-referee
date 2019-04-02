package model;

import java.util.ArrayList;
import java.util.List;

public class Board {
    public static int WIDTH = 12;
    public static int HEIGHT = 7;
    public static int NUMBER_OF_UNITS_PER_PLAYER = 3;
    private static int[] INITIAL_COLS = new int[]{1, 1, 1, 10, 10, 10};
    private static int[] INITIAL_ROWS = new int[]{1, 3, 5, 1, 3, 5};
    private static int[] colModifiers = new int[]{0, 1, 0, -1};
    private static int[] rowModifiers = new int[]{-1, 0, 1, 0};
    private static String[] DIRECTIONS = new String[]{E.UP, E.RIGHT, E.DOWN, E.LEFT};

    private Tile[][] tiles;
    private List<Unit> allUnits;
    private int currentUnit;


    public Board() {
        initTiles();
        initUnits();
        currentUnit = 0;
    }

    private Tile[][] initTiles() {
        tiles = new Tile[WIDTH][];
        for (int x = 0; x < WIDTH; x++) {
            tiles[x] = new Tile[HEIGHT];
            for (int y = 0; y < HEIGHT; y++) {
                tiles[x][y] = new Tile(x, y, Tile.Type.FLOOR);
            }
        }

        tiles[3][3].setType(Tile.Type.OBSTACLE);
        tiles[4][3].setType(Tile.Type.OBSTACLE);
        tiles[7][3].setType(Tile.Type.OBSTACLE);
        tiles[8][3].setType(Tile.Type.OBSTACLE);

        // TODO: add more random obstacles

        return tiles;
    }

    private void initUnits() {
        allUnits = new ArrayList<>();

        int unitId = 0;
        for (int i = 0; i < NUMBER_OF_UNITS_PER_PLAYER; i++) {

            // TODO: add mage too
            if (false) {
                Tile tile = tiles[INITIAL_COLS[i]][INITIAL_ROWS[i]];
                Mage magePlayerOne = new Mage(
                        unitId++,
                        tile,
                        E.PLAYER_ONE_ID);
                allUnits.add(magePlayerOne);

                tile = tiles[INITIAL_COLS[i + NUMBER_OF_UNITS_PER_PLAYER]][INITIAL_ROWS[i + NUMBER_OF_UNITS_PER_PLAYER]];
                Mage magePlayerTwo = new Mage(
                        unitId++,
                        tile,
                        E.PLAYER_TWO_ID
                );
                allUnits.add(magePlayerTwo);
            } else {
                Tile tile = tiles[INITIAL_COLS[i]][INITIAL_ROWS[i]];
                Gunman gunmanPlayerOne = new Gunman(
                        unitId++,
                        tile,
                        E.PLAYER_ONE_ID);
                allUnits.add(gunmanPlayerOne);

                tile = tiles[INITIAL_COLS[i + NUMBER_OF_UNITS_PER_PLAYER]][INITIAL_ROWS[i + NUMBER_OF_UNITS_PER_PLAYER]];
                Gunman gunmanPlayerTwo = new Gunman(
                        unitId++,
                        tile,
                        E.PLAYER_TWO_ID);
                allUnits.add(gunmanPlayerTwo);
            }
        }

    }


    public void initExtraObstacles() {
        // TODO
    }

    public List<Unit> getUnits() {
        return allUnits;
    }

    public Tile getTile(int col, int row) {
        return tiles[col][row];
    }

    public Unit getNextUnit() {
        if (currentUnit == E.TOTAL_UNIT_NUM) currentUnit = 0;
        return allUnits.get(currentUnit++);
    }

    public List<Action> getValidActions(Unit currentUnit) {
        List<Action> validActions = new ArrayList<>();
        validActions.add(new Action(E.WAIT, "0"));

        for (int i = 0; i < 4; i++) {
            int col = currentUnit.getCol() + colModifiers[i];
            int row = currentUnit.getRow() + rowModifiers[i];

            if (!areValidCoordinates(col, row) || !isTileFree(col, row)) {
                continue;
            }

            validActions.add(new Action(E.MOVE, DIRECTIONS[i]));

        }

        if (currentUnit.getClass().equals(Gunman.class)) {
            for (Unit enemyUnit : allUnits) {
                if (enemyUnit.getPlayerId() != currentUnit.getPlayerId()
                        && isInRange(currentUnit, enemyUnit)
                        && enemyUnit.isInGame()) {
                    validActions.add(
                            new Action(E.SHOOT, String.valueOf(enemyUnit.getUnitId())));
                }
            }
        } else {
            // TODO: add mage actions
        }


        return validActions;
    }

    private boolean isInRange(Unit currentUnit, Unit enemyUnit) {
        return tiles[currentUnit.getCol()][currentUnit.getRow()]
                .distanceFrom(tiles[enemyUnit.getCol()][enemyUnit.getRow()])
                <= Gunman.RANGE;
    }

    private boolean areValidCoordinates(int col, int row) {
        return col >= 0 && col < Board.WIDTH && row >= 0 && row < Board.HEIGHT;
    }

    private boolean isTileFree(int col, int row) {
        if (tiles[col][row].getType().equals(Tile.Type.OBSTACLE)) {
            return false;
        }

        // check if move is occupied by other unit
        boolean free = true;
        for (Unit unit : allUnits) {
            if (unit.getCol() == col && unit.getRow() == row && unit.isInGame()) {
                free = false;
                break;
            }
        }
        return free;
    }

    public Unit getUnit(int index) {
        return allUnits.get(index);
    }

    public Tile update(Unit currentUnit, Action action) {
        switch (action.command) {
            case WAIT:
                return null;
            case MOVE:
                handleMove(currentUnit, action);
                return null;
            case SHOOT:
                return handleShooting(currentUnit, action);
            default:
                return null;


            // TODO: handle rest of actions
        }
    }

    private void handleMove(Unit currentUnit, Action action) {
        switch (action.target) {
            case "UP":
                currentUnit.setTile(tiles[currentUnit.getCol()][currentUnit.getRow() - 1]);
                break;
            case "DOWN":
                currentUnit.setTile(tiles[currentUnit.getCol()][currentUnit.getRow() + 1]);
                break;
            case "RIGHT":
                currentUnit.setTile(tiles[currentUnit.getCol() + 1][currentUnit.getRow()]);
                break;
            case "LEFT":
                currentUnit.setTile(tiles[currentUnit.getCol() - 1][currentUnit.getRow()]);
                break;
        }
    }

    private Tile handleShooting(Unit currentUnit, Action action) {
        // TODO: add obstacle checking
        // TODO: add friendly fire
        int targetId = Integer.parseInt(action.target);
        Unit target = allUnits.get(targetId);
        int distance = unitDistance(currentUnit, target);

        Tile startTile = tiles[currentUnit.getCol()][currentUnit.getRow()];
        Tile targetTile = tiles[target.getCol()][target.getRow()];
        Tile hitTile = checkBulletPath(startTile, targetTile);

        if (hitTile.getUnit() != null) {
            double damage = E.MAX_DAMAGE - distance * E.DAMAGE_REDUCTION_COEFF;
            hitTile.getUnit().takeDamage(damage);
        }
        return hitTile;
    }

    /*
    Atapted from:
    https://github.com/fragkakis/bresenham/blob/master/src/main/java/org/fragkakis/Bresenham.java
     */
    public Tile checkBulletPath(Tile startTile, Tile targetTile) {
        int x0 = startTile.getX();
        int y0 = startTile.getY();
        int x1 = targetTile.getX();
        int y1 = targetTile.getY();

        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);

        int sx = x0 < x1 ? 1 : -1;
        int sy = y0 < y1 ? 1 : -1;

        int err = dx - dy;
        int e2;
        int currentX = x0;
        int currentY = y0;

        while (true) {
            if (currentX == x1 && currentY == y1) break;

            e2 = 2* err;
            if (e2 > -1 * dy) {
                err -= dy;
                currentX += sx;
            }

            if (e2 < dx) {
                err += dx;
                currentY += sy;
            }

            if (tiles[currentX][currentY].getType().equals(Tile.Type.OBSTACLE)
                    || tiles[currentX][currentY].getUnit() != null) {
                return tiles[currentX][currentY];
            }
        }
        return targetTile;
    }

    private int unitDistance(Unit currentUnit, Unit target) {
        Tile unitTile = tiles[currentUnit.getCol()][currentUnit.getRow()];
        Tile targetTile = tiles[target.getCol()][target.getRow()];
        return unitTile.distanceFrom(targetTile);
    }

}

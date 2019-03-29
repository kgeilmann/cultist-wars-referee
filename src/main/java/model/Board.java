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
    private List<Unit> playerOneUnits;
    private List<Unit> playerTwoUnits;
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


        return tiles;
    }

    private void initUnits() {
        playerOneUnits = new ArrayList<>();
        playerTwoUnits = new ArrayList<>();
        allUnits = new ArrayList<>();

        int unitId = 0;
        for (int i = 0; i < NUMBER_OF_UNITS_PER_PLAYER; i++) {
            
            // TODO: add mage too
            if (false) {
                Mage magePlayerOne = new Mage(
                        unitId++,
                        INITIAL_COLS[i],
                        INITIAL_ROWS[i],
                        E.PLAYER_ONE_ID);
                playerOneUnits.add(magePlayerOne);
                allUnits.add(magePlayerOne);

                Mage magePlayerTwo = new Mage(
                        unitId++,
                        INITIAL_COLS[i + NUMBER_OF_UNITS_PER_PLAYER],
                        INITIAL_ROWS[i + NUMBER_OF_UNITS_PER_PLAYER],
                        E.PLAYER_TWO_ID
                );
                playerTwoUnits.add(magePlayerTwo);
                allUnits.add(magePlayerTwo);
            } else {

                Gunman gunmanPlayerOne = new Gunman(
                        unitId++,
                        INITIAL_COLS[i],
                        INITIAL_ROWS[i],
                        E.PLAYER_ONE_ID);

                playerOneUnits.add(gunmanPlayerOne);
                allUnits.add(gunmanPlayerOne);

                Gunman gunmanPlayerTwo = new Gunman(
                        unitId++,
                        INITIAL_COLS[i + NUMBER_OF_UNITS_PER_PLAYER],
                        INITIAL_ROWS[i + NUMBER_OF_UNITS_PER_PLAYER],
                        E.PLAYER_TWO_ID);
                playerTwoUnits.add(gunmanPlayerTwo);
                allUnits.add(gunmanPlayerTwo);
            }
        }

    }

    public List<Unit> getUnits() {
        return allUnits;
    }

    public List<Unit> getPlayerOneUnits() {
        return playerOneUnits;
    }

    public List<Unit> getPlayerTwoUnits() {
        return playerTwoUnits;
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
            // TODO: add shooting
            for (Unit enemyUnit : allUnits) {
                if (enemyUnit.getPlayerId() != currentUnit.getPlayerId()
                        && isInRange(currentUnit, enemyUnit)) {
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
            if (unit.getCol() == col && unit.getRow() == row) {
                free = false;
                break;
            }
        }
        return free;
    }

    public Unit getUnit(int index) {
        return allUnits.get(index);
    }

    public void update(Unit currentUnit, Action action) {
        switch (action.command) {
            case WAIT:
                return;
            case MOVE:
                switch (action.target) {
                    case "UP":
                        currentUnit.setRow(currentUnit.getRow() - 1);
                        return;
                    case "DOWN":
                        currentUnit.setRow(currentUnit.getRow() + 1);
                        return;
                    case "RIGHT":
                        currentUnit.setCol(currentUnit.getCol() + 1);
                        return;
                    case "LEFT":
                        currentUnit.setCol(currentUnit.getCol() - 1);
                        return;
                }
                // TODO: handle rest of actions
        }
    }
}

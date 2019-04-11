package model;

import java.util.*;

public class Board {
    public static int WIDTH = 13;
    public static int HEIGHT = 7;
    public static int NEUTRAL_BOUND = 5;
    public static int NUMBER_OF_UNITS = 14;
    public static int NUMBER_OF_NEUTRALS = NUMBER_OF_UNITS - 2;
    private static int[] INITIAL_PRIEST_COLS = new int[]{0, Board.WIDTH - 1};
    private static int[] INITIAL_PRIEST_ROWS = new int[]{3, 3};

    private static int[] X_MODIFIER = new int[]{0, 1, 0, -1};
    private static int[] Y_MODIFIER = new int[]{-1, 0, 1, 0};
    private static final double OBSTACLE_CHANCE = 0.2;

    public Tile[][] tiles;
    public List<Unit> allUnits;
    int[] numberOfUnits;

    public void removeDeadUnitsFromTiles() {
        for (Unit unit : allUnits) {
            if (!unit.isInGame() && unit.getTile() != null) {
                unit.getTile().setUnit(null);
                unit.setTile(null);
            }
        }
    }

    private class BfsNode {
        BfsNode parent;
        Tile tile;
        int distance;

        public BfsNode(BfsNode parent, Tile tile, int distance) {
            this.parent = parent;
            this.tile = tile;
            this.distance = distance;
        }
    }

    public Board(boolean testing) {
        allUnits = new ArrayList<>();
        initTiles();
        initCultLeaders();
        if (!testing) {
            initLaymen();
            initExtraObstacles();
        }
        numberOfUnits = new int[]{1, 1};
    }

    private Tile[][] initTiles() {
        tiles = new Tile[WIDTH][];
        for (int x = 0; x < WIDTH; x++) {
            tiles[x] = new Tile[HEIGHT];
            for (int y = 0; y < HEIGHT; y++) {
                tiles[x][y] = new Tile(x, y, Tile.Type.FLOOR);
            }
        }

        return tiles;
    }

    private void initCultLeaders() {
        // Change cult leader initial positions
        Tile tile = tiles[INITIAL_PRIEST_COLS[0]][INITIAL_PRIEST_ROWS[0]];
        CultLeader cultLeaderPlayerOne = new CultLeader(
                0,
                tile,
                E.PLAYER_ONE_ID);
        allUnits.add(cultLeaderPlayerOne);

        tile = tiles[INITIAL_PRIEST_COLS[1]][INITIAL_PRIEST_ROWS[1]];
        CultLeader cultLeaderPlayerTwo = new CultLeader(
                1,
                tile,
                E.PLAYER_TWO_ID
        );
        allUnits.add(cultLeaderPlayerTwo);
    }

    private void initLaymen() {
        int laymanCounter = 2;
        for (int i = 0; i < NUMBER_OF_NEUTRALS / 2; i++) {
            Tile tile = tiles[E.random.nextInt(NEUTRAL_BOUND) + 1][E.random.nextInt(HEIGHT)];
            while (tile.getUnit() != null) {
                tile = tiles[E.random.nextInt(NEUTRAL_BOUND) + 1][E.random.nextInt(HEIGHT)];
            }
            Cultist layman = new Cultist(
                    laymanCounter++,
                    tile,
                    E.PLAYER_NEUTRAL_ID
            );
            allUnits.add(layman);
        }

        for (int i = 0; i < NUMBER_OF_NEUTRALS / 2; i++) {
            Unit mirror = allUnits.get(2 + i);
            Tile tile = tiles[WIDTH - 1 - mirror.getCol()][mirror.getRow()];
            Cultist layman = new Cultist(
                    laymanCounter++,
                    tile,
                    E.PLAYER_NEUTRAL_ID
            );
            allUnits.add(layman);
        }
    }


    private void initExtraObstacles() {
        int obstacleCount;
        do {
            obstacleCount = 0;
            resetTiles();
            for (int col = 0; col < (WIDTH - 1) / 2; col++) {
                for (int row = 0; row < Board.HEIGHT; row++) {
                    if (tiles[col][row].getUnit() == null
                            && E.random.nextDouble() < OBSTACLE_CHANCE) {
                        tiles[col][row].setType(Tile.Type.OBSTACLE);
                        tiles[WIDTH - 1 - col][row].setType(Tile.Type.OBSTACLE);
                        obstacleCount += 2;
                    }
                }
            }
        } while (!allCellsAreReachable(obstacleCount));

        // TODO: add sleep pod in the middle
    }

    private void resetTiles() {
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                tiles[x][y].setType(Tile.Type.FLOOR);
            }
        }
    }

    private boolean allCellsAreReachable(int obstacleCount) {
        Queue<BfsNode> toVisit = new LinkedList<>();
        Set<Tile> visitedTiles = new HashSet<>();

        toVisit.add(new BfsNode(null, allUnits.get(0).getTile(), 0));

        while (!toVisit.isEmpty()) {
            BfsNode currentNode = toVisit.poll();
            visitedTiles.add(currentNode.tile);

            // check children
            for (int i = 0; i < 4; i++) {
                int x = currentNode.tile.getX() + X_MODIFIER[i];
                int y = currentNode.tile.getY() + Y_MODIFIER[i];
                if (areValidCoordinates(x, y)) {
                    Tile childTile = tiles[x][y];
                    if (tiles[childTile.getX()][childTile.getY()].getType().equals(Tile.Type.FLOOR)
                            && !visitedTiles.contains(childTile)) {
                        toVisit.add(new BfsNode(currentNode, childTile, currentNode.distance + 1));
                    }
                }
            }
        }

        return visitedTiles.size() == (WIDTH * HEIGHT) - obstacleCount;
    }

    public List<Unit> getUnits() {
        return allUnits;
    }

    public Tile getTile(int col, int row) {
        return tiles[col][row];
    }

    public List<Action> getValidActionsOfNeutralUnit(Unit currentUnit) {
        List<Action> validActions = new ArrayList<>();
        if (!currentUnit.isInGame()) return validActions;
        for (int i = 0; i < 4; i++) {
            int col = currentUnit.getCol() + X_MODIFIER[i];
            int row = currentUnit.getRow() + Y_MODIFIER[i];

            if (!areValidCoordinates(col, row) || !isTileFree(col, row)) {
                continue;
            }

            validActions.add(new MoveAction(
                    currentUnit.getUnitId(),
                    Action.Command.MOVE,
                    col, row));

        }
        if (validActions.isEmpty()) {
            validActions.add(new Action(Action.Command.WAIT));
        }
        return validActions;
    }

    private boolean isInRange(Unit currentUnit, Unit enemyUnit) {
        return tiles[currentUnit.getCol()][currentUnit.getRow()]
                .distanceFrom(tiles[enemyUnit.getCol()][enemyUnit.getRow()])
                <= Cultist.RANGE;
    }

    private boolean areValidCoordinates(int x, int y) {
        return x >= 0 && x < Board.WIDTH && y >= 0 && y < Board.HEIGHT;
    }

    private boolean isValidUnitId(int unitId) {
        return unitId >= 0 && unitId < NUMBER_OF_UNITS;
    }

    private boolean isTileFree(int col, int row) {
        return tiles[col][row].getType().equals(Tile.Type.FLOOR)
                && tiles[col][row].getUnit() == null;
    }

    public Unit getUnit(int index) {
        return allUnits.get(index);
    }

    public Tile update(Unit currentUnit, Action action) {
        switch (action.getCommand()) {
            case WAIT:
                return null;
            case MOVE:
                handleMove(currentUnit, (MoveAction) action);
                return null;
            case SHOOT:
                return handleShooting(currentUnit, (SpecialAction) action);
            case CONVERT:
                return handleConvert((SpecialAction) action);
            default:
                return null;
        }
    }

    private Tile handleConvert(SpecialAction action) {
        int playerId = allUnits.get(action.getUnitId()).getPlayerId();
        Unit affectedUnit = allUnits.get(action.getTargetId());
        if (affectedUnit.getPlayerId() != E.PLAYER_NEUTRAL_ID) {
            numberOfUnits[affectedUnit.getPlayerId()]--;
        }
        affectedUnit.setPlayerId(playerId);
        numberOfUnits[playerId]++;
        return affectedUnit.getTile();
    }

    private void handleMove(Unit currentUnit, MoveAction action) {
        currentUnit.moveTo(tiles[action.getX()][action.getY()]);
    }

    private Tile handleShooting(Unit currentUnit, SpecialAction action) {
        int targetId = action.getTargetId();
        Unit target = allUnits.get(targetId);

        Tile startTile = tiles[currentUnit.getCol()][currentUnit.getRow()];
        Tile targetTile = tiles[target.getCol()][target.getRow()];
        Tile hitTile = checkBulletPath(startTile, targetTile);

        if (hitTile.getUnit() != null) {
            int distance = unitDistance(currentUnit, hitTile.getUnit());
            int damage = E.MAX_DAMAGE - distance * E.DAMAGE_REDUCTION_COEFF;
            hitTile.getUnit().takeDamage(damage);
            if (!hitTile.getUnit().isInGame()) {
                if (hitTile.getUnit().getPlayerId() != E.PLAYER_NEUTRAL_ID) {
                    numberOfUnits[hitTile.getUnit().getPlayerId()]--;
                }
            }
        }
        return hitTile;
    }

    /*
    Adapted from:
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

            e2 = 2 * err;
            if (e2 > -1 * dy) {
                err -= dy;
                currentX += sx;
            }

            if (e2 < dx) {
                err += dx;
                currentY += sy;
            }

            if (tiles[currentX][currentY].getType().equals(Tile.Type.OBSTACLE)
                    || (tiles[currentX][currentY].getUnit() != null
                    && tiles[currentX][currentY].getUnit().isInGame)) {
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

    public List<Unit> getNeutralUnits() {
        List<Unit> neutralUnits = new ArrayList<>();
        for (Unit unit : allUnits) {
            if (unit.getPlayerId() == E.PLAYER_NEUTRAL_ID) {
                neutralUnits.add(unit);
            }
        }
        return neutralUnits;
    }

    public int getNumberOfUnits(int playerId) {
        return numberOfUnits[playerId];
    }

    public List<Unit> getUnitsInGame() {
        List<Unit> unitsInGame = new ArrayList<>();
        for (Unit unit : allUnits) {
            if (unit.isInGame()) {
                unitsInGame.add(unit);
            }
        }
        return unitsInGame;
    }

    public void checkActionValidity(Action action, int playerId) {
        switch (action.getCommand()) {
            case MOVE:
                validateMoveAction((MoveAction) action, playerId);
                break;
            case SHOOT:
                validateShootAction((SpecialAction) action, playerId);
                break;
            case CONVERT:
                validateConvertAction((SpecialAction) action, playerId);
                break;
        }
    }

    private void validateConvertAction(SpecialAction action, int playerId) {
        if (!isValidUnitId(action.getUnitId())) {
            throw new IllegalArgumentException(action.getUnitId() + " is not a valid unit ID");
        }
        if (allUnits.get(action.getUnitId()).getPlayerId() != playerId) {
            throw new IllegalArgumentException(action.getUnitId() + " is not your unit");
        }
        if (!isValidUnitId(action.getTargetId())) {
            throw new IllegalArgumentException("Invalid target ID");
        }
        if (allUnits.get(action.getTargetId()).getPlayerId() == playerId) {
            throw new IllegalArgumentException("Can't convert own unit");
        }
        if (action.getTargetId() == E.PLAYER_ONE_ID
                || action.getTargetId() == E.PLAYER_TWO_ID) {
            throw new IllegalArgumentException("Cult leaders cannot be converted");
        }
        if (action.getUnitId() != E.PLAYER_ONE_ID
                && action.getUnitId() != E.PLAYER_TWO_ID) {
            throw new IllegalArgumentException("Only cult leaders can convert");
        }
        if (!allUnits.get(action.getUnitId()).isInGame()) {
            throw new IllegalArgumentException("Unit " + action.getUnitId() +  " is no longer in game");
        }
        if (!allUnits.get(action.getTargetId()).isInGame()) {
            throw new IllegalArgumentException("Unit " + action.getTargetId() +  " is no longer in game");
        }
    }

    private void validateShootAction(SpecialAction action, int playerId) {
        if (!isValidUnitId(action.getUnitId())) {
            throw new IllegalArgumentException(action.getUnitId() + " is not a valid unit ID");
        }
        if (allUnits.get(action.getUnitId()).getPlayerId() != playerId) {
            throw new IllegalArgumentException(action.getUnitId() + " is not your unit");
        }
        if (!isValidUnitId(action.getTargetId())) {
            throw new IllegalArgumentException("Invalid target ID");
        }
        if (action.getUnitId() == action.getTargetId()) {
            throw new IllegalArgumentException("Unit cannot shoot itself");
        }
        if (action.getUnitId() == playerId) {
            throw new IllegalArgumentException("Cult leaders cannot shoot.");
        }
        if (!allUnits.get(action.getUnitId()).isInGame()) {
            throw new IllegalArgumentException("Unit " + action.getUnitId() +  " is no longer in game");
        }
        if (!allUnits.get(action.getTargetId()).isInGame()) {
            throw new IllegalArgumentException("Unit " + action.getTargetId() +  " is no longer in game");
        }
    }

    private void validateMoveAction(MoveAction action, int playerId) {
        if (!isValidUnitId(action.getUnitId())) {
            throw new IllegalArgumentException(action.getUnitId() + " is not a valid unit ID");
        }
        if (allUnits.get(action.getUnitId()).getPlayerId() != playerId) {
            throw new IllegalArgumentException(action.getUnitId() + " is not your unit");
        }
        if (!areValidCoordinates(action.getX(), action.getY())) {
            throw new IllegalArgumentException("Invalid coordinates");
        }
        if (!allUnits.get(action.getUnitId()).isInGame()) {
            throw new IllegalArgumentException("Unit " + action.getUnitId() +  " is no longer in game");
        }
    }



    @Override
    public String toString() {
        StringBuilder boardSb = new StringBuilder();
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                if (tiles[x][y].getType().equals(Tile.Type.FLOOR)) {
                    boardSb.append(".");
                } else {
                    boardSb.append("x");
                }
            }
            boardSb.append("\n");
        }
        return boardSb.toString();
    }

    public Action getFinalAction(Action action) {
        switch (action.getCommand()) {
            case WAIT:
                return action;
            case MOVE:
                return getMoveAction((MoveAction) action);
            case SHOOT:
                return getShootAction((SpecialAction) action);
            case CONVERT:
                return getConvertAction((SpecialAction) action);
            default:
                return action;
        }
    }

    private Action getConvertAction(SpecialAction specialAction) {
        Tile unitTile = allUnits.get(specialAction.getUnitId()).getTile();
        Tile targetTile = allUnits.get(specialAction.getTargetId()).getTile();
        if (unitTile.distanceFrom(targetTile) == 1) {
            return specialAction;
        } else {
            return getMoveAction(new MoveAction(specialAction.getUnitId(),
                    Action.Command.MOVE, targetTile.getX(), targetTile.getY()));
        }
    }

    private Action getShootAction(SpecialAction specialAction) {
        Tile unitTile = allUnits.get(specialAction.getUnitId()).getTile();
        Tile targetTile = allUnits.get(specialAction.getTargetId()).getTile();
        if (unitTile.distanceFrom(targetTile) <= 6) {
            return specialAction;
        } else {
            return getMoveAction(new MoveAction(specialAction.getUnitId(),
                    Action.Command.MOVE, targetTile.getX(), targetTile.getY()));
        }
    }

    private Action getMoveAction(MoveAction moveAction) {
        Tile unitTile = allUnits.get(moveAction.getUnitId()).getTile();
        Tile targetTile = tiles[moveAction.getX()][moveAction.getY()];
        // if moving to same tile
        if (unitTile.equals(targetTile)) {
            return new Action(Action.Command.WAIT);
        }
        // is on adjacent cell?
        else if (unitTile.distanceFrom(targetTile) == 1) {
            if (isTileFree(targetTile.getX(), targetTile.getY())) {
                return moveAction;
            } else {
                return new Action(Action.Command.WAIT);
            }
        } else {
            // find path to cell
            Tile firstTileOfPath = findPath(unitTile, targetTile);
            if (firstTileOfPath != null) {
                return new MoveAction(moveAction.getUnitId(), Action.Command.MOVE,
                        firstTileOfPath.getX(), firstTileOfPath.getY());
            } else {
                Tile tileInDirection = getTileInDirection(unitTile, targetTile);
                if (tileInDirection != null) {
                    return new MoveAction(moveAction.getUnitId(), Action.Command.MOVE,
                            tileInDirection.getX(), tileInDirection.getY());
                } else {
                    return new Action(Action.Command.WAIT);
                }
            }
        }
    }

    private Tile getTileInDirection(Tile unitTile, Tile targetTile) {
        // TODO: improve pathfinding
        Queue<BfsNode> toVisit = new LinkedList<>();
        Set<Tile> visitedTiles = new HashSet<>();

        toVisit.add(new BfsNode(null, unitTile, 0));

        while (!toVisit.isEmpty()) {
            BfsNode currentNode = toVisit.poll();
            visitedTiles.add(currentNode.tile);

            // check children
            for (int i = 0; i < 4; i++) {
                int x = currentNode.tile.getX() + X_MODIFIER[i];
                int y = currentNode.tile.getY() + Y_MODIFIER[i];
                if (areValidCoordinates(x, y)) {
                    Tile childTile =
                            tiles[x][y];
                    if (childTile.equals(targetTile)) {
                        Tile ancestor = getAncestor(currentNode);
                        if (ancestor.getUnit() == null) {
                            return ancestor;
                        } else {
                            return null;
                        }
                    }
                    if (tiles[childTile.getX()][childTile.getY()].getType().equals(Tile.Type.FLOOR)
                            && !visitedTiles.contains(childTile)) {
                        toVisit.add(new BfsNode(currentNode, childTile, currentNode.distance + 1));
                    }
                }
            }
        }

        return null;
    }

    private Tile findPath(Tile unitTile, Tile targetTile) {
        // TODO: improve pathfinding and merge two pathfinding functions
        Queue<BfsNode> toVisit = new LinkedList<>();
        Set<Tile> visitedTiles = new HashSet<>();

        toVisit.add(new BfsNode(null, unitTile, 0));

        while (!toVisit.isEmpty()) {
            BfsNode currentNode = toVisit.poll();
            visitedTiles.add(currentNode.tile);

            // check children
            for (int i = 0; i < 4; i++) {
                int x = currentNode.tile.getX() + X_MODIFIER[i];
                int y = currentNode.tile.getY() + Y_MODIFIER[i];
                if (areValidCoordinates(x, y)) {
                    Tile childTile =
                            tiles[x][y];
                    if (childTile.equals(targetTile)) {
                        return getAncestor(currentNode);
                    }
                    if (isTileFree(childTile.getX(), childTile.getY())
                            && !visitedTiles.contains(childTile)) {
                        toVisit.add(new BfsNode(currentNode, childTile, currentNode.distance + 1));
                    }
                }
            }
        }

        return null;
    }

    private Tile getAncestor(BfsNode currentNode) {
        while (currentNode.parent.parent != null) {
            currentNode = currentNode.parent;
        }
        return currentNode.tile;
    }
}

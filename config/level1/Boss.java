import java.util.List;
import java.util.*;
import java.util.Scanner;

class Action {
    Unit unit;
    Command command;
    Target target;

    public Action(Unit unit, Command command, Target target) {
        this.unit = unit;
        this.command = command;
        this.target = target;
    }

    @Override
    public String toString() {
        if (command.equals(Command.WAIT)) {
            return command.toString();
        } else {
            return unit.unitId + " " + command.toString() + " " + target.getTargetString();
        }
    }
}

class Cache {
    int cachedVal_1;
    int cachedVal_2;

    public Cache(int x, int y) {
        cachedVal_1 = x;
        cachedVal_2 = y;
    }

    public Cache(int i) {
        cachedVal_1 = i;
    }


    public Tile getOldTile(Tile[][] tiles) {
        return tiles[cachedVal_1][cachedVal_2];
    }

    public int getOldId() {
        return cachedVal_1;
    }

    public int getOldHp() {
        return cachedVal_1;
    }
}

enum Command {
    WAIT, MOVE, SHOOT, CONVERT
}


class Player {

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);

        int myId = in.nextInt(); // 0 - you are the first player, 1 - you are the second player
        int width = in.nextInt(); // Width of the board
        int height = in.nextInt(); // Height of the board

        State state = new State(myId, width, height);
        Search search = new Search(false);

        for (int y = 0; y < height; y++) {
            state.addRow(in.next(), y); // A row of the board: "." is empty, "x" is obstacle
        }

        // game loop
        while (true) {
            state.reset();
            int numOfUnits = in.nextInt(); // The total number of units on the board
            for (int i = 0; i < numOfUnits; i++) {
                Unit unit = new Unit(in.nextInt(), in.nextInt(), in.nextInt(), state.tiles[in.nextInt()][in.nextInt()], in.nextInt());
                if (unit.playerId == myId) {
                    state.myUnits.add(unit);
                } else if (unit.playerId == 2) {
                    state.neutralUnits.add(unit);
                } else {
                    state.enemyUnits.add(unit);
                }
            }

            // WAIT | unitId MOVE x y | unitId SHOOT target| unitId CONVERT target

            List<Action> possibleActions = state.getPossibleActions(myId);
            Action bestAction = null;
            int depth = 0;
            long startTime = System.currentTimeMillis();
            while (true) {
                try {
                    bestAction = search.getBestAction(state, possibleActions, startTime, depth++);
                } catch (TimeOutException e) {
                    break;
                }
            }

            System.out.println(bestAction == null ? "WAIT" : bestAction);
        }
    }
}


class Search {
    final int TIME_CUTOFF;

    public Search(boolean isTesting) {
        TIME_CUTOFF = isTesting ? Integer.MAX_VALUE : 40;
    }

    public Action getBestAction(State state, List<Action> possibleActions, long startTime, int depth) throws TimeOutException {
        Action bestAction = null;
        double bestScore = Double.NEGATIVE_INFINITY;
        for (Action currentAction : possibleActions) {
            Cache cache = state.executeAction(currentAction);
            double currentScore = 0;
            try {
                currentScore = alphaBeta(false, state, depth, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, startTime);
                if (currentScore > bestScore) {
                    bestAction = currentAction;
                    bestScore = currentScore;
                }
            } catch (TimeOutException e) {
                throw e;
            } finally {
                state.undoAction(currentAction, cache);
            }
        }

        return bestAction;
    }

    public double alphaBeta(boolean isMax, State state, int depth, double alpha, double beta, long startTime) throws TimeOutException {
        if (System.currentTimeMillis() - startTime > TIME_CUTOFF) {
            throw new TimeOutException();
        }

        if (depth == 0) {
            return state.evaluate();
        }

        if (isMax) {
            double bestScore = Double.NEGATIVE_INFINITY;
            for (Action currentAction : state.getPossibleActions(state.myId)) {
                Cache cache = state.executeAction(currentAction);
                try {
                    double currentScore = alphaBeta(false, state, depth - 1, alpha, beta, startTime);
                    bestScore = Math.max(currentScore, bestScore);
                    alpha = Math.max(alpha, bestScore);

                } catch (TimeOutException e) {
                    throw e;
                } finally {
                    state.undoAction(currentAction, cache);
                }
                if (alpha >= beta) {
                    break;
                }
            }
            return bestScore;
        } else {
            double bestScore = Double.POSITIVE_INFINITY;
            for (Action currentAction : state.getPossibleActions(state.myId == 0 ? 1 : 0)) {
                Cache cache = state.executeAction(currentAction);
                try {
                    double currentScore = alphaBeta(true, state, depth - 1, alpha, beta, startTime);
                    state.undoAction(currentAction, cache);
                    bestScore = Math.min(currentScore, bestScore);
                    beta = Math.min(beta, bestScore);

                } catch (TimeOutException e) {
                    throw e;
                } finally {
                    state.undoAction(currentAction, cache);
                }
                if (alpha >= beta) {
                    break;
                }
            }
            return bestScore;
        }
    }

}


class State {
    static final int[] X_MOD = new int[]{0, 1, 0, -1};
    static final int[] Y_MOD = new int[]{1, 0, -1, 0};

    final int width;
    final int height;
    final int myId;
    Tile[][] tiles;
    List<Unit> myUnits;
    List<Unit> enemyUnits;
    List<Unit> neutralUnits;

    public State(int myId, int width, int height) {
        this.width = width;
        this.height = height;
        this.myId = myId;
        tiles = new Tile[width][];
        for (int x = 0; x < width; x++) {
            tiles[x] = new Tile[height];
        }
        myUnits = new ArrayList<>();
        enemyUnits = new ArrayList<>();
        neutralUnits = new ArrayList<>();
    }

    public void addRow(String row, int y) {
        for (int x = 0; x < tiles.length; x++) {
            if (row.charAt(x) == '.') {
                tiles[x][y] = new Tile(x, y, Tile.Type.FLOOR);
            } else {
                tiles[x][y] = new Tile(x, y, Tile.Type.OBSTACLE);
            }
        }
    }

    public void reset() {
        for (Tile[] tileCol : tiles) {
            for (Tile tile : tileCol) {
                tile.unit = null;
            }
        }
        myUnits = new ArrayList<>();
        enemyUnits = new ArrayList<>();
        neutralUnits = new ArrayList<>();
    }

    // Only get shoot actions that hit target
    // No shooting neutrals for now
    public List<Action> getPossibleActions(int playerId) {
        List<Action> possibleActions = new ArrayList<>();

        for (Unit unit : (playerId == myId? myUnits : enemyUnits)) {
            if (!unit.isInGame()) continue;
            // Move & Convert
            for (int i = 0; i < 4; i++) {
                int x = unit.tile.x + X_MOD[i];
                int y = unit.tile.y + Y_MOD[i];
                if (isValidTile(x, y)) {
                    if (tiles[x][y].unit == null  || !tiles[x][y].unit.isInGame()) {
                        possibleActions.add(new Action(unit, Command.MOVE, tiles[x][y]));
                    } else if (unit.type.equals(Unit.Type.CULT_LEADER)
                            && tiles[x][y].unit.type.equals(Unit.Type.CULTIST)
                            && tiles[x][y].unit.playerId != playerId
                            && tiles[x][y].unit.isInGame()) {
                        possibleActions.add(new Action(unit, Command.CONVERT, tiles[x][y].unit));
                    }
                }
            }

            // Shoot
            if (unit.type.equals(Unit.Type.CULTIST)) {
                for (Unit enemyUnit : (playerId == myId? enemyUnits : myUnits)) {
                    if (!enemyUnit.isInGame()) continue;
                    if (isInRange(unit, enemyUnit) && isLineOfSightUnblocked(unit, enemyUnit)) {
                        possibleActions.add(new Action(unit, Command.SHOOT, enemyUnit));
                    }
                }
            }
        }
        return possibleActions;
    }

    private boolean isLineOfSightUnblocked(Unit unit, Unit enemyUnit) {
        // Bresenham line algo
        int x0 = unit.tile.x;
        int y0 = unit.tile.y;
        int x1 = enemyUnit.tile.x;
        int y1 = enemyUnit.tile.y;

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

            if (tiles[currentX][currentY].type.equals(Tile.Type.OBSTACLE)
                    || (tiles[currentX][currentY].unit != null
                    && tiles[currentX][currentY].unit.isInGame()
                    && !tiles[currentX][currentY].unit.equals(enemyUnit))) {
                return false;
            }
        }
        return true;
    }

    private boolean isInRange(Unit unit, Unit enemyUnit) {
        return unit.tile.distanceFrom(enemyUnit.tile) <= 6;
    }

    private boolean isValidTile(int x, int y) {
        return x >= 0
                && x < width
                && y >= 0
                && y < height
                && tiles[x][y].type.equals(Tile.Type.FLOOR);
    }


    private double sumHp(List<Unit> units) {
        int sum = 0;
        for (Unit unit : units) {
            if (unit.type.equals(Unit.Type.CULT_LEADER)){
                sum += (unit.hp * 10);
            } else {
                sum += unit.hp;
            }
        }
        return sum;
    }

    public Cache executeAction(Action currentAction) {
        Cache cache = null;
        switch (currentAction.command) {
            case MOVE:
                Unit currentUnit = currentAction.unit;
                cache = currentUnit.cacheUnit(currentAction.command);
                currentUnit.tile.unit = null;
                currentUnit.tile = (Tile) currentAction.target;
                currentUnit.tile.unit = currentUnit;
                break;
            case CONVERT:
                Unit targetUnit = (Unit) currentAction.target;
                cache = targetUnit.cacheUnit(currentAction.command);
                if (targetUnit.playerId == 2) {
                    neutralUnits.remove(targetUnit);
                } else {
                    enemyUnits.remove(targetUnit);
                }

                myUnits.add(targetUnit);
                targetUnit.playerId = currentAction.unit.playerId;
                break;
            case SHOOT:
                targetUnit = (Unit) currentAction.target;
                cache = targetUnit.cacheUnit(currentAction.command);
                int damage = 7 - currentAction.unit.tile.distanceFrom(targetUnit.tile);
                targetUnit.hp = Math.max(0, targetUnit.hp - damage);
                break;
        }

        return cache;
    }

    public void undoAction(Action currentAction, Cache cache) {
        switch (currentAction.command) {
            case MOVE:
                Unit currentUnit = currentAction.unit;
                currentUnit.tile.unit = null;
                currentUnit.tile = cache.getOldTile(tiles);
                currentUnit.tile.unit = currentUnit;
                break;
            case CONVERT:
                Unit targetUnit = (Unit) currentAction.target;
                targetUnit.playerId = cache.getOldId();
                if (targetUnit.playerId == 2) {
                    neutralUnits.add(targetUnit);
                } else {
                    enemyUnits.add(targetUnit);
                }

                myUnits.remove(targetUnit);
                break;
            case SHOOT:
                targetUnit = (Unit) currentAction.target;
                targetUnit.hp = cache.getOldHp();
                break;
        }
    }

    public double evaluate() {
        int myUnitsAlive = unitsAlive(myUnits);
        int enemyUnitsAlive = unitsAlive(enemyUnits);
        if (myUnitsAlive == 0) {
            return -1_000_000_000;
        } else if (enemyUnitsAlive == 0) {
            return 1_000_000_000;
        }
        return sumHp(myUnits) - sumHp(enemyUnits) + leaderDistanceToNeutralUnit() + myUnitsAlive * 1000 - enemyUnitsAlive * 1000;
    }

    private int unitsAlive(List<Unit> units) {
        int sum = 0;
        for (Unit unit : units) {
            if (unit.isInGame()) {
                sum++;
            }
        }
        return sum;
    }

    private double leaderDistanceToNeutralUnit() {
        // TODO: add pathfinding
        if (neutralUnits.isEmpty()) return 0;  // if there are no more neutrals left
        if (myUnits.get(0).unitId != myId) return 0;  // if cult leader is dead

        Unit leader = myUnits.get(0);
        int smallestDistance = Integer.MAX_VALUE;
        for (Unit neutralUnit : neutralUnits) {
            int distance = leader.tile.distanceFrom(neutralUnit.tile);
            if (distance < smallestDistance) {
                smallestDistance = distance;
            }
        }
        return 1 - smallestDistance * 0.001;
    }

    @Override
    public String toString() {
        return boardToString();
    }

    private String boardToString() {
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                sb.append(tiles[x][y]);
            }
            sb.append(System.lineSeparator());
        }
        return sb.toString();
    }

    public String newUnitStrings() {
        StringBuilder sb = new StringBuilder();
        for (Unit myUnit : myUnits) {
            sb.append("state.myUnits.add(new Unit(" + myUnit.unitId + ", " + myUnit.type + ", " + myUnit.hp
                    + ", state.tiles[" + myUnit.tile.x + "][" + myUnit.tile.y + "], " + myUnit.playerId + "));"
                    + System.lineSeparator());
        }
        return sb.toString();
    }

    public boolean hasWon(int playerId) {
        if (playerId == myId) {
            return enemyUnits.isEmpty();
        } else {
            return myUnits.isEmpty();
        }
    }
}

interface Target {
    public String getTargetString();
}

class Tile implements Target{
    int x;
    int y;
    Type type;
    Unit unit = null;



    enum Type {
        FLOOR, OBSTACLE
    }

    public Tile(int x, int y, Type type) {
        this.x = x;
        this.y = y;
        this.type = type;
    }

    public int distanceFrom(Tile other) {
        return Math.abs(this.x - other.x) + Math.abs(this.y - other.y);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tile tile = (Tile) o;

        if (x != tile.x) return false;
        return y == tile.y;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        return result;
    }

    @Override
    public String getTargetString() {
        return x + " " + y;
    }

    @Override
    public String toString() {
        if (type.equals(Type.FLOOR)) {
            return ".";
        } else {
            return "x";
        }
    }
}

class TimeOutException extends Exception {
}

class Unit implements Target {
    int unitId;
    Type type;
    int hp;
    Tile tile;
    int playerId;

    public Cache cacheUnit(Command command) {
        switch (command) {
            case MOVE:
                return new Cache(tile.x, tile.y);
            case CONVERT:
                return new Cache(playerId);
            case SHOOT:
                return new Cache(hp);
            default:
                throw new IllegalArgumentException();
        }
    }

    enum Type {
        CULT_LEADER, CULTIST
    }

    public Unit(int unitId, int typeInt, int hp, Tile tile, int playerId) {
        this(unitId, (typeInt == 0 ? Type.CULTIST : Type.CULT_LEADER), hp, tile, playerId);
    }

    public Unit(int unitId, Type type, int hp, Tile tile, int playerId) {
        this.unitId = unitId;
        this.type = type;
        this.hp = hp;
        this.tile = tile;
        tile.unit = this;
        this.playerId = playerId;
    }

    public boolean isInGame() {
        return hp != 0;
    }

    @Override
    public String getTargetString() {
        return String.valueOf(unitId);
    }

    @Override
    public String toString() {
        return "Unit{" +
                "unitId=" + unitId +
                ", type=" + type +
                ", hp=" + hp +
                ", tile=[" + tile.x + ", " + tile.y + "]" +
                ", playerId=" + playerId +
                '}';
    }
}


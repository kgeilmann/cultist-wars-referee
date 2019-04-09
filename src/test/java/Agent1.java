import java.util.List;
import java.util.Scanner;
import java.util.SplittableRandom;
import java.util.ArrayList;


class Action {
    protected final int unitId;
    protected final Command command;

    static public Action parseAction(String[] output) {
        switch (Command.valueOf(output[1])) {
            case WAIT:
                return new Action(Integer.parseInt(output[0]), Command.WAIT);
            case MOVE:
                return new MoveAction(
                        Integer.parseInt(output[0]),
                        Command.MOVE,
                        Integer.parseInt(output[2]),
                        Integer.parseInt(output[3]));
            case SHOOT:
                return new SpecialAction(
                        Integer.parseInt(output[0]),
                        Command.SHOOT,
                        Integer.parseInt(output[2]));
            case CONVERT:
                return new SpecialAction(
                        Integer.parseInt(output[0]),
                        Command.CONVERT,
                        Integer.parseInt(output[2]));
            default:
                throw new IllegalArgumentException();
        }

    }

    public Action(int unitId, Command command) {
        this.unitId = unitId;
        this.command = command;
    }

    public Command getCommand() {
        return command;
    }

    public int getUnitId() {
        return unitId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Action action = (Action) o;

        if (unitId != action.unitId) return false;
        return command == action.command;
    }

    @Override
    public int hashCode() {
        int result = unitId;
        result = 31 * result + (command != null ? command.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return unitId + " " + command;
    }
}

enum Command {
    MOVE, SHOOT, CONVERT, WAIT
}

class MoveAction extends Action {
    final int col;
    final int row;

    public MoveAction(int unitId, Command command, int col, int row) {
        super(unitId, command);
        this.col = col;
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public int getRow() {
        return row;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        MoveAction that = (MoveAction) o;

        if (col != that.col) return false;
        return row == that.row;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + col;
        result = 31 * result + row;
        return result;
    }

    @Override
    public String toString() {
        return unitId + " " + command + " " + col + " " + row;
    }
}


/**
 * Convert neutral units and attack enemy ones
 **/
class Agent1 {
    static SplittableRandom random = new SplittableRandom();

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int myId = in.nextInt(); // 0 - you are the first player, 1 - you are the second player
        int width = in.nextInt(); // Width of the board
        int height = in.nextInt(); // Height of the board
        State state = new State(myId, width, height);
        for (int i = 0; i < height; i++) {
            String row = in.next(); // A row of the board: "." is empty, "x" is obstacle
            state.addRow(row, i);
        }

        // game loop
        while (true) {
            state.reset();
            int numOfUnits = in.nextInt(); // The total number of units on the board
            for (int i = 0; i < numOfUnits; i++) {
                int unitId = in.nextInt(); // The unit's ID
                int unitType = in.nextInt(); // The unit's type: 0 = Cultist, 1 = Cult Leader
                int hp = in.nextInt(); // Health points of the unit
                int x = in.nextInt(); // X coordinate of the unit
                int y = in.nextInt(); // Y coordinate of the unit
                int owner = in.nextInt();
                Unit unit = new Unit(unitId, unitType, hp, state.tiles[x][y], owner);
                if (owner == myId) {
                    state.myUnits.add(unit);
                } else {
                    state.enemyAndNeutralUnits.add(unit);
                }
            }
            Unit closestUnitToConvert = state.findClosestUnitToConvert();
//            System.err.println(closestUnitToConvert);
            System.out.println(myId + " CONVERT " + closestUnitToConvert.unitId);
        }
    }
}

class SpecialAction extends Action {
    private int targetId;

    public SpecialAction(int unitId, Command command, int targetId) {
        super(unitId, command);
        this.targetId = targetId;
    }

    public int getTargetId() {
        return targetId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (!super.equals(o)) return false;

        SpecialAction that = (SpecialAction) o;

        return targetId == that.targetId;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + targetId;
        return result;
    }


    @Override
    public String toString() {
        return unitId + " " + command + " " + targetId;
    }
}


class State {
    static int[] xMod = new int[]{0, 1, 0, -1};
    static int[] yMod = new int[]{1, 0, -1, 0};

    final int width;
    final int height;
    final int myId;
    Tile[][] tiles;
    List<Unit> myUnits;
    List<Unit> enemyAndNeutralUnits;

    public State(int myId, int width, int height) {
        this.width = width;
        this.height = height;
        this.myId = myId;
        tiles = new Tile[width][];
        for (int x = 0; x < width; x++) {
            tiles[x] = new Tile[height];
        }
        myUnits = new ArrayList<>();
        enemyAndNeutralUnits = new ArrayList<>();
    }

    public void addRow(String row, int y){
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
        enemyAndNeutralUnits = new ArrayList<>();
    }

    public Unit findClosestUnitToConvert() {
        int minDistance = Integer.MAX_VALUE;
        Unit closest = null;
        Unit cultLeader = myUnits.get(0);
        for (Unit unit : enemyAndNeutralUnits) {
            if (unit.type.equals(Unit.Type.CULT_LEADER)) {
                continue;
            }
            int distance = cultLeader.tile.distanceFrom(unit.tile);
            if (distance < minDistance) {
                minDistance = distance;
                closest = unit;
            }
        }
        return closest;
    }
}

class Tile {
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
}

class Unit {
    int unitId;
    Type type;
    int hp;
    Tile tile;
    int playerId;

    enum Type {
        CULT_LEADER, CULTIST
    }

    public Unit(int unitId, int typeInt, int hp, Tile tile, int playerId) {
        this.unitId = unitId;
        this.type = typeInt == 0 ? Type.CULTIST : Type.CULT_LEADER;
        this.hp = hp;
        this.tile = tile;
        tile.unit = this;
        this.playerId = playerId;
    }
}


import java.util.ArrayList;
import java.util.Scanner;
import java.util.SplittableRandom;

class Action {
    protected final int unitId;
    protected final Command command;

    public enum Command {
        MOVE, SHOOT, CONVERT, WAIT
    }

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

class MoveAction extends Action {
    private final int col;
    private final int row;

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


/**
 * Convert neutral units and attack enemy ones
 **/
class Player {
    static SplittableRandom random = new SplittableRandom();

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int firstOrSecond = in.nextInt(); // 0 - you are the first player, 2 - you are the second player
        int width = in.nextInt(); // Width of the board
        int height = in.nextInt(); // Height of the board
        for (int i = 0; i < height; i++) {
            String row = in.next(); // A row of the board: "." is empty, "x" is obstacle
        }
        in.nextLine();

        // game loop
        while (true) {
            int numOfUnits = in.nextInt(); // The total number of units on the board
            for (int i = 0; i < numOfUnits; i++) {
                int unitId = in.nextInt(); // The unit's ID
                int unitType = in.nextInt(); // The unit's type: 0 = Cultist, 1 = Cult Leader
                int hp = in.nextInt(); // Health points of the unit
                int x = in.nextInt(); // X coordinate of the unit
                int y = in.nextInt(); // Y coordinate of the unit
                int owner = in.nextInt(); // unit: 0 - your unit, 1 - opponent, 2 - neutral
            }
            int numOfActions = in.nextInt(); // The number of your valid actions
            if (in.hasNextLine()) {
                in.nextLine();
            }
            ArrayList<Action> actions = new ArrayList<>();
            Action chosenAction = null;
            for (int i = 0; i < numOfActions; i++) {
                Action action = Action.parseAction(in.nextLine().trim().split(" "));
                actions.add(action); // A possible valid action
                if (action.getCommand().equals(Action.Command.CONVERT)) {
                    chosenAction = action;
                }
            }

            // Write an action using System.out.println()
            // To debug: System.err.println("Debug messages...");


            // unitId WAIT 0 | unitId MOVE direction | unitId SHOOT target| unitId CONVERT target
            chosenAction = chosenAction == null  ? actions.get(random.nextInt(actions.size())) : chosenAction;
            System.out.println(chosenAction);
        }
    }
}
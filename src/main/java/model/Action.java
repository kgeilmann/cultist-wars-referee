package model;

public class Action {
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

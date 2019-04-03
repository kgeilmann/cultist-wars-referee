package model;

public class Action {
    private final int unitId;
    private final Command command;
    private final String target;

    public enum Command {
        MOVE, SHOOT, CONVERT, WAIT
    }

    public Action(int unitId, String commandString, String target) {
        this.unitId = unitId;
        this.command = Command.valueOf(commandString);
        this.target = target;
    }

    public Command getCommand() {
        return command;
    }

    public String getTarget() {
        return target;
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
        if (command != action.command) return false;
        return target != null ? target.equals(action.target) : action.target == null;
    }

    @Override
    public int hashCode() {
        int result = unitId;
        result = 31 * result + (command != null ? command.hashCode() : 0);
        result = 31 * result + (target != null ? target.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return unitId + " " + command + " " + target;
    }
}

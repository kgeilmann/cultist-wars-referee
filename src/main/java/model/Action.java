package model;

public class Action {
    Command command;
    String target;

    public enum Command {
        MOVE, SHOOT, HEAL, WAIT
    }

    public Action(String commandString, String target) {
        this.command = Command.valueOf(commandString);
        this.target = target;
    }

    public Command getCommand() {
        return command;
    }

    public String getTarget() {
        return target;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Action action = (Action) o;

        if (command != action.command) return false;
        return target != null ? target.equals(action.target) : action.target == null;
    }

    @Override
    public int hashCode() {
        int result = command != null ? command.hashCode() : 0;
        result = 31 * result + (target != null ? target.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return command + " " + target;
    }
}

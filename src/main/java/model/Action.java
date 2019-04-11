package model;

public class Action {
    protected int unitId;
    protected final Command command;

    public enum Command {
        MOVE, SHOOT, CONVERT, WAIT
    }

    static public Action parseAction(String[] output) {
        if (output[0].equals("WAIT")) {

            return new Action(Command.WAIT);
        }

        switch (Command.valueOf(output[1])) {
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
                throw new IllegalArgumentException(output[1] + " is not a valid action");
        }

    }

    public Action(Command command) {
        this.command = command;
    }

    public Command getCommand() {
        return command;
    }


    public int getUnitId() {
        if (command.equals(Command.WAIT)) {
            throw new IllegalArgumentException("Trying to access WAIT command unitId");
        }
        return unitId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Action action = (Action) o;

        return command == action.command;
    }

    @Override
    public int hashCode() {
        return command != null ? command.hashCode() : 0;
    }

    @Override
    public String toString() {
        return  command.toString();
    }
}

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;


enum Command {
    MOVE, SHOOT, CONVERT, WAIT
}

class Action {
    private final int unitId;
    private final Command command;
    private final String target;

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


public class Agent1 {
    static int counter = 0;
    static Random random = new Random();


    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            int numberOfActions = scanner.nextInt();
            scanner.nextLine();

            List<Action> validActions = new ArrayList<>();
            Action chosenAction = null;
            for (int i = 0; i < numberOfActions; i++) {
                String[] actionString = scanner.nextLine().split(" ");
                Action newAction = new Action(
                        Integer.parseInt(actionString[0]),
                        actionString[1],
                        actionString[2]);
                if (newAction.getCommand().equals(Command.CONVERT)) {
                    chosenAction = newAction;
                }
                validActions.add(newAction);
            }

            int randomMoveIndex = random.nextInt(numberOfActions);
            if (chosenAction == null) {
                chosenAction = validActions.get(randomMoveIndex);
            }
//            Action chosenAction = validActions.size() > 1 ? validActions.get(1) : validActions.get(0);
//            Action chosenAction = validActions.get(counter++ % (validActions.size()));
            System.out.println(chosenAction);
        }
    }
}

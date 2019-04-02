import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


enum Command {
    MOVE, SHOOT, HEAL, WAIT
}

class Action {
    Command command;
    String target;

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


public class Agent1 {
    static int counter = 0;


    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            int currentUnitId = scanner.nextInt();
            scanner.nextLine();

            int numberOfActions = scanner.nextInt();
            scanner.nextLine();

            List<Action> validActions = new ArrayList<>();
            for (int i = 0; i < numberOfActions; i++) {
                String[] actionString = scanner.nextLine().split(" ");
                validActions.add(new Action(actionString[0], actionString[1]));
            }

//            int randomMoveIndex = E.random.nextInt(numberOfActions);
//            Action chosenAction = validActions.get(randomMoveIndex);
//            Action chosenAction = validActions.size() > 1 ? validActions.get(1) : validActions.get(0);
            Action chosenAction = validActions.get(counter++ % (validActions.size()));
            System.out.println(chosenAction);
        }
    }
}

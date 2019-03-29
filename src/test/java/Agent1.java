import model.Action;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Agent1 {
    static int counter = 0;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            int currentUnitId = scanner.nextInt();
            long startingTime = System.currentTimeMillis();
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
            System.err.println("chosenAction: " + chosenAction);
            System.err.println("elapsedTime: " + (System.currentTimeMillis() - startingTime));
        }
    }
}

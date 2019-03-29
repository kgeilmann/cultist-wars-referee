import model.Action;
import model.E;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Agent1 {
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
            int randomMoveIndex = E.random.nextInt(numberOfActions);
            Action chosenAction = validActions.get(randomMoveIndex);
            System.out.println(chosenAction);
        }
    }
}

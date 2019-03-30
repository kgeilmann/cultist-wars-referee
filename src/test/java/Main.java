import com.codingame.gameengine.runner.MultiplayerGameRunner;

public class Main {
    public static void main(String[] args) {
        MultiplayerGameRunner gameRunner = new MultiplayerGameRunner();

        // Adds as many player as you need to test your game

        // Another way to add a player
        gameRunner.addAgent("java -cp target/test-classes/ Agent1");
        gameRunner.addAgent("java -cp target/test-classes/ Agent1");

        gameRunner.start();
    }
}

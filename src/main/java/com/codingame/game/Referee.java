package com.codingame.game;

import com.codingame.gameengine.core.AbstractPlayer.TimeoutException;
import com.codingame.gameengine.core.AbstractReferee;
import com.codingame.gameengine.core.MultiplayerGameManager;
import com.codingame.gameengine.module.entities.GraphicEntityModule;
import com.google.inject.Inject;
import model.Action;
import model.Board;
import model.Tile;
import model.Unit;
import view.ViewController;

import java.util.List;

// TODO: make stub

public class Referee extends AbstractReferee {
    public static int MAX_ROUNDS = 400;
    private static int FRAME_DURATION = 400;

    @Inject
    private MultiplayerGameManager<Player> gameManager;
    @Inject
    private GraphicEntityModule graphicEntityModule;

    private ViewController viewController;
    private Board board;

    @Override
    public void init() {
        gameManager.setMaxTurns(MAX_ROUNDS);
        gameManager.setFrameDuration(FRAME_DURATION);
        gameManager.setTurnMaxTime(50);

        board = new Board();
//        board.initExtraObstacles();

        viewController = new ViewController(graphicEntityModule, gameManager, board);
        viewController.createTilesView();
        viewController.createUnitsView();
        viewController.createFxView();
    }

    @Override
    public void gameTurn(int turn) {
        Player player = gameManager.getPlayer(turn % 2);
        List<Action> validActions = board.getValidActions(player.getIndex());
        sendInputs(player, validActions);
        player.execute();

        try {
            Action action = player.getAction();
            // TODO: update game summary

            // Check validity of the player output and compute the new game state
            if (!validActions.contains(action)) {
                player.deactivate(String.format("$%d illegal move!", player.getIndex()));
                gameManager.endGame();
            }

            Unit currentUnit = board.getUnit(action.getUnitId());

            Tile affectedTile = board.update(currentUnit, action);

            if (action.getCommand().equals(Action.Command.SHOOT)) {
                Unit affectedUnit = affectedTile.getUnit();
                if (affectedUnit != null) {
                    gameManager.addToGameSummary("Unit " + currentUnit.getUnitId() +
                            " hit unit "
                            + affectedUnit.getUnitId()
                            + ". New hp: " + affectedUnit.getHp());
                } else {
                    gameManager.addToGameSummary("Unit " + currentUnit.getUnitId() +
                            " hit obstacle.");
                }

            }

            viewController.updateView(currentUnit, action, affectedTile);

        } catch (TimeoutException e) {
            player.deactivate(String.format("$%d timeout!", player.getIndex()));
            System.err.println(e);
            gameManager.endGame();
        } catch (IllegalArgumentException e) {
            player.deactivate(String.format("$%d illegal move!", player.getIndex()));
            gameManager.endGame();
        }
    }

    private void sendInitInput(Player player) {
        // TODO: send init input
    }

    private void sendInputs(Player player, List<Action> validActions) {
        player.sendInputLine(String.valueOf(validActions.size()));
        for (Action validAction : validActions) {
            player.sendInputLine(validAction.toString());
        }
    }

}

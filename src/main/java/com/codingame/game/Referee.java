package com.codingame.game;

import com.codingame.gameengine.core.AbstractPlayer.TimeoutException;
import com.codingame.gameengine.core.AbstractReferee;
import com.codingame.gameengine.core.MultiplayerGameManager;
import com.codingame.gameengine.module.entities.GraphicEntityModule;
import com.google.inject.Inject;
import model.*;
import view.ViewController;

import java.util.List;

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
        board.initExtraObstacles();

        viewController = new ViewController(graphicEntityModule, gameManager, board);
        viewController.createTilesView();
        viewController.createUnitsView();
        viewController.createFxView();
        viewController.createHudsView();
    }

    @Override
    public void gameTurn(int turn) {
        int playerId = turn % 2;

        Player player = gameManager.getPlayer(playerId);

        if (turn == 0 || turn == 1) {
            sendInitInput(player);
        }

        List<Action> validActions = board.getValidActions(player.getIndex());
        sendInputs(player, validActions);
        player.execute();

        try {
            Action action = player.getAction();
            // Check validity of the player output and compute the new game state
            if (!validActions.contains(action)) {
                gameManager.addToGameSummary(String.format("$%d illegal move!", player.getIndex()));
                player.setScore(-1);
                onEndGame();
                return;
            }

            Unit currentUnit = board.getUnit(action.getUnitId());

            Tile affectedTile = board.update(currentUnit, action);

            updateGameSummary(action, affectedTile, currentUnit);

            viewController.updateView(currentUnit, action, affectedTile);

            moveNeutralUnit();

        } catch (TimeoutException e) {
            gameManager.addToGameSummary(String.format("$%d timeout!", player.getIndex()));
            player.setScore(-1);
            System.err.println(e);
            onEndGame();
            return;
        } catch (Exception e) {
            gameManager.addToGameSummary(String.format("$%d invalid output!", player.getIndex()));
            player.setScore(-1);
            System.err.println(e);
            onEndGame();
            return;
        }
        gameManager.addToGameSummary("\nTurn: " + turn);
        if (gameManager.isGameEnd()
                || board.getNumberOfUnits(E.PLAYER_ONE_ID) < 1
                || board.getNumberOfUnits(E.PLAYER_TWO_ID) < 1
                || turn == MAX_ROUNDS - 1) {
            gameManager.getPlayer(E.PLAYER_ONE_ID).setScore(board.getNumberOfUnits(E.PLAYER_ONE_ID));
            gameManager.getPlayer(E.PLAYER_TWO_ID).setScore(board.getNumberOfUnits(E.PLAYER_TWO_ID));
            onEndGame();
        }
    }

    private void updateGameSummary(Action action, Tile affectedTile, Unit currentUnit) {
        switch (action.getCommand()) {
            case SHOOT:
                Unit affectedUnit = affectedTile.getUnit();
                if (affectedUnit != null) {
                    gameManager.addToGameSummary("Unit " + currentUnit.getUnitId() +
                            " hit unit "
                            + affectedUnit.getUnitId()
                            + ". New hp: " + affectedUnit.getHp());
                } else {
                    gameManager.addToGameSummary("Unit " + currentUnit.getUnitId() +
                            " hit obstacle");
                }
                break;
            case MOVE:
                Unit unit = board.getUnit(action.getUnitId());
                gameManager.addToGameSummary("Unit " + unit.getUnitId()
                        + " moved to row " + unit.getCol()
                        + " col " + unit.getRow());
                break;
            case CONVERT:
                SpecialAction specialAction = (SpecialAction) action;
                gameManager.addToGameSummary("Unit " + action.getUnitId()
                        + " converted unit " + specialAction.getTargetId());
                break;
            case WAIT:
                gameManager.addToGameSummary("Unit " + action.getUnitId()
                        + " is waiting");
                break;
        }

    }

    private void sendInitInput(Player player) {
        player.sendInputLine(String.valueOf(player.getIndex()));
        player.sendInputLine(Board.WIDTH + " " + Board.HEIGHT);
        player.sendInputLine(board.toString().trim());
    }

    private void sendInputs(Player player, List<Action> validActions) {
        List<Unit> unitsInGame = board.getUnitsInGame();
        player.sendInputLine(String.valueOf(unitsInGame.size()));
        for (Unit unit : unitsInGame) {
            // unit id
            int playerId = player.getIndex();
            player.sendInputLine(String.valueOf(
                    // unit id
                    unit.getUnitId())
                    // type
                    + " " + String.valueOf(unit.getClass().equals(Cultist.class) ? 0 : 1)
                    // hp
                    + " " + String.valueOf(unit.getHp())
                    // coords
                    + " " + String.valueOf(unit.getCol())
                    + " " + String.valueOf(unit.getRow())
                    // player id
                    + " " + String.valueOf(unit.getPlayerId()));
        }

        player.sendInputLine(String.valueOf(validActions.size()));
        for (Action validAction : validActions) {
            player.sendInputLine(validAction.toString());
        }
    }

    private void moveNeutralUnit() {
        List<Unit> neutralUnits = board.getNeutralUnits();
        if (!neutralUnits.isEmpty()) {
            Unit neutralUnit = neutralUnits.get(E.random.nextInt(neutralUnits.size()));
            List<Action> validActions = board.getValidActionsOfUnit(neutralUnit);
            if (!validActions.isEmpty()) {
                Action action = validActions.get(E.random.nextInt(validActions.size()));
                board.update(neutralUnit, action);
                viewController.updateView(neutralUnit, action, null);
            }
        }
    }

    public void onEndGame() {
        String winningString;
        if (gameManager.getPlayer(E.PLAYER_ONE_ID).getScore() > gameManager.getPlayer(E.PLAYER_TWO_ID).getScore()) {
            winningString= viewController.endGameView(gameManager.getPlayer(E.PLAYER_ONE_ID).getAvatarToken());
        } else if (gameManager.getPlayer(E.PLAYER_ONE_ID).getScore() < gameManager.getPlayer(E.PLAYER_TWO_ID).getScore()) {
            winningString = viewController.endGameView(gameManager.getPlayer(E.PLAYER_TWO_ID).getAvatarToken());
        } else {
            winningString = viewController.endGameView(null);
        }
        gameManager.endGame();
        gameManager.addToGameSummary(winningString);
    }
}

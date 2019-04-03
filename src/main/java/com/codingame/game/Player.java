package com.codingame.game;

import com.codingame.gameengine.core.AbstractMultiplayerPlayer;
import model.Action;

public class Player extends AbstractMultiplayerPlayer {
    @Override
    public int getExpectedOutputLines() {
        return 1;
    }

    public Action getAction() throws TimeoutException, IllegalArgumentException {
        String[] output = getOutputs().get(0).split(" ");
        return new Action(Integer.parseInt(output[0]), output[1], output[2]);
    }
}

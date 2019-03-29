package com.codingame.game;

import com.codingame.gameengine.core.AbstractMultiplayerPlayer;
import model.Action;

public class Player extends AbstractMultiplayerPlayer {
    @Override
    public int getExpectedOutputLines() {
        return 1;
    }

    public Action getAction() throws TimeoutException, IllegalArgumentException {
        System.err.println("BEFORE OUTPUTS");
        String[] output = getOutputs().get(0).split(" ");
        System.err.println("Outputs: " + getOutputs());
        return new Action(output[0], output[1]);
    }
}

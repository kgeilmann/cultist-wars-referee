package model;

import java.util.SplittableRandom;

public class E {
    public static final int MAX_DAMAGE = 7;
    public static final SplittableRandom random = new SplittableRandom();

    public static final int PLAYER_ONE_ID = 0;
    public static final int PLAYER_TWO_ID = 1;
    public static final int PLAYER_NEUTRAL_ID = 2;
    public static final int MAX_UNIT_NUM = 16;

    public static final String WAIT = "WAIT";
    public static final String MOVE = "MOVE";
    public static final String SHOOT = "SHOOT";
    public static final String CONVERT = "CONVERT";
    public static final String UP = "UP";
    public static final String RIGHT = "RIGHT";
    public static final String DOWN = "DOWN";
    public static final String LEFT = "LEFT";
    public static final int DAMAGE_REDUCTION_COEFF = 1;
}

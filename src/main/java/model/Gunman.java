package model;

public class Gunman extends Unit {
    public static int RANGE = 6;

    public Gunman(int id, Tile tile, int playerId) {
        super(id, tile, playerId);
    }
}

package model;

public abstract class Unit {
    protected  int id;
    protected double hp;
    protected Tile tile;
    protected int playerId;
    protected boolean isInGame;

    public Unit(int id, Tile tile, int playerId) {
        this.id = id;
        this.playerId = playerId;
        hp = 10.0;
        isInGame = true;
        tile.setUnit(this);
        this.tile = tile;
    }

    public Tile getTile() {
        return tile;
    }

    public void setTile(Tile tile) {
        this.tile.setUnit(null);
        tile.setUnit(this);
        this.tile = tile;
    }

    public int getCol() {
        return tile.getX();
    }

    public int getRow() {
        return tile.getY();
    }

    public int getPlayerId() {
        return playerId;
    }

    public boolean isInGame() {
        return isInGame;
    }

    public int getUnitId() {
        return id;
    }

    public double getHp() {
        return hp;
    }

    public void takeDamage(double damage) {
        hp = Math.max(0, hp - damage);
        if (hp == 0) {
            isInGame = false;
        }
    }
}

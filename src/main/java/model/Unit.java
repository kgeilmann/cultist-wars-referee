package model;

public abstract class Unit {
    protected int id;
    protected int hp;
    protected Tile tile;
    protected int playerId;
    protected boolean isInGame;

    public Unit(int id, Tile tile, int playerId) {
        this.id = id;
        this.playerId = playerId;
        hp = 10;
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

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public boolean isInGame() {
        return isInGame;
    }

    public int getUnitId() {
        return id;
    }

    public int getHp() {
        return hp;
    }

    public void takeDamage(int damage) {
        hp = Math.max(0, hp - damage);
        if (hp == 0) {
            isInGame = false;
        }
    }
}

package model;

public abstract class Unit {
    protected  int id;
    protected double hp;
    protected int col;
    protected int row;
    protected int playerId;
    protected boolean isInGame;

    public Unit(int id, int col, int row, int playerId) {
        this.id = id;
        this.col = col;
        this.row = row;
        this.playerId = playerId;
        hp = 10.0;
        isInGame = true;
    }

    public int getCol() {
        return col;
    }

    public int getRow() {
        return row;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public void setRow(int row) {
        this.row = row;
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

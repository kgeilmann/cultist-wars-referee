package model;

public class Tile {
    public enum Type {
        FLOOR, OBSTACLE
    }

    private int x;
    private int y;
    private Type type;

    public Tile(int x, int y, Type type) {
        this.x = x;
        this.y = y;
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public int distanceFrom(Tile other) {
        return Math.abs(this.x - other.x) + Math.abs(this.y - other.y);
    }

    @Override
    public String toString() {
        switch (type) {
            case OBSTACLE:
                return "X";
            default:
                return ".";
        }
    }
}

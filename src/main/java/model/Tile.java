package model;

public class Tile {
    public enum Type {
        FLOOR, OBSTACLE
    }

    private final int x;
    private final int y;
    private Type type;
    private Unit unit;

    public Tile(int x, int y, Type type) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.unit = null;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tile tile = (Tile) o;

        if (x != tile.x) return false;
        if (y != tile.y) return false;
        return type == tile.type;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
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

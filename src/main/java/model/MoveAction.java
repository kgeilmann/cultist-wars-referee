package model;

public class MoveAction extends Action {
    private final int x;
    private final int y;

    public MoveAction(int unitId, Command command, int x, int y) {
        super(command);
        this.unitId = unitId;
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        MoveAction that = (MoveAction) o;

        if (unitId != that.unitId) return false;
        if (x != that.x) return false;
        return y == that.y;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + unitId;
        result = 31 * result + x;
        result = 31 * result + y;
        return result;
    }

    @Override
    public String toString() {
        return unitId + " " + command + " " + x + " " + y;
    }
}

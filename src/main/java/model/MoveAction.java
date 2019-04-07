package model;

public class MoveAction extends Action {
    private final int col;
    private final int row;

    public MoveAction(int unitId, Command command, int col, int row) {
        super(unitId, command);
        this.col = col;
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public int getRow() {
        return row;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        MoveAction that = (MoveAction) o;

        if (col != that.col) return false;
        return row == that.row;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + col;
        result = 31 * result + row;
        return result;
    }

    @Override
    public String toString() {
        return unitId + " " + command + " " + col + " " + row;
    }
}

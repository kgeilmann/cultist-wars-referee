package model;

public class SpecialAction extends Action {
    private int targetId;

    public SpecialAction(int unitId, Command command, int targetId) {
        super(command);
        this.targetId = targetId;
        this.unitId = unitId;
    }

    public int getTargetId() {
        return targetId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        SpecialAction that = (SpecialAction) o;

        if (unitId != that.unitId) return false;
        return targetId == that.targetId;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + unitId;
        result = 31 * result + targetId;
        return result;
    }

    @Override
    public String toString() {
        return unitId + " " + command + " " + targetId;
    }
}

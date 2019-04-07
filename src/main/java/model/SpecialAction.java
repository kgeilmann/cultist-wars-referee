package model;

public class SpecialAction extends Action {
    private int targetId;

    public SpecialAction(int unitId, Command command, int targetId) {
        super(unitId, command);
        this.targetId = targetId;
    }

    public int getTargetId() {
        return targetId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (!super.equals(o)) return false;

        SpecialAction that = (SpecialAction) o;

        return targetId == that.targetId;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + targetId;
        return result;
    }


    @Override
    public String toString() {
        return unitId + " " + command + " " + targetId;
    }
}

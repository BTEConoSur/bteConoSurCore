package pizzaaxx.bteconosur.country.ProjectTypes.Points;

public class RangePoints implements ProjectPointType {

    private final int max;
    private final int min;

    public RangePoints(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    @Override
    public boolean isValid(int amount) {
        return (amount > min && amount < max);
    }
}

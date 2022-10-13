package pizzaaxx.bteconosur.country.ProjectTypes.Points;

public class StaticPoints implements ProjectPointType {

    private final int amount;

    public StaticPoints(int amount) {
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

    @Override
    public boolean isValid(int amount) {
        return this.amount == amount;
    }
}

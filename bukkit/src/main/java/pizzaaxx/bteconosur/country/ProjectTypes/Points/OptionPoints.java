package pizzaaxx.bteconosur.country.ProjectTypes.Points;

import java.util.List;

public class OptionPoints implements ProjectPointType {

    private final List<Integer> points;

    OptionPoints(List<Integer> points) {
        this.points = points;
    }

    public List<Integer> getPoints() {
        return points;
    }

    @Override
    public boolean isValid(int amount) {
        return this.points.contains(amount);
    }
}

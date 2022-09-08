package pizzaaxx.bteconosur.HelpMethods.Geographics;

import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.helper.Pair;

public class GeoVector {

    final Pair<Double, Double> from;
    final Pair<Double, Double> to;

    public GeoVector(Pair<Double, Double> from, Pair<Double, Double> to) {
        this.from = from;
        this.to = to;
    }

    public GeoVector(double fromLon, double fromLat, double toLon, double toLat) {
        this(new Pair<>(fromLon, fromLat), new Pair<>(toLon, toLat));
    }

   public boolean intersects(@NotNull GeoVector vector) {

        // GENERAL CASE
        int o1 = orientation(from, to, vector.from);
        int o2 = orientation(from, to, vector.to);
        int o3 = orientation(vector.from, vector.to, from);
        int o4 = orientation(vector.from, vector.to, to);

        if (o1 != o2 && o3 != o4) {
            return true;
        }

        // SPECIAL CASES
       return (o1 == 0 && onSegment(from, vector.from, to)) || (o2 == 0 && onSegment(from, vector.to, to)) || (o3 == 0 && onSegment(vector.from, from, vector.to)) || (o4 == 0 && onSegment(vector.from, to, vector.to));
   }

    private int orientation(@NotNull Pair<Double, Double> p1, @NotNull Pair<Double, Double> p2, @NotNull Pair<Double, Double> p3) {
        double val = (p2.getValue() - p1.getValue()) * (p3.getKey() - p2.getValue()) - (p2.getKey() - p1.getKey()) * (p3.getValue() - p2.getValue());
        return (val == 0 ? 0 : (val > 0 ? 1 : 2));
    }

    private boolean onSegment(@NotNull Pair<Double, Double> p1, @NotNull Pair<Double, Double> p2, @NotNull Pair<Double, Double> p3) {
        return (p2.getKey() <= Math.max(p1.getKey(), p3.getKey()) && p2.getKey() >= Math.min(p1.getKey(), p3.getKey()) && p2.getValue() <= Math.max(p1.getValue(), p3.getValue()) && p2.getValue() >= Math.min(p1.getValue(), p3.getValue()));
    }

}

package pizzaaxx.bteconosur.HelpMethods.Geographics;

import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.coords.Coords2D;
import pizzaaxx.bteconosur.helper.Pair;

public class GeoVector {

    final GeoPoint from;
    final GeoPoint to;

    public GeoVector(GeoPoint from, GeoPoint to) {
        this.from = from;
        this.to = to;
    }

    public GeoVector(double fromLon, double fromLat, double toLon, double toLat) {
        this(new GeoPoint(fromLon, fromLat), new GeoPoint(toLon, toLat));
    }

    public GeoVector(Coords2D from, Coords2D to) {
        this(new GeoPoint(from), new GeoPoint(to));
    }

   public boolean intersects(@NotNull GeoVector vector) {

       // COLLINEAR: 0
       // CLOCKWISE: 1
       // ANTICLOCKWISE: 2

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

    private int orientation(@NotNull GeoPoint p1, @NotNull GeoPoint p2, @NotNull GeoPoint p3) {
        double val = (p1.getY() - p2.getY()) * (p2.getX() - p3.getX()) - (p1.getX() - p2.getX()) * (p2.getY() - p3.getY());
        return (val == 0 ? 0 : (val > 0 ? 1 : 2));
    }

    private boolean onSegment(@NotNull GeoPoint p1, @NotNull GeoPoint p2, @NotNull GeoPoint p3) {

        // Both on X and Y: Middle point is greater than minimum and less tha maximum

        return (p2.getX() <= Math.max(p1.getX(), p3.getX()) && p2.getX() >= Math.min(p1.getX(), p3.getX()) && p2.getY() <= Math.max(p1.getY(), p3.getY()) && p2.getY() >= Math.min(p1.getY(), p3.getY()));
    }

}

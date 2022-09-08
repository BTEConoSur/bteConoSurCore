package pizzaaxx.bteconosur.HelpMethods.Geographics;

import com.sk89q.worldedit.BlockVector2D;
import pizzaaxx.bteconosur.coords.Coords2D;

public class GeoPoint {

    private final double x;
    private final double y;

    public GeoPoint(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public GeoPoint(Coords2D coords) {
        this(coords.getLon(), coords.getLat());
    }

    public GeoPoint(BlockVector2D vector) {
        this(new Coords2D(vector));
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}

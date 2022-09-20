package pizzaaxx.bteconosur.HelpMethods.Geographics;

import pizzaaxx.bteconosur.coords.Coords2D;

import java.util.ArrayList;
import java.util.List;

public class GeoPolygon {

    private final List<GeoVector> vectors;

    public GeoPolygon(List<GeoPoint> points) {

        vectors = new ArrayList<>();

        List<GeoPoint> modPoints = new ArrayList<>(points);
        modPoints.add(points.get(points.size() - 1));

        for (int i = 0; i + 1 < modPoints.size(); i++) {
            vectors.add(new GeoVector(modPoints.get(i), modPoints.get(i + 1)));
        }
    }

    public List<GeoVector> getVectors() {
        return vectors;
    }

    public boolean intersects(GeoPolygon polygon) {

        for (GeoVector vector1 : vectors) {
            for (GeoVector vector2 : polygon.getVectors()) {
                if (vector1.intersects(vector2)) {
                    return true;
                }
            }
        }

        return false;
    }

}

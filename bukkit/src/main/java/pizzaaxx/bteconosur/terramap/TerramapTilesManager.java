package pizzaaxx.bteconosur.terramap;

import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.HelpMethods.Geographics.GeoPoint;
import pizzaaxx.bteconosur.HelpMethods.Geographics.GeoPolygon;
import pizzaaxx.bteconosur.coords.Coords2D;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TerramapTilesManager {

    public void drawPolygon(@NotNull List<Coords2D> points, Color color) {

        // METHOD: Create BufferedImage polygon and crop from it to generate layer tiles. Then loop all affected tiles and combine all available layers into one.

    }




























    public List<TerramapTile> getIntersectingTiles(int zoom, @NotNull List<Coords2D> points) {

        List<TerramapTile> tiles = new ArrayList<>();

        TerramapTile firstTile = new TerramapTile(points.get(0).getLon(), points.get(0).getLat(), zoom);

        // GET MINs AND MAXs
        int minX = firstTile.getX();
        int minY = firstTile.getY();

        int maxX = firstTile.getX();
        int maxY = firstTile.getY();

        List<GeoPoint> polygonPoints = new ArrayList<>();

        for (Coords2D coords : points) {

            polygonPoints.add(new GeoPoint(coords));

            TerramapTile tile = new TerramapTile(coords.getLon(), coords.getLat(), zoom);

            if (tile.getX() < minX) {
                minX = tile.getX();
            }

            if (tile.getY() < minY) {
                minY = tile.getY();
            }

            if (tile.getX() > maxX) {
                maxX = tile.getX();
            }

            if (tile.getY() > maxY) {
                maxY = tile.getY();
            }
        }

        GeoPolygon polygon = new GeoPolygon(polygonPoints);

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {

                TerramapTile tile = new TerramapTile(x, y, zoom);

                GeoPolygon square = new GeoPolygon(
                        Arrays.asList(
                                new GeoPoint(tile.getMaxLon(), tile.getMaxLat()),
                                new GeoPoint(tile.getMaxLon(), tile.getMinLat()),
                                new GeoPoint(tile.getMinLon(), tile.getMinLat()),
                                new GeoPoint(tile.getMinLon(), tile.getMaxLat())
                        )
                );

                if (polygon.intersects(square)) {
                    tiles.add(tile);
                }
            }
        }
        return tiles;
    }
}

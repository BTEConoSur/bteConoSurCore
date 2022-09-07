package pizzaaxx.bteconosur.terramap;

import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.coords.Coords2D;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TerramapTilesManager {

    public void drawPolygon(@NotNull List<Coords2D> points, Color color) {

        // FIND 15-ZOOM TILES THAT CONTAIN PART OF THE POLYGON

        List<TerramapTile> tiles = getIntersectingTiles(15, points);

    }

    public List<TerramapTile> getIntersectingTiles(int zoom, @NotNull List<Coords2D> points) {

        TerramapTile firstTile = new TerramapTile(points.get(0).getLon(), points.get(0).getLat(), zoom);

        // GET MINs AND MAXs
        int minX = firstTile.getX();
        int minY = firstTile.getY();

        int maxX = firstTile.getX();
        int maxY = firstTile.getY();

        for (Coords2D coords : points) {

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

        List<Coords2D> coords = new ArrayList<>(points);
        coords.add(coords.get(coords.size() - 1));

        for (int i = 0; i + 1 < coords.size(); i++) {



        }

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {



            }
        }

    }

}

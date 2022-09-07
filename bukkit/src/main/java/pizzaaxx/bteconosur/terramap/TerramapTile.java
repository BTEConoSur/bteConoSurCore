package pizzaaxx.bteconosur.terramap;

import org.jetbrains.annotations.NotNull;

public class TerramapTile {

    private final int x;
    private final int y;

    private final int zoom;

    private final double maxLon;
    private final double maxLat;

    private final double minLon;
    private final double minLat;

    public TerramapTile(int x, int y, int zoom) {

        this.x = x;
        this.y = y;

        this.zoom = zoom;

        int xSize = 360 / (2^zoom);
        int ySize = 180 / (2^zoom);

        // 0 -> -180
        // 360 -> 180

        minLon = (xSize * x) - 180;
        maxLon = minLon + xSize;

        // 0 -> 90
        // 180 -> -90

        maxLat = - (ySize * y) + 90;
        minLat = maxLat - ySize;
    }

    public TerramapTile(double lon, double lat, int zoom) {
        this((int) Math.floor((lon - 180) / (360 / (2^zoom))), (int) Math.floor((-(lat) + 90) / (180 / (2^zoom))), zoom);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZoom() {
        return zoom;
    }

    public double getMaxLon() {
        return maxLon;
    }

    public double getMaxLat() {
        return maxLat;
    }

    public double getMinLon() {
        return minLon;
    }

    public double getMinLat() {
        return minLat;
    }

    public boolean equals(@NotNull TerramapTile tile) {
        return (this.x == tile.x && this.y == tile.y && this.zoom == tile.zoom);
    }
}

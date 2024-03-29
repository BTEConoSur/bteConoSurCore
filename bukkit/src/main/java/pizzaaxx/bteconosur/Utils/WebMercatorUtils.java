package pizzaaxx.bteconosur.Utils;

import static java.lang.Math.*;

public class WebMercatorUtils {

    private WebMercatorUtils() {}

    /*
     * This is not really a limit of web Mercator,
     * but since tile positions are encoded as integers,
     * this is the max value that avoid overflows.
     */
    public static final int MAX_ZOOM = 30;

    /**
     *
     * @param longitude The longitude in degrees, between -180.0 and 180.0
     * @param zoomLevel The web-mercator zoom level
     *
     * @return The X pixel position corresponding to the given longitude
     * on a web-mercator map of the given zoom level, with 0;0 being the top left corner of the map.
     */
    public static double getXFromLongitude(double longitude, double zoomLevel){
        return getXFromLongitudeRads(toRadians(longitude), zoomLevel);
    }

    /**
     *
     * @param latitude The latitude in degrees, between -90.0 and 90.0
     * @param zoomLevel The web-mercator zoom level
     *
     * @return The Y pixel position corresponding to the given latitude
     * on a web-mercator map of the given zoom level, with 0;0 being the top left corner of the map.
     */
    public static double getYFromLatitude(double latitude, double zoomLevel){
        return getYFromLatitudeRads(Math.toRadians(latitude), zoomLevel);
    }

    /**
     *
     * @param longitude The longitude in radians, between -pi and pi
     * @param zoom The web-mercator zoom level
     *
     * @return The X position corresponding to the given longitude
     *         on a web-mercator map of the given zoom level, with 0;0 being the top left corner of the map.
     */
    public static double getXFromLongitudeRads(double longitude, double zoom){
        return pow(2d, zoom + 7d) * (longitude + PI) / PI;
    }

    /**
     *
     * @param latitude The latitude in radians, between -pi/2 and pi/2
     * @param zoom The web-mercator zoom level
     *
     * @return The Y position corresponding to the given latitude
     *         on a web-mercator map of the given zoom level, with 0;0 being the top left corner of the map.
     */
    public static double getYFromLatitudeRads(double latitude, double zoom){
        return 128d / PI * pow(2d, zoom) * (PI - log(Math.tan( PI / 4d  + latitude / 2d)));
    }

    /**
     * @param y a coordinate on a web-mercator map, as an integer with 0;0 as the top left corner
     * @param zoom The web-mercator zoom level
     *
     * @return The corresponding latitude in degrees, between -180.0 and 180.0
     */
    public static double getLatitudeFromY(double y, double zoom){
        return toDegrees(getLatitudeFromYRads(y, zoom));
    }

    /**
     * @param x a coordinate on a web-mercator map, as an integer with 0;0 as the top left corner
     * @param zoom The web-mercator zoom level
     *
     * @return The corresponding longitude in degrees, between -90.0 and 90.0
     */
    public static double getLongitudeFromX(double x, double zoom){
        return toDegrees(getLongitudeFromXRads(x, zoom));
    }

    /**
     * @param y a coordinate on a web-mercator map, as an integer with 0;0 as the top left corner
     * @param zoom The web-mercator zoom level
     *
     * @return The corresponding latitude in radians, between -pi and pi
     */
    public static double getLatitudeFromYRads(double y, double zoom){
        return 2 * atan(exp(-(y * PI / pow(2d, zoom + 7d) - PI))) - PI / 2;
    }

    /**
     * @param x a coordinate on a web-mercator map, as an integer with 0;0 as the top left corner
     * @param zoom The web-mercator zoom level
     *
     * @return The corresponding longitude in radians, between -pi/2 and pi/2
     */
    public static double getLongitudeFromXRads(double x, double zoom){
        return PI * x / pow(2d, 7d + zoom) - PI;
    }

    /**
     * @param tX    a tile X coordinate
     * @param tY    a tile Y coordinate
     * @param zoom  a zoom level for the map
     * @return true if (zoom, x, y) is a valid tile position
     */
    public static boolean isValidTilePosition(int zoom, int tX, int tY){
        int mS = 1 << zoom;
        return zoom >= 0 && zoom < MAX_ZOOM && tX >= 0 && tX < mS && tY >= 0 && tY < mS;
    }

    /**
     * Returns the length of a side of a map of the given  zoom level, in tile
     * It is simply 2 raised to the power of the zoom
     *
     * @param zoomLevel the zoom level of the map to consider
     * @return 2^zoomLevel
     */
    public static int getDimensionsInTile(int zoomLevel) {
        return 1 << zoomLevel;
    }

}

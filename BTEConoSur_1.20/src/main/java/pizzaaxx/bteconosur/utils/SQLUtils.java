package pizzaaxx.bteconosur.utils;

import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.nio.ByteBuffer;
import java.util.UUID;

public class SQLUtils {

    public static @NotNull UUID uuidFromBytes(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        long high = buffer.getLong();
        long low = buffer.getLong();
        return new UUID(high, low);
    }

    public static @NotNull Polygon polygonFromWKT(@NotNull String wkt) {
        String[] points = wkt.substring(10, wkt.length() - 2).split(",");
        Polygon polygon = new Polygon();
        for (String point : points) {
            String[] coords = point.trim().split(" ");
            polygon.addPoint(
                    (int) (Double.parseDouble(coords[0])),
                    (int) (Double.parseDouble(coords[1]))
            );
        }
        return polygon;
    }

}

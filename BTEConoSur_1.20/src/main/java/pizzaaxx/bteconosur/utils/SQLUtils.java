package pizzaaxx.bteconosur.utils;

import org.jetbrains.annotations.NotNull;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;

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

    public static @NotNull org.locationtech.jts.geom.Polygon polygonFromWKT(@NotNull String wkt) {
        String[] points = wkt.substring(9, wkt.length() - 2).split(",");
        Coordinate[] coordinates = new Coordinate[points.length];
        for (int i = 0; i < points.length - 1; i++) {
            String[] point = points[i].split(" ");
            coordinates[i] = new Coordinate(
                    Double.parseDouble(point[0]),
                    Double.parseDouble(point[1])
            );
            coordinates[points.length - 1] = coordinates[0];
        }
        return new GeometryFactory().createPolygon(
                coordinates
        );
    }

}

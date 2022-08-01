package pizzaaxx.bteconosur.terramap;

import com.sk89q.worldedit.BlockVector2D;
import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.coords.Coords2D;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class TerramapHandler implements HttpHandler {

    // localhost:8001/terramap/z/x/y

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        String uri = exchange.getRequestURI().toString();

        String parameters = uri.split("terramap/")[1];

        String[] parts = parameters.split("/");

        int zoom = Integer.parseInt(parts[0]);
        int x = Integer.parseInt(parts[1]);
        int y = Integer.parseInt(parts[2]);

        BufferedImage image = ImageIO.read(new URL("https://tile.openstreetmap.org/" + zoom + "/" + x + "/" + y + ".png"));

        List<BlockVector2D> points = getPointsFromRequest(zoom, x, y);

        ProtectedRegion region = new ProtectedPolygonalRegion("terramapRequest", points, 100, 100);


        // TODO IMPLEMENT CITIES WITH THIS
        region.getIntersectingRegions()
    }

    private @NotNull List<BlockVector2D> getPointsFromRequest(int zoom, int x, int y) {

        double xStep = 360.0 / (2 ^ zoom);
        double yStep = 180.0 / (2 ^ zoom);

        double minX = (xStep * x) - 180;
        double maxX = minX + xStep;

        double maxY = -(yStep * y) + 90;
        double minY = minX - yStep;

        List<BlockVector2D> points = new ArrayList<>();

        points.add(new Coords2D(maxY, maxX).toBlockVector2D());
        points.add(new Coords2D(maxY, minX).toBlockVector2D());
        points.add(new Coords2D(minY, minX).toBlockVector2D());
        points.add(new Coords2D(minY, maxX).toBlockVector2D());

        return points;
    }
}
package pizzaaxx.bteconosur.Terramap;

import com.sk89q.worldguard.util.net.HttpRequest;
import net.buildtheearth.terraplusplus.util.http.Http;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSur;
import pizzaaxx.bteconosur.Geo.Coords2D;
import pizzaaxx.bteconosur.Utils.ImageUtils;
import pizzaaxx.bteconosur.Utils.NumberUtils;
import pizzaaxx.bteconosur.Utils.WebMercatorUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileTime;
import java.util.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class TerramapHandler {

    private final BTEConoSur plugin;

    public TerramapHandler(BTEConoSur plugin) {
        this.plugin = plugin;
    }

    // terramap/final/Z/X,Y.png
    // terramap/layers/Z/X,Y.png

    public InputStream getTileStream(int x, int y, int zoom) throws IOException, IllegalArgumentException {

        if (zoom < 15 || zoom > 19) {
            throw new IllegalArgumentException();
        }

        double tiles = Math.pow(2, zoom);
        if (x < 0 || y < 0 || x >= tiles || y >= tiles) {
            throw new IllegalArgumentException();
        }

        File image = new File(plugin.getDataFolder(), "terramap/final/" + zoom + "/" + x + "," + y + ".png");
        if (image.exists()) {
            return Files.newInputStream(image.toPath());
        } else {
            HttpRequest request = HttpRequest.get(new URL("https://a.tile.openstreetmap.org/" + zoom + "/" + x + "/" + y + ".png"));
            return request.execute().getInputStream();
        }
    }

    public void drawPolygon(@NotNull List<Coords2D> coordinates, Color color, String id) throws IOException {
        this.drawPolygon(coordinates, color, 15, id);
    }

    private void drawPolygon(@NotNull List<Coords2D> coordinates, Color color, int zoom, String id) throws IOException {

        // 1. Find tile coordinates of bounding box.
        // 2. Create Buffered of size according to bounding box.
        // 3. Draw polygon on image.
        // 4. Cut image into tiles.
        // 5. Save tiles.
        // 6. Update tiles.

        Coords2D firstCoordinate = coordinates.get(0);
        double maxLat = firstCoordinate.getLat();
        double minLat = firstCoordinate.getLat();
        double maxLon = firstCoordinate.getLon();
        double minLon = firstCoordinate.getLon();
        for (Coords2D coordinate : coordinates) {
            if (maxLat < coordinate.getLat()) {
                maxLat = coordinate.getLat();
            }
            if (maxLon < coordinate.getLon()) {
                maxLon = coordinate.getLon();
            }
            if (minLat > coordinate.getLat()) {
                minLat = coordinate.getLat();
            }
            if (minLon > coordinate.getLon()) {
                minLon = coordinate.getLon();
            }


        } // FIND MAXs AND MINs

        int maxTileX = Math.floorDiv((int) WebMercatorUtils.getXFromLongitude(maxLon, zoom), 256);
        int minTileX = Math.floorDiv((int) WebMercatorUtils.getXFromLongitude(minLon, zoom), 256);
        int maxTileY = Math.floorDiv((int) WebMercatorUtils.getYFromLatitude(maxLat, zoom), 256);
        int minTileY = Math.floorDiv((int) WebMercatorUtils.getYFromLatitude(minLat, zoom), 256);

        plugin.log("maxY: " + maxTileY);
        plugin.log("minY: " + minTileY);

        int xSize = (Math.abs(maxTileX - minTileX) + 1) * 256;
        int ySize = (Math.abs(maxTileY - minTileY) + 1) * 256;

        BufferedImage image = new BufferedImage(xSize, ySize, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();

        int[] xCoordinates = new int[coordinates.size()];
        int[] yCoordinates = new int[coordinates.size()];

        double maxImageLon = WebMercatorUtils.getLongitudeFromX((maxTileX + 1) * 256, zoom);
        double minImageLon = WebMercatorUtils.getLongitudeFromX(minTileX * 256, zoom);
        double maxImageLat = WebMercatorUtils.getLatitudeFromY((minTileY + 1) * 256, zoom);
        double minImageLat = WebMercatorUtils.getLatitudeFromY(maxTileY * 256, zoom);

        int i = 0;
        for (Coords2D coordinate : coordinates) {
            xCoordinates[i] = (int) NumberUtils.getInNewRange(minImageLon, maxImageLon, 0, xSize, coordinate.getLon());
            yCoordinates[i] = (int) NumberUtils.getInNewRange(minImageLat, maxImageLat, 0, ySize, coordinate.getLat());
            i++;
        }
        Polygon polygon = new Polygon(xCoordinates, yCoordinates, coordinates.size());

        Color fillColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), 50);
        graphics.setColor(fillColor);
        graphics.fillPolygon(polygon);

        graphics.setColor(color);
        graphics.setStroke(new BasicStroke(5));
        graphics.drawPolygon(polygon);

        for (int x = 0; x < xSize; x += 256) {
            for (int y = 0; y < ySize; y += 256) {
                BufferedImage tile = image.getSubimage(x, y, 256, 256);

                int tileX = minTileX + (x / 256);
                int tileY = maxTileY + (y / 256);

                File tileFile = new File(plugin.getDataFolder(), "terramap/layers/" + zoom + "/" + tileX + "_" + tileY + "_" + id + ".png");
                tileFile.createNewFile();

                ImageIO.write(tile, "png", tileFile);

                this.updateTile(tileX, tileY, zoom);
            }
        }
    }

    private void deletePolygon(String id) throws IOException {
        this.deletePolygon(id, 15);
        this.deletePolygon(id, 16);
        this.deletePolygon(id, 17);
        this.deletePolygon(id, 18);
        this.deletePolygon(id, 19);
    }

    private void deletePolygon(String id, int zoom) throws IOException {
        File layersFolder = new File(plugin.getDataFolder(), "terramap/layers/" + zoom);
        File[] layers = layersFolder.listFiles((FilenameFilter) new RegexFileFilter("-?\\d{1,12}_-?\\d{1,12}_" + id + "\\.png"));
        if (layers == null) {
            return;
        }
        for (File layerFile: layers) {
            String name = layerFile.getName().replace(".png", "");
            String[] parts = name.split("_");
            int tileX = Integer.parseInt(parts[0]);
            int tileY = Integer.parseInt(parts[1]);
            if (layerFile.delete()) {
                this.updateTile(tileX, tileY, zoom);
            }
        }
    }

    private void updateTile(int x, int y, int zoom) throws IOException {
        File layersFolder = new File(plugin.getDataFolder(), "terramap/layers/" + zoom);
        File[] layers = layersFolder.listFiles((FilenameFilter) new RegexFileFilter(x + "_" + y + "_[a-zA-Z]{1,32}\\.png"));
        if (layers ==  null) {
            return;
        }

        HttpRequest request = HttpRequest.get(new URL("https://a.tile.openstreetmap.org/" + zoom + "/" + x + "/" + y + ".png"));
        InputStream inputStream = request.execute().getInputStream();
        BufferedImage baseTile = ImageIO.read(inputStream);
        Graphics2D graphics = baseTile.createGraphics();

        List<Map.Entry<File, FileTime>> fileTimes = new ArrayList<>();
        for (File layerFile : layers) {
            BasicFileAttributes attributes = Files.readAttributes(layerFile.toPath(), BasicFileAttributes.class);
            fileTimes.add(
                    new AbstractMap.SimpleEntry<>(
                            layerFile,
                            attributes.creationTime()
                    )
            );
        }
        fileTimes.sort(Map.Entry.comparingByValue());

        for (Map.Entry<File, FileTime> entry : fileTimes) {
            File layerFile = entry.getKey();

            BufferedImage layer = ImageIO.read(layerFile);
            graphics.drawImage(layer, 0, 0, new ImageObserver() {
                @Override
                public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
                    return false;
                }
            });
        }

        File tile = new File(plugin.getDataFolder(), "terramap/final/" + zoom + "/" + x + "_" + y + ".png");
        ImageIO.write(baseTile, "png", tile);
    }

}

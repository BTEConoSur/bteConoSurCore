package pizzaaxx.bteconosur.Terramap;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSur;
import pizzaaxx.bteconosur.Geo.Coords2D;
import pizzaaxx.bteconosur.Utils.ImageUtils;
import pizzaaxx.bteconosur.Utils.NumberUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileTime;
import java.util.*;
import java.util.List;

public class TerramapHandler {

    private final BTEConoSur plugin;

    public TerramapHandler(BTEConoSur plugin) {
        this.plugin = plugin;
    }

    // terramap/final/Z/X,Y.png
    // terramap/layers/Z/X,Y.png

    public InputStream getTileStream(int x, int y, int zoom) throws IOException {
        File image = new File(plugin.getDataFolder(), "terramap/final/" + zoom + "/" + x + "," + y + ".png");
        if (image.exists()) {
            return new FileInputStream(image);
        } else {
            URL url = new URL("a"); // TODO GET OSM URL
            return url.openStream();
        }
    }

    public void drawPolygon(@NotNull List<Coords2D> coordinates, Color color, String id) throws IOException {
        this.drawPolygon(coordinates, color, 15, id);
        this.drawPolygon(coordinates, color, 16, id);
        this.drawPolygon(coordinates, color, 17, id);
        this.drawPolygon(coordinates, color, 18, id);
        this.drawPolygon(coordinates, color, 19, id);
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

        int maxTileX = this.getTileX(maxLon, zoom);
        int minTileX = this.getTileX(minLon, zoom);
        int maxTileY = this.getTileY(maxLat, zoom);
        int minTileY = this.getTileY(minLat, zoom);

        int xSize = (maxTileX - minTileX + 1) * 256;
        int ySize = (maxTileY - minTileY + 1) * 256;

        BufferedImage image = new BufferedImage(xSize, ySize, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();

        int[] xCoordinates = new int[coordinates.size()];
        int[] yCoordinates = new int[coordinates.size()];

        int i = 0;
        for (Coords2D coordinate : coordinates) {
            xCoordinates[i] = (int) NumberUtils.getInNewRange(minLon, maxLon, 0, xSize, coordinate.getLon());
            yCoordinates[i] = (int) NumberUtils.getInNewRange(maxLat, minLat, 0, ySize, coordinate.getLat());
            i++;
        }
        Polygon polygon = new Polygon(xCoordinates, yCoordinates, coordinates.size());

        Color fillColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), 125);
        graphics.setColor(fillColor);
        graphics.fillPolygon(polygon);

        graphics.setColor(color);
        graphics.drawPolygon(polygon);

        for (int x = 0; x < xSize; x += 256) {
            for (int y = 0; y < ySize; y += 256) {
                BufferedImage tile = image.getSubimage(x, y, 256, 256);

                int tileX = minTileX + (x / 256);
                int tileY = maxTileY - (y / 256);
                File tileFile = new File(plugin.getDataFolder(), "terramap/layers/" + zoom + "/" + tileX + "_" + tileY + "_" + id + ".png");
                tileFile.createNewFile();

                InputStream is = ImageUtils.getStream(tile);
                FileOutputStream os = new FileOutputStream(tileFile, false);
                IOUtils.copy(is, os);

                this.updateTile(x / 256, y / 256, zoom);
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

        InputStream baseTileURL = new URL("https://tile.openstreetmap.org/" + x + "/" + y + "/" + zoom + ".png").openStream();
        BufferedImage baseTile = ImageIO.read(baseTileURL);
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
            graphics.drawImage(layer, 0, 0, null);
        }

        InputStream is = ImageUtils.getStream(baseTile);
        File tile = new File(plugin.getDataFolder(), "terramap/final/" + x + "_" + y + ".png");
        FileOutputStream os = new FileOutputStream(tile, false);
        IOUtils.copy(is, os);
    }

    private int getTileX(double lon, int zoom) {
        double tileXStep = 360.0 / (2 ^ zoom);
        double tileX = lon / tileXStep;
        return (int) Math.floor(tileX);
    }

    private int getTileY(double lat, int zoom) {
        double tileYStep = 180.0 / (2 ^ zoom);
        double tileY = lat / tileYStep;
        return (int) Math.floor(tileY);
    }

}

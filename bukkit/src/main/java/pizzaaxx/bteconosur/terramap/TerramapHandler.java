package pizzaaxx.bteconosur.terramap;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BteConoSur;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;

public class TerramapHandler implements HttpHandler {

    // localhost:8001/terramap/z/x/y

    private final BteConoSur plugin;

    public TerramapHandler(BteConoSur plugin) {
        this.plugin = plugin;
    }

    @Override
    public void handle(@NotNull HttpExchange exchange) throws IOException {

        String uri = exchange.getRequestURI().toString();

        String parameters = uri.split("terramap/")[1];

        String[] parts = parameters.split("/");

        int zoom = Integer.parseInt(parts[0]);
        int x = Integer.parseInt(parts[1]);
        int y = Integer.parseInt(parts[2]);

        File image = new File(plugin.getDataFolder(), "terramap/tiles/" + zoom + "/" + x + "," + y + ".png");

        OutputStream os = exchange.getResponseBody();

        if (image.exists()) {
            byte[] response = FileUtils.readFileToByteArray(image);

            os.write(response);
            os.close();

        } else {

            URL url = new URL("https://tile.openstreetmap.org/" + zoom + "/" + x + "/" + y + ".png");
            byte[] response = IOUtils.toByteArray(url);

            os.write(response);
            os.close();
        }

    }
}
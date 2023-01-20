package pizzaaxx.bteconosur.Terramap;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSur;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class TerramapRequestHandler implements HttpHandler {

    private final BTEConoSur plugin;

    public TerramapRequestHandler(BTEConoSur plugin) {
        this.plugin = plugin;
    }

    @Override
    public void handle(@NotNull HttpExchange exchange) throws IOException {

        byte[] bytes = "Test.".getBytes();
        exchange.sendResponseHeaders(200, bytes.length);
        exchange.getResponseBody().write(bytes);
        exchange.close();

        /*Map<String, Integer> values = new HashMap<>();
        for (String part : exchange.getRequestURI().getQuery().split("&")) {
            String[] sides = part.split("=");
            values.put(sides[0], Integer.parseInt(sides[1]));
        }
        try {
            InputStream is = plugin.getTerramapHandler().getTileStream(values.get("x"), values.get("y"), values.get("z"));
            OutputStream os = exchange.getResponseBody();
            byte[] bytes = IOUtils.toByteArray(is);
            exchange.sendResponseHeaders(200, bytes.length);
            os.write(bytes);
            exchange.close();
        } catch (IllegalArgumentException e) {
            byte[] bytes = "Wrong coordinates or zoom.".getBytes();
            exchange.sendResponseHeaders(404, bytes.length);
            exchange.getResponseBody().write(bytes);
            exchange.close();
        }*/
    }
}

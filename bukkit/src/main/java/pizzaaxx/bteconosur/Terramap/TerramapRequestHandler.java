package pizzaaxx.bteconosur.Terramap;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import pizzaaxx.bteconosur.BTEConoSur;

import java.io.IOException;

public class TerramapRequestHandler implements HttpHandler {

    private final BTEConoSur plugin;

    public TerramapRequestHandler(BTEConoSur plugin) {
        this.plugin = plugin;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

    }
}

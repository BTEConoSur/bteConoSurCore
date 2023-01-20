package pizzaaxx.bteconosur.Terramap;

import com.sun.net.httpserver.HttpServer;
import pizzaaxx.bteconosur.BTEConoSur;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class TerramapServer {

    private final BTEConoSur plugin;

    public TerramapServer(BTEConoSur plugin) {
        this.plugin = plugin;
    }

    public void init() throws IOException {

        HttpServer server = HttpServer.create(new InetSocketAddress(0), 0);
        server.setExecutor(Executors.newSingleThreadExecutor());
        server.createContext("/terramap", new TerramapRequestHandler(plugin));
        server.start();
        plugin.log("Terramap tiles server started.");

    }
}

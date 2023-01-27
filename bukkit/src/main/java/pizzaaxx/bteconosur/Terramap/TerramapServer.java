package pizzaaxx.bteconosur.Terramap;

import com.sun.net.httpserver.HttpServer;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.NetworkTrafficServerConnector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.session.DefaultSessionIdManager;
import org.eclipse.jetty.util.resource.FileResource;
import org.eclipse.jetty.util.thread.ExecutorThreadPool;
import pizzaaxx.bteconosur.BTEConoSur;

import java.io.File;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

public class TerramapServer {

    private final BTEConoSur plugin;
    private HttpServer server;

    public TerramapServer(BTEConoSur plugin) {
        this.plugin = plugin;
    }

    public void init() throws Exception {

        server = HttpServer.create(
                new InetSocketAddress(
                        "0.0.0.0",
                        25616
                ),
                0
        );
        server.setExecutor(Executors.newSingleThreadExecutor());
        server.createContext("/terramap", new TerramapRequestHandler(plugin));
        server.start();

    }

    public void stop() {
        if (server != null) {
            server.stop(5);
        }
    }

    public HttpServer getServer() {
        return server;
    }
}

package pizzaaxx.bteconosur.terramap;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class ImagesServer {

    public ImagesServer() {

        HttpServer server = null;
        try {
            server = HttpServer.create(new InetSocketAddress("localhost", 8001), 0);
            server.createContext("/terramap", new TerramapHandler());
            server.setExecutor((ThreadPoolExecutor) Executors.newFixedThreadPool(2));
            server.start();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
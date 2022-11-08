package pizzaaxx.bteconosur;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import pizzaaxx.bteconosur.Configuration.Configuration;
import pizzaaxx.bteconosur.Events.PreLoginEvent;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class BTEConoSur extends JavaPlugin {

    private final String CONSOLE_PREFIX = "§f[§2CONO §aSUR§f] §7>> ";

    private Connection connection;

    public Connection getDBConnection() {
        return connection;
    }

    @Override
    public void onEnable() {
        this.log("BUILD THE EARTH: CONO SUR");
        this.log("Developed by PIZZAAXX");
        this.log(" ");
        this.log("Starting plugin...");

        this.log("Starting database connection...");
        Configuration databaseConfig = new Configuration(this, "database");
        try {
            connection = DriverManager.getConnection(
                    databaseConfig.getString("url"),
                    databaseConfig.getString("username"),
                    databaseConfig.getString("password")
            );
        } catch (SQLException e) {
            this.log("Plugin starting stopped. Database connection failed.");
            e.printStackTrace();
            return;
        }
        this.log("Database connection established.");

        Bukkit.getPluginManager().registerEvents(new PreLoginEvent(this), this);
    }

    public void log(String message) {
        Bukkit.getConsoleSender().sendMessage(CONSOLE_PREFIX + message);
    }

    public void alert(String message) {
        Bukkit.getConsoleSender().sendMessage(CONSOLE_PREFIX + "§e" + message);
    }

    public void error(String message) {
        Bukkit.getConsoleSender().sendMessage(CONSOLE_PREFIX + "§c" + message);
    }
}

package pizzaaxx.bteconosur.server.player;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class DataManager extends pizzaaxx.bteconosur.yaml.Configuration {

    private final OfflinePlayer player;

    public DataManager(ServerPlayer serverPlayer) {
        super(Bukkit.getPluginManager().getPlugin("bteConoSur"), "playerData/" + serverPlayer.getPlayer().getUniqueId().toString());
        player = serverPlayer.getPlayer();
    }

    public OfflinePlayer getPlayer() {
        return player;
    }

}

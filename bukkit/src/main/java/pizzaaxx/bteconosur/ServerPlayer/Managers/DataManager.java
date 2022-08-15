package pizzaaxx.bteconosur.ServerPlayer.Managers;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import pizzaaxx.bteconosur.ServerPlayer.ServerPlayer;

public class DataManager extends pizzaaxx.bteconosur.configuration.Configuration {

    private final OfflinePlayer player;

    public DataManager(ServerPlayer serverPlayer) {
        super(Bukkit.getPluginManager().getPlugin("bteConoSur"), "playerData/" + serverPlayer.getPlayer().getUniqueId().toString());
        player = serverPlayer.getPlayer();
    }

    public OfflinePlayer getPlayer() {
        return player;
    }

}

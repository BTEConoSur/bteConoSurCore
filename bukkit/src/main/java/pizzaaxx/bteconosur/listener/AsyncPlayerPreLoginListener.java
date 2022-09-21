package pizzaaxx.bteconosur.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BteConoSur;
import pizzaaxx.bteconosur.ServerPlayer.PlayerRegistry;
import pizzaaxx.bteconosur.ServerPlayer.ServerPlayer;
import pizzaaxx.bteconosur.configuration.Configuration;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static pizzaaxx.bteconosur.Config.maxPlayers;

public class AsyncPlayerPreLoginListener implements Listener {

    private final PlayerRegistry playerRegistry;
    private final Random random = new Random();
    private final BteConoSur plugin;

    public AsyncPlayerPreLoginListener(PlayerRegistry playerRegistry, BteConoSur plugin) {
        this.playerRegistry = playerRegistry;
        this.plugin = plugin;
    }

    @EventHandler
    public void onPreLogin(@NotNull AsyncPlayerPreLoginEvent event) {

        boolean isFirst = false;
        File file = new File(plugin.getDataFolder(), "playerData/" + event.getUniqueId() + ".yml");
        try {
            if (file.createNewFile()) {
                Bukkit.getLogger().info("Created new playerData file for player with UUID " + event.getUniqueId());
            }
            isFirst = true;
        } catch (IOException e) {
            e.printStackTrace();
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "Ha ocurrido un error.");
        }

        Configuration data = new Configuration(plugin, "playerData/" + event.getUniqueId());

        if (isFirst) {
            data.set("isFirstJoin", true);
        }

        if (!data.getString("name").equals(event.getName())) {
            data.set("name", event.getName());
        }

        if (!data.contains("primaryGroup")) {
            data.set("primaryGroup", "default");
        }

        if (!data.contains("chat")) {
            data.set("chat.global", "global");
        }

        if (!data.contains("chat.default")) {
            data.set("chat.default", "global");
        }

        if (!data.contains("chat.hide")) {
            data.set("chat.hide", false);
        }

        if (!data.contains("increment")) {
            data.set("increment", 1);
        }

        if (!data.contains("scoreboard.hidden")) {
            data.set("scoreboard.hidden", false);
        }

        if (!data.contains("scoreboard.type")) {
            data.set("scoreboard.type", "server");
        }

        if (!data.contains("scoreboard.auto")) {
            data.set("scoreboard.auto", true);
        }

        data.save();

        ServerPlayer serverPlayer = plugin.getPlayerRegistry().get(event.getUniqueId());

        // PRIORIDAD DE ENTRADA

        if (Bukkit.getOnlinePlayers().size() >= maxPlayers) {
            int priority = serverPlayer.getGroupsManager().getEntrancePriority();

            int minPriority = 8;
            Map<Integer, List<Player>> players = new HashMap<>();
            for (Player player : Bukkit.getOnlinePlayers()) {
                int entrancePriority = playerRegistry.get(player.getUniqueId()).getGroupsManager().getEntrancePriority();
                List<Player> local = (players.containsKey(entrancePriority) ? players.get(entrancePriority) : new ArrayList<>());
                local.add(player);
                players.put(entrancePriority, local);

                if (entrancePriority < minPriority) {
                    minPriority = entrancePriority;
                }
            }

            if (priority <= minPriority) {
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "El servidor está lleno. Sube de rango o dona al proyecto para obtener mayor prioridad de entrada.");
            } else {
                int rndm = random.nextInt(players.get(minPriority).size());

                players.get(minPriority).get(rndm).kickPlayer("Ha entrado alguien con mayor prioridad de entrada que tú. Sube de rango o dona al proyecto para obtener mayor prioridad de entrada.");
            }

        }

        playerRegistry.load(serverPlayer.getId());
    }

}

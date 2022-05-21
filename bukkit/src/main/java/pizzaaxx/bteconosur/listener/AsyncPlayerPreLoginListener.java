package pizzaaxx.bteconosur.listener;

import com.destroystokyo.paper.profile.PlayerProfile;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.server.player.PlayerRegistry;
import pizzaaxx.bteconosur.server.player.ServerPlayer;

import java.util.*;

import static pizzaaxx.bteconosur.Config.maxPlayers;

public class AsyncPlayerPreLoginListener implements Listener {

    private final PlayerRegistry playerRegistry;
    private final Random random = new Random();

    public AsyncPlayerPreLoginListener(PlayerRegistry playerRegistry) {
        this.playerRegistry = playerRegistry;
    }

    @EventHandler
    public void onPreLogin(@NotNull AsyncPlayerPreLoginEvent event) {

        Bukkit.getConsoleSender().sendMessage("a");

        ServerPlayer serverPlayer = new ServerPlayer(event.getUniqueId());

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

        playerRegistry.add(serverPlayer);
    }

}

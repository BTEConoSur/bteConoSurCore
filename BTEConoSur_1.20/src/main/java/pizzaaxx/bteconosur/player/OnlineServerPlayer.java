package pizzaaxx.bteconosur.player;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.PeterMassmann.Conditions.SQLANDConditionSet;
import com.github.PeterMassmann.Conditions.SQLOperatorCondition;
import com.github.PeterMassmann.Values.SQLValue;
import com.github.PeterMassmann.Values.SQLValuesSet;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSurPlugin;
import pizzaaxx.bteconosur.player.chat.ChatManager;
import pizzaaxx.bteconosur.player.scoreboard.ScoreboardManager;
import pizzaaxx.bteconosur.player.worldedit.WorldEditManager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OnlineServerPlayer extends OfflineServerPlayer {

    private final BTEConoSurPlugin plugin;
    private final ScoreboardManager scoreboardManager;
    private final WorldEditManager worldEditManager;
    private final ChatManager chatManager;

    public OnlineServerPlayer(@NotNull BTEConoSurPlugin plugin, OfflineServerPlayer offlinePlayer) throws SQLException, JsonProcessingException {
        super(offlinePlayer);
        this.plugin = plugin;
        this.scoreboardManager = new ScoreboardManager(plugin, this);
        this.worldEditManager = new WorldEditManager(plugin, this);
        this.chatManager = new ChatManager(plugin, this);
    }

    public OfflineServerPlayer asOfflinePlayer() {
        return new OfflineServerPlayer(this);
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(super.getUUID());
    }

    @Override
    public void disconnected() throws SQLException {
        plugin.getRegionListener().lastVisitedRegions.remove(this.getUUID());
        super.lastLocation = this.getPlayer().getLocation();
        plugin.getSqlManager().update(
                "players",
                new SQLValuesSet(
                        new SQLValue(
                                "last_location", this.getPlayer().getLocation()
                        )
                ),
                new SQLANDConditionSet(
                        new SQLOperatorCondition(
                                "uuid", "=", super.getUUID()
                        )
                )
        ).execute();
    }

    public ScoreboardManager getScoreboardManager() {
        return scoreboardManager;
    }

    public WorldEditManager getWorldEditManager() {
        return worldEditManager;
    }

    public ChatManager getChatManager() {
        return chatManager;
    }

    public List<Component> getPrefixes() {
        List<Component> result = new ArrayList<>();
        for (Role role : super.getRoles()) {
            result.add(Component.text(role.getPrefix()));
        }

        switch (super.getProjectsManager().getBuilderRank()) {
            case BUILDER -> result.add(Component.text("[§9BUILDER§f]"));
            case APPLIER -> result.add(Component.text("[§7POSTULANTE§f]"));
            case NONE -> result.add(Component.text("[§fVISITA§f]"));
        }

        result.add(this.getChatManager().getCountryPrefixChat());
        return result;
    }

    @Override
    public void sendNotification(String minecraftMessage, String discordMessage) {
        this.getPlayer().sendMessage(minecraftMessage);
    }
}

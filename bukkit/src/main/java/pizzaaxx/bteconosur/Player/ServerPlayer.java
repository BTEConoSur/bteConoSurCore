package pizzaaxx.bteconosur.Player;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSur;
import pizzaaxx.bteconosur.Chat.PrefixHolder;
import pizzaaxx.bteconosur.Player.Managers.*;
import pizzaaxx.bteconosur.SQL.Columns.SQLColumnSet;
import pizzaaxx.bteconosur.SQL.Conditions.SQLANDConditionSet;
import pizzaaxx.bteconosur.SQL.Conditions.SQLOperatorCondition;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class ServerPlayer {

    private final BTEConoSur plugin;
    private final UUID uuid;
    private final String name;
    private final ChatManager chatManager;
    private final WorldEditManager worldEditManager;
    private final DiscordManager discordManager;
    private final MiscManager miscManager;
    private final ProjectManager projectManager;
    private final List<SecondaryRoles> secondaryRoles;

    public enum BuilderRank implements PrefixHolder {
        VISITA("§f[VISITA§f] §r", "[:flag_white:] "),
        POSTULANTE("§f[§7POSTULANTE§f] §r", "[:books:] "),
        BUILDER("§f[§9BUILDER§f] §r", "[:pick_hammer:] ");

        private final String prefix;
        private final String discordPrefix;

        BuilderRank(String prefix, String discordPrefix) {
            this.prefix = prefix;
            this.discordPrefix = discordPrefix;
        }

        @Override
        public String getPrefix() {
            return prefix;
        }

        @Override
        public String getDiscordPrefix() {
            return discordPrefix;
        }
    }

    public enum SecondaryRoles implements PrefixHolder {
        ADMIN("§f[§cADMIN§f] §r", "[:crown:] ", "§6§l"),
        MOD("§f[§5MOD§f] §r", "[:shield:] ", "§5§l"),
        STREAMER("§f[§aSTREAMER§f] §r", "[:video_game:] ", null),
        DONADOR("§f[§dDONADOR§f] §r", "[:gem:] ", null);

        private final String prefix;
        private final String discordPrefix;
        private final String chatColor;

        SecondaryRoles(String prefix, String discordPrefix, String chatColor) {
            this.prefix = prefix;
            this.discordPrefix = discordPrefix;
            this.chatColor = chatColor;
        }

        @Override
        public String getPrefix() {
            return prefix;
        }

        @Override
        public String getDiscordPrefix() {
            return discordPrefix;
        }

        public String getChatColor() {
            return chatColor;
        }
    }

    public ServerPlayer(@NotNull BTEConoSur plugin, UUID uuid) throws SQLException, JsonProcessingException {

        ResultSet set = plugin.getSqlManager().select(
                "players",
                new SQLColumnSet(
                        "*"
                ),
                new SQLANDConditionSet(
                        new SQLOperatorCondition(
                                "uuid", "=", uuid
                        )
                )
        ).retrieve();

        if (set.next()) {

            this.plugin = plugin;
            this.uuid = uuid;
            this.name = set.getString("name");
            this.secondaryRoles = new ArrayList<>();
            List<String> rawRoles = plugin.getJSONMapper().readValue(set.getString("roles"), ArrayList.class);
            for (String role : rawRoles) {
                secondaryRoles.add(SecondaryRoles.valueOf(role.toUpperCase()));
            }
            Collections.sort(secondaryRoles);

            this.chatManager = new ChatManager(plugin, this);
            this.worldEditManager = new WorldEditManager(plugin, this);
            this.discordManager = new DiscordManager(plugin, this);
            this.miscManager = new MiscManager(plugin, this, set);
            this.projectManager = new ProjectManager(plugin, this);

        } else {
            plugin.error("Missing player data: " + uuid);
            throw new SQLException();
        }

    }

    public ChatManager getChatManager() {
        return chatManager;
    }

    public WorldEditManager getWorldEditManager() {
        return worldEditManager;
    }

    public DiscordManager getDiscordManager() {
        return discordManager;
    }

    public MiscManager getMiscManager() {
        return miscManager;
    }

    public ProjectManager getProjectManager() {
        return projectManager;
    }

    public BTEConoSur getPlugin() {
        return plugin;
    }

    public UUID getUUID() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public boolean isBuilder() {
        return true;
    }

    public boolean canBuild(Location loc) {
        return true;
    }

    public List<String> getLore(boolean name) {
        List<String> lore = new ArrayList<>();
        if (name) {
            lore.add("§a§l" + this.name);
            lore.add("§r ");
        }
        if (discordManager.isLinked()) {
            lore.add("§aDiscord: §f" + discordManager.getName() + "#" + discordManager.getDiscriminator());
        } else {
            lore.add("§aDiscord: §fN/A");
        }

        /*
        String[][] values = new String[4][0];
        values[0][0] = "País";
        values[0][1] = "Puntos";
        values[0][2] = "P. Activos";
        values[0][3] = "P. Terminados";
        */

        return lore;
    }

    public void sendNotification(
            String minecraftMessage,
            String discordMessage
    ) {
        plugin.getNotificationsService().sendNotification(this.uuid, minecraftMessage, discordMessage);
    }

    public BuilderRank getBuilderRank() {
        if (projectManager.getFinishedProjects() > 0) {
            return BuilderRank.BUILDER;
        } else if (projectManager.getAllProjectIDs().size() > 0) {
            return BuilderRank.POSTULANTE;
        } else {
            return BuilderRank.VISITA;
        }
    }

    public List<SecondaryRoles> getSecondaryRoles() {
        return secondaryRoles;
    }
}

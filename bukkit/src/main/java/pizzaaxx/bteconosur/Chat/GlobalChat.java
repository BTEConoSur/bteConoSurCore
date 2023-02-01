package pizzaaxx.bteconosur.Chat;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import pizzaaxx.bteconosur.BTEConoSur;
import pizzaaxx.bteconosur.Countries.Country;
import pizzaaxx.bteconosur.Player.ServerPlayer;
import xyz.upperlevel.spigot.book.BookUtil;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class GlobalChat implements Chat {

    private final BTEConoSur plugin;
    private final ChatHandler handler;


    Set<UUID> players = new HashSet<>();

    public GlobalChat(BTEConoSur plugin, ChatHandler handler) {
        this.plugin = plugin;
        this.handler = handler;
    }

    @Override
    public boolean isUnloadable() {
        return false;
    }

    @Override
    public String getID() {
        return "global";
    }

    @Override
    public String getDisplayName() {
        return "Global";
    }

    @Override
    public String getEmoji() {
        return ":globe_with_meridians:";
    }

    @Override
    public boolean acceptsPlayer(UUID uuid) {
        return true;
    }

    @Override
    public Set<UUID> getPlayers() {
        return players;
    }

    @Override
    public void addPlayer(UUID uuid) {
        if (!players.contains(uuid)) {
            players.add(uuid);
        }
    }

    @Override
    public void removePlayer(UUID uuid) {
        if (players.contains(uuid)) {
            players.remove(uuid);
            handler.tryUnregister(this);
        }
    }

    @Override
    public void sendMessage(UUID uuid, String message) {
        ServerPlayer senderPlayer = plugin.getPlayerRegistry().get(uuid);
        for (UUID playerUUID : players) {
            ServerPlayer serverPlayer = plugin.getPlayerRegistry().get(playerUUID);
            if (!serverPlayer.getChatManager().isHidden()) {
                Bukkit.getPlayer(playerUUID).sendMessage(
                        BookUtil.TextBuilder.of("<").color(ChatColor.WHITE).build(),
                        BookUtil.TextBuilder.of(senderPlayer.getName()).onHover(BookUtil.HoverAction.showText(String.join("\n", serverPlayer.getLore(true)))).build(),
                        BookUtil.TextBuilder.of("> ").color(ChatColor.WHITE).build(),
                        BookUtil.TextBuilder.of(message).color(ChatColor.WHITE).build()
                );
            }
        }

        for (Country country : plugin.getCountryManager().getAllCountries()) {
            country.getGlobalChatChannel().sendMessage(
                    "<:chat:1042295395625209886> **" + senderPlayer.getName() + ":** " + message
            ).queue();
        }

    }

    @Override
    public void broadcast(String message, boolean ignoreHidden) {
        for (UUID uuid : players) {
            ServerPlayer serverPlayer = plugin.getPlayerRegistry().get(uuid);
            if (ignoreHidden || !serverPlayer.getChatManager().isHidden()) {
                Bukkit.getPlayer(uuid).sendMessage(message);
            }
        }
    }
}

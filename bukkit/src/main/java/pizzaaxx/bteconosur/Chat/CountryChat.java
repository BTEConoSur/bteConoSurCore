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

public class CountryChat implements Chat {

    private final BTEConoSur plugin;
    private final ChatHandler handler;

    private final Country country;

    private final Set<UUID> players = new HashSet<>();

    public CountryChat(BTEConoSur plugin, ChatHandler handler, Country country) {
        this.plugin = plugin;
        this.handler = handler;
        this.country = country;
    }

    @Override
    public boolean isUnloadable() {
        return false;
    }

    @Override
    public String getID() {
        return country.getName();
    }

    @Override
    public String getDisplayName() {
        return country.getDisplayName();
    }

    @Override
    public String getEmoji() {
        return ":flag_" + country.getAbbreviation() + ":";
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
            ServerPlayer s = plugin.getPlayerRegistry().get(uuid);
            this.broadcast(handler.getPrefix() + "§a" + s.getName() + " §fha entrado al chat.", false);
            players.add(uuid);
            country.getCountryChatChannel().sendMessage("<:plus:1042295433969537055> **" + plugin.getPlayerRegistry().get(uuid).getName() + "** ha entrado al chat.").queue();
        }
    }

    @Override
    public void removePlayer(UUID uuid) {
        if (players.contains(uuid)) {
            players.remove(uuid);
            ServerPlayer s = plugin.getPlayerRegistry().get(uuid);
            this.broadcast(handler.getPrefix() + "§a" + s.getName() + " §fha salido del chat.", false);
            handler.tryUnregister(this);
            country.getCountryChatChannel().sendMessage("<:minus:1042295467322654736> **" + plugin.getPlayerRegistry().get(uuid).getName() + "** ha salido del chat.").queue();
        }
    }

    @Override
    public void sendMessageFromOther(Chat originChat, UUID uuid, String message) {
        ServerPlayer senderPlayer = plugin.getPlayerRegistry().get(uuid);
        for (UUID playerUUID : players) {
            ServerPlayer serverPlayer = plugin.getPlayerRegistry().get(playerUUID);
            if (!serverPlayer.getChatManager().isHidden()) {
                Bukkit.getPlayer(playerUUID).sendMessage(
                        BookUtil.TextBuilder.of("§f[§ePING§f] §7(" + originChat.getDisplayName() + ") §f<").color(ChatColor.WHITE).build(),
                        BookUtil.TextBuilder.of(senderPlayer.getName()).color(ChatColor.GREEN).onHover(BookUtil.HoverAction.showText(String.join("\n", serverPlayer.getLore(true)))).build(),
                        BookUtil.TextBuilder.of("> ").color(ChatColor.WHITE).build(),
                        BookUtil.TextBuilder.of(message).color(ChatColor.WHITE).build()
                );
            }
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
                        BookUtil.TextBuilder.of(senderPlayer.getName()).color(ChatColor.GREEN).onHover(BookUtil.HoverAction.showText(String.join("\n", serverPlayer.getLore(true)))).build(),
                        BookUtil.TextBuilder.of("> ").color(ChatColor.WHITE).build(),
                        BookUtil.TextBuilder.of(message).color(ChatColor.WHITE).build()
                );
            }
        }

        country.getCountryChatChannel().sendMessage(
                "<:chat:1042295395625209886> **" + senderPlayer.getName() + ":** " + ChatColor.stripColor(message)
        ).queue();
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

package pizzaaxx.bteconosur.Chat;

import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BteConoSur;
import pizzaaxx.bteconosur.ServerPlayer.ChatManager;
import pizzaaxx.bteconosur.ServerPlayer.GroupsManager;
import pizzaaxx.bteconosur.ServerPlayer.ServerPlayer;
import pizzaaxx.bteconosur.configuration.Configuration;
import xyz.upperlevel.spigot.book.BookUtil;

import java.util.*;

import static pizzaaxx.bteconosur.Config.gateway;

public class GlobalChat implements IChat {
    private final BteConoSur plugin;

    public GlobalChat(BteConoSur plugin) {
        this.plugin = plugin;
    }

    private final Set<UUID> members = new HashSet<>();

    @Override
    public String getId() {
        return "global";
    }

    @Override
    public String getDisplayName() {
        return "Global";
    }

    @Override
    public String getDiscordEmoji() {
        return ":earth_americas:";
    }

    @Override
    public Set<UUID> getMembers() {
        return members;
    }

    @Override
    public void sendMessage(String message, UUID member) {

        ServerPlayer s = plugin.getPlayerRegistry().get(member);
        GroupsManager gManager = s.getGroupsManager();
        ChatManager cManager = s.getChatManager();

        // MINECRAFT MESSAGE
        List<BaseComponent> parts = new ArrayList<>();

        parts.add(
                BookUtil.TextBuilder.of(gManager.getPrimaryGroup().getAsPrefix() + " ").build()
        );

        for (GroupsManager.SecondaryGroup secondaryGroup : gManager.getSecondaryGroups()) {
            parts.add(
                    BookUtil.TextBuilder.of(secondaryGroup.getAsPrefix() + " ").build()
            );
        }

        if (cManager.hasCountryPrefix()) {
            parts.add(
                    BookUtil.TextBuilder.of(cManager.getCountryPrefix() + " ").build()
            );
        }

        parts.add(BookUtil.TextBuilder.of("§f<").build());
        parts.add(
                BookUtil.TextBuilder.of(cManager.getDisplayName())
                        .onHover(BookUtil.HoverAction.showText(s.getLoreWithoutTitle()))
                        .build()
        );
        parts.add(BookUtil.TextBuilder.of("§f> ").build());

        parts.add(BookUtil.TextBuilder.of("§f" + message).build());

        for (UUID uuid : members) {
            Player player = Bukkit.getPlayer(uuid);

            player.sendMessage(parts.toArray(new BaseComponent[0]));
        }

        // DISCORD
        List<String> strings = new ArrayList<>();
        strings.add("<:EmojiChat:848630810667909140> **");

        GroupsManager groupsManager = s.getGroupsManager();

        strings.add("[" + groupsManager.getPrimaryGroup().getDiscordEmoji() + "] ");

        for (GroupsManager.SecondaryGroup group : groupsManager.getSecondaryGroups()) {
            strings.add("[" + group.getDiscordEmoji() + "] ");

        }

        strings.add(s.getName() + ":** " + ChatColor.stripColor(message.replace("~", "**")));

        plugin.getGateway().sendMessage(String.join("", strings)).queue();

    }

    @Override
    public void broadcast(String message) {

        for (UUID uuid : members) {

            if (!plugin.getPlayerRegistry().get(uuid).getChatManager().isHidden()) {
                Bukkit.getPlayer(uuid).sendMessage(message);
            }

        }

    }

    @Override
    public void broadcast(String message, boolean ignoreHidden) {

        for (UUID uuid : members) {

            if (ignoreHidden) {
                Bukkit.getPlayer(uuid).sendMessage(message);
            } else {
                if (!plugin.getPlayerRegistry().get(uuid).getChatManager().isHidden()) {
                    Bukkit.getPlayer(uuid).sendMessage(message);
                }
            }


        }

    }

    @Override
    public void receiveMember(UUID uuid) {
        Bukkit.getPlayer(uuid).sendMessage(CHAT_PREFIX + "Te has unido al chat §aGLOBAL§f. §7(Jugadores: " + members.size() + ")");
        members.add(uuid);
        ServerPlayer s = plugin.getPlayerRegistry().get(uuid);
        broadcast(CHAT_PREFIX + s.getChatManager().getDisplayName() + "§f se ha unido al chat.", true);

    }

    @Override
    public void sendMember(UUID uuid, @NotNull IChat chat) {

        members.remove(uuid);
        ServerPlayer s = plugin.getPlayerRegistry().get(uuid);
        broadcast(CHAT_PREFIX + s.getChatManager().getDisplayName() + "§f ha abandonado el chat.", true);
        chat.receiveMember(uuid);

    }

    public BteConoSur getPlugin() {
        return plugin;
    }
}

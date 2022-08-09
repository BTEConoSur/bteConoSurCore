package pizzaaxx.bteconosur.chats;

import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pizzaaxx.bteconosur.BteConoSur;
import pizzaaxx.bteconosur.server.player.ChatManager;
import pizzaaxx.bteconosur.server.player.GroupsManager;
import pizzaaxx.bteconosur.server.player.ServerPlayer;
import xyz.upperlevel.spigot.book.BookUtil;

import java.util.*;

public class GlobalChat implements IChat {

    private final String CHAT_PREFIX = "§f[§aCHAT§f] §7>>§r ";
    private final BteConoSur plugin;

    public GlobalChat(BteConoSur plugin) {
        this.plugin = plugin;
    }

    private final Set<UUID> members = new HashSet<>();

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

        members.add(uuid);
        ServerPlayer s = plugin.getPlayerRegistry().get(uuid);
        broadcast(CHAT_PREFIX + s.getChatManager().getDisplayName() + "§f se ha unido al chat.", true);

    }

    @Override
    public void sendMember(UUID uuid, IChat chat) {

        members.remove(uuid);
        ServerPlayer s = plugin.getPlayerRegistry().get(uuid);
        broadcast(CHAT_PREFIX + s.getChatManager().getDisplayName() + "§f ha abandonado el chat.", true);
        chat.receiveMember(uuid);

    }
}

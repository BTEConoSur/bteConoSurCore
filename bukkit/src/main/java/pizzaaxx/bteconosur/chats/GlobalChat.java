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
            Bukkit.getPlayer(uuid).sendMessage(message);
        }

    }

    @Override
    public void receiveMember(UUID uuid) {

    }

    @Override
    public void sendMember(IChat chat) {

    }
}

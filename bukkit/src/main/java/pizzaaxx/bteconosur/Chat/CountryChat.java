package pizzaaxx.bteconosur.Chat;

import net.md_5.bungee.api.chat.BaseComponent;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BteConoSur;
import pizzaaxx.bteconosur.country.Country;
import pizzaaxx.bteconosur.ServerPlayer.Managers.ChatManager;
import pizzaaxx.bteconosur.ServerPlayer.Managers.GroupsManager;
import pizzaaxx.bteconosur.ServerPlayer.Managers.PointsManager;
import pizzaaxx.bteconosur.ServerPlayer.ServerPlayer;
import xyz.upperlevel.spigot.book.BookUtil;

import java.util.*;

public class CountryChat implements IChat {

    private final BteConoSur plugin;
    private final Country country;
    private final Set<UUID> members = new HashSet<>();

    public CountryChat(@NotNull Country country) {

        this.plugin = country.getPlugin();
        this.country = country;

    }

    @Override
    public String getId() {
        return "country_" + country.getName();
    }

    @Override
    public String getDisplayName() {
        return StringUtils.capitalize(country.getName());
    }

    @Override
    public String getDiscordEmoji() {
        return country.getDiscordEmoji();
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
        PointsManager pManager = s.getPointsManager();

        // MINECRAFT MESSAGE
        List<BaseComponent> parts = new ArrayList<>();

        GroupsManager.PrimaryGroup primaryGroup = gManager.getPrimaryGroup();
        if (primaryGroup == GroupsManager.PrimaryGroup.BUILDER) {

            PointsManager.BuilderRank bRank = PointsManager.BuilderRank.getFrom(pManager.getPoints(country));
            parts.add(
                    BookUtil.TextBuilder.of(bRank.getAsPrefix() + " ").build()
            );

        } else {
            parts.add(
                    BookUtil.TextBuilder.of(gManager.getPrimaryGroup().getAsPrefix() + " ").build()
            );
        }


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
        Bukkit.getPlayer(uuid).sendMessage(CHAT_PREFIX + "Te has unido al chat de §a" + country.getName().toUpperCase() + "§f. §7(Jugadores: " + members.size() + ")");
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

    public Country getCountry() {
        return country;
    }

    @Override
    public void quitMember(UUID uuid) {
        members.remove(uuid);
        ServerPlayer s = plugin.getPlayerRegistry().get(uuid);
        broadcast(CHAT_PREFIX + s.getChatManager().getDisplayName() + "§f ha abandonado el chat.", true);
        if (members.isEmpty()) {
            plugin.getChatManager().remove(this);
        }
    }
}

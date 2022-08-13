package pizzaaxx.bteconosur.Chat;

import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BteConoSur;
import pizzaaxx.bteconosur.country.cities.projects.Project;
import pizzaaxx.bteconosur.country.cities.projects.ProjectsRegistry;
import pizzaaxx.bteconosur.ServerPlayer.ChatManager;
import pizzaaxx.bteconosur.ServerPlayer.GroupsManager;
import pizzaaxx.bteconosur.ServerPlayer.PointsManager;
import pizzaaxx.bteconosur.ServerPlayer.ServerPlayer;
import xyz.upperlevel.spigot.book.BookUtil;

import java.util.*;

public class ProjectChat implements IChat {

    private final String id;
    private final ProjectsRegistry registry;
    private final Set<UUID> members = new HashSet<>();
    private final BteConoSur plugin;

    public ProjectChat(@NotNull Project project) {
        this.id = project.getId();
        this.registry = project.getRegistry();
        this.plugin = project.getPlugin();
    }

    public ProjectChat(@NotNull String id, @NotNull ProjectsRegistry registry) {
        this.id = id;
        this.registry = registry;
        this.plugin = registry.getPlugin();
    }

    @Override
    public String getId() {
        return "project_" + id;
    }

    @Override
    public String getDisplayName() {
        return "Proyecto " + registry.get(id).getName();
    }

    @Override
    public String getDiscordEmoji() {
        return ":hammer_pick:";
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
        Project project = registry.get(id);

        // MINECRAFT MESSAGE
        List<BaseComponent> parts = new ArrayList<>();

        GroupsManager.PrimaryGroup primaryGroup = gManager.getPrimaryGroup();
        if (primaryGroup == GroupsManager.PrimaryGroup.BUILDER) {

            PointsManager.BuilderRank bRank = PointsManager.BuilderRank.getFrom(pManager.getPoints(project.getCountry()));
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

        if (project.getOwner() == s.getId()) {
            parts.add(
                    BookUtil.TextBuilder.of("§f[§6LÍDER§f]").build()
            );
        } else {
            parts.add(
                    BookUtil.TextBuilder.of("§f[§7MIEMBRO§f]").build()
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
        Bukkit.getPlayer(uuid).sendMessage(CHAT_PREFIX + "Te has unido al chat del proyecto §a" + getProject().getName() + "§f. §7(Jugadores: " + members.size() + ")");
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
        if (members.isEmpty()) {
            plugin.getChatManager().remove(this);
        }
    }

    public BteConoSur getPlugin() {
        return plugin;
    }

    public Project getProject() {
        return registry.get(id);
    }
}

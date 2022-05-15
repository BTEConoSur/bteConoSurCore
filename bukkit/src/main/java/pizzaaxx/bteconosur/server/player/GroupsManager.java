package pizzaaxx.bteconosur.server.player;

import net.dv8tion.jda.api.EmbedBuilder;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import net.luckperms.api.node.types.InheritanceNode;
import org.apache.commons.lang.StringUtils;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static pizzaaxx.bteconosur.Config.gateway;
import static pizzaaxx.bteconosur.Config.groupsPrefixes;
import static pizzaaxx.bteconosur.ranks.PromoteDemote.*;

public class GroupsManager {

    public enum PrimaryGroup {

        DEFAULT(1), POSTULANTE(2), BUILDER(3), MOD(4), ADMIN(5);

        public static PrimaryGroup fromInt(int i) {
            if (i < 6 && i > 0) {
                switch (i) {
                    case 2:
                        return POSTULANTE;
                    case 3:
                        return BUILDER;
                    case 4:
                        return MOD;
                    case 5:
                        return ADMIN;
                    default:
                        return DEFAULT;
                }
            }
            return null;
        }

        private final int priority;

        PrimaryGroup(Integer priority) {
            this.priority = priority;
        }

        public int getPriority() {
            return priority;
        }

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }

        public String getAsPrefix() {
            return groupsPrefixes.get(this.toString());
        }

        public String getCapitalized() {
            return StringUtils.capitalize(this.toString());
        }
    }

    public enum SecondaryGroup {
        EVENTO, DONADOR, STREAMER;

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }

        public String getAsPrefix() {
            return groupsPrefixes.get(this.toString());
        }

        public String getCapitalized() {
            return StringUtils.capitalize(this.toString());
        }
    }

    private final DataManager data;
    private final ServerPlayer serverPlayer;
    private PrimaryGroup primaryGroup;
    private final List<SecondaryGroup> secondaryGroups = new ArrayList<>();

    public GroupsManager(ServerPlayer s) {
        serverPlayer = s;
        data = s.getDataManager();
        primaryGroup = PrimaryGroup.valueOf(data.getString("primaryGroup").toUpperCase());
        if (data.contains("secondaryGroups")) {
            for (String group : data.getStringList("secondaryGroups")) {
                secondaryGroups.add(SecondaryGroup.valueOf(group.toUpperCase()));
            }
        }
    }

    public void setPrimaryGroup(PrimaryGroup group) {
        if (primaryGroup != group) {
            addLuckPermsGroup(group.toString());
            removeLuckPermsGroup(primaryGroup.toString());
            EmbedBuilder embed = new EmbedBuilder();
            String name = serverPlayer.getName();
            String action;
            String prefix;
            if (group.getPriority() > primaryGroup.getPriority()) {
                embed.setColor(new Color(0, 255, 42));
                action = "promovid@";
                prefix = promotePrefix;
            } else {
                embed.setColor(new Color(255, 0, 0));
                action = "degradad@";
                prefix = demotePrefix;
            }
            String newGroup = group.toString().replace("default", "visita").toUpperCase();
            embed.setAuthor(name + " ha sido " + action + " a " + newGroup, null, "https://cravatar.eu/helmavatar/" + name + "/190.png");
            serverPlayer.sendNotification(prefix + "Has sido " + action + " a §a" + newGroup + "§f.");
            gateway.sendMessageEmbeds(embed.build()).queue();
            primaryGroup = group;
            data.set("primaryGroup", group.toString());
            data.save();
            ScoreboardManager manager = serverPlayer.getScoreboardManager();
            if (manager.getType() == ScoreboardManager.ScoreboardType.ME) {
                serverPlayer.getScoreboardManager().update();
            }
        }
    }

    public PrimaryGroup getPrimaryGroup() {
        return primaryGroup;
    }

    public void addSecondaryGroup(SecondaryGroup group) {
        if (!secondaryGroups.contains(group)) {
            secondaryGroups.add(group);
            List<String> groups = new ArrayList<>();
            secondaryGroups.forEach(g -> groups.add(g.toString()));
            Collections.sort(groups);
            data.set("secondaryGroups", groups);
            data.save();
            addLuckPermsGroup(group.toString());
            ScoreboardManager manager = serverPlayer.getScoreboardManager();
            if (manager.getType() == ScoreboardManager.ScoreboardType.ME) {
                serverPlayer.getScoreboardManager().update();
            }
        }
    }

    public int getEntrancePriority() {
        if (primaryGroup == PrimaryGroup.ADMIN) {
            return 8;
        }
        if (primaryGroup == PrimaryGroup.MOD) {
            return 7;
        }
        if (secondaryGroups.contains(SecondaryGroup.DONADOR)) {
            return 6;
        }
        if (secondaryGroups.contains(SecondaryGroup.STREAMER)) {
            return 5;
        }
        if (primaryGroup == PrimaryGroup.BUILDER) {
            return 4;
        }
        if (primaryGroup == PrimaryGroup.POSTULANTE) {
            return 3;
        }
        if (secondaryGroups.contains(SecondaryGroup.EVENTO)) {
            return 2;
        }
        if (primaryGroup == PrimaryGroup.DEFAULT) {
            return 1;
        }
        return 1;
    }

    public void removeSecondaryGroup(SecondaryGroup group) {
        if (secondaryGroups.contains(group)) {
            secondaryGroups.remove(group);
            List<String> groups = new ArrayList<>();
            secondaryGroups.forEach(g -> groups.add(g.toString()));
            Collections.sort(groups);
            data.set("secondaryGroups", (groups.isEmpty() ? null : groups));
            data.save();
            removeLuckPermsGroup(group.toString());
            ScoreboardManager manager = serverPlayer.getScoreboardManager();
            if (manager.getType() == ScoreboardManager.ScoreboardType.ME) {
                serverPlayer.getScoreboardManager().update();
            }
        }
    }

    public List<SecondaryGroup> getSecondaryGroups() {
        return secondaryGroups;
    }

    // LUCKPERMS

    private void removeLuckPermsGroup(String group) {
        InheritanceNode node = InheritanceNode.builder(group).build();

        UserManager userManager = lp.getUserManager();
        CompletableFuture<User> userFuture = userManager.loadUser(serverPlayer.getPlayer().getUniqueId());

        userFuture.thenAcceptAsync(user -> {
            user.data().remove(node);

            lp.getUserManager().saveUser(user);
        });
    }

    private void addLuckPermsGroup(String group) {

        InheritanceNode node = InheritanceNode.builder(group).build();

        UserManager userManager = lp.getUserManager();
        CompletableFuture<User> userFuture = userManager.loadUser(serverPlayer.getPlayer().getUniqueId());

        userFuture.thenAcceptAsync(user -> {
            user.data().add(node);

            lp.getUserManager().saveUser(user);
        });
    }

    public void promote() {
        PrimaryGroup actual = getPrimaryGroup();
        if (actual != PrimaryGroup.ADMIN) {
            setPrimaryGroup(PrimaryGroup.fromInt(actual.getPriority() + 1));
            secondaryGroups.remove(SecondaryGroup.EVENTO);
        }
    }

    public void demote() {
        PrimaryGroup actual = getPrimaryGroup();
        if (actual != PrimaryGroup.DEFAULT) {
            setPrimaryGroup(PrimaryGroup.fromInt(actual.getPriority() - 1));
        }
    }

    public void checkGroups() {
        if (primaryGroup == PrimaryGroup.ADMIN || primaryGroup == PrimaryGroup.MOD) {
            return;
        }

        // ---------
        // Pts   Pjts   Group
        //  0     0      DEFAULT
        //  0     1      POSTULANTE
        // >15    ?      BUILDER
        // ---------

        ProjectsManager projectsManager = serverPlayer.getProjectsManager();
        PointsManager pointsManager = serverPlayer.getPointsManager();
        int points = pointsManager.getMaxPoints().getValue();
        int projects = projectsManager.getTotalProjects();

        DiscordManager manager = serverPlayer.getDiscordManager();

        if (points < 15) {
            if (projects == 0) {
                if (primaryGroup != PrimaryGroup.DEFAULT) {
                    setPrimaryGroup(PrimaryGroup.DEFAULT);
                }
            } else  if (projects > 0) {
                if (primaryGroup != PrimaryGroup.POSTULANTE) {
                    setPrimaryGroup(PrimaryGroup.POSTULANTE);
                }
            }
        } else {
            if (primaryGroup != PrimaryGroup.BUILDER) {
                setPrimaryGroup(PrimaryGroup.BUILDER);
            }
        }
    }
}

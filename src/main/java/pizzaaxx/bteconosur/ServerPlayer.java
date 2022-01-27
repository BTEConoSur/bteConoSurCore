package pizzaaxx.bteconosur;

import net.dv8tion.jda.api.entities.User;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.UserManager;
import net.luckperms.api.node.types.InheritanceNode;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import pizzaaxx.bteconosur.chats.Chat;
import pizzaaxx.bteconosur.country.Country;
import pizzaaxx.bteconosur.playerData.PlayerData;
import pizzaaxx.bteconosur.projects.Project;
import pizzaaxx.bteconosur.yaml.YamlManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static pizzaaxx.bteconosur.bteConoSur.pluginFolder;
import static pizzaaxx.bteconosur.discord.bot.conoSurBot;
import static pizzaaxx.bteconosur.ranks.main.primaryGroupsList;
import static pizzaaxx.bteconosur.ranks.promote_demote.lp;

public class ServerPlayer {

    private OfflinePlayer player = null;
    private final PlayerData data;

    // CONSTRUCTOR
    public ServerPlayer(OfflinePlayer p) {
        this.player = p;

        data = new PlayerData(p);
    }

    public ServerPlayer(User user) throws Exception {
        if (YamlManager.getYamlData(pluginFolder, "link/links.yml").containsKey(user.getId())) {
            this.player = Bukkit.getOfflinePlayer(UUID.fromString((String) YamlManager.getYamlData(pluginFolder, "link/links.yml").get(user.getId())));
            this.data = new PlayerData(this.player);
        } else {
            throw new Exception();
        }
    }

    // PROJECTS

    public Integer getTotalFinishedProjects() {
        int total = 0;

        for (String country : "ar bo cl py pe uy".split(" "))
            if (data.getData("finished_projects_" + country) != null) {
                total = total + (Integer) new PlayerData(this.player).getData("finished_projects_" + country);
            }

        return total;
    }

    public List<Project> getProjects() {
        List<Project> projects = new ArrayList<>();
        if (data.getData("projects") != null) {
            for (String id : (List<String>) data.getData("projects")) {
                try {
                    projects.add(new Project(id));
                } catch (Exception e) {
                    Bukkit.getConsoleSender().sendMessage("No se pudo encontrar el proyecto \"" + id + "\".");
                }
            }
        }
        return projects;
    }

    // POINTS

    public Integer getPoints(Country country) {
        if (data.getData("points_" + country.getCountry()) != null) {
            return (Integer) data.getData("points_" + country.getCountry());
        }
        return 0;
    }

    public void setPoints(Country country, Integer amount) {
        data.setData("points_" + country.getCountry(), amount);
        data.save();

        if ((Integer) data.getData("points_" + country.getCountry()) > amount) {
            country.getLogs().sendMessage(":chart_with_upwards_trend: Se han añadido `" + ((Integer) data.getData("points_" + country.getCountry()) - amount) + "` puntos a **" + getName() + "**. Total: `" + amount + "`.").queue();
        } else {
            country.getLogs().sendMessage(":chart_with_upwards_trend: Se han quitado `" + (amount - (Integer) data.getData("points_" + country.getCountry())) + "` puntos de **" + getName() + "**. Total: `" + amount + "`.").queue();
        }
    }

    public void addPoints(Country country, Integer amount) {
        setPoints(country, getPoints(country) + amount);
    }

    public void removePoints(Country country, Integer amount) {
        setPoints(country, getPoints(country) - amount);
    }

    // NAMES AND TEXTS

    public String getLore() {
        List<String> lines = new ArrayList<>();

        lines.add("-[ §a§l" + getName() + " §r]-");
        lines.add("§aProyectos activos: §r" + getProjects().size());
        lines.add("§aProyectos terminados: §r" + getTotalFinishedProjects());

        boolean title = false;
        for (String country : "argentina bolivia chile paraguay peru uruguay".split(" "))
            if (getPoints(new Country(country)) != 0) {
                if (!title) {
                    lines.add("§aPuntos:§r");
                    title = true;
                }
                lines.add("· " + StringUtils.capitalize(country) + ": " + getPoints(new Country(country)));
            }

        if (getDiscordUser() != null) {
            lines.add("§aDiscord: §r" + getDiscordUser().getName() + "#" + getDiscordUser().getDiscriminator());
        }

        return String.join("\n", lines);
    }

    public String getName() {
        return (String) data.getData("name");
    }

    public String getNickame() {
        return (String) data.getData("nickname");
    }

    public void setNickname(String nickname) {
        if (nickname != null) {
            data.setData("nickname", nickname);
        } else {
            data.deleteData("nickname");
        }
        data.save();
    }

    public String getDisplayName() {
        if (getNickame() != null) {
            return getNickame().replace("&", "§");
        }
        return (String) data.getData("name");
    }

    public String getPrefix() {
        return (String) data.getData("prefix");
    }

    public void setPrefix(String prefix) {
        if (prefix != null) {
            data.setData("prefix", prefix);
        } else {
            data.deleteData("prefix");
        }
        data.save();
    }

    public List<String> getPrefixes() {
        List<String> prefixes = new ArrayList<>();

        prefixes.add("§f[" + ChatColor.getByChar((String) YamlManager.getYamlData(pluginFolder, "chat/colors.yml").get(getPrimaryGroup())) + getPrimaryGroup().toUpperCase() + "§f]");

        for (String group : getSecondaryGroups()) {
            prefixes.add("§f[" + ChatColor.getByChar((String) YamlManager.getYamlData(pluginFolder, "chat/colors.yml").get(group)) + group.toUpperCase() + "§f]");
        }

        if (getPrefix() != null) {
            prefixes.add(getPrefix().replace("&", "§"));
        }

        return prefixes;
    }

    // DISCORD AND CHATS

    public User getDiscordUser() {
        User discordUser;
        if (data.getData("discord") != null) {
            discordUser = conoSurBot.getUserById((String) data.getData("discord"));
        } else {
            discordUser = null;
        }
        return discordUser;
    }

    public Chat getDefaultChat() {
        return new Chat((String) data.getData("defaultChat"));
    }

    public Chat getChat() {
        return new Chat((String) data.getData("chat"));
    }

    public Boolean isChatHidden() {
        return (Boolean) data.getData("hideChat");
    }

    public void setChatHidden(Boolean hide) {
        data.setData("hideChat", hide);
        data.save();
    }

    public void setDefaultChat(String chat) {
        data.setData("defaultChat", chat);
        data.save();
    }

    public void setChat(String chat) {
        new Chat(chat).addPlayer((Player) this.player);

        data.setData("chat", chat);
        data.save();
    }

    // GROUPS AND PERMISSIONS

    public void demote() {
        String targetRank = primaryGroupsList.get(primaryGroupsList.indexOf(getPrimaryGroup()) - 1);

        Group targetGroup = lp.getGroupManager().getGroup(targetRank);
        InheritanceNode node = InheritanceNode.builder(targetGroup).build();

        Group oldGroup = lp.getGroupManager().getGroup(getPrimaryGroup());
        InheritanceNode oldNode = InheritanceNode.builder(oldGroup).build();

        UserManager userManager = lp.getUserManager();
        CompletableFuture<net.luckperms.api.model.user.User> userFuture = userManager.loadUser(this.player.getUniqueId());

        userFuture.thenAcceptAsync(user -> {
            user.data().add(node);
            user.data().remove(oldNode);

            lp.getUserManager().saveUser(user);
        });

        setPrimaryGroup(targetRank);
    }

    public void promote() {
        String targetRank = primaryGroupsList.get(primaryGroupsList.indexOf(getPrimaryGroup()) + 1);

        Group targetGroup = lp.getGroupManager().getGroup(targetRank);
        InheritanceNode node = InheritanceNode.builder(targetGroup).build();

        Group oldGroup = lp.getGroupManager().getGroup(getPrimaryGroup());
        InheritanceNode oldNode = InheritanceNode.builder(oldGroup).build();

        UserManager userManager = lp.getUserManager();
        CompletableFuture<net.luckperms.api.model.user.User> userFuture = userManager.loadUser(this.player.getUniqueId());

        userFuture.thenAcceptAsync(user -> {
            user.data().add(node);
            user.data().remove(oldNode);

            lp.getUserManager().saveUser(user);
        });

        setPrimaryGroup(targetRank);
    }

    public List<String> getPermissionCountries() {
        Player p = (Player) this.player;
        List<String> permissionCountries = new ArrayList<>();
        if (p.hasPermission("bteconosur.projects.manage.country.ar")) {
            permissionCountries.add("argentina");
        }
        if (p.hasPermission("bteconosur.projects.manage.country.bo")) {
            permissionCountries.add("bolivia");
        }
        if (p.hasPermission("bteconosur.projects.manage.country.cl")) {
            permissionCountries.add("chile");
        }
        if (p.hasPermission("bteconosur.projects.manage.country.pe")) {
            permissionCountries.add("peru");
        }
        return permissionCountries;
    }

    public String getPrimaryGroup() {
        return (String) data.getData("primaryGroup");
    }

    public void setPrimaryGroup(String group) {
        data.setData("primaryGroup", group);
        data.save();
    }

    public List<String> getSecondaryGroups() {
        if (data.getData("secondaryGroups") != null) {
            List<String> groups = (List <String>) data.getData("secondaryGroups");
            Collections.sort(groups);
            return groups;
        } else {
            return new ArrayList<>();
        }
    }

    public void addSecondaryGroup(String newGroup) {
        List<String> groups;
        if (data.getData("secondaryGroups") != null) {
             groups = (List<String>) data.getData("secondaryGroups");
        } else {
            groups = new ArrayList<>();
        }

        if (!(groups.contains(newGroup))) {
            groups.add(newGroup);
            Collections.sort(groups);
            data.setData("secondaryGroups", groups);
            data.save();

            // LUCKPERMS

            Group targetGroup = lp.getGroupManager().getGroup(newGroup);
            InheritanceNode node = InheritanceNode.builder(targetGroup).build();

            UserManager userManager = lp.getUserManager();
            CompletableFuture<net.luckperms.api.model.user.User> userFuture = userManager.loadUser(this.player.getUniqueId());

            userFuture.thenAcceptAsync(user -> {
                user.data().add(node);

                lp.getUserManager().saveUser(user);
            });
        }
    }

    public void removeSecondaryGroup(String group) {
        List<String> groups;
        if (data.getData("secondaryGroups") != null) {
            groups = (List<String>) data.getData("secondaryGroups");
        } else {
            groups = new ArrayList<>();
        }

        if (groups.contains(group)) {
            groups.remove(group);
            if (groups.size() > 0) {
                Collections.sort(groups);
                data.setData("secondaryGroups", groups);
            } else {
                data.deleteData("secondaryGroups");
            }
            data.save();

            Group targetGroup = lp.getGroupManager().getGroup(group);
            InheritanceNode node = InheritanceNode.builder(targetGroup).build();

            UserManager userManager = lp.getUserManager();
            CompletableFuture<net.luckperms.api.model.user.User> userFuture = userManager.loadUser(this.player.getUniqueId());

            userFuture.thenAcceptAsync(user -> {
                user.data().remove(node);

                lp.getUserManager().saveUser(user);
            });
        }
    }

    // NOTIFICATIONS

    public void sendNotification(String message) {
        if (this.player.isOnline()) {
            ((Player) this.player).sendMessage(message.replace("**", "").replace("`", ""));
        } else if (getDiscordUser() != null) {
            getDiscordUser().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage(ChatColor.stripColor("**Notificación:** " + message)).queue());
        } else {
            List<String> notifications = null;
            if (data.getData("notifications") != null) {
                notifications = (List<String>) data.getData("notifications");
            } else {
                notifications = new ArrayList<>();
            }
            notifications.add(message.replace("§", "&").replace("**", "").replace("`", ""));
            data.setData("notifications", notifications);
            data.save();
        }
    }

    public List<String> getNotifications() {
        if (data.getData("notifications") != null) {
            List<String> notifications = new ArrayList<>();
            for (String message : (List<String>) data.getData("notifications")) {
                notifications.add(message);
            }
            return notifications;
        }
        return new ArrayList<>();
    }

}

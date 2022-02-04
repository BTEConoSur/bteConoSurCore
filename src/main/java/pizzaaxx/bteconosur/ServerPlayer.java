package pizzaaxx.bteconosur;

import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.minuskube.netherboard.Netherboard;
import fr.minuskube.netherboard.bukkit.BPlayerBoard;
import net.dv8tion.jda.api.EmbedBuilder;
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
import pizzaaxx.bteconosur.country.CountryPlayer;
import pizzaaxx.bteconosur.playerData.PlayerData;
import pizzaaxx.bteconosur.projects.Project;
import pizzaaxx.bteconosur.worldedit.trees.Tree;
import pizzaaxx.bteconosur.yaml.YamlManager;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static pizzaaxx.bteconosur.Config.gateway;
import static pizzaaxx.bteconosur.bteConoSur.mainWorld;
import static pizzaaxx.bteconosur.bteConoSur.pluginFolder;
import static pizzaaxx.bteconosur.discord.bot.conoSurBot;
import static pizzaaxx.bteconosur.ranks.main.primaryGroupsList;
import static pizzaaxx.bteconosur.ranks.promote_demote.lp;
import static pizzaaxx.bteconosur.worldguard.worldguard.getWorldGuard;

public class ServerPlayer {

    private OfflinePlayer player = null;
    private PlayerData data;

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

    public OfflinePlayer getPlayer() {
        return this.player;
    }

    // PROJECTS

    public Integer getTotalFinishedProjects() {
        int total = 0;

        for (String country : "argentina bolivia chile paraguay peru uruguay".split(" "))
            if (data.getData("finished_projects_" + country) != null) {
                total = total + (Integer) new PlayerData(this.player).getData("finished_projects_" + country);
            }

        return total;
    }

    public List<Project> getOwnedProjects() {
        List<Project> projects = new ArrayList<>();
        for (Project project : getProjects()) {
            if (project.getOwner() == this.player) {
                projects.add(project);
            }
        }
        return projects;
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

    // DATA

    public void updateData() {
        this.data = new PlayerData(this.player);
    }

    // POINTS

    public Integer getPoints(Country country) {
        if (data.getData("points_" + country.getCountry()) != null) {
            return (Integer) data.getData("points_" + country.getCountry());
        }
        return 0;
    }

    public void setPoints(Country country, Integer amount) {
        int preAmount = getPoints(country);
        data.setData("points_" + country.getCountry(), amount);
        data.save();

        if (preAmount < amount) {
            country.getLogs().sendMessage(":chart_with_upwards_trend: Se han añadido `" + (amount - preAmount) + "` puntos a **" + getName() + "**. Total: `" + amount + "`.").queue();
        } else if (preAmount > amount) {
            country.getLogs().sendMessage(":chart_with_upwards_trend: Se han quitado `" + (preAmount - amount) + "` puntos de **" + getName() + "**. Total: `" + amount + "`.").queue();
        }

        checkTop(country);

        updateRanks();

        for (Player player : Bukkit.getOnlinePlayers()) {
            ServerPlayer p = new ServerPlayer(player);
            if (country.equals(new Country(player.getLocation())) || p.getScoreboard().equals("top")) {
                p.updateScoreboard();
            }
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
        if (getScoreboard().equals("me")) {
            updateScoreboard();
        }
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

    public List<Tree> getTreeGroup(String name) {
        if (data.getData("treegroups") != null) {
            Map<String, List<String>> treeGroups = (Map<String, List<String>>) data.getData("treegroups");
            if (treeGroups.containsKey(name)) {
                List<Tree> trees = new ArrayList<>();
                for (String str : treeGroups.get(name)) {
                    try {
                        trees.add(new Tree(str));
                    } catch (Exception exception) {
                        Bukkit.getConsoleSender().sendMessage("No se pudo encontrar el árbol \"" + name + "\".");
                    }
                }
                return trees;
            } else {
                return null;
            }
        } else {
            return null;
        }
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

        prefixes.add("§f[" + ChatColor.getByChar((String) YamlManager.getYamlData(pluginFolder, "chat/colors.yml").get(getPrimaryGroup())) + getPrimaryGroup().replace("default", "visita").toUpperCase() + "§f]");

        for (String group : getSecondaryGroups()) {
            prefixes.add("§f[" + ChatColor.getByChar((String) YamlManager.getYamlData(pluginFolder, "chat/colors.yml").get(group)) + group.replace("donator", "donador").toUpperCase() + "§f]");
        }

        if (getPrefix() != null) {
            prefixes.add(getPrefix().replace("&", "§"));
        }

        return prefixes;
    }

    // DISCORD AND CHATS

    public boolean hasDiscordUser() {
        return (data.getData("discord") != null);
    }

    public User getDiscordUser() {
        if (data.getData("discord") != null) {
            return conoSurBot.retrieveUserById((String) data.getData("discord")).complete();
        } else {
            return null;
        }
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
        if (getScoreboard().equals("me")) {
            updateScoreboard();
        }
    }

    public void setDefaultChat(String chat) {
        data.setData("defaultChat", chat);
        data.save();
    }

    public void setChat(String chat) {
        new Chat(chat).addPlayer((Player) this.player);

        data.setData("chat", chat);
        data.save();

        if (getScoreboard().equals("me")) {
            updateScoreboard();
        }
    }

    // GROUPS AND PERMISSIONS

    public int getMaxPoints() {
        int max = getPoints(new Country("argentina"));
        for (String c : "argentina bolivia chile paraguay peru uruguay".split(" ")) {
            if (getPoints(new Country(c)) > max) {
                max = getPoints(new Country(c));
            }
        }
        return max;
    }

    public int getTotalProjects() {
        return getProjects().size() + getTotalFinishedProjects();
    }

    public void updateRanks() {
        updateData();
        String pGroup = getPrimaryGroup();
        int projects = getProjects().size();
        int finishedProjects = getTotalFinishedProjects();
        if (pGroup.equals("mod") || pGroup.equals("admin")) {
            return;
        }

        if (pGroup.equals("default")) {
            if (finishedProjects > 0) {
                promote();
                promote();
            } else if (projects > 0) {
                promote();
            }
        } else if (pGroup.equals("postulante")) {
            if (projects == 0 && finishedProjects == 0) {
                demote();
            } else if (finishedProjects > 0) {
                promote();
            }
        }
    }

    public String getBuilderRank(Country country) {
        updateData();
        if (getPoints(country) >= 15) {
            int points = getPoints(country);
            if (points >= 1000) {
                return "maestro";
            } else if (points >= 500) {
                return "veterano";
            } else if (points >= 150) {
                return "avanzado";
            } else {
                return "builder";
            }
        } else {
            return null;
        }
    }

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

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(new Color(255, 0, 0));
        embed.setAuthor(getName() + " ha sido degradad@ a " + getPrimaryGroup().replace("default", "visita").toUpperCase(), null, "https://cravatar.eu/helmavatar/" + getName() + "/190.png");

        gateway.sendMessageEmbeds(embed.build()).queue();

        if (getScoreboard().equals("me")) {
            updateScoreboard();
        }
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

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(new Color(0, 255, 42));
        embed.setAuthor(getName() + " ha sido promovid@ a " + getPrimaryGroup().replace("default", "visita").toUpperCase(), null, "https://cravatar.eu/helmavatar/" + getName() + "/190.png");

        gateway.sendMessageEmbeds(embed.build()).queue();

        if (getScoreboard().equals("me")) {
            updateScoreboard();
        }
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

        if (getScoreboard().equals("me")) {
            updateScoreboard();
        }
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

        if (getScoreboard().equals("me")) {
            updateScoreboard();
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

        if (getScoreboard().equals("me")) {
            updateScoreboard();
        }
    }

    // NOTIFICATIONS

    public void sendNotification(String message) {
        if (this.player.isOnline()) {
            ((Player) this.player).sendMessage(message.replace("**", "").replace("`", ""));
        } else if (hasDiscordUser()) {
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

    // SCOREBOARD

    public void setScoreboardHide(boolean status) {
        data.setData("hideScoreboard", status);
        data.save();
        updateScoreboard();
    }

    public boolean isScoreboardHidden() {
        return (boolean) data.getData("hideScoreboard");
    }

    public void setScoreboardAuto(boolean status) {
        data.setData("scoreboardAuto", status);
        data.save();
    }

    public boolean isScoreboardAuto() {
        return (boolean) data.getData("scoreboardAuto");
    }

    public void setScoreboard(String name) {
        data.setData("scoreboard", name);
        data.save();
        updateScoreboard();
    }

    public String getScoreboard() {
        return (String) data.getData("scoreboard");
    }

    public void updateScoreboard() {
        String scoreboard = getScoreboard();
        boolean hide = isScoreboardHidden();

        if (!hide) {
            if (scoreboard.equals("server")) {
                List<String> lines = new ArrayList<>();

                lines.add("§aIP: §fbteconosur.com");
                lines.add(" ");

                lines.add("§aJugadores: §f" + Bukkit.getOnlinePlayers().size() + "/20");

                int arg = 0;
                int bol = 0;
                int chi = 0;
                int par = 0;
                int per = 0;
                int uru = 0;
                RegionManager regionManager = getWorldGuard().getRegionManager(mainWorld);
                for (Player player : Bukkit.getOnlinePlayers()) {
                    Set<ProtectedRegion> regions = regionManager.getApplicableRegions(player.getLocation()).getRegions();
                    if (regions.contains(regionManager.getRegion("argentina"))) {
                        arg++;
                    } else if (regions.contains(regionManager.getRegion("bolivia"))) {
                        bol++;
                    }
                    else if (regions.contains(regionManager.getRegion("chile_cont")) || regions.contains(regionManager.getRegion("chile_idp"))) {
                        chi++;
                    }
                    else if (regions.contains(regionManager.getRegion("paraguay"))) {
                        par++;
                    }
                    else if (regions.contains(regionManager.getRegion("peru"))) {
                        per++;
                    }
                    else if (regions.contains(regionManager.getRegion("uruguay"))) {
                        uru++;
                    }
                }
                lines.add("§aArgentina: §f" + arg);
                lines.add("§aBolivia: §f" + bol);
                lines.add("§aChile: §f" + chi);
                lines.add("§aParaguay: §f" + par);
                lines.add("§aPerú: §f" + per);
                lines.add("§aUruguay: §f" + uru);

                BPlayerBoard board = Netherboard.instance().createBoard((Player) this.player, "§3§lBTE Cono Sur");
                board.setAll(lines.toArray(new String[0]));
            } else if (scoreboard.equals("me")) {
                List<String> lines = new ArrayList<>();

                lines.add(" ");

                lines.add("§aRango: §f" + getPrefixes().get(0));
                if (getPrefixes().size() > 1) {
                    lines.add("§aOtros rangos:§f");
                    for (String prefix : getPrefixes().subList(1, getPrefixes().size())) {
                        lines.add("- " + prefix);
                    }
                }

                if (hasDiscordUser()) {
                    lines.add("§aDiscord: §f" + getDiscordUser().getName() + "#" + getDiscordUser().getDiscriminator());
                } else {
                    lines.add("§aDiscord: §fN/A");
                }

                ChatColor color;
                color = isChatHidden() ? ChatColor.DARK_GRAY : ChatColor.WHITE;
                lines.add("§aChat: " + color + getChat().getFormattedName());
                if (getMaxPoints() > 0) {
                    lines.add("§aPuntos:§f");

                    List<CountryPlayer> list = new ArrayList<>();
                    for (String c : "bolivia chile paraguay peru uruguay".split(" ")) {
                        if (getPoints(new Country(c)) > 0) {
                            list.add(new CountryPlayer(this, new Country(c)));
                        }
                    }

                    Collections.sort(list);

                    for (CountryPlayer p : list) {
                        lines.add("- " + StringUtils.capitalize(p.getCountry().getCountry().replace("peru", "perú")) + ": " + getPoints(p.getCountry()));
                    }
                }

                lines.add("§aProyectos activos: §f" + getProjects().size());
                lines.add("§aProyectos terminados: §f" + getTotalFinishedProjects());

                BPlayerBoard board = Netherboard.instance().createBoard((Player) this.player, "§a§l" + getDisplayName());
                board.setAll(lines.toArray(new String[0]));
            } else if (scoreboard.equals("project")) {
                try {
                    Project project = new Project(((Player) getPlayer()).getLocation());

                    ChatColor color;
                    if (project.getDifficulty().equals("facil")) {
                        color = ChatColor.GREEN;
                    } else if (project.getDifficulty().equals("intermedio")) {
                        color = ChatColor.YELLOW;
                    } else {
                        color = ChatColor.RED;
                    }

                    List<String> lines = new ArrayList<>();

                    lines.add("§aDificultad: §f" + StringUtils.capitalize(project.getDifficulty().replace("facil", "fácil").replace("dificil", "difícil")));
                    lines.add("§aPaís: §f" + StringUtils.capitalize(project.getCountry()));
                    if (project.getTag() != null) {
                        lines.add("§aEtiqueta: §f" + StringUtils.capitalize(project.getTag().replace("_", " ")));
                    }
                    if (project.getOwner() != null) {
                        lines.add("§aLíder: §f" + new ServerPlayer(project.getOwner()).getName());
                    }
                    if (project.getMembers() != null) {
                        lines.add("§aMiembros: §f");
                        int i = 0;
                        for (OfflinePlayer member : project.getMembers()) {
                            lines.add("- " + new ServerPlayer(member).getName());
                            i++;
                            if (i >= 9) {
                                lines.add("etc...");
                            }
                        }
                    }

                    BPlayerBoard board = Netherboard.instance().createBoard((Player) this.player, color +  "§l" + project.getName(true));
                    board.setAll(lines.toArray(new String[0]));
                } catch (Exception e) {
                    List<String> lines = new ArrayList<>();

                    lines.add("§cNo disponible.");

                    BPlayerBoard board = Netherboard.instance().createBoard((Player) this.player, "§c§lProyecto");
                    board.setAll(lines.toArray(new String[0]));
                }
            } else if (scoreboard.equals("top")) {
                Country country = new Country(((Player) this.player).getLocation());

                if (!country.getCountry().equals("global") && !country.getCountry().equals("argentina")) {

                    List<String> lines = new ArrayList<>();

                    int i = 1;
                    for (ServerPlayer s : country.getScoreboard()) {

                        ChatColor color = ChatColor.WHITE;

                        if (s.getBuilderRank(country) != null) {
                            String bRank = s.getBuilderRank(country);
                            if (bRank.equals("builder")) {
                                color = ChatColor.BLUE;
                            } else if (bRank.equals("avanzado")) {
                                color = ChatColor.DARK_BLUE;
                            } else if (bRank.equals("veterano")) {
                                color = ChatColor.YELLOW;
                            } else if (bRank.equals("maestro")) {
                                color = ChatColor.GOLD;
                            }
                        }
                        lines.add(i + ". §a" + s.getPoints(country) + " §7- " + color + s.getName());
                        i++;
                    }

                    BPlayerBoard board = Netherboard.instance().createBoard((Player) this.player, "§6§lMayores puntajes");
                    board.setAll(lines.toArray(new String[0]));
                } else {
                    List<String> lines = new ArrayList<>();

                    lines.add("§cNo disponible.");

                    BPlayerBoard board = Netherboard.instance().createBoard((Player) this.player, "§6§lMayores puntajes");
                    board.setAll(lines.toArray(new String[0]));
                }
            }
        } else {
            Netherboard.instance().getBoard((Player) getPlayer()).clear();
        }
    }

    public void checkTop(Country country) {
        List<CountryPlayer> top = new ArrayList<>();
        for (ServerPlayer m : country.getScoreboard()) {
            if (!m.getName().equals(getName())) {
                top.add(new CountryPlayer(m, country));
            }
        }

        top.add(new CountryPlayer(this, country));

        Collections.sort(top);

        List<String> newUUIDs = new ArrayList<>();
        for (CountryPlayer countryPlayer : top.subList(0, Math.min(10, top.size()))) {
            newUUIDs.add(countryPlayer.getPlayer().getPlayer().getUniqueId().toString());
        }

        YamlManager yaml = new YamlManager(pluginFolder, "points/max.yml");
        yaml.setValue(country.getAbbreviation() + "_max", newUUIDs);
        yaml.write();
    }
}

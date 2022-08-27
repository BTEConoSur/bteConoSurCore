package pizzaaxx.bteconosur.country.cities.projects.Command;

import com.google.common.collect.Lists;
import com.sk89q.worldedit.BlockVector2D;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.regions.selector.Polygonal2DRegionSelector;
import com.sk89q.worldedit.world.World;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.md_5.bungee.api.chat.BaseComponent;
import org.apache.commons.lang.StringUtils;
import org.bukkit.*;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import pizzaaxx.bteconosur.BteConoSur;
import pizzaaxx.bteconosur.ServerPlayer.*;
import pizzaaxx.bteconosur.ServerPlayer.Managers.GroupsManager;
import pizzaaxx.bteconosur.ServerPlayer.Managers.PointsManager;
import pizzaaxx.bteconosur.ServerPlayer.Managers.ProjectsManager;
import pizzaaxx.bteconosur.ServerPlayer.Managers.ScoreboardManager;
import pizzaaxx.bteconosur.configuration.Configuration;
import pizzaaxx.bteconosur.coords.Coords2D;
import pizzaaxx.bteconosur.country.Country;
import pizzaaxx.bteconosur.country.OldCountry;
import pizzaaxx.bteconosur.country.cities.City;
import pizzaaxx.bteconosur.country.cities.projects.Project;
import pizzaaxx.bteconosur.country.cities.projects.ProjectsRegistry;
import pizzaaxx.bteconosur.misc.Misc;
import pizzaaxx.bteconosur.projects.OldProject;
import pizzaaxx.bteconosur.worldedit.WorldEditHelper;
import pizzaaxx.bteconosur.worldguard.WorldGuardProvider;
import xyz.upperlevel.spigot.book.BookUtil;

import java.awt.Color;
import java.io.IOException;
import java.util.List;
import java.util.*;

import static pizzaaxx.bteconosur.BteConoSur.key;
import static pizzaaxx.bteconosur.Config.*;
import static pizzaaxx.bteconosur.ServerPlayer.Managers.PointsManager.pointsPrefix;
import static pizzaaxx.bteconosur.misc.Misc.*;
import static pizzaaxx.bteconosur.projects.ProjectManageInventoryListener.inventoryActions;
import static pizzaaxx.bteconosur.worldedit.WorldEditHelper.getSelection;
import static pizzaaxx.bteconosur.worldedit.WorldEditHelper.polyRegion;
import static pizzaaxx.bteconosur.worldguard.WorldGuardProvider.getPlayersInRegion;

public class ProjectsCommand implements CommandExecutor {
    public static String projectsPrefix = "§f[§dPROYECTO§f] §7>>§r ";
    public static Set<Player> transferConfirmation = new HashSet<>();
    private final Set<Player> leaveConfirmation = new HashSet<>();
    private final Set<Player> finishConfirmation = new HashSet<>();
    private final Set<Player> deleteConfirmation = new HashSet<>();
    public static Map<UUID, Integer> tutorialSteps = new HashMap<>();
    public static ItemStack background;

    private final BteConoSur plugin;

    public ProjectsCommand(BteConoSur plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(projectsPrefix + "Este comando solo puede ser usado por jugadores.");
                return true;
            }

            Player p = (Player) sender;
            ServerPlayer s = plugin.getPlayerRegistry().get(p.getUniqueId());
            ProjectsManager projectsManager = s.getProjectsManager();
            if (args.length == 0) {
                sender.sendMessage(projectsPrefix + "Debes introducir un subcomando.");
                return true;
            }

            if (args[0].equalsIgnoreCase("create") || args[0].equalsIgnoreCase("crear")) {
                // GET POINTS

                List<BlockVector2D> points;
                try {
                    points = polyRegion(getSelection(p)).getPoints();
                } catch (IncompleteRegionException e) {
                    p.sendMessage(projectsPrefix + "Selecciona un área primero.");
                    return true;
                } catch (IllegalArgumentException e) {
                    p.sendMessage(projectsPrefix + "Debes seleccionar una region cúbica o poligonal.");
                    return true;
                }
                if (points.size() > maxProjectPoints) {
                    p.sendMessage(projectsPrefix + "La selección no puede tener más de " + maxProjectPoints + " puntos.");
                    return true;
                }

                if (!plugin.getCountryManager().isInsideAnyCountry(points.get(0))) {
                    p.sendMessage(projectsPrefix + "La selección no está dentro de ningún país.");
                    return true;
                }

                Country country = plugin.getCountryManager().get(points.get(0));

                if (!country.allowsProjects()) {
                    p.sendMessage(projectsPrefix + "El país en el que estas no tiene soporte para proyectos.");
                    return true;
                }

                City city;
                if (country.getCityRegistry().isInsideCity(points.get(0))) {
                    city = country.getCityRegistry().get(points.get(0));
                } else {
                    city = country.getCityRegistry().get("default");
                }

                if (s.getPermissionCountries().contains(country.getName())) {

                    if (args.length < 2) {
                        p.sendMessage(projectsPrefix + "Introduce una dificultad, puede ser §afacil§f, §aintermedio§f o §adificil§f.");
                        return true;
                    }

                    if ((!args[1].equalsIgnoreCase("facil")) && (!(args[1].equalsIgnoreCase("intermedio"))) && (!(args[1].equalsIgnoreCase("dificil")))) {
                        p.sendMessage(projectsPrefix + "Introduce una dificultad válida, puede ser §afacil§f, §aintermedio§f o §adificil§f.");
                        return true;
                    }

                    ProjectsRegistry registry = city.getProjectsRegistry();

                    try {

                        Project project =  registry.createProject(Project.Difficulty.valueOf(args[1].toUpperCase()), points);
                        boolean usedTag = false;
                        if (args.length > 2) {
                            try {
                                project.setTag(Project.Tag.valueOf(args[2].toUpperCase())).exec();
                                usedTag = true;
                            } catch (IllegalArgumentException e) {
                                p.sendMessage(projectsPrefix + "Etiqueta inválida.");
                            }
                        }
                        project.updatePlayersScoreboard();

                        // SEND MESSAGES
                        p.sendMessage(projectsPrefix + "Proyecto con la ID §a" + project.getId()  + "§f creado con la dificultad §a" + project.getDifficulty().toString().toUpperCase() + "§f" + (usedTag ? " y la etiqueta §a" + project.getTag().toString().replace("_", " ") + "§f" : "") + ".");

                        StringBuilder dscMessage = new StringBuilder(":clipboard: **" + p.getName() + "** ha creado el proyecto `" + project.getId() + "` con dificultad `" + args[1].toUpperCase() + "`" + (usedTag ? " y la etiqueta `" + project.getTag().toString().replace("_", " ") + "`" : "") + " en las coordenadas: \n");
                        for (BlockVector2D point : project.getPoints()) {
                            dscMessage.append("> ").append(Math.floor(point.getX())).append(" ").append(Math.floor(p.getWorld().getHighestBlockAt(point.getBlockX(), point.getBlockZ()).getY())).append(" ").append(Math.floor(point.getZ())).append("\n");
                        }
                        dscMessage = new StringBuilder(dscMessage.toString().replace(".0", ""));

                        project.getCountry().getProjectsLogsChannel().sendMessage(dscMessage.toString()).queue();

                    } catch (IOException e) {
                        p.sendMessage(projectsPrefix + "Ha ocurrido un error al crear tu proyecto.");
                        return true;
                    }
                } else {
                    if (projectsManager.getProjects(country).size() < maxProjectsPerPlayer) {
                        OldProject project = new OldProject(getCountryAtLocation(new Location(mainWorld, points.get(0).getX(), 100 , points.get(0).getZ())), OldProject.Difficulty.FACIL, points);

                        EmbedBuilder request = new EmbedBuilder();
                        request.setColor(new Color(0, 255, 42));

                        request.setTitle(new ServerPlayer(p).getName() + " quiere crear un proyecto.");

                        List<String> coords = new ArrayList<>();
                        for (BlockVector2D point : project.getPoints()) {
                            coords.add(("> " + point.getX() + " " + new Coords2D(point).getHighestY() + " " + point.getZ()).replace(".0", ""));
                        }
                        request.addField(":round_pushpin: Coordenadas:", String.join("\n", coords), false);

                        // GMAPS

                        BlockVector2D average = project.getAverageCoordinate();

                        Coords2D geoCoord = new Coords2D(average);

                        request.addField(":map: Google Maps:", "https://www.google.com/maps/@" + geoCoord.getLat() + "," + geoCoord.getLon() + ",19z", false);

                        // IMAGE

                        request.setImage(project.getImageUrl());
                        Bukkit.getConsoleSender().sendMessage(project.getImageUrl());

                        ActionRow actionRow = ActionRow.of(
                                Button.of(ButtonStyle.SECONDARY, "facil", "Fácil", Emoji.fromMarkdown("\uD83D\uDFE2")),
                                Button.of(ButtonStyle.SECONDARY, "intermedio", "Intermedio", Emoji.fromMarkdown("\uD83D\uDFE1")),
                                Button.of(ButtonStyle.SECONDARY, "dificil", "Difícil", Emoji.fromMarkdown("\uD83D\uDD34")),
                                Button.of(ButtonStyle.DANGER, "rechazar", "Rechazar", Emoji.fromMarkdown("✖️"))
                        );

                        MessageBuilder message = new MessageBuilder();
                        message.setEmbeds(request.build());
                        message.setActionRows(actionRow);

                        country.getRequests().sendMessage(message.build()).queue();

                        p.sendMessage(projectsPrefix + "Se ha enviado una solicitud para crear tu proyecto.");
                    } else {
                        p.sendMessage(projectsPrefix + "No puedes ser líder de más de 10 proyectos.");
                    }
                }
            }

            if (args[0].equalsIgnoreCase("claim") || args[0].equalsIgnoreCase("reclamar")) {

                try {
                    OldProject project = new OldProject(p.getLocation());
                    if (project.getOwner() != null) {
                        if (project.getOwner() == p) {
                            p.sendMessage(projectsPrefix + "Ya eres dueñ@ de este proyecto.");
                        } else if (project.getMembers().contains(p)) {
                            p.sendMessage("Alguien más ya es dueñ@ de este proyecto. Usa §a/p request §fpara solicitar unirte.");
                        }
                    } else if (projectsManager.getOwnedProjects().getOrDefault(project.getCountry(), new ArrayList<>()).size() >= maxProjectsPerPlayer){
                        p.sendMessage(projectsPrefix + "No puedes ser líder de más de 10 proyectos al mismo tiempo.");
                    } else {
                        GroupsManager manager = s.getGroupsManager();
                        if ((manager.getPrimaryGroup() == GroupsManager.PrimaryGroup.POSTULANTE || manager.getPrimaryGroup() == GroupsManager.PrimaryGroup.DEFAULT) && project.getDifficulty() != OldProject.Difficulty.FACIL) {
                            p.sendMessage(projectsPrefix + "En tu rango solo puedes reclamar proyectos fáciles.");
                            return true;
                        }
                        project.setOwner(p);
                        project.save();

                        for (Player player : getPlayersInRegion("project_" + project.getId())) {
                            ScoreboardManager scoreboardManager = playerRegistry.get(player.getUniqueId()).getScoreboardManager();
                            if (scoreboardManager.getType() == ScoreboardManager.ScoreboardType.PROJECT) {
                                scoreboardManager.update();
                            }
                        }

                        p.sendMessage(projectsPrefix + "Ahora eres dueñ@ de este proyecto.");

                        project.getCountry().getLogs().sendMessage(":inbox_tray: **" + s.getName() + "** ha reclamado el proyecto `" + project.getId() + "`.").queue();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    p.sendMessage(projectsPrefix + "No estás dentro de ningún proyecto.");
                    return true;
                }
                return true;
            }

            if (args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("eliminar")) {
                if (!(deleteConfirmation.contains(p))) {
                    deleteConfirmation.add(p);
                    p.sendMessage(projectsPrefix + "§cNo puedes deshacer esta acción. §fUsa el comando de nuevo para confirmar.");
                    return true;
                }

                deleteConfirmation.remove(p);
                if (args.length >= 2) {
                    if (OldProject.projectExists(args[1])) {
                        OldProject project = new OldProject(args[1]);

                        if (!s.getPermissionCountries().contains(project.getCountry().getName())) {

                            p.sendMessage(projectsPrefix + "No puedes hacer esto en este país.");
                            return true;
                        }

                        project.delete();

                        for (Player player : getPlayersInRegion("project_" + project.getId())) {
                            ScoreboardManager manager = playerRegistry.get(player.getUniqueId()).getScoreboardManager();
                            if (manager.getType() == ScoreboardManager.ScoreboardType.PROJECT) {
                                manager.update();
                            }
                        }

                        p.sendMessage(projectsPrefix + "Has eliminado el proyecto §a" + project.getId() + "§f.");

                        for (OfflinePlayer member : project.getAllMembers()) {
                            new ServerPlayer(member).sendNotification(projectsPrefix + "Tu proyecto **§a" + project.getName(true) + "§f** ha sido eliminado.");
                        }

                        project.getCountry().getLogs().sendMessage(":wastebasket: **" + s.getName() + "** ha eliminado el proyecto `" + project.getId() + "`.").queue();
                    } else {
                        p.sendMessage(projectsPrefix + "Este proyecto no existe.");
                        return true;
                    }
                } else {
                    try {
                        OldProject project = new OldProject(p.getLocation());

                        if (!s.getPermissionCountries().contains(project.getCountry().getName())) {

                            p.sendMessage(projectsPrefix + "No puedes hacer esto en este país.");
                            return true;

                        }

                        project.delete();

                        p.sendMessage(projectsPrefix + "Has eliminado el proyecto §a" + project.getId() + "§f.");
                        project.getCountry().getLogs().sendMessage(":wastebasket: **" + s.getName() + "** ha eliminado el proyecto `" + project.getId() + "`.").queue();
                    } catch (Exception e) {
                        p.sendMessage(projectsPrefix + "No estás dentro de ningun proyecto.");
                    }
                }
                return true;
            }

            if (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("agregar")) {

                OldProject project;
                try {
                    project = new OldProject(p.getLocation());
                    if (project.getOwner() == p) {
                        if (project.isPending()) {
                            p.sendMessage("No puedes hacer esto mientras el proyecto está pendiente de revisión.");
                            return true;
                        }
                        if (project.getAllMembers().size() < maxProjectMembers) {
                            if (args.length >= 2) {
                                if (Bukkit.getOfflinePlayer(args[1]).isOnline()) {
                                    Player target = Bukkit.getPlayer(args[1]);
                                    project.addMember(target);
                                    project.save();

                                    for (Player player : getPlayersInRegion("project_" + project.getId())) {
                                        ScoreboardManager manager = playerRegistry.get(player.getUniqueId()).getScoreboardManager();
                                        if (manager.getType() == ScoreboardManager.ScoreboardType.PROJECT) {
                                            manager.update();
                                        }
                                    }

                                    ServerPlayer sTarget = new ServerPlayer(target);

                                    p.sendMessage(projectsPrefix + "Has agregado a §a" + sTarget.getName() + "§f al proyecto §a" + project.getName() + "§f.");
                                    target.sendMessage(projectsPrefix + "Has sido añadido al proyecto §a" + project.getName() + "§f.");

                                    project.getCountry().getLogs().sendMessage(":pencil: **" + s.getName() + "** ha agregado a **" + sTarget.getName() + "** al proyecto `" + project.getId() + "`.").queue();
                                } else {
                                    p.sendMessage(projectsPrefix + "El jugador introducido no existe o no se encuentra online.");
                                }
                            } else {
                                p.sendMessage(projectsPrefix + "Introduce un jugador.");
                            }
                        } else {
                            p.sendMessage(projectsPrefix + "El proyecto ya alcanzó la capacidad máxima de miembros.");
                        }
                    } else {
                        p.sendMessage(projectsPrefix + "No eres el líder de este proyecto.");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    p.sendMessage(projectsPrefix + "No estás dentro de ningún proyecto.");
                }
                return true;
            }

            if (args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("remover") || args[0].equalsIgnoreCase("quitar")) {

                OldProject project;
                try {
                    project = new OldProject(p.getLocation());
                    if (project.getOwner() == p) {
                        if (project.isPending()) {
                            p.sendMessage("No puedes hacer esto mientras el proyecto está pendiente de revisión.");
                            return true;
                        }
                        if (!project.getMembers().isEmpty()) {
                            if (args.length >= 2) {
                                OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
                                ServerPlayer sTarget = new ServerPlayer(target);
                                if (project.getMembers().contains(target)) {
                                    project.removeMember(target);
                                    project.save();

                                    for (Player player : getPlayersInRegion("project_" + project.getId())) {
                                        ScoreboardManager manager = playerRegistry.get(player.getUniqueId()).getScoreboardManager();
                                        if (manager.getType() == ScoreboardManager.ScoreboardType.PROJECT) {
                                            manager.update();
                                        }
                                    }

                                    p.sendMessage(projectsPrefix + "Has removido a §a" + sTarget.getName() + "§f del proyecto §a" + project.getName() + "§f.");

                                    sTarget.sendNotification(projectsPrefix + "Has sido removido del proyecto **§a" + project.getName(true) + "§f**.");

                                    project.getCountry().getLogs().sendMessage(":pencil: **" + s.getName() + "** ha removido a **" + sTarget.getName() + "** del proyecto `" + project.getId() + "`.").queue();
                                } else {
                                    p.sendMessage(projectsPrefix + "El jugador introducido no es parte del proyecto.");
                                }
                            } else {
                                p.sendMessage(projectsPrefix + "Introduce un jugador.");
                            }
                        } else {
                            p.sendMessage(projectsPrefix + "El proyecto no tiene miembros.");
                        }
                    } else {
                        p.sendMessage(projectsPrefix + "No eres el líder de este proyecto.");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    p.sendMessage(projectsPrefix + "No estás dentro de ningún proyecto.");
                }
                return true;
            }

            if (args[0].equalsIgnoreCase("transfer") || args[0].equalsIgnoreCase("transferir")) {

                try {
                    OldProject project = new OldProject(p.getLocation());
                    if (project.getOwner() == p) {
                        if (project.isPending()) {
                            p.sendMessage("No puedes hacer esto mientras el proyecto está pendiente de revisión.");
                            return true;
                        }
                        if (args.length >= 2) {
                            if (project.getMembers().contains(Bukkit.getOfflinePlayer(args[1]))) {
                                if (Bukkit.getOfflinePlayer(args[1]).isOnline()) {
                                    if (transferConfirmation.contains(p)) {
                                        transferConfirmation.remove(p);
                                        Player target = Bukkit.getPlayer(args[1]);

                                        ServerPlayer t = new ServerPlayer(target);

                                        if ((t.getProjectsManager().getOwnedProjects().containsKey(project.getCountry()) ? t.getProjectsManager().getOwnedProjects().get(project.getCountry()).size() : 0)  > maxProjectsPerPlayer) {
                                            p.sendMessage(projectsPrefix + "El jugador introducido ya alcanzó su límite de proyectos en este país.");
                                            return true;
                                        }

                                        project.transfer(target);
                                        project.save();

                                        for (Player player : getPlayersInRegion("project_" + project.getId())) {
                                            ScoreboardManager manager = playerRegistry.get(player.getUniqueId()).getScoreboardManager();
                                            if (manager.getType() == ScoreboardManager.ScoreboardType.PROJECT) {
                                                manager.update();
                                            }
                                        }


                                        p.sendMessage(projectsPrefix + "Has transferido el proyecto §a" + project.getName() + " §fa §a" + target.getName() + "§f.");
                                        target.sendMessage(projectsPrefix + "§a" + p.getName() + " §fte ha transferido el proyecto §a" + project.getName() + "§f.");
                                        project.getCountry().getLogs().sendMessage(":incoming_envelope: **" + s.getName() + "** ha transferido el proyecto `" + project.getId() + "` a **" + t.getName() + "**.").queue();
                                    } else {
                                        transferConfirmation.add(p);
                                        p.sendMessage(projectsPrefix + "§cNo puedes deshacer esta acción. §fUsa el comando de nuevo para confirmar.");
                                    }
                                } else {
                                    p.sendMessage(projectsPrefix + "El jugador no se encuentra online.");
                                }
                            } else {
                                p.sendMessage(projectsPrefix + "El jugador no es un miembro de tu proyecto.");
                            }
                        } else {
                            p.sendMessage(projectsPrefix + "Introduce un jugador.");
                        }
                    } else {
                        p.sendMessage(projectsPrefix + "No eres el líder de este proyecto.");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    p.sendMessage(projectsPrefix + "No estás dentro de ningún proyecto.");
                }
            }

            if (args[0].equalsIgnoreCase("leave") || args[0].equalsIgnoreCase("abandonar")) {

                try {
                    OldProject project = new OldProject(p.getLocation());
                    TextChannel logs = project.getCountry().getLogs();

                    if (project.isPending()) {
                        p.sendMessage("No puedes hacer esto mientras el proyecto está pendiente de revisión.");
                        return true;
                    }

                    if (project.getOwner() != null && project.getOwner() == p) {
                        if (leaveConfirmation.contains(p)) {
                            leaveConfirmation.remove(p);
                            p.sendMessage(projectsPrefix + "Has abandonado el proyecto §a" + project.getName(true) + "§f.");
                            logs.sendMessage(":outbox_tray: **" + s.getName() + "** ha abandonado el proyecto `" + project.getId() + "`.").queue();
                            for (OfflinePlayer member : project.getMembers()) {

                                ServerPlayer sMember = new ServerPlayer(member);
                                sMember.sendNotification("El líder de tu proyecto **§a" + project.getName(true) + "§f** ha abandonado el proyecto, por lo que tú también has salido.");
                                logs.sendMessage(":outbox_tray: **" + sMember.getName() + "** ha abandonado el proyecto `" + project.getId() + "`.").queue();
                            }
                            logs.sendMessage(":flag_white: El proyecto `" + project.getId() + "` está disponible de nuevo.").queue();

                            project.empty();
                            project.setName(null);
                            project.save();

                            for (Player player : getPlayersInRegion("project_" + project.getId())) {
                                ScoreboardManager manager = playerRegistry.get(player.getUniqueId()).getScoreboardManager();
                                if (manager.getType() == ScoreboardManager.ScoreboardType.PROJECT) {
                                    manager.update();
                                }
                            }

                        } else {
                            leaveConfirmation.add(p);
                            p.sendMessage(projectsPrefix + "§cEl proyecto quedará vacío si el líder abandonda el proyecto. Esta acción no se puede deshacer. §fUsa el comando de nuevo para confirmar.");
                        }
                    } else if (project.getMembers().contains(p)) {
                        project.removeMember(p);
                        project.save();

                        for (Player player : getPlayersInRegion("project_" + project.getId())) {
                            ScoreboardManager manager = playerRegistry.get(player.getUniqueId()).getScoreboardManager();
                            if (manager.getType() == ScoreboardManager.ScoreboardType.PROJECT) {
                                manager.update();
                            }
                        }

                        p.sendMessage(projectsPrefix + "Has abandonado el proyecto §a" + project.getName() + "§f.");

                        new ServerPlayer(project.getOwner()).sendNotification(projectsPrefix + "**§a" + new ServerPlayer(p).getName() + "§f** ha abandonado tu proyecto **§a" + project.getName(true) + "§f**.");

                        logs.sendMessage(":outbox_tray: **" + s.getName() + "** ha abandonado el proyecto `" + project.getId() + "`.").queue();
                    } else {
                        p.sendMessage(projectsPrefix + "No eres miembro de este proyecto.");
                    }
                } catch (Exception e) {
                    p.sendMessage(projectsPrefix + "No estás dentro de ningún proyecto.");
                    e.printStackTrace();
                }
            }

            if (args[0].equalsIgnoreCase("borders") || args[0].equalsIgnoreCase("bordes")) {

                try {
                    OldProject project = new OldProject(p.getLocation());

                    int maxY = p.getLocation().getBlockY() + 10;
                    int minY = p.getLocation().getBlockY() - 10;

                    Polygonal2DRegionSelector selector = new Polygonal2DRegionSelector((World) new BukkitWorld(mainWorld), project.getPoints(), minY, maxY);
                    WorldEditHelper.setSelection(p, selector);

                    p.sendMessage(BookUtil.TextBuilder.of(projectsPrefix + "Mostrando los bordes del proyecto §a" + project.getName() + "§f. §eRequiere ").build(), BookUtil.TextBuilder.of("§e§nWorldEdit CUI").onClick(BookUtil.ClickAction.openUrl("https://www.curseforge.com/minecraft/mc-mods/worldeditcui-forge-edition-2/download/2629418")).build(), BookUtil.TextBuilder.of(".").color(ChatColor.YELLOW).build());
                } catch (Exception e) {
                    e.printStackTrace();
                    p.sendMessage(projectsPrefix + "No estás dentro de ningún proyecto.");
                }
            }

            if (args[0].equalsIgnoreCase("review") || args[0].equalsIgnoreCase("revisar")) {
                try {
                    OldProject project = new OldProject(p.getLocation());
                    OldCountry country = project.getCountry();

                    if (s.getPermissionCountries().contains(project.getCountry().getName())) {

                        if (project.isPending()) {

                            if (args.length > 1) {
                                if (args[1].equalsIgnoreCase("accept") || args[1].equalsIgnoreCase("aceptar")) {
                                    // ADD POINTS

                                    int amount = project.getDifficulty().getPoints();

                                    country.getLogs().sendMessage(":mag: **" + s.getName() + "** ha aprobado el proyecto `" + project.getId() + "`.").queue();
                                    p.sendMessage(projectsPrefix + "Has aceptado el proyecto §a" + project.getId() + "§f.");

                                    for (OfflinePlayer member : project.getAllMembers()) {
                                        ServerPlayer m = new ServerPlayer(member);
                                        PointsManager mPointsManager = m.getPointsManager();
                                        mPointsManager.addPoints(country, amount);

                                        projectsManager.addFinishedProject(country);

                                        m.sendNotification(projectsPrefix + "Tu proyecto **§a" + project.getName(true) + "§f** ha sido aceptado.");
                                        m.sendNotification(pointsPrefix + "Has conseguido **§a" + amount + "§f** puntos. §7Total: " + mPointsManager.getPoints(country));
                                    }

                                    project.delete();

                                    for (OfflinePlayer member : project.getAllMembers()) {
                                        ServerPlayer m = new ServerPlayer(member);
                                        if (m.getScoreboardManager().getType() ==  ScoreboardManager.ScoreboardType.ME) {
                                            m.getScoreboardManager().update();
                                        }
                                    }

                                    for (Player player : Bukkit.getOnlinePlayers()) {
                                        ServerPlayer sPO = new ServerPlayer(player);
                                        if (sPO.getScoreboardManager().getType() == ScoreboardManager.ScoreboardType.TOP) {
                                            sPO.getScoreboardManager().update();
                                        }
                                    }

                                    for (Player player : WorldGuardProvider.getPlayersInRegion("project_" + project.getId())) {
                                        ServerPlayer sPO = new ServerPlayer(player);
                                        if (sPO.getScoreboardManager().getType() == ScoreboardManager.ScoreboardType.PROJECT) {
                                            sPO.getScoreboardManager().update();
                                        }
                                    }

                                }
                                if (args[1].equalsIgnoreCase("continue") || args[1].equalsIgnoreCase("continuar")) {
                                    project.setPending(false);
                                    project.save();

                                    p.sendMessage(projectsPrefix + "Has continuado el proyecto §a" + project.getId() + "§f.");

                                    for (OfflinePlayer member : project.getAllMembers()) {
                                        new ServerPlayer(member).sendNotification(projectsPrefix + "Tu proyecto **§a" + project.getName() + "§f** ha sido continuado.");
                                    }

                                    country.getLogs().sendMessage(":mag: **" + s.getName() + "** ha continuado el proyecto `" + project.getId() + "`.").queue();
                                }
                                if (args[1].equalsIgnoreCase("deny") || args[1].equalsIgnoreCase("denegar") || args[1].equalsIgnoreCase("rechazar")) {

                                    for (OfflinePlayer member : project.getAllMembers()) {
                                        ServerPlayer m = new ServerPlayer(member);
                                        m.sendNotification(projectsPrefix + "Tu proyecto **§a" + project.getName(true) + "§f** ha sido rechazado.");
                                    }

                                    project.setPending(false);
                                    project.empty();
                                    project.setName(null);
                                    project.save();
                                    p.sendMessage(projectsPrefix + "Has rechazado el proyecto §a" + project.getId() + "§f.");

                                    for (OfflinePlayer member : project.getAllMembers()) {
                                        ServerPlayer m = new ServerPlayer(member);
                                        if (m.getScoreboardManager().getType() ==  ScoreboardManager.ScoreboardType.ME) {
                                            m.getScoreboardManager().update();
                                        }
                                    }

                                    for (Player player : WorldGuardProvider.getPlayersInRegion("project_" + project.getId())) {
                                        ServerPlayer sPO = new ServerPlayer(player);
                                        if (sPO.getScoreboardManager().getType() == ScoreboardManager.ScoreboardType.PROJECT) {
                                            sPO.getScoreboardManager().update();
                                        }
                                    }

                                    country.getLogs().sendMessage(":mag: **" + s.getName() + "** ha rechazado el proyecto `" + project.getId() + "`.").queue();
                                }
                            } else {
                                p.sendMessage(projectsPrefix + "Introduce una acción.");
                            }

                        } else {
                            p.sendMessage(projectsPrefix + "Este proyecto no esta pendiente de revisión.");
                        }
                    } else {
                        p.sendMessage(projectsPrefix + "No puedes hacer esto aquí.");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    p.sendMessage(projectsPrefix + "No estás dentro de ningún proyecto.");
                }
            }

            if (args[0].equalsIgnoreCase("name") || args[0].equalsIgnoreCase("nombre")) {

                try {
                    OldProject project = new OldProject(p.getLocation());
                    if (project.getOwner() == p) {
                        if (!(project.isPending())) {
                            if (args.length > 1 && args[1].matches("[a-zA-Z0-9_-]{1,32}")) {
                                project.setName(args[1]);
                                project.save();

                                p.sendMessage(projectsPrefix + "Has cambiado el nombre del proyecto a §a" + project.getName() + "§f.");

                                for (Player player : getPlayersInRegion("project_" + project.getId())) {
                                    ScoreboardManager manager = playerRegistry.get(player.getUniqueId()).getScoreboardManager();
                                    if (manager.getType() == ScoreboardManager.ScoreboardType.PROJECT) {
                                        manager.update();
                                    }
                                }
                            } else {
                                p.sendMessage(projectsPrefix + "Introduce un nombre válido.");
                            }
                        } else {
                           p.sendMessage(projectsPrefix + "No puedes administrar tu proyecto mientras está pendiente.");
                        }
                    } else {
                        p.sendMessage(projectsPrefix + "No eres el líder de este proyecto.");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    p.sendMessage(projectsPrefix + "No estás dentro de nigún proyecto.");
                }
            }

            if (args[0].equalsIgnoreCase("pending") || args[0].equalsIgnoreCase("pendientes")) {

                OldCountry country = new OldCountry(p.getLocation());

                if (!s.getPermissionCountries().contains(country.getName())) {

                    p.sendMessage(projectsPrefix + "§cNo puedes revisar los proyectos de este país.");
                    return true;

                }

                Configuration pendingConfig = new Configuration(Bukkit.getPluginManager().getPlugin("bteConoSur"), "pending_projects/pending");
                List<String> pending = pendingConfig.getStringList(country.getName());
                if (!pending.isEmpty()) {
                    BookUtil.BookBuilder book = BookUtil.writtenBook();

                    List<BaseComponent[]> pages = new ArrayList<>();

                    List<List<String>> subLists= Lists.partition(pending, 12);

                    for (List<String> subList : subLists) {
                        BookUtil.PageBuilder page = new BookUtil.PageBuilder();
                        page.add("§7---[ §rPENDIENTES §7]---");
                        page.newLine();

                        for (String str : subList) {
                            try {
                                OldProject project = new OldProject(str);

                                String coord = project.getAverageCoordinate().getBlockX() + " " + new Coords2D(project.getAverageCoordinate()).getHighestY() + " " + project.getAverageCoordinate().getBlockZ();

                                page.add("- ");
                                page.add(BookUtil.TextBuilder.of(str)
                                        .onHover(BookUtil.HoverAction.showText("Click para ir"))
                                        .onClick(BookUtil.ClickAction.runCommand("/tp " + coord))
                                        .build());
                                page.newLine();

                            } catch (Exception exception) {
                                exception.printStackTrace();
                            }
                        }
                        pages.add(page.build());
                    }

                    book.pages(pages);

                    BookUtil.openPlayer(p, book.build());
                } else {
                    p.sendMessage(projectsPrefix + "No hay proyectos pendientes de revisión en este país.");
                }
            }

            if (args[0].equalsIgnoreCase("finish") || args[0].equalsIgnoreCase("terminar")|| args[0].equalsIgnoreCase("finalizar")) {

                try {
                    OldProject project = new OldProject(p.getLocation());

                    if (project.getOwner() == p) {
                        if (!(project.isPending())) {
                            if (finishConfirmation.contains(p)) {
                                finishConfirmation.remove(p);
                                project.setPending(true);
                                project.save();

                                p.sendMessage(projectsPrefix + "Has marcado el proyecto §a" + project.getName() + "§f como terminado.");

                                for (OfflinePlayer member : project.getMembers()) {
                                    new ServerPlayer(member).sendNotification(projectsPrefix + "**§a" + new ServerPlayer(project.getOwner()).getName() + "§f** ha marcado el proyecto **§a" + project.getName(true) + "§f** como terminado.");
                                }

                                project.getCountry().getLogs().sendMessage(":lock: **" + s.getName() + "** ha marcado el proyecto `" + project.getId() + "` como terminado.").queue();
                            } else {
                                finishConfirmation.add(p);
                                p.sendMessage(projectsPrefix + "§cNo podrás construir ni administrar tu proyecto mientras está en revisión. §fUsa el comando de nuevo para confirmar.");
                            }
                        } else {
                            p.sendMessage(projectsPrefix + "Este proyecto ya está marcado como terminado.");
                        }
                    } else {
                        p.sendMessage(projectsPrefix + " No eres el líder de este proyecto.");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    p.sendMessage(projectsPrefix + "No estás dentro de ningún proyecto.");
                }
            }

            if (args[0].equalsIgnoreCase("info") || args[0].equalsIgnoreCase("informacion")) {

                try {
                    OldProject project = new OldProject(p.getLocation());

                    //--------------------------------

                    BookUtil.BookBuilder builder = BookUtil.writtenBook();

                    BookUtil.PageBuilder page = new BookUtil.PageBuilder();

                    page.add("----[ PROYECTO ]----");

                    page.newLine();

                    if (!Objects.equals(project.getName(), project.getId())) {
                        page.add(BookUtil.TextBuilder.of("Nombre: ")
                                .color(ChatColor.GREEN)
                                .style(ChatColor.BOLD)
                                .build()
                        );
                        page.add(project.getName());
                        page.newLine();
                    }

                    page.add(BookUtil.TextBuilder.of("ID: ")
                            .color(ChatColor.GREEN)
                            .style(ChatColor.BOLD)
                            .build()
                    );
                    page.add(project.getId());
                    page.newLine();

                    page.add(BookUtil.TextBuilder.of("Dificultad: ")
                            .color(ChatColor.GREEN)
                            .style(ChatColor.BOLD)
                            .build()
                    );
                    page.add(project.getDifficulty().toString().toLowerCase().replace("facil", "Fácil").replace("intermedio", "Intermedio").replace("dificil", "Difícil"));
                    page.newLine();

                    page.add(BookUtil.TextBuilder.of("País: ")
                            .color(ChatColor.GREEN)
                            .style(ChatColor.BOLD)
                            .build()
                    );
                    page.add(StringUtils.capitalize(project.getCountry().getName().replace("peru", "perú")));
                    page.newLine();

                    // TAG

                    if (project.getTag() != null) {
                        page.add(BookUtil.TextBuilder.of("Tipo: ")
                                .color(ChatColor.GREEN)
                                .style(ChatColor.BOLD)
                                .build()
                        );
                        page.add(StringUtils.capitalize(project.getTag().toString().toLowerCase().replace("_", " ")));
                        page.newLine();
                    }

                    // GMAPS

                    Coords2D gMaps = new Coords2D(project.getAverageCoordinate());
                    page.add(BookUtil.TextBuilder.of("GoogleMaps: ")
                            .color(ChatColor.GREEN)
                            .style(ChatColor.BOLD)
                            .build()
                    );
                    page.add(BookUtil.TextBuilder.of("ENLACE")
                            .color(ChatColor.BLACK)
                            .style(ChatColor.UNDERLINE)
                            .onHover(BookUtil.HoverAction.showText("Haz click para abrir el enlace."))
                            .onClick(BookUtil.ClickAction.openUrl("https://www.google.com/maps/@" + gMaps.getLat() + "," + gMaps.getLon() + ",19z"))
                            .build());
                    page.newLine();


                    if (project.getOwner() != null) {
                        page.add(BookUtil.TextBuilder.of("Líder: ")
                                .color(ChatColor.GREEN)
                                .style(ChatColor.BOLD)
                                .build()
                        );
                        page.add(BookUtil.TextBuilder.of(new ServerPlayer(project.getOwner()).getName())
                                .onHover(BookUtil.HoverAction.showText(new ServerPlayer(project.getOwner()).getLore()))
                                .build()
                        );
                        page.newLine();
                    }
                    
                    int i = 1;
                    if (!project.getMembers().isEmpty()) {
                        page.add("§a§lMiembro(s) (" + project.getMembers().size() + "): §r");
                        for (OfflinePlayer member : project.getMembers()) {
                            page.add(
                                    BookUtil.TextBuilder.of(new ServerPlayer(member).getName())
                                            .onHover(BookUtil.HoverAction.showText(new ServerPlayer(member).getLore()))
                                            .build()
                            );

                            if (i < project.getMembers().size()) {
                                page.add(", ");
                            }
                            i++;
                        }
                    }

                    builder.pages(page.build());

                    BookUtil.openPlayer(p, builder.build());

                } catch (Exception e) {
                    p.sendMessage(projectsPrefix + "No estás dentro de ningún proyecto.");
                }

            }

            if (args[0].equalsIgnoreCase("list") || args[0].equalsIgnoreCase("lista")) {

                if (projectsManager.getAllProjects().size() != 0) {
                    BookUtil.BookBuilder book = BookUtil.writtenBook();

                    List<BaseComponent[]> pages = new ArrayList<>();

                    for (String id : projectsManager.getAllProjects()) {

                        try {

                            OldProject project = new OldProject(id);

                            BookUtil.PageBuilder page = new BookUtil.PageBuilder();

                            if (!Objects.equals(project.getName(), project.getId())) {
                                page.add("§a§lNombre: §r" + project.getName());
                                page.newLine();
                            }

                            page.add("§a§lID: §r" + project.getId());
                            page.newLine();

                            page.add("§a§lDificultad: §r" + project.getDifficulty().toString().toUpperCase());
                            page.newLine();

                            page.add("§a§lPaís: §r" + StringUtils.capitalize(project.getCountry().getName().replace("peru", "perú")));
                            page.newLine();

                            page.add("§a§lCoordenadas: §r\n");
                            page.add(
                                    BookUtil.TextBuilder.of(project.getAverageCoordinate().getBlockX() + " " + new Coords2D(project.getAverageCoordinate()).getHighestY() + " " + project.getAverageCoordinate().getBlockZ())
                                            .onHover(BookUtil.HoverAction.showText("Click para ir"))
                                            .onClick(BookUtil.ClickAction.runCommand("/tp " + project.getAverageCoordinate().getBlockX() + " " + new Coords2D(project.getAverageCoordinate()).getHighestY() + " " + project.getAverageCoordinate().getBlockZ()))
                                            .build()
                            );
                            page.newLine();


                            page.add("§a§lLíder: §r");
                            ServerPlayer sOwner = new ServerPlayer(project.getOwner());
                            page.add(
                                    BookUtil.TextBuilder.of(sOwner.getName())
                                            .onHover(BookUtil.HoverAction.showText(sOwner.getLore()))
                                            .build()
                            );
                            page.newLine();

                            int i = 1;
                            List<OfflinePlayer> members = project.getMembers();
                            if (!members.isEmpty()) {
                                page.add("§a§lMiembro(s)" + project.getMembers().size() + ": §r");
                                for (OfflinePlayer member : members) {
                                    ServerPlayer sMember = new ServerPlayer(member);
                                    page.add(
                                            BookUtil.TextBuilder.of(sMember.getName())
                                                    .onHover(BookUtil.HoverAction.showText(sMember.getLore()))
                                                    .build()
                                    );

                                    if (i < members.size()) {
                                        page.add(", ");
                                    }
                                    i++;
                                }
                            }

                            pages.add(page.build());
                        } catch (Exception e) {
                            Bukkit.getConsoleSender().sendMessage("Problema con el proyecto: " + id);
                        }
                    }

                    book.pages(pages);
                    BookUtil.openPlayer(p, book.build());
                } else {
                    p.sendMessage(projectsPrefix + "No tienes proyectos activos.");
                }
            }

            if (args[0].equalsIgnoreCase("manage") || args[0].equalsIgnoreCase("manejar")) {

                try {
                    OldProject project = new OldProject(p.getLocation());

                    if (project.getOwner() == p) {
                        Inventory gui = Bukkit.createInventory(null, 54, "Proyecto " + project.getId().toUpperCase() + (project.getName() != null ? " - " + project.getName(true) : ""));

                        List<Integer> membersSlots = Arrays.asList(28, 29, 30, 31, 32, 33, 34, 37, 38, 39, 40, 41, 42, 43);

                        for (int i = 0; i <= 53; i++) {
                            if (!membersSlots.contains(i)) {
                                gui.setItem(i, background);
                            }
                        }

                        final Map<Integer, String> actions = new HashMap<>();

                        int i = 0;
                        for (OfflinePlayer member : project.getMembers()) {
                            ItemStack memberHead = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
                            SkullMeta meta = (SkullMeta) memberHead.getItemMeta();
                            ServerPlayer sMember = new ServerPlayer(member);
                            meta.setDisplayName("§f" + sMember.getName());
                            meta.setLore(Arrays.asList(
                                    sMember.getLoreWithoutTitle(),
                                    "\n§c[-] §7Haz click para §cremover §7al jugador del proyecto"
                            ));
                            actions.put(membersSlots.get(i), "remove " + sMember.getPlayer().getUniqueId());
                            meta.setOwningPlayer(member);
                            memberHead.setItemMeta(meta);

                            gui.setItem(membersSlots.get(i), memberHead);
                            i++;
                        }

                        if (project.getMembers().size() < 14) {
                            ItemStack add = Misc.getCustomHead("§fAgregar miembros", "§a[+] §7Haz click para §aagregar §7miembros al proyecto", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWZmMzE0MzFkNjQ1ODdmZjZlZjk4YzA2NzU4MTA2ODFmOGMxM2JmOTZmNTFkOWNiMDdlZDc4NTJiMmZmZDEifX19");
                            gui.setItem(membersSlots.get(i), add);
                            actions.put(membersSlots.get(i), "add");
                        }

                        OfflinePlayer owner = project.getOwner();
                        ServerPlayer sOwner = new ServerPlayer(owner);
                        ItemStack ownerHead = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
                        SkullMeta meta = (SkullMeta) ownerHead.getItemMeta();
                        meta.setDisplayName("§a§lLíder: §f" + sOwner.getName());
                        meta.setLore(Arrays.asList(
                                s.getLoreWithoutTitle(),
                                "\n§e[➡] §7Haz click para §etransferir §7el proyecto"
                        ));
                        actions.put(13, "transfer");
                        meta.setOwningPlayer(owner);
                        ownerHead.setItemMeta(meta);

                        gui.setItem(13, ownerHead);

                        p.openInventory(gui);

                        inventoryActions.put(p.getUniqueId(), actions);

                    } else {
                        p.sendMessage(projectsPrefix + "No eres el líder de este proyecto.");
                    }
                } catch (Exception e) {
                    p.sendMessage(projectsPrefix + "No estás dentro de ningún proyecto.");
                }
            }

            if (args[0].equalsIgnoreCase("request") || args[0].equalsIgnoreCase("solicitar")) {

                try {
                    OldProject project = new OldProject(p.getLocation());
                    if (project.getOwner() != null) {
                        if (!(project.getAllMembers().contains(p))) {
                            new ServerPlayer(project.getOwner()).sendNotification(projectsPrefix + "**§a" + s.getName() + "§f** ha solicitado unirse a tu proyecto **§a" + project.getName(true) + "§f**.");
                            p.sendMessage(projectsPrefix + "Se ha enviado la solicitud.");

                        } else {
                            p.sendMessage(projectsPrefix + "Ya eres parte de este proyecto.");
                        }
                    } else {
                        p.sendMessage(projectsPrefix + "Este proyecto no está reclamado aún.");
                    }
                } catch (Exception e) {
                    p.sendMessage(projectsPrefix + "No estás dentro de ningun proyecto.");
                }
            }

            if (args[0].equalsIgnoreCase("tutorial")) {

                UUID uuid = p.getUniqueId();
                if (args.length > 1 && (args[1].equalsIgnoreCase("exit") || args[1].equalsIgnoreCase("salir"))) {

                    if (tutorialSteps.containsKey(uuid)) {
                        p.sendMessage(" ");
                        p.sendMessage(projectsPrefix + "Has salido del tutorial.");
                        p.sendMessage(" ");
                    }
                    tutorialSteps.remove(uuid);
                    return true;
                }


                if (!tutorialSteps.containsKey(uuid)) {
                    if (args.length < 2) {
                        tutorialSteps.put(uuid, 1);

                        p.sendMessage(projectsPrefix + "¡Has iniciado el tutorial!");
                        p.sendMessage(" ");
                        p.sendMessage(formatStringWithBaseComponents("[§d1§f] Ve a donde quieres construir. Puedes hacer esto usando §a/tpdir [dirección]§f. %s%",
                                Arrays.asList("§7§n[HECHO]", "Haz click para avanzar al siguiente paso", "/p tutorial step2")));
                        p.sendMessage(" ");
                    }

                } else {

                    if (args.length > 1) {
                        int step = tutorialSteps.get(uuid);
                        if (args[1].equals("step2")) {
                            if (step == 1) {
                                tutorialSteps.put(uuid, 2);

                                p.sendMessage(" ");
                                p.sendMessage(formatStringWithBaseComponents("[§d2§f] §6¡Vamos a conseguir un proyecto! §fPrimero, verifica si es que no hay un proyecto ya creado aquí. Párate en el centro de donde quieres construir y aprieta el siguiente botón. %s%",
                                        Arrays.asList("§7§n[VERIFICAR]", "Haz click para verificar", "/p verifyTutorial")));
                                p.sendMessage(" ");
                            }
                        }
                        if (args[1].equals("step3claim")) {
                            if (step == 2) {
                                tutorialSteps.put(uuid, 3);
                                p.sendMessage(" ");
                                p.sendMessage(formatStringWithBaseComponents( "[§d3§f] ¡Ya hay un proyecto creado aquí! Usa %s%§f para reclamarlo.",
                                        Arrays.asList("§a/p claim", "Haz click para usar el comando", "/p claimTutorial")));
                                p.sendMessage(" ");
                            }
                        }
                        if (args[1].equals("step3claimed")) {
                            if (step == 2) {
                                p.sendMessage(" ");
                                p.sendMessage("[§d3§f] ¡Ya hay un proyecto reclamado aquí! Puedes solicitar unirte a este proyecto o ir a otro lugar.");
                                p.sendMessage(formatStringWithBaseComponents("%s% %s%",
                                        Arrays.asList("§a§n[SOLICITAR UNIRME]", "Haz click para solicitar unirte al proyecto", "/p requestTutorial"),
                                        Arrays.asList("§7§n[VERIFICAR EN OTRO LUGAR]", "Haz click aquí luego de haber ido a otro lugar", "/p verifyTutorial")));
                                p.sendMessage(" ");
                            }
                        }
                        if (args[1].equals("step3create")) {
                            if (step == 2) {
                                tutorialSteps.put(uuid, 3);
                                p.sendMessage(" ");
                                p.sendMessage("[§d3§f] No hay ningún proyecto creado aquí. ¡Crea uno!");
                                p.sendMessage(formatStringWithBaseComponents("%s%",
                                        Arrays.asList("§7§n[COMO CREAR UN PROYECTO]", "Haz click para avanzar al siguiente paso", "/p tutorial step4create")));
                                p.sendMessage(" ");
                            }
                        }
                        if (args[1].equals("step4create")) {
                            if (step == 3) {
                                tutorialSteps.put(uuid, 4);
                                p.sendMessage(" ");
                                p.sendMessage(formatStringWithBaseComponents("[§d4§f] Usa %s%§f para obtener el hacha de WorldEdit. %s%",
                                        Arrays.asList("§a//wand", "Haz click para usar el comando", "//wand"),
                                        Arrays.asList("§7§n[SIGUIENTE]", "Haz click para avanzar al siguiente paso", "/p tutorial step5create")));
                                p.sendMessage(" ");
                            }
                        }
                        if (args[1].equals("step5create")) {
                            if (step == 4) {
                                tutorialSteps.put(uuid, 5);
                                p.sendMessage(" ");
                                p.sendMessage(formatStringWithBaseComponents("[§d5§f] Usa %s%§f para cambiar tu forma de selección a poligonal. %s%",
                                        Arrays.asList("§a//sel poly", "Haz click para usar el comando", "//sel poly"),
                                        Arrays.asList("§7§n[SIGUIENTE]", "Haz click para avanzar al siguiente paso", "/p tutorial step6create")));
                                p.sendMessage(" ");
                            }
                        }
                        if (args[1].equals("step6create")) {
                            if (step == 5) {
                                tutorialSteps.put(uuid, 6);
                                p.sendMessage(" ");
                                p.sendMessage(formatStringWithBaseComponents("[§d6§f] Selecciona usando el hacha los bordes de tu proyecto. %s%",
                                        Arrays.asList("§7§n[SIGUIENTE]", "Haz click para avanzar al siguiente paso", "/p tutorial step7create")));
                                p.sendMessage("§7> Click izquierdo para el primer punto, click derecho para el resto.");
                                p.sendMessage(formatStringWithBaseComponents("§7> Si te equivocas, puedes usar %s%§7 para deshacer tu selección.",
                                        Arrays.asList("§f//sel", "Haz click para usar el comando", "//sel")));
                                p.sendMessage(" ");
                            }
                        }
                        if (args[1].equals("step7create")) {
                            if (step == 6) {
                                tutorialSteps.put(uuid, 7);
                                p.sendMessage(" ");
                                p.sendMessage(formatStringWithBaseComponents("[§d7§f] Usa %s%§f para crear tu proyecto. Se enviará una solicitud que deberá ser aceptada. %s%",
                                        Arrays.asList("§a/p create", "Haz click para usar el comando", "/p create"),
                                        Arrays.asList("§7§n[SIGUIENTE]", "Haz click para avanzar al siguiente paso", "/p tutorial step8create")));
                                p.sendMessage(" ");
                            }
                        }
                        if (args[1].equals("step8create")) {
                            if (step == 7) {
                                tutorialSteps.put(uuid, 8);
                                p.sendMessage(" ");
                                p.sendMessage(formatStringWithBaseComponents("[§d8§f] ¡Felicidades! Ya creaste tu primer proyecto. Ahora solo queda esperar a que sea aprobado. %s%",
                                        Arrays.asList("§c§n[SALIR]", "Haz click para avanzar al salir del tutorial", "/p tutorial exit")));
                                p.sendMessage("§7> Recibirás una notificación cuando el proyecto sea aprobado.");
                                p.sendMessage(" ");
                            }
                        }
                    }

                }
            }

            if (args[0].equals("claimTutorial")) {
                if (tutorialSteps.containsKey(p.getUniqueId()) && tutorialSteps.get(p.getUniqueId()) == 3) {
                    p.performCommand("p claim");
                    p.performCommand("p tuturial exit");
                }
            }

            if (args[0].equals("requestTutorial")) {
                if (tutorialSteps.containsKey(p.getUniqueId()) && tutorialSteps.get(p.getUniqueId()) == 3) {
                    p.performCommand("p request");
                    p.performCommand("p tuturial exit");
                }
            }

            if (args[0].equals("verifyTutorial")) {
                if (tutorialSteps.containsKey(p.getUniqueId()) && tutorialSteps.get(p.getUniqueId()) == 2) {
                    if (OldProject.isProjectAt(p.getLocation())) {

                        OldProject project = new OldProject(p.getLocation());

                        if (project.isClaimed()) {
                            p.performCommand("p tutorial step3claimed");
                        } else {
                            p.performCommand("p tutorial step3claim");
                        }

                    } else {
                        p.performCommand("p tutorial step3create");
                    }
                }
            }

            if (args[0].equalsIgnoreCase("redefine") || args[0].equalsIgnoreCase("redefinir")) {

                if (OldProject.isProjectAt(p.getLocation())) {
                    OldProject project = new OldProject(p.getLocation());

                    // GET POINTS

                    List<BlockVector2D> points;
                    try {
                        points = polyRegion(getSelection(p)).getPoints();
                    } catch (IncompleteRegionException e) {
                        p.sendMessage(projectsPrefix + "Selecciona un área primero.");
                        return true;
                    } catch (IllegalArgumentException e) {
                        p.sendMessage(projectsPrefix + "Debes seleccionar una region cúbica o poligonal.");
                        return true;
                    }

                    if (points.size() > maxProjectPoints) {
                        p.sendMessage(projectsPrefix + "La selección no puede tener más de " + maxProjectPoints + " puntos.");
                        return true;
                    }

                    if (!project.getCountry().getName().equals(new OldCountry(points.get(0)).getName())) {

                        p.sendMessage(projectsPrefix + "No puedes redefinir un proyecto fuera del país original.");
                        return true;

                    }

                    if (s.getPermissionCountries().contains(project.getCountry().getName())) {

                        if (project.isPending()) {
                            p.sendMessage("No puedes hacer esto mientras el proyecto está pendiente de revisión.");
                            return true;
                        }

                        if (args.length < 2) {
                            p.sendMessage(projectsPrefix + "Introduce una dificultad, puede ser §afacil§f, §aintermedio§f o §adificil§f.");
                            return true;
                        }

                        if ((!(args[1].equalsIgnoreCase("facil"))) && (!(args[1].equalsIgnoreCase("intermedio"))) && (!(args[1].equalsIgnoreCase("dificil")))) {
                            p.sendMessage(projectsPrefix + "Introduce una dificultad válida, puede ser §afacil§f, §aintermedio§f o §adificil§f.");
                            return true;
                        }

                        project.setDifficulty(OldProject.Difficulty.valueOf(args[1].toUpperCase()));
                        project.setPoints(points);

                        project.save();

                        for (Player player : getPlayersInRegion("project_" + project.getId())) {
                            ScoreboardManager manager = playerRegistry.get(player.getUniqueId()).getScoreboardManager();
                            if (manager.getType() == ScoreboardManager.ScoreboardType.PROJECT) {
                                manager.update();
                            }
                        }
                        // SEND MESSAGES

                        p.sendMessage(projectsPrefix + "Proyecto con la ID §a" + project.getId() + "§f redefinido con dificultad §a" + project.getDifficulty().toString().toUpperCase()  + "§f.");

                        StringBuilder dscMessage = new StringBuilder(":clipboard: **" + p.getName() + "** ha redefinido el proyecto `" + project.getId() + "` con dificultad `" + args[1].toUpperCase() + "` en las coordenadas: \n");
                        for (BlockVector2D point : project.getPoints()) {
                            dscMessage.append("> ").append(Math.floor(point.getX())).append(" ").append(Math.floor(p.getWorld().getHighestBlockAt(point.getBlockX(), point.getBlockZ()).getY())).append(" ").append(Math.floor(point.getZ())).append("\n");
                        }
                        dscMessage = new StringBuilder(dscMessage.toString().replace(".0", ""));

                        StringBuilder notif = new StringBuilder("Tu proyecto **§a" + project.getName(true) + "§f** ha sido redefinido con dificultad **§a" + project.getDifficulty().toString().toUpperCase() + "§f** en las coordenadas: \n§7");
                        for (BlockVector2D point : project.getPoints()) {
                            notif.append("> ").append(Math.floor(point.getX())).append(" ").append(Math.floor(p.getWorld().getHighestBlockAt(point.getBlockX(), point.getBlockZ()).getY())).append(" ").append(Math.floor(point.getZ())).append("\n");
                        }

                        if (project.getOwner() != null) {
                            new ServerPlayer(project.getOwner()).sendNotification(projectsPrefix + notif);
                        }

                        project.getCountry().getLogs().sendMessage(dscMessage.toString()).queue();
                        return true;
                    } else {

                        if (project.getOwner() != p) {
                            p.sendMessage(projectsPrefix + "No eres el líder de este proyecto.");
                            return true;
                        }

                        if (project.isPending()) {
                            p.sendMessage("No puedes hacer esto mientras el proyecto está pendiente de revisión.");
                            return true;
                        }

                        EmbedBuilder request = new EmbedBuilder();
                        request.setColor(new Color(0, 255, 42));
                        request.setTitle(new ServerPlayer(p).getName() + " quiere redefinir el proyecto " + project.getId().toUpperCase() + ".");

                        List<String> oldCoords = new ArrayList<>();
                        for (BlockVector2D point : project.getPoints()) {
                            oldCoords.add(("> " + point.getX() + " " + new Coords2D(point).getHighestY() + " " + point.getZ()).replace(".0", ""));
                        }
                        request.addField(":blue_circle: Coordenadas antiguas:", String.join("\n", oldCoords), false);

                        List<String> newCoords = new ArrayList<>();
                        for (BlockVector2D point : points) {
                            newCoords.add(("> " + point.getX() + " " + new Coords2D(point).getHighestY() + " " + point.getZ()).replace(".0", ""));
                        }
                        request.addField(":red_circle: Coordenadas nuevas:", String.join("\n", newCoords), false);

                        // GMAPS

                        BlockVector2D average = project.getAverageCoordinate();

                        Coords2D geoCoord = new Coords2D(average);

                        request.addField(":map: Google Maps:", "https://www.google.com/maps/@" + geoCoord.getLat() + "," + geoCoord.getLon() + ",19z", false);

                        // IMAGE

                        String url;

                        List<String> coordsOld = new ArrayList<>();
                        for (BlockVector2D point : project.getPoints()) {
                            coordsOld.add(new Coords2D(point).getLat() + "," + new Coords2D(point).getLon());
                        }
                        coordsOld.add(new Coords2D(project.getPoints().get(0)).getLat() + "," + new Coords2D(project.getPoints().get(0)).getLon());

                        List<String> coordsNew = new ArrayList<>();
                        for (BlockVector2D point : points) {
                            coordsNew.add(new Coords2D(point).getLat() + "," + new Coords2D(point).getLon());
                        }
                        coordsNew.add(new Coords2D(points.get(0)).getLat() + "," + new Coords2D(points.get(0)).getLon());

                        url = "https://open.mapquestapi.com/staticmap/v5/map?key=" + key + "&type=sat&shape=" + String.join("|", coordsOld) + "|fill:6382DC50&shape=" + String.join("|", coordsNew) + "|fill:ff000050|border:ff0000&size=1280,720&imagetype=png";

                        request.setImage(url);

                        ActionRow actionRow = ActionRow.of(
                                Button.of(ButtonStyle.SECONDARY, "facil", "Fácil", Emoji.fromMarkdown("\uD83D\uDFE2")),
                                Button.of(ButtonStyle.SECONDARY, "intermedio", "Intermedio", Emoji.fromMarkdown("\uD83D\uDFE1")),
                                Button.of(ButtonStyle.SECONDARY, "dificil", "Difícil", Emoji.fromMarkdown("\uD83D\uDD34")),
                                Button.of(ButtonStyle.DANGER, "rechazar", "Rechazar", Emoji.fromMarkdown("✖️"))
                        );

                        MessageBuilder message = new MessageBuilder();
                        message.setEmbeds(request.build());
                        message.setActionRows(actionRow);

                        project.getCountry().getRequests().sendMessage(message.build()).queue();

                        p.sendMessage(projectsPrefix + "Se ha enviado una solicitud para redefinir tu proyecto.");
                    } p.sendMessage(projectsPrefix + "§cNo tienes permiso para hacer eso.");
                } else {
                    p.sendMessage(projectsPrefix + "No estás dentro de ningún proyecto.");
                }
            }

            if (args[0].equalsIgnoreCase("tag") || args[0].equalsIgnoreCase("etiqueta")) {
                if (OldProject.isProjectAt(p.getLocation())) {
                    OldProject project = new OldProject(p.getLocation());

                    if (!(s.getPermissionCountries().contains(project.getCountry().getName()))) {
                        p.sendMessage(projectsPrefix + "No puedes hacer esto aquí.");
                        return true;
                    }

                    if (args.length > 1) {
                        if (args[1].equalsIgnoreCase("edificios") || args[1].equalsIgnoreCase("departamentos") || args[1].equalsIgnoreCase("casas") || args[1].equalsIgnoreCase("parques") || args[1].equalsIgnoreCase("establecimientos") || args[1].equalsIgnoreCase("carreteras") || args[1].equalsIgnoreCase("centros_comerciales")) {
                            project.setTag(OldProject.Tag.valueOf(args[1].toUpperCase()));
                            project.save();

                            for (Player player : getPlayersInRegion("project_" + project.getId())) {
                                ScoreboardManager manager = playerRegistry.get(player.getUniqueId()).getScoreboardManager();
                                if (manager.getType() == ScoreboardManager.ScoreboardType.PROJECT) {
                                    manager.update();
                                }
                            }

                            project.getCountry().getLogs().sendMessage(":label: **" + s.getName() + "** ha establecido la etiqueta del proyecto `" + project.getId() + "` en **" + args[1].replace("_", " ").toUpperCase() + "**.").queue();

                            p.sendMessage(projectsPrefix + "Has establecido la etiquteda del proyecto §a" + project.getId() + "§f en §a" + args[1].replace("_", " ").toUpperCase() + "§f.");
                        } else if (args[1].equalsIgnoreCase("delete")) {
                            project.setTag(null);
                            project.save();

                            for (Player player : getPlayersInRegion("project_" + project.getId())) {
                                ScoreboardManager manager = playerRegistry.get(player.getUniqueId()).getScoreboardManager();
                                if (manager.getType() == ScoreboardManager.ScoreboardType.PROJECT) {
                                    manager.update();
                                }
                            }

                            project.getCountry().getLogs().sendMessage(":label: **" + s.getName() + "** ha eliminado la etiqueta del proyecto `" + project.getId() + "`.").queue();

                            p.sendMessage(projectsPrefix + "Has eliminado la etiqueta del proyecto §a" + project.getId() + "§f.");

                        } else {
                            p.sendMessage(projectsPrefix + "Introduce una etiqueta válida.");
                        }
                    } else {
                        p.sendMessage(projectsPrefix + "Introduce una etiqueta.");
                    }
                } else {
                    p.sendMessage(projectsPrefix + "No estás dentro de ningún proyecto.");
                }
            }

            if (args[0].equalsIgnoreCase("find") || args[0].equalsIgnoreCase("encontrar")) {
                Inventory pRandomGui = Bukkit.createInventory(null, 27, "1. Elige una dificultad");

                ItemStack glass = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15);
                ItemMeta gMeta = glass.getItemMeta();
                gMeta.setDisplayName(" ");
                glass.setItemMeta(gMeta);

                for (int i=0; i < 27; i++) {
                    pRandomGui.setItem(i, background);
                }

                pRandomGui.setItem(11, getCustomHead("§aFácil §f- 15 puntos", "§fProyectos de un área pequeña, con construcciones simples y rápidas.", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmFlMTE5YTIzODJlZGE4NjRiMjQ0ZmE4YzUzYWMzZTU0NDE2MzEwM2VlNjY3OTVmMGNkNmM2NGY3YWJiOGNmMSJ9fX0="));
                pRandomGui.setItem(13, getCustomHead("§eIntermedio §f- 50 puntos", "§fProyectos con una dificultad intermedia, que requieren un cierto nivel de planeación y dedicación.", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjIxOTk5NDM3YjFkZTJjNDcyMGFhZDQzMmIyZWE1MzgyZTE1NDgyNzc1MjNmMjViMGY1NWMzOWEwMWMyYTljMCJ9fX0="));
                pRandomGui.setItem(15, getCustomHead("§cDifícil §f- 100 puntos", "§fProyectos de gran tamaño y/o dificultad, que requieren gran detalle y planificación previa.", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjQ1ZTg1ZWRkYTFhODFkMjI0YWRiNzEzYjEzYjcwMzhkNWNjNmJlY2Q5OGE3MTZiOGEzZGVjN2UzYTBmOTgxNyJ9fX0="));

                p.openInventory(pRandomGui);
            }

            if (args[0].equalsIgnoreCase("wand")) {

                p.sendMessage(projectsPrefix + "Ahora la selección se hace con WorldEdit. Puedes usar una selección de tipo §acuboid§f o §apoly§f.");

            }
        return true;
    }
}
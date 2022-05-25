package pizzaaxx.bteconosur.projects;

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
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.coords.Coords2D;
import pizzaaxx.bteconosur.country.OldCountry;
import pizzaaxx.bteconosur.methods.CodeGenerator;
import pizzaaxx.bteconosur.misc.Misc;
import pizzaaxx.bteconosur.server.player.*;
import pizzaaxx.bteconosur.worldedit.WorldEditHelper;
import pizzaaxx.bteconosur.yaml.Configuration;
import xyz.upperlevel.spigot.book.BookUtil;

import java.awt.Color;
import java.util.List;
import java.util.*;

import static pizzaaxx.bteconosur.BteConoSur.*;
import static pizzaaxx.bteconosur.Config.*;
import static pizzaaxx.bteconosur.misc.Misc.getCountryAtLocation;
import static pizzaaxx.bteconosur.misc.Misc.getCustomHead;
import static pizzaaxx.bteconosur.projects.ProjectManageInventoryListener.inventoryActions;
import static pizzaaxx.bteconosur.server.player.PointsManager.pointsPrefix;
import static pizzaaxx.bteconosur.worldedit.WorldEditHelper.getSelection;
import static pizzaaxx.bteconosur.worldedit.WorldEditHelper.polyRegion;
import static pizzaaxx.bteconosur.worldguard.WorldGuardProvider.getPlayersInRegion;

public class ProjectsCommand implements CommandExecutor {
    public static String projectsPrefix = "§f[§dPROYECTO§f] §7>>§r ";
    public static Set<Player> transferConfirmation = new HashSet<>();
    public Set<Player> leaveConfirmation = new HashSet<>();
    public Set<Player> finishConfirmation = new HashSet<>();
    public Set<Player> deleteConfirmation = new HashSet<>();
    public static ItemStack background;
    public static Map<String, String> projectRequestsIDs = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(projectsPrefix + "Este comando solo puede ser usado por jugadores.");
            }

            Player p = (Player) sender;
            ServerPlayer s = new ServerPlayer(p);
            ProjectsManager projectsManager = s.getProjectsManager();
            if (args.length == 0) {
                sender.sendMessage(projectsPrefix + "Debes introducir un subcomando.");
                return true;
            }

            if (args[0].equals("create") || args[0].equals("crear")) {
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

                OldCountry country = new OldCountry(points.get(0));

                if (country.getName().equals("global")) {
                    p.sendMessage(projectsPrefix + "Los proyectos no funcionan aquí.");
                    return true;
                }

                if (p.hasPermission("bteconosur.projects.manage.create")) {

                    if (!s.getPermissionCountries().contains(country.getName())) {
                        p.sendMessage(projectsPrefix + "No puedes hacer esto aquí.");
                        return true;
                    }

                    if (args.length < 2) {
                        p.sendMessage(projectsPrefix + "Introduce una dificultad, puede ser §afacil§f, §aintermedio§f o §adificil§f.");
                        return true;
                    }

                    if ((!(args[1].equals("facil"))) && (!(args[1].equals("intermedio"))) && (!(args[1].equals("dificil")))) {
                        p.sendMessage(projectsPrefix + "Introduce una dificultad válida, puede ser §afacil§f, §aintermedio§f o §adificil§f.");
                        return true;
                    }

                    Project project = new Project(new OldCountry(new Location(mainWorld, points.get(0).getX(), 100 , points.get(0).getZ())), Project.Difficulty.valueOf(args[1].toUpperCase()), points);

                    project.save();

                    for (Player player : getPlayersInRegion("project_" + project.getId())) {
                        ScoreboardManager manager = playerRegistry.get(player.getUniqueId()).getScoreboardManager();
                        if (manager.getType() == ScoreboardManager.ScoreboardType.PROJECT) {
                            manager.update();
                        }
                    }

                    // SEND MESSAGES

                    p.sendMessage(projectsPrefix + "Proyecto con la ID §a" + project.getId()  + "§f creado con la dificultad §a" + project.getDifficulty().toString().toUpperCase() + "§f.");

                    StringBuilder dscMessage = new StringBuilder(":clipboard: **" + p.getName() + "** ha creado el proyecto `" + project.getId() + "` con dificultad `" + args[1].toUpperCase() + "` en las coordenadas: \n");
                    for (BlockVector2D point : project.getPoints()) {
                        dscMessage.append("> ").append(Math.floor(point.getX())).append(" ").append(Math.floor(p.getWorld().getHighestBlockAt(point.getBlockX(), point.getBlockZ()).getY())).append(" ").append(Math.floor(point.getZ())).append("\n");
                    }
                    dscMessage = new StringBuilder(dscMessage.toString().replace(".0", ""));

                    project.getCountry().getLogs().sendMessage(dscMessage.toString()).queue();

                    return true;
                } else if (p.hasPermission("bteconosur.projects.create")) {
                    if (projectsManager.getProjects(country).size() < maxProjectsPerPlayer) {
                        Project project = new Project(getCountryAtLocation(new Location(mainWorld, points.get(0).getX(), 100 , points.get(0).getZ())), Project.Difficulty.FACIL, points);

                        EmbedBuilder request = new EmbedBuilder();
                        request.setColor(new Color(0, 255, 42));

                        String id = CodeGenerator.generateCode(6);

                        request.setAuthor("ID de la solicitud: " + id);
                        projectRequestsIDs.put(id, project.getId());

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
                } else {
                    p.sendMessage(projectsPrefix + "§cNo tienes permiso para hacer eso.");
                }
            }

            if (args[0].equals("claim") || args[0].equals("reclamar")) {
                if (!(p.hasPermission("bteconosur.projects.claim"))) {
                    p.sendMessage(projectsPrefix + "§cNo tienes permiso para hacer eso.");
                    return true;
                }

                try {
                    Project project = new Project(p.getLocation());
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
                        if ((manager.getPrimaryGroup() == GroupsManager.PrimaryGroup.POSTULANTE || manager.getPrimaryGroup() == GroupsManager.PrimaryGroup.DEFAULT) && project.getDifficulty() != Project.Difficulty.FACIL) {
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

            if (args[0].equals("delete") || args[0].equals("eliminar")) {
                if (p.hasPermission("bteconosur.projects.manage.delete")) {
                    if (!(deleteConfirmation.contains(p))) {
                        deleteConfirmation.add(p);
                        p.sendMessage(projectsPrefix + "§cNo puedes deshacer esta acción. §fUsa el comando de nuevo para confirmar.");
                        return true;
                    }

                    deleteConfirmation.remove(p);
                    if (args.length >= 2) {
                        try {
                            Project project = new Project(args[1]);
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
                        } catch (Exception e) {
                            p.sendMessage(projectsPrefix + "Este proyecto no existe.");
                            return true;
                        }
                    } else {
                        try {
                            Project project = new Project(p.getLocation());
                            project.delete();

                            p.sendMessage(projectsPrefix + "Has eliminado el proyecto §a" + project.getId() + "§f.");
                            project.getCountry().getLogs().sendMessage(":wastebasket: **" + s.getName() + "** ha eliminado el proyecto `" + project.getId() + "`.").queue();
                        } catch (Exception e) {
                            p.sendMessage(projectsPrefix + "No estás dentro de ningun proyecto.");
                        }
                    }
                } else {
                    p.sendMessage(projectsPrefix + "§cNo puedes hacer esto.");
                }
                return true;
            }

            if (args[0].equals("add") || args[0].equals("agregar")) {
                if (!(p.hasPermission("bteconosur.projects.add"))) {
                    p.sendMessage(projectsPrefix + "§cNo tienes permiso para hacer eso.");
                    return true;
                }

                Project project;
                try {
                    project = new Project(p.getLocation());
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

            if (args[0].equals("remove") || args[0].equals("remover") || args[0].equals("quitar")) {
                if (!(p.hasPermission("bteconosur.projects.remove"))) {
                    p.sendMessage(projectsPrefix + "§cNo tienes permiso para hacer eso.");
                    return true;
                }

                Project project;
                try {
                    project = new Project(p.getLocation());
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

            if (args[0].equals("transfer") || args[0].equals("transferir")) {
                if (!(p.hasPermission("bteconosur.projects.transfer"))) {
                    p.sendMessage(projectsPrefix + "§cNo tienes permiso para hacer eso.");
                    return true;
                }

                try {
                    Project project = new Project(p.getLocation());
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

            if (args[0].equals("leave") || args[0].equals("abandonar")) {
                if (!(p.hasPermission("bteconosur.projects.leave"))) {
                    p.sendMessage(projectsPrefix + "§cNo tienes permiso para hacer eso.");
                    return true;
                }

                try {
                    Project project = new Project(p.getLocation());
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

            if (args[0].equals("borders") || args[0].equals("bordes")) {
                if (!(p.hasPermission("bteconosur.projects.showborder"))) {
                    p.sendMessage(projectsPrefix + "§cNo tienes permiso para hacer eso.");
                    return true;
                }

                try {
                    Project project = new Project(p.getLocation());

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

            if (args[0].equals("review") || args[0].equals("revisar")) {
                if (!(p.hasPermission("bteconosur.projects.manage.review"))) {
                    p.sendMessage(projectsPrefix + "§cNo tienes permiso para hacer eso.");
                    return true;
                }

                try {
                    Project project = new Project(p.getLocation());
                    OldCountry country = project.getCountry();

                    if (s.getPermissionCountries().contains(project.getCountry())) {

                        if (project.isPending()) {

                            if (args.length > 1) {
                                if (args[1].equals("accept") || args[1].equals("aceptar")) {
                                    // ADD POINTS

                                    int amount = project.getDifficulty().getPoints();

                                    country.getLogs().sendMessage(":mag: **" + s.getName() + "** ha aprobado el proyecto `" + project.getId() + "`.").queue();
                                    p.sendMessage(projectsPrefix + "Has aceptado el proyecto §a" + project.getId() + "§f.");


                                    ServerPlayer owner = new ServerPlayer(project.getOwner());
                                    ProjectsManager ownerProjectsManager = owner.getProjectsManager();
                                    PointsManager ownerPointsManager = owner.getPointsManager();

                                    owner.getPointsManager().addPoints(country, amount);

                                    ownerProjectsManager.addFinishedProject(country);

                                    owner.sendNotification(projectsPrefix + "Tu proyecto **§a" + project.getName(true) + "§f** ha sido aceptado.");
                                    owner.sendNotification(pointsPrefix + "Has conseguido **§a" + amount + "§f** puntos. §7Total: " + ownerPointsManager.getPoints(country));

                                    for (OfflinePlayer member : project.getMembers()) {
                                        ServerPlayer m = new ServerPlayer(member);
                                        PointsManager mPointsManager = m.getPointsManager();
                                        mPointsManager.addPoints(country, amount);

                                        projectsManager.addFinishedProject(country);

                                        m.sendNotification(projectsPrefix + "Tu proyecto **§a" + project.getName(true) + "§f** ha sido aceptado.");
                                        m.sendNotification(pointsPrefix + "Has conseguido **§a" + amount + "§f** puntos. §7Total: " + mPointsManager.getPoints(country));
                                    }

                                    project.delete();

                                }
                                if (args[1].equals("continue") || args[1].equals("continuar")) {
                                    project.setPending(false);
                                    project.save();

                                    p.sendMessage(projectsPrefix + "Has continuado el proyecto §a" + project.getId() + "§f.");

                                    for (OfflinePlayer member : project.getAllMembers()) {
                                        new ServerPlayer(member).sendNotification(projectsPrefix + "Tu proyecto **§a" + project.getName() + "§f** ha sido continuado.");
                                    }

                                    country.getLogs().sendMessage(":mag: **" + s.getName() + "** ha continuado el proyecto `" + project.getId() + "`.").queue();
                                }
                                if (args[1].equals("deny") || args[1].equals("denegar") || args[1].equals("rechazar")) {
                                    project.setPending(false);
                                    project.empty();
                                    project.setName(null);
                                    project.save();
                                    p.sendMessage(projectsPrefix + "Has rechazado el proyecto §a" + project.getId() + "§f.");

                                    for (OfflinePlayer member : project.getAllMembers()) {
                                        new ServerPlayer(member).sendNotification(projectsPrefix + "Tu proyecto **§a" + project.getName(true) + "§f** ha sido rechazado.");
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
                        p.sendMessage(projectsPrefix + "No puedes hacer esto.");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    p.sendMessage(projectsPrefix + "No estás dentro de ningún proyecto.");
                }
            }

            if (args[0].equals("name") || args[0].equals("nombre")) {
                if (!(p.hasPermission("bteconosur.projects.name"))) {
                    p.sendMessage(projectsPrefix + "§cNo tienes permiso para hacer eso.");
                    return true;
                }

                try {
                    Project project = new Project(p.getLocation());
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

            if (args[0].equals("pending") || args[0].equals("pendientes")) {
                if (!(p.hasPermission("bteconosur.projects.manage.pending"))) {
                    p.sendMessage(projectsPrefix + "§cNo tienes permiso para hacer eso.");
                    return true;
                }

                Configuration pendingConfig = new Configuration(Bukkit.getPluginManager().getPlugin("bteConoSur"), "pending_projects/pending");
                List<String> pending = pendingConfig.getStringList(new OldCountry(p.getLocation()).getName());
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
                                Project project = new Project(str);

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

            if (args[0].equals("finish") || args[0].equals("terminar")|| args[0].equals("finalizar")) {
                if (!(p.hasPermission("bteconosur.projects.finish"))) {
                    p.sendMessage(projectsPrefix + "§cNo tienes permiso para hacer eso.");
                    return true;
                }

                try {
                    Project project = new Project(p.getLocation());

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
                            p.sendMessage("Este proyecto ya está marcado como terminado.");
                        }
                    } else {
                        p.sendMessage(projectsPrefix + " No eres el líder de este proyecto.");
                    }
                } catch (Exception e) {
                    p.sendMessage(projectsPrefix + "No estás dentro de ningún proyecto.");
                }
            }

            if (args[0].equals("info") || args[0].equals("informacion")) {
                if (!(p.hasPermission("bteconosur.projects.info"))) {
                    p.sendMessage(projectsPrefix + "§cNo tienes permiso para hacer eso.");
                    return true;
                }

                try {
                    Project project = new Project(p.getLocation());

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
                    e.printStackTrace();
                    p.sendMessage(projectsPrefix + "No estás dentro de ningún proyecto.");
                }

            }

            if (args[0].equals("list") || args[0].equals("lista")) {
                if (!(p.hasPermission("bteconosur.projects.list"))) {
                    p.sendMessage(projectsPrefix + "§cNo tienes permiso para hacer eso.");
                    return true;
                }

                if (projectsManager.getAllProjects().size() != 0) {
                    BookUtil.BookBuilder book = BookUtil.writtenBook();

                    List<BaseComponent[]> pages = new ArrayList<>();

                    for (String id : projectsManager.getAllProjects()) {

                        Project project = new Project(id);

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
                    }

                    book.pages(pages);
                    BookUtil.openPlayer(p, book.build());
                } else {
                    p.sendMessage(projectsPrefix + "No tienes proyectos activos.");
                }
            }

            if (args[0].equals("manage") || args[0].equals("manejar")) {
                // TODO FINISH THIS
                if (!(p.hasPermission("bteconosur.projects.members"))) {
                    p.sendMessage(projectsPrefix + "§cNo tienes permiso para hacer eso.");
                    return true;
                }

                try {
                    Project project = new Project(p.getLocation());

                    if (project != null) {
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
                    } else {
                        p.sendMessage(projectsPrefix + "Algo ha salido mal.");
                    }
                } catch (Exception e) {
                    p.sendMessage(projectsPrefix + "No estás dentro de ningún proyecto.");
                }
            }

            if (args[0].equals("request") || args[0].equals("solicitar")) {
                if (!(p.hasPermission("bteconosur.projects.request"))) {
                    p.sendMessage(projectsPrefix + "§cNo tienes permiso para hacer eso.");
                    return true;
                }

                try {
                    Project project = new Project(p.getLocation());
                    if (project.getOwner() != null) {
                        if (!(project.getAllMembers().contains(p))) {
                            new ServerPlayer(project.getOwner()).sendNotification(projectsPrefix + "**§a" + s.getName() + "§f** ha solicitado unirse a tu proyecto **§a" + project.getName(true) + "§f**.");
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

            if (args[0].equals("tutorial")) {
                if (!(p.hasPermission("bteconosur.projects.tutorial"))) {
                    p.sendMessage(projectsPrefix + "§cNo tienes permiso para hacer eso.");
                    return true;
                }
            }

            if (args[0].equals("redefine") || args[0].equals("redefinir")) {
                if (!(p.hasPermission("bteconosur.projects.redefine"))) {
                    p.sendMessage(projectsPrefix + "§cNo tienes permiso para hacer eso.");
                    return true;
                }

                try {
                    Project project = new Project(p.getLocation());

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

                    if (p.hasPermission("bteconosur.projects.manage.redefine")) {

                        if (!s.getPermissionCountries().contains(project.getCountry().getName())) {
                            p.sendMessage(projectsPrefix + "No puedes hacer esto aquí.");
                            return true;
                        }

                        if (project.isPending()) {
                            p.sendMessage("No puedes hacer esto mientras el proyecto está pendiente de revisión.");
                            return true;
                        }

                        if (args.length < 2) {
                            p.sendMessage(projectsPrefix + "Introduce una dificultad, puede ser §afacil§f, §aintermedio§f o §adificil§f.");
                            return true;
                        }

                        if ((!(args[1].equals("facil"))) && (!(args[1].equals("intermedio"))) && (!(args[1].equals("dificil")))) {
                            p.sendMessage(projectsPrefix + "Introduce una dificultad válida, puede ser §afacil§f, §aintermedio§f o §adificil§f.");
                            return true;
                        }

                        project.setDifficulty(Project.Difficulty.valueOf(args[1].toUpperCase()));
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
                    } else if (p.hasPermission("bteconosur.projects.redefine")) {

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
                    } else {
                        p.sendMessage(projectsPrefix + "§cNo tienes permiso para hacer eso.");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    p.sendMessage(projectsPrefix + "No estás dentro de ningún proyecto.");
                }
            }

            // TODO DISCORD TAG COMMAND

            if (args[0].equals("tag") || args[0].equals("etiqueta")) {
                if (p.hasPermission("bteconosur.projects.manage.tag")) {
                    try {
                        Project project = new Project(p.getLocation());

                        if (!(s.getPermissionCountries().contains(project.getCountry().getName()))) {
                            p.sendMessage(projectsPrefix + "No puedes hacer esto aquí.");
                        }

                        if (args.length > 1) {
                            if (args[1].equals("edificios") || args[1].equals("departamentos") || args[1].equals("casas") || args[1].equals("parques") || args[1].equals("establecimientos") || args[1].equals("carreteras") || args[1].equals("centros_comerciales")) {
                                project.setTag(Project.Tag.valueOf(args[1].toUpperCase()));
                                project.save();

                                for (Player player : getPlayersInRegion("project_" + project.getId())) {
                                    ScoreboardManager manager = playerRegistry.get(player.getUniqueId()).getScoreboardManager();
                                    if (manager.getType() == ScoreboardManager.ScoreboardType.PROJECT) {
                                        manager.update();
                                    }
                                }

                                project.getCountry().getLogs().sendMessage(":label: **" + s.getName() + "** ha establecido la etiqueta del proyecto `" + project.getId() + "` en **" + args[1].replace("_", " ").toUpperCase() + "**.").queue();

                                p.sendMessage(projectsPrefix + "Has establecido la etiquteda del proyecto §a" + project.getId() + "§f en §a" + args[1].replace("_", " ").toUpperCase() + "§f.");
                            } else if (args[1].equals("delete")) {
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
                    } catch (Exception e) {
                        e.printStackTrace();
                        p.sendMessage(projectsPrefix + "No estás dentro de ningún proyecto.");
                    }
                } else {
                    p.sendMessage(projectsPrefix + "§cNo tienes permiso para hacer eso.");
                }
            }

            if (args[0].equals("find") || args[0].equals("encontrar")) {
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
        return true;
    }
}

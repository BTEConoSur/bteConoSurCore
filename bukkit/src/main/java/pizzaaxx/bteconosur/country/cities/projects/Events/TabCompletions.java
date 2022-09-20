package pizzaaxx.bteconosur.country.cities.projects.Events;

import com.sk89q.worldedit.BlockVector2D;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldedit.regions.Region;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import pizzaaxx.bteconosur.BteConoSur;
import pizzaaxx.bteconosur.ServerPlayer.ServerPlayer;
import pizzaaxx.bteconosur.country.Country;
import pizzaaxx.bteconosur.country.cities.projects.Project;
import pizzaaxx.bteconosur.country.cities.projects.ProjectSelector.NoProjectsFoundException;
import pizzaaxx.bteconosur.country.cities.projects.ProjectSelector.NotInsideProjectException;
import pizzaaxx.bteconosur.country.cities.projects.ProjectSelector.OwnerProjectSelector;
import pizzaaxx.bteconosur.country.cities.projects.ProjectSelector.SmallestProjectSelector;
import pizzaaxx.bteconosur.worldedit.WorldEditHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class TabCompletions implements TabCompleter {

    private final BteConoSur plugin;

    public TabCompletions(BteConoSur plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        List<String> completions = new ArrayList<>();

        if (sender instanceof Player) {

            Player p = (Player) sender;
            ServerPlayer s = plugin.getPlayerRegistry().get(p.getUniqueId());
            if (args.length == 1) {

                completions.add("find");
                completions.add("create");
                completions.add("tutorial");
                completions.add("list");

                if (p.hasPermission("bteconosur.projects.manage.pending")) {
                    completions.add("pending");
                }

                try {
                    Project project = plugin.getProjectsManager().getProjectAt(p.getLocation(), new SmallestProjectSelector(plugin));

                    completions.add("leave");
                    completions.add("borders");
                    completions.add("claim");
                    completions.add("request");
                    completions.add("add");
                    completions.add("remove");
                    completions.add("transfer");
                    completions.add("name");
                    completions.add("redefine");
                    completions.add("finish");
                    completions.add("redefine");
                    completions.add("manage");
                    completions.add("info");



                    if (s.getPermissionCountries().contains(project.getCountry().getName())) {
                        completions.add("review");
                        completions.add("tag");
                        completions.add("delete");
                    }
                } catch (NotInsideProjectException | NoProjectsFoundException ignored) {}
                Collections.sort(completions);

            } else if (args.length == 2) {

                if (args[0].equalsIgnoreCase("add")) {
                    try {
                        Project project = plugin.getProjectsManager().getProjectAt(p.getLocation(), new OwnerProjectSelector(p.getUniqueId(), true, plugin));

                        for (Player player : Bukkit.getOnlinePlayers()) {
                            if (!project.getMembers().contains(player.getUniqueId())) {
                                completions.add(player.getName());
                            }
                        }
                    } catch (NotInsideProjectException | NoProjectsFoundException ignored) {}
                    Collections.sort(completions);
                } else if (args[0].equalsIgnoreCase("create")) {

                    Location loc;

                    try {
                        Region region = plugin.getWorldEditHelper().getSelection(p);

                        Polygonal2DRegion polygon = plugin.getWorldEditHelper().polyRegion(region);

                        BlockVector2D vector = polygon.getPoints().get(0);

                        loc = new Location(plugin.getWorld(), vector.getX(), 100, vector.getZ());

                    } catch (IncompleteRegionException e) {
                        loc = p.getLocation();
                    }

                    if (plugin.getCountryManager().isInsideAnyCountry(loc)) {
                        Country country = plugin.getCountryManager().get(loc);

                        if (s.getPermissionCountries().contains(country.getName())) {

                            completions.add("facil");
                            completions.add("intermedio");
                            completions.add("dificil");

                        }
                    }
                } else if (args[0].equalsIgnoreCase("redefine")) {

                    try {
                        Project project = plugin.getProjectsManager().getProjectAt(p.getLocation(), new SmallestProjectSelector(plugin));

                        Country country = project.getCountry();

                        if (s.getPermissionCountries().contains(country.getName())) {

                            completions.add("facil");
                            completions.add("intermedio");
                            completions.add("dificil");

                        }
                    } catch (NotInsideProjectException | NoProjectsFoundException ignored) {}

                } else if (args[0].equalsIgnoreCase("remove")) {

                    try {
                        Project project = plugin.getProjectsManager().getProjectAt(p.getLocation(), new OwnerProjectSelector(p.getUniqueId(), true, plugin));

                        for (UUID member : project.getMembers()) {
                            completions.add(plugin.getPlayerName(member));
                        }
                    } catch (NotInsideProjectException | NoProjectsFoundException ignored) {}
                    Collections.sort(completions);

                } else if (args[0].equalsIgnoreCase("tag")) {

                    try {
                        Project project = plugin.getProjectsManager().getProjectAt(p.getLocation(), new SmallestProjectSelector(plugin));

                        Country country = project.getCountry();

                        if (s.getPermissionCountries().contains(country.getName())) {

                            completions.add("edificios");
                            completions.add("departamentos");
                            completions.add("casas");
                            completions.add("parques");
                            completions.add("establecimientos");
                            completions.add("carreteras");
                            completions.add("centros_comerciales");
                            completions.add("delete");

                        }
                    } catch (NotInsideProjectException | NoProjectsFoundException ignored) {}

                } else if (args[0].equalsIgnoreCase("transfer")) {

                    try {
                        Project project = plugin.getProjectsManager().getProjectAt(p.getLocation(), new OwnerProjectSelector(p.getUniqueId(), true, plugin));

                        for (UUID member : project.getMembers()) {

                            Player player = Bukkit.getPlayer(member);

                            if (player.isOnline()) {

                                completions.add(plugin.getPlayerName(member));

                            }

                        }
                    } catch (NotInsideProjectException | NoProjectsFoundException ignored) {}
                    Collections.sort(completions);

                } else if (args[0].equalsIgnoreCase("review")) {

                    try {
                        Project project = plugin.getProjectsManager().getProjectAt(p.getLocation(), new SmallestProjectSelector(plugin));

                        Country country = project.getCountry();

                        if (project.isPending() && s.getPermissionCountries().contains(country.getName())) {
                            completions.add("accept");
                            completions.add("continue");
                            completions.add("deny");
                        }
                    } catch (NotInsideProjectException | NoProjectsFoundException ignored) {}

                }

            } else if (args.length == 3) {

                if (args[0].equalsIgnoreCase("create") && (args[1].equalsIgnoreCase("facil") || args[1].equalsIgnoreCase("intermedio") || args[1].equalsIgnoreCase("dificil"))) {

                    Location loc;

                    try {
                        Region region = plugin.getWorldEditHelper().getSelection(p);
                        Polygonal2DRegion polygon = plugin.getWorldEditHelper().polyRegion(region);
                        BlockVector2D vector = polygon.getPoints().get(0);

                        loc = new Location(plugin.getWorld(), vector.getX(), 100, vector.getZ());
                    } catch (IncompleteRegionException e) {
                        loc = p.getLocation();
                    }

                    if (plugin.getCountryManager().isInsideAnyCountry(loc)) {
                        Country country = plugin.getCountryManager().get(loc);

                        if (s.getPermissionCountries().contains(country.getName())) {

                            for (Project.Tag tag : Project.Tag.values()) {

                                completions.add(tag.toString().toLowerCase());

                            }
                        }
                    }
                }
            }
        }

        List<String> finalCompletions = new ArrayList<>();

        for (String completion : completions) {

            if (completion.startsWith(args[args.length - 1])) {

                finalCompletions.add(completion);

            }

        }

        return finalCompletions;
    }

}

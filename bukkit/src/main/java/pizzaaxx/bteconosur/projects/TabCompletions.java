package pizzaaxx.bteconosur.projects;

import com.sk89q.worldedit.BlockVector2D;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldedit.regions.Region;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import pizzaaxx.bteconosur.server.player.ServerPlayer;
import pizzaaxx.bteconosur.country.OldCountry;
import pizzaaxx.bteconosur.worldedit.WorldEditHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static pizzaaxx.bteconosur.BteConoSur.mainWorld;

public class TabCompletions implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        List<String> completions = new ArrayList<>();

        if (sender instanceof Player) {

            Player p = (Player) sender;
            ServerPlayer s = new ServerPlayer(p);
            if (args.length == 1) {

                completions.add("find");
                completions.add("create");
                completions.add("tutorial");
                completions.add("list");

                if (p.hasPermission("bteconosur.projects.manage.pending")) {
                    completions.add("pending");
                }

                if (Project.isProjectAt(p.getLocation())) {

                    Project project = new Project(p.getLocation());


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
                }
                Collections.sort(completions);
            } else if (args.length == 2) {

                if (args[0].equals("add")) {

                    if (Project.isProjectAt(p.getLocation())) {

                        Project project = new Project(p.getLocation());

                        if (project.getOwner().getUniqueId() == p.getUniqueId()) {

                            for (Player player : Bukkit.getOnlinePlayers()) {

                                if (!project.getMemberUUIDs().contains(player.getUniqueId())) {

                                    completions.add(player.getName());

                                }

                            }

                        }
                    }
                    Collections.sort(completions);
                } else if (args[0].equals("create")) {

                    OldCountry country;

                    try {

                        Region region = WorldEditHelper.getSelection(p);

                        Polygonal2DRegion polygon = WorldEditHelper.polyRegion(region);

                        BlockVector2D vector = polygon.getPoints().get(0);

                        country = new OldCountry(new Location(mainWorld, vector.getX(), mainWorld.getHighestBlockYAt(vector.getBlockX(), vector.getBlockZ()), vector.getZ()));

                    } catch (IncompleteRegionException e) {

                        country = new OldCountry(p.getLocation());

                    }

                    if (s.getPermissionCountries().contains(country.getName())) {

                        completions.add("facil");
                        completions.add("intermedio");
                        completions.add("dificil");

                    }

                } else if (args[0].equals("redefine")) {

                    if (Project.isProjectAt(p.getLocation())) {

                        OldCountry country = new Project(p.getLocation()).getCountry();

                        if (s.getPermissionCountries().contains(country.getName())) {

                            completions.add("facil");
                            completions.add("intermedio");
                            completions.add("dificil");

                        }
                    }
                } else if (args[0].equals("remove")) {

                    if (Project.isProjectAt(p.getLocation())) {

                        Project project = new Project(p.getLocation());

                        if (project.getOwner().getUniqueId() == p.getUniqueId()) {

                            for (OfflinePlayer player : project.getMembers()) {

                                ServerPlayer member = new ServerPlayer(player);

                                completions.add(member.getName());

                            }

                        }
                    }
                    Collections.sort(completions);
                } else if (args[0].equals("tag")) {

                    if (Project.isProjectAt(p.getLocation())) {

                        Project project = new Project(p.getLocation());

                        if (s.getPermissionCountries().contains(project.getCountry().getName())) {

                            completions.add("edificios");
                            completions.add("departamentos");
                            completions.add("casas");
                            completions.add("parques");
                            completions.add("establecimientos");
                            completions.add("carreteras");
                            completions.add("centros_comerciales");
                            completions.add("delete");

                        }

                    }

                } else if (args[0].equals("transfer")) {

                    if (Project.isProjectAt(p.getLocation())) {

                        Project project = new Project(p.getLocation());

                        if (project.getOwner().getUniqueId() == p.getUniqueId()) {

                            for (OfflinePlayer player : project.getMembers()) {

                                if (player.isOnline()) {

                                    ServerPlayer member = new ServerPlayer(player);

                                    completions.add(member.getName());

                                }

                            }

                        }

                    }
                    Collections.sort(completions);

                } else if (args[0].equals("review")) {

                    if (Project.isProjectAt(p.getLocation())) {

                        Project project = new Project(p.getLocation());

                        if (project.isPending() && s.getPermissionCountries().contains(project.getCountry().getName())) {

                            completions.add("accept");
                            completions.add("continue");
                            completions.add("deny");

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

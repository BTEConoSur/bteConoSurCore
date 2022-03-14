package pizzaaxx.bteconosur.projects;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import pizzaaxx.bteconosur.serverPlayer.ServerPlayer;
import pizzaaxx.bteconosur.country.Country;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TabCompletions implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        List<String> completions = new ArrayList<>();

        if (sender instanceof Player) {
            Player p = (Player) sender;
            ServerPlayer s = new ServerPlayer(p);
            if (command.getName().equals("project")) {
                if (args.length == 0) {
                    completions.add("find");
                    completions.add("create");
                    completions.add("tutorial");

                    try {
                        Project project = new Project(p.getLocation());

                        completions.add("leave");
                        completions.add("borders");

                        if (!(project.isPending())) {
                            completions.add("claim");
                        }

                        if (project.getOwnerOld() != null) {
                            completions.add("request");
                        }

                        if (project.getOwnerOld() == p) {
                            completions.add("add");
                            completions.add("remove");
                            completions.add("transfer");
                            completions.add("name");
                            completions.add("redefine");
                            completions.add("finish");
                            completions.add("redefine");

                        }

                        if (s.getPermissionCountries().contains(new Country(p.getLocation()).getName())) {
                            if (project.isPending()) {
                                completions.add("review");
                            }
                            if (p.hasPermission("bteconosur.projects.manage.tag")) {
                                completions.add("tag");
                            }

                        }
                    } catch (Exception e) {

                    }

                    if (p.hasPermission("bteconosur.projects.manage.pending")) {
                        completions.add("pending");
                    }

                    if (s.getPermissionCountries().contains(new Country(p.getLocation()).getName())) {
                        completions.add("delete");
                    }
                }
            }
        }

        Collections.sort(completions);
        return completions;
    }
}

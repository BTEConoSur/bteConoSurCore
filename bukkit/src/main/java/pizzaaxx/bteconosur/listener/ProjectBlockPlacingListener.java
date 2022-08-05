package pizzaaxx.bteconosur.listener;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.country.OldCountry;
import pizzaaxx.bteconosur.projects.OldProject;
import pizzaaxx.bteconosur.worldguard.WorldGuardProvider;
import xyz.upperlevel.spigot.book.BookUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static pizzaaxx.bteconosur.projects.ProjectsCommand.projectsPrefix;

public class ProjectBlockPlacingListener implements Listener {

    private final Map<UUID, Long> lastWarn = new HashMap<>();

    @EventHandler
    public void onPlayerInteract(@NotNull PlayerInteractEvent event) {

        Player p = event.getPlayer();

        if (event.getItem() != null && event.getItem().getType() == Material.WOOD_AXE) {
            return;
        }

        if (lastWarn.containsKey(p.getUniqueId()) && System.currentTimeMillis() - lastWarn.get(p.getUniqueId()) < 3000) {
            return;
        }

        Block targetBlock = null;
        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {

            targetBlock = event.getClickedBlock();

        } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {

            if (event.getItem() != null) {
                targetBlock = event.getClickedBlock().getRelative(event.getBlockFace());
            }

        }

        if (targetBlock != null) {

            OldCountry country = new OldCountry(targetBlock.getLocation());

            if (!(country.getName().equals("global") || (country.getName().equals("argentina") && !WorldGuardProvider.getRegionNamesAt(targetBlock.getLocation()).contains("postulantes_arg")))) {

                boolean canBuild = false;

                for (String name : WorldGuardProvider.getRegionNamesAt(targetBlock.getLocation())) {

                        if (name.startsWith("project_")) {

                            String id = name.replace("project_", "");

                            if (OldProject.projectExists(id)) {

                                OldProject project = new OldProject(id);

                                if (project.getAllMembers().stream().map(OfflinePlayer::getUniqueId).collect(Collectors.toList()).contains(p.getUniqueId())) {

                                    canBuild = true;
                                    break;

                                }

                            }

                        }

                }

                if (!canBuild) {

                    if (OldProject.isProjectAt(targetBlock.getLocation())) {

                        OldProject project = new OldProject(targetBlock.getLocation());

                        if (project.isClaimed()) {

                            p.sendMessage(
                                    BookUtil.TextBuilder.of(projectsPrefix + "§cEnvía una solicitud al dueño de este proyecto para poder construir aquí. ").build(),
                                    BookUtil.TextBuilder.of("§a[SOLICITAR]").onClick(BookUtil.ClickAction.runCommand("/p request")).onHover(BookUtil.HoverAction.showText("Haz click para enviar una solicitud")).build()
                            );

                        } else {

                            p.sendMessage(
                                    BookUtil.TextBuilder.of(projectsPrefix + "§cReclama este proyecto para poder construir aquí. ").build(),
                                    BookUtil.TextBuilder.of("§a[RECLAMAR]").onClick(BookUtil.ClickAction.runCommand("/p claim")).onHover(BookUtil.HoverAction.showText("Haz click para reclamar")).build()
                            );

                        }
                        lastWarn.put(p.getUniqueId(), System.currentTimeMillis());

                    } else {

                        p.sendMessage(
                                BookUtil.TextBuilder.of(projectsPrefix + "§cCrea un proyecto para poder construir aquí. ").build(),
                                BookUtil.TextBuilder.of("§a[TUTORIAL]").onClick(BookUtil.ClickAction.runCommand("/p tutorial")).onHover(BookUtil.HoverAction.showText("Haz click para iniciar el tutorial")).build()
                        );
                        lastWarn.put(p.getUniqueId(), System.currentTimeMillis());
                    }
                }
            }
        }
    }
}

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
import pizzaaxx.bteconosur.projects.Project;
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

            if (Project.isProjectAt(targetBlock.getLocation())) {

                Project project = new Project(targetBlock.getLocation());

                if (!project.getAllMembers().stream().map(OfflinePlayer::getUniqueId).collect(Collectors.toList()).contains(p.getUniqueId())) {

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

                }

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

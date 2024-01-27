package pizzaaxx.bteconosur.player.projects;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import pizzaaxx.bteconosur.BTEConoSurPlugin;
import pizzaaxx.bteconosur.countries.Country;
import pizzaaxx.bteconosur.player.OfflineServerPlayer;
import pizzaaxx.bteconosur.player.scoreboard.ScoreboardDisplay;
import pizzaaxx.bteconosur.player.scoreboard.ScoreboardDisplayProvider;
import pizzaaxx.bteconosur.projects.ProjectType;
import pizzaaxx.bteconosur.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static pizzaaxx.bteconosur.utils.ChatUtils.*;
import static pizzaaxx.bteconosur.utils.ColorUtils.blendWithGray;

public class CountryProjectManagerScoreboard implements ScoreboardDisplay {

    private final BTEConoSurPlugin plugin;
    private final UUID uuid;
    private final Country country;
    private final CountryProjectManagerScoreboardProvider provider;

    public CountryProjectManagerScoreboard(BTEConoSurPlugin plugin, UUID uuid, Country country, CountryProjectManagerScoreboardProvider provider) {
        this.plugin = plugin;
        this.uuid = uuid;
        this.country = country;
        this.provider = provider;
    }

    @Override
    public Component getTitle() {
        return Component.text(
                StringUtils.transformToSmallCapital(
                        "Progreso en " + country.getName()
                ),
                Style.style(
                        TextColor.color(GREEN),
                        TextDecoration.BOLD
                )
        );
    }

    @Override
    public List<Component> getLines() {
        List<Component> lines = new ArrayList<>(List.of(
                Component.text("◆")
                        .append(Component.text("                                      ", Style.style(TextDecoration.STRIKETHROUGH)))
                        .append(Component.text("◆"))
                        .color(TextColor.color(DARK_GRAY))
        ));

        OfflineServerPlayer s = plugin.getPlayerRegistry().get(uuid);
        ProjectsManager manager = s.getProjectsManager();
        for (ProjectType type : country.getProjectTypes()) {
            if (manager.hasUnlocked(type)) {

                lines.add(
                        Component.text(StringUtils.transformToSmallCapital("  " + type.getDisplayName()), Style.style(TextColor.color(type.getColor().getRGB()), TextDecoration.BOLD))
                );
                lines.add(
                        Component.text("    ▪ ", Style.style(TextColor.color(DARK_GRAY)))
                                .append(Component.text(StringUtils.transformToSmallCapital("Terminados: "), TextColor.color(GRAY)))
                                .append(Component.text(Integer.toString(
                                        manager.getFinishedProjects(type)
                                ), TextColor.color(191, 242, 233)))
                );

            } else {
                lines.add(
                        Component.text(StringUtils.transformToSmallCapital("  " + type.getDisplayName()), Style.style(TextColor.color(
                                blendWithGray(type.getColor(), 0.9f).getRGB()
                        ), TextDecoration.BOLD))
                );
                for (String requirementName : type.getUnlockRequirements().keySet()) {
                    ProjectType type2 = country.getProjectType(requirementName);
                    int currentlyFinished = manager.getFinishedProjects(type2);
                    int required = type.getUnlockRequirements().get(requirementName);
                    Component value = Component.text("[");
                    int counter = 0;
                    while (counter < 12 * currentlyFinished / required) {
                        value = value.append(
                                Component.text("|", Style.style(TextDecoration.BOLD))
                        );
                        counter++;
                    }
                    while (counter < 12) {
                        value = value.append(
                                Component.text("•")
                        );
                        counter++;
                    }
                    value = value.append(Component.text("] " + currentlyFinished + "/" + required));
                    lines.add(
                            Component.text("    ▪ ", Style.style(TextColor.color(DARK_GRAY)))
                                    .append(Component.text(StringUtils.transformToSmallCapital(type2.getDisplayName() + ": "), TextColor.color(GRAY)))
                                    .append(value.color(TextColor.color(191, 242, 233)))
                    );
                }
            }

        }

        lines.add(
                Component.text("◆")
                        .append(Component.text("                                      ", Style.style(TextDecoration.STRIKETHROUGH)))
                        .append(Component.text("◆"))
                        .color(TextColor.color(DARK_GRAY))
        );

        return lines;
    }

    @Override
    public ScoreboardDisplayProvider getProvider() {
        return provider;
    }

    @Override
    public boolean isSavable() {
        return true;
    }
}

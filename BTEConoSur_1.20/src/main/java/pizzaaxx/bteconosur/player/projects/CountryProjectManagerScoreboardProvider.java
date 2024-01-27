package pizzaaxx.bteconosur.player.projects;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSurPlugin;
import pizzaaxx.bteconosur.countries.Country;
import pizzaaxx.bteconosur.player.OfflineServerPlayer;
import pizzaaxx.bteconosur.player.scoreboard.ScoreboardDisplay;
import pizzaaxx.bteconosur.player.scoreboard.ScoreboardDisplayProvider;
import pizzaaxx.bteconosur.utils.StringUtils;

import java.util.List;

import static net.kyori.adventure.text.format.NamedTextColor.RED;
import static pizzaaxx.bteconosur.utils.ChatUtils.DARK_GRAY;
import static pizzaaxx.bteconosur.utils.ChatUtils.DARK_RED;

public class CountryProjectManagerScoreboardProvider implements ScoreboardDisplayProvider {

    private final BTEConoSurPlugin plugin;

    public CountryProjectManagerScoreboardProvider(BTEConoSurPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public ScoreboardDisplay getDisplay(@NotNull Player player) {

        Country country = plugin.getCountriesRegistry().getCountryAt(player.getLocation());

        if (country == null) {
            return new ScoreboardDisplay() {
                @Override
                public Component getTitle() {
                    return Component.text(
                            StringUtils.transformToSmallCapital("Progreso"),
                            Style.style(
                                    TextColor.color(DARK_RED),
                                    TextDecoration.BOLD
                            )
                    );
                }

                @Override
                public List<Component> getLines() {
                    return List.of(
                            Component.text("◆")
                                    .append(Component.text("                                 ", Style.style(TextDecoration.STRIKETHROUGH)))
                                    .append(Component.text("◆"))
                                    .color(TextColor.color(DARK_GRAY)),

                            Component.text(StringUtils.transformToSmallCapital("  País no encontrado"), Style.style(RED, TextDecoration.BOLD)),
                            Component.text("◆")
                                    .append(Component.text("                                 ", Style.style(TextDecoration.STRIKETHROUGH)))
                                    .append(Component.text("◆"))
                                    .color(TextColor.color(DARK_GRAY))
                    );
                }

                @Override
                public ScoreboardDisplayProvider getProvider() {
                    return CountryProjectManagerScoreboardProvider.this;
                }

                @Override
                public boolean isSavable() {
                    return true;
                }
            };
        } else {
            return new CountryProjectManagerScoreboard(
                    plugin,
                    player.getUniqueId(),
                    country,
                    this
            );
        }
    }

    @Override
    public String getIdentifier() {
        return "progress";
    }
}

package pizzaaxx.bteconosur.Discord.SlashCommands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSur;
import pizzaaxx.bteconosur.Player.ServerPlayer;
import pizzaaxx.bteconosur.Projects.Project;
import pizzaaxx.bteconosur.Utils.DiscordUtils;

public class ProjectCommand extends ListenerAdapter implements SlashCommandContainer {

    private final BTEConoSur plugin;

    public ProjectCommand(BTEConoSur plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {

        if (event.getName().equals("project")) {

            OptionMapping idMapping = event.getOption("id");
            assert idMapping != null;
            String id = idMapping.getAsString();

            if (!plugin.getProjectRegistry().exists(id)) {
                DiscordUtils.respondError(event, "La ID introducida no existe.");
                return;
            }

        }

    }

    public void projectEmbed(IReplyCallback event, String id) {

        Project project = plugin.getProjectRegistry().get(id);

        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(project.getType().getColor());
        builder.setTitle("Proyecto " + project.getDisplayName());
        if (project.getOwner() != null) {
            ServerPlayer owner = plugin.getPlayerRegistry().get(project.getOwner());
            builder.setThumbnail("https://mc-heads.net/head/" + owner.getUUID());
        }

        // TIPO PAIS ETIQUETA
        // COORDENADAS
        // LIDER MIEMBROS

        builder.addField(
                ":globe_with_meridians: País",
                ":flag_" + project.getCountry().getAbbreviation() + ": " + project.getCountry().getDisplayName(),
                true
        );
        builder.addField(
                ":game_die: Tipo:",
                project.getType().getDisplayName() + " (" + project.getPoints() + ")",
                true
        );


    }

    @Override
    public CommandData getCommandData() {
        return Commands.slash(
                "project",
                "Obtén información sobre un proyecto."
        )
                .addOption(
                        OptionType.STRING,
                        "id",
                        "La ID del proyecto.",
                        true
                )
                .setNameLocalization(DiscordLocale.SPANISH, "proyecto");
    }

    @Override
    public JDA getJDA() {
        return plugin.getBot();
    }
}

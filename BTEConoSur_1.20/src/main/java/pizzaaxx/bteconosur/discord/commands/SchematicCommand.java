package pizzaaxx.bteconosur.discord.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.utils.FileUpload;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSurPlugin;
import pizzaaxx.bteconosur.discord.DiscordConnector;
import pizzaaxx.bteconosur.player.OfflineServerPlayer;
import pizzaaxx.bteconosur.player.discord.DiscordManager;
import pizzaaxx.bteconosur.player.projects.ProjectsManager;

import java.io.File;

public class SchematicCommand extends ListenerAdapter implements DiscordCommandHolder {

    private final BTEConoSurPlugin plugin;

    public SchematicCommand(BTEConoSurPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {

        if (!event.getName().equals("schematic")) return;

        // Player has to have a linked Minecraft account
        // Minecraft account has to have Builder role
        // Send schematic to player's DM
        // schematic is on WorldEdit's schematics folder

        if (!plugin.getLinkRegistry().isConnected(event.getUser().getId())) {
            DiscordConnector.respondError(event, "No tienes una cuenta de Minecraft vinculada.");
            return;
        }

        DiscordManager manager = plugin.getLinkRegistry().get(event.getUser().getId());
        OfflineServerPlayer s = manager.getPlayer();
        if (s.getProjectsManager().getBuilderRank() != ProjectsManager.BuilderRank.BUILDER) {
            DiscordConnector.respondError(event, "Debes ser constructor en el servidor para poder descargar schematics.");
            return;
        }

        OptionMapping nameMapping = event.getOption("nombre");
        assert nameMapping != null;
        String name = nameMapping.getAsString();
        File schematic = new File(plugin.getWorldEdit().getDataFolder(), "schematics/" + name + ".schematic");
        if (!schematic.exists()) {
            DiscordConnector.respondError(event, "No se encontró el schematic.");
            return;
        }

        event.getUser().openPrivateChannel().queue(
                channel -> channel.sendFiles(
                        FileUpload.fromData(schematic, name + ".schematic")
                ).queue(
                        message -> DiscordConnector.respondSuccess(event, "Schematic enviado a tus mensajes privados."),
                        error -> DiscordConnector.respondError(event, "Ha ocurrido un error enviando el schematic.")
                )
        );
    }

    @Override
    public CommandData[] getCommandData() {
        return new CommandData[] {
                Commands.slash(
                        "schematic",
                        "Obtén un schematic del servidor."
                ).addOption(
                        OptionType.STRING,
                        "nombre",
                        "Nombre del schematic",
                        true
                )
        };
    }
}

package pizzaaxx.bteconosur.Posts;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.modals.Modal;
import org.bukkit.Server;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSur;
import pizzaaxx.bteconosur.Discord.SlashCommands.SlashCommandContainer;
import pizzaaxx.bteconosur.Player.Managers.DiscordManager;
import pizzaaxx.bteconosur.Player.ServerPlayer;
import pizzaaxx.bteconosur.Projects.Project;
import pizzaaxx.bteconosur.Utils.DiscordUtils;

import java.io.IOException;
import java.sql.SQLException;

public class PostProjectCommand extends ListenerAdapter implements SlashCommandContainer {

    private final BTEConoSur plugin;

    public PostProjectCommand(BTEConoSur plugin) {
        this.plugin = plugin;
    }

    @Override
    public void checkCommand() {
        plugin.getBot().retrieveCommands().queue(
                commands -> {
                    boolean found = false;
                    for (Command command : commands) {
                        if (command.getName().equals("postproject")) {
                            found = true;
                            break;
                        }
                    }

                    if (!found){
                        plugin.getBot().upsertCommand(
                                "postproject",
                                "Publica tu proyecto en Discord."
                        ).addOption(
                                OptionType.STRING,
                                "id",
                                "ID del proyecto",
                                true
                        ).queue();
                    }

                }
        );
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {

        if (event.getName().equals("postproject")) {

            OptionMapping idMapping = event.getOption("id");
            assert idMapping != null;
            String id = idMapping.getAsString();

            if (!plugin.getProjectRegistry().exists(id)) {
                DiscordUtils.respondError(event, "El proyecto introducido no existe.");
                return;
            }

            Project project = plugin.getProjectRegistry().get(id);

            if (project.hasPost()) {
                Post post = project.getPost();
            }

            if (!plugin.getLinksRegistry().isLinked(event.getUser().getId())) {
                DiscordUtils.respondError(event, "Debes conectar tu cuenta de Minecraft para usar este comando.");
                return;
            }

            ServerPlayer s = plugin.getPlayerRegistry().get(plugin.getLinksRegistry().get(event.getUser().getId()));

            if (!project.getOwner().equals(s.getUUID())) {
                DiscordUtils.respondError(event, "Solo el líder de un proyecto puede publicarlo.");
                return;
            }

        }

    }
}

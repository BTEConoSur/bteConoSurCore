package pizzaaxx.bteconosur.discord.slashCommands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.server.player.ServerPlayer;

import java.awt.*;
import java.io.File;
import java.util.concurrent.TimeUnit;

import static pizzaaxx.bteconosur.discord.HelpMethods.errorEmbed;

public class SchematicCommand  extends ListenerAdapter {

    private final File schematicsFolder = new File(Bukkit.getPluginManager().getPlugin("WorldEdit").getDataFolder(), "schematics");

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        try {
            ServerPlayer s = new ServerPlayer(event.getUser());

            if (s.getGroupsManager().getPrimaryGroup().getPriority() >= 3) {

                String name = event.getOption("schematic").getAsString();

                File schem = new File(schematicsFolder, name);

                if (schem.exists()) {

                    event.replyEmbeds(
                            new EmbedBuilder()
                                    .setColor(Color.GREEN)
                                    .setTitle("Se ha enviado el schematic \"" + name + "\" a tus mensajes directos.")
                                    .setDescription("Si no has recibido un mensaje, asegúrate de tener la opción \"Permitir mensajes directos de miembros del servidor\" activada.")
                                    .build()
                    ).queue(
                            msg -> msg.deleteOriginal().queueAfter(5, TimeUnit.MINUTES)
                    );

                    event.getUser().openPrivateChannel().queue(
                            channel -> channel.sendFile(schem).queue()
                    );
                } else {
                    event.replyEmbeds(errorEmbed("El schematic introducido no existe.")).queue(
                            msg -> msg.deleteOriginal().queueAfter(10, TimeUnit.SECONDS)
                    );
                }

            } else {
                event.replyEmbeds(errorEmbed("Debes ser constructor para poder usar este comando.")).queue(
                        msg -> msg.deleteOriginal().queueAfter(10, TimeUnit.SECONDS)
                );
            }
        } catch (Exception e) {
            event.replyEmbeds(
                    new EmbedBuilder()
                            .setColor(Color.RED)
                            .setTitle("Tu cuenta no está conectada.")
                            .setDescription("Usa `/link` en Discord o en Minecraft para conectar tus cuentas.")
                            .build()
            ).queue(
                    msg -> msg.deleteOriginal().queueAfter(10, TimeUnit.SECONDS)
            );
        }
    }
}

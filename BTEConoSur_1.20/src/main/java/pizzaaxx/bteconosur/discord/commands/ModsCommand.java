package pizzaaxx.bteconosur.discord.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.utils.FileUpload;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSurPlugin;
import pizzaaxx.bteconosur.discord.DiscordConnector;

import java.awt.*;
import java.io.File;

public class ModsCommand extends ListenerAdapter implements DiscordCommandHolder {

    private final BTEConoSurPlugin plugin;

    public ModsCommand(BTEConoSurPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (!event.getName().equals("mods")) return;

        File mods = new File(plugin.getDataFolder(), "mods.zip");
        event.replyFiles(FileUpload.fromData(mods, "mods.zip"))
                .addEmbeds(
                        new EmbedBuilder()
                                .setColor(Color.GREEN)
                                .setTitle("Modpack")
                                .setDescription(
                                        "Estos son los mods que usamos en el servidor. Si necesitas ayuda instalándolos, ve <#942838899863081010>."
                                )
                                .build()
                )
                .addComponents(
                        ActionRow.of(
                                DiscordConnector.deleteButton(event.getUser())
                        )
                )
                .queue();
    }

    @Override
    public CommandData[] getCommandData() {
        return new CommandData[] {
                Commands.slash(
                        "mods",
                        "Obtén el modpack del servidor."
                )
        };
    }
}

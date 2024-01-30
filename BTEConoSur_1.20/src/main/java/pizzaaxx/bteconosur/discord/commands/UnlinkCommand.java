package pizzaaxx.bteconosur.discord.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSurPlugin;
import pizzaaxx.bteconosur.discord.DiscordConnector;
import pizzaaxx.bteconosur.player.OfflineServerPlayer;
import pizzaaxx.bteconosur.player.discord.DiscordManager;

import static pizzaaxx.bteconosur.BTEConoSurPlugin.PREFIX;

public class UnlinkCommand extends ListenerAdapter implements CommandExecutor, DiscordCommandHolder {

    private final BTEConoSurPlugin plugin;

    public UnlinkCommand(BTEConoSurPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cSolo jugadores pueden usar este comando.");
            return true;
        }

        OfflineServerPlayer offlineServerPlayer = plugin.getPlayerRegistry().get(player.getUniqueId());

        // warn if not linked
        if (!offlineServerPlayer.getDiscordManager().isLinked()) {
            player.sendMessage(PREFIX + "§cTu cuenta de Discord no está vinculada.");
            return true;
        }

        // unlink
        offlineServerPlayer.getDiscordManager().unlink();

        player.sendMessage(PREFIX + "Tu cuenta de Discord ha sido desvinculada.");

        return true;
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {

        if (!event.getName().equals("unlink")) return;

        // warn if not linked
        if (!plugin.getLinkRegistry().exists(event.getUser().getId())) {
            DiscordConnector.respondError(
                    event,
                    "Tu cuenta de Discord no está vinculada."
            );
            return;
        }

        // unlink
        DiscordManager manager = plugin.getLinkRegistry().get(event.getUser().getId());
        manager.unlink();

        DiscordConnector.respondSuccessEphemeral(
                event,
                "Tu cuenta de Discord ha sido desvinculada."
        );

    }

    @Override
    public CommandData[] getCommandData() {
        return new CommandData[] {
                Commands.slash(
                        "unlink",
                        "Desvincula tu cuenta de Discord de tu cuenta de Minecraft."
                )
        };
    }
}

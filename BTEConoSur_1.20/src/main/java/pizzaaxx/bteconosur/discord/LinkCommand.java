package pizzaaxx.bteconosur.discord;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSurPlugin;
import pizzaaxx.bteconosur.utils.ChatUtils;
import pizzaaxx.bteconosur.utils.StringUtils;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static pizzaaxx.bteconosur.BTEConoSurPlugin.PREFIX_C;

public class LinkCommand extends ListenerAdapter implements CommandExecutor, DiscordCommandHolder {

    private final BTEConoSurPlugin plugin;
    private final Map<String, String> codesDsToMc = new HashMap<>();
    private final Map<String, UUID> codesMcToDs = new HashMap<>();

    public LinkCommand(BTEConoSurPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cSolo jugadores pueden usar este comando.");
            return true;
        }

        if (args.length == 0) {

            // check if already linked
            if (plugin.getPlayerRegistry().get(player.getUniqueId()).getDiscordManager().isLinked()) {
                player.sendMessage(PREFIX_C.append(Component.text("Tu cuenta de Minecraft ya está vinculada.", TextColor.color(ChatUtils.RED))));
                return true;
            }

            // Generar código
            String code = StringUtils.generateCode(
                    6,
                    codesMcToDs.keySet(),
                    StringUtils.DIGITS,
                    StringUtils.LOWER_CASE
            );

            codesMcToDs.put(code, player.getUniqueId());
            Bukkit.getScheduler().runTaskLater(
                     plugin,
                    scheduledTask -> codesMcToDs.remove(code),
                    5 * 60 * 20
            );
            player.sendMessage(
                    PREFIX_C
                            .append(Component.text("Tu código es "))
                            .append(Component.text(code, TextColor.color(ChatUtils.GREEN))
                                    .clickEvent(ClickEvent.copyToClipboard(code))
                                    .hoverEvent(Component.text("Haz click para copiar el código."))
                            )
                            .append(Component.text(". "))
                            .append(Component.text("Usa "))
                            .append(Component.text("/link <codigo>", TextColor.color(ChatUtils.GREEN)))
                            .append(Component.text(" en Discord para enlazar tu cuenta."))
            );

        } else {

            String code = args[0];

            if (!codesDsToMc.containsKey(code)) {
                player.sendMessage(PREFIX_C.append(Component.text("Código inválido.", TextColor.color(ChatUtils.RED))));
                return true;
            }

            try {
                plugin.getPlayerRegistry().get(player.getUniqueId()).getDiscordManager().link(codesDsToMc.get(code));

                player.sendMessage(PREFIX_C.append(Component.text("Tu cuenta de Discord ha sido vinculada.", TextColor.color(ChatUtils.GREEN))));
            } catch (Exception e) {
                player.sendMessage(PREFIX_C.append(Component.text("Ha ocurrido un error al enlazar tu cuenta.", TextColor.color(ChatUtils.RED))));
                return true;
            }

            codesDsToMc.remove(code);

        }
        return true;
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {

        if (!event.getName().equals("link")) return;

        OptionMapping codeMapping = event.getOption("codigo");
        if (codeMapping == null) {

            // check if already linked
            if (plugin.getLinkRegistry().exists(event.getUser().getId())) {
                DiscordConnector.respondError(event, "Tu cuenta de Discord ya está vinculada.");
                return;
            }

            // Generar código
            String code = StringUtils.generateCode(
                    6,
                    codesDsToMc.keySet(),
                    StringUtils.DIGITS,
                    StringUtils.LOWER_CASE
            );

            codesDsToMc.put(code, event.getUser().getId());
            Bukkit.getScheduler().runTaskLater(
                    plugin,
                    scheduledTask -> codesDsToMc.remove(code),
                    5 * 60 * 20
            );
            // Send code to user via DM
            event.getUser().openPrivateChannel().queue(
                    channel -> channel.sendMessageEmbeds(
                            DiscordConnector.fastEmbed(
                                    new Color(0, 171, 238),
                                    "Tu código es " + code + ".",
                                    "Usa `/link " + code + "` en el servidor para enlazar tu cuenta."
                            )
                    ).queue()
            );
            // Notify code is in DM
            DiscordConnector.respondSuccessEphemeral(event, "Se ha enviado un código a tus mensajes directos.");
        } else {

            String code = codeMapping.getAsString();

            if (!codesMcToDs.containsKey(code)) {
                DiscordConnector.respondError(event, "Código inválido.");
                return;
            }

            try {
                plugin.getPlayerRegistry().get(codesMcToDs.get(code)).getDiscordManager().link(event.getUser().getId());

                DiscordConnector.respondSuccessEphemeral(event, "Tu cuenta de Minecraft ha sido vinculada.");
            } catch (Exception e) {
                DiscordConnector.respondError(event, "Ha ocurrido un error al enlazar tu cuenta.");
                return;
            }

            codesMcToDs.remove(code);

        }
    }

    @Override
    public CommandData[] getCommandData() {
        return new CommandData[] {
                Commands.slash("link", "Enlaza tu cuenta de Minecraft con Discord.")
                        .addOption(
                                OptionType.STRING,
                                "codigo",
                                "El código que te aparece en el juego.",
                                false
                        )
        };
    }
}

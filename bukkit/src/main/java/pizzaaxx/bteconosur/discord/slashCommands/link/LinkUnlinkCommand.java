package pizzaaxx.bteconosur.discord.slashCommands.link;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.methods.CodeGenerator;
import pizzaaxx.bteconosur.server.player.DiscordManager;
import pizzaaxx.bteconosur.server.player.ServerPlayer;
import pizzaaxx.bteconosur.yaml.Configuration;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static pizzaaxx.bteconosur.discord.HelpMethods.errorEmbed;
import static pizzaaxx.bteconosur.discord.slashCommands.link.LinkUnlinkMinecraftCommand.minecraftToDiscord;

public class LinkUnlinkCommand extends ListenerAdapter {

    private final Configuration links;
    private final Plugin plugin;

    public LinkUnlinkCommand(Configuration links, Plugin plugin) {
        this.links = links;
        this.plugin = plugin;
    }

    public static Map<String, String> discordToMinecraft = new HashMap<>();

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (event.getName().equals("link")) {

            if (event.getOption("código") == null) {

                String code = CodeGenerator.generateCode(6, discordToMinecraft.keySet());

                discordToMinecraft.put(code, event.getUser().getId());

                event.getUser().openPrivateChannel().queue(
                        channel -> channel.sendMessageEmbeds(
                                new EmbedBuilder()
                                        .setColor(new Color(0, 172, 238))
                                        .setTitle("Tu código es \"" + code + "\".")
                                        .setDescription("Usa `/link [código]` en Minecraft para terminar de conectar tus cuentas.")
                                        .build()
                        ).queue(
                                msg -> msg.delete().queueAfter(10, TimeUnit.MINUTES)
                        )
                );

                event.replyEmbeds(
                        new EmbedBuilder()
                                .setColor(Color.GREEN)
                                .setTitle("Se ha enviado el código a tus mensajes directos.")
                                .setDescription("Si no has recibido un mensaje, asegúrate de tener la opción \"Permitir mensajes directos de miembros del servidor\" activada.")
                                .build()
                ).queue(
                        msg -> msg.deleteOriginal().queueAfter(1, TimeUnit.MINUTES)
                );

                BukkitRunnable runnable = new BukkitRunnable() {
                    @Override
                    public void run() {
                        discordToMinecraft.put(code, null);
                    }
                };

                runnable.runTaskLaterAsynchronously(plugin, 12000);

            } else {

                String code = event.getOption("código").getAsString();

                if (code.matches("[a-z]{6}")) {
                    if (minecraftToDiscord.containsKey(code)) {
                        ServerPlayer s = new ServerPlayer(Bukkit.getOfflinePlayer(minecraftToDiscord.get(code)));
                        s.getDiscordManager().connect(event.getUser(), plugin);

                        minecraftToDiscord.remove(code);
                        event.replyEmbeds(
                                new EmbedBuilder()
                                        .setColor(Color.GREEN)
                                        .setAuthor("Se ha conectado exitosamente tu cuenta a la cuenta de Minecraft \"" + s.getName() + "\".", null, "https://cravatar.eu/helmavatar/" + s.getName() + "/190.png")
                                        .build()
                        ).queue(
                                msg -> msg.deleteOriginal().queueAfter(5, TimeUnit.MINUTES)
                        );

                    } else {
                        event.replyEmbeds(errorEmbed("El código introducido no existe.")).queue(
                                msg -> msg.deleteOriginal().queueAfter(10, TimeUnit.SECONDS)
                        );
                    }
                } else {
                    event.replyEmbeds(errorEmbed("El código introducido es inválido.")).queue(
                            msg -> msg.deleteOriginal().queueAfter(10, TimeUnit.SECONDS)
                    );
                }

            }

        } else if (event.getName().equals("unlink")) {

            String id = event.getUser().getId();

            if (DiscordManager.isLinked(id)) {
                ServerPlayer s = new ServerPlayer(Bukkit.getOfflinePlayer(UUID.fromString(links.getString(id))));
                s.getDiscordManager().disconnect(plugin);
                event.replyEmbeds(
                        new EmbedBuilder()
                                .setColor(Color.GREEN)
                                .setTitle("Tu cuenta se ha desconectado exitosamente.")
                                .build()
                ).queue(
                        msg -> msg.deleteOriginal().queueAfter(1, TimeUnit.MINUTES)
                );
            } else {
                event.replyEmbeds(errorEmbed("Tu cuenta de Discord no está conectada a ninguna cuenta de Minecraft.")).queue(
                        msg -> msg.deleteOriginal().queueAfter(10, TimeUnit.SECONDS)
                );
            }

        }
    }
}

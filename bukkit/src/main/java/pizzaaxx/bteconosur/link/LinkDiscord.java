package pizzaaxx.bteconosur.link;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.serverPlayer.DiscordManager;
import pizzaaxx.bteconosur.serverPlayer.ServerPlayer;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import static pizzaaxx.bteconosur.link.LinkMinecraft.minecraftLinks;
import static pizzaaxx.bteconosur.methods.CodeGenerator.generateCode;

public class LinkDiscord implements EventListener {

    public static Map<String, User> discordLinks = new HashMap<>();

    @Override
    public void onEvent(@NotNull GenericEvent event) {
        if (event instanceof MessageReceivedEvent) {
            MessageReceivedEvent e = (MessageReceivedEvent) event;
            if (e.getMessage().getContentRaw().startsWith("/")) {
                String fullCommand = e.getMessage().getContentRaw();
                String[] args = fullCommand.replaceFirst("/", "").split(" ");
                if (args.length > 0) {
                    if (args[0].equals("link")) {
                        if (args.length == 1) {
                            String code = generateCode(6);
                            while (discordLinks.containsKey(code)) {
                                code = generateCode(6);
                            }
                            discordLinks.put(code, e.getAuthor());

                            // EMBED
                            EmbedBuilder embed = new EmbedBuilder();
                            embed.addField("Se te ha enviado el código a tus mensajes directos.", "Si no has recibido nada, asegúrate de tener la opción *Mensajes directos de miembros del servidor* activada.", false);
                            embed.setColor(new Color(24, 109, 182));
                            e.getTextChannel().sendMessageEmbeds(embed.build()).queue();

                            EmbedBuilder userEmbed = new EmbedBuilder();
                            userEmbed.addField("Conecta tu cuenta:", "Entra al servidor de Minecraft y usa `/link " + code + "` para conectar ambas cuentas.", false);
                            userEmbed.setColor(new Color(24, 109, 182));

                            e.getAuthor().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessageEmbeds(userEmbed.build()).queue());
                        } else {
                            if (args[1].matches("[a-z]{6}")) {
                                if (minecraftLinks.containsKey(args[1])) {
                                    OfflinePlayer player = minecraftLinks.get(args[1]);

                                    DiscordManager manager = new ServerPlayer(player).getDiscordManager();
                                    manager.connect(e.getAuthor());

                                    EmbedBuilder success = new EmbedBuilder();
                                    success.setColor(new Color(0, 255, 42));
                                    success.addField("Cuenta conectada", "Has conectado exitosamente la cuenta de Discord \"" + e.getAuthor().getName() + "#" + e.getAuthor().getDiscriminator() + "\" con la cuenta de Minecraft \"" + player.getName() + "\".", false);
                                    success.addField("Notificaciones:", "Desde ahora recibirás las notificaciones del servidor por medio de Discord.", false);
                                    e.getTextChannel().sendMessageEmbeds(success.build()).queue();
                                }
                            } else {
                                EmbedBuilder error = new EmbedBuilder();
                                error.setColor(new Color(255, 0, 0));
                                error.setAuthor("Introduce un código válido.");
                                e.getTextChannel().sendMessageEmbeds(error.build()).queue();
                            }
                        }
                    }

                    if (args[0].equals("unlink")) {
                        try {
                            DiscordManager manager = new ServerPlayer(e.getAuthor()).getDiscordManager();

                            manager.disconnect();

                            EmbedBuilder success = new EmbedBuilder();
                            success.setColor(new Color(0, 255, 42));
                            success.setAuthor("Se ha desconectado exitosamente tu cuenta de Discord de tu cuenta de Minecraft.");
                            e.getTextChannel().sendMessageEmbeds(success.build()).queue();


                        } catch (Exception ex) {
                            EmbedBuilder error = new EmbedBuilder();
                            error.setColor(new Color(255, 0, 0));
                            error.setAuthor("Tu cuenta de Discord no está conectada a ninguna cuenta de Minecraft.");
                            e.getTextChannel().sendMessageEmbeds(error.build()).queue();
                        }
                    }
                }
            }
        }
    }
}

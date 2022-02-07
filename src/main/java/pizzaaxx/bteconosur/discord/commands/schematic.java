package pizzaaxx.bteconosur.discord.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.ServerPlayer;

import java.awt.*;
import java.io.File;

import static pizzaaxx.bteconosur.bteConoSur.pluginFolder;

public class schematic implements EventListener {


    @Override
    public void onEvent(@NotNull GenericEvent event) {
        if (event instanceof MessageReceivedEvent) {
            MessageReceivedEvent e = (MessageReceivedEvent) event;
            if (e.getMessage().getContentRaw().startsWith("/")) {
                String fullCommand = e.getMessage().getContentRaw();
                String[] args = fullCommand.replaceFirst("/", "").split(" ");
                if (args.length > 0) {
                    if (args[0].equals("schematic")) {
                        try {
                            ServerPlayer s = new ServerPlayer(e.getAuthor());

                            if (s.getPrimaryGroup().equals("builder") || s.getPrimaryGroup().equals("mod") || s.getPrimaryGroup().equals("admin")) {
                                if (args.length > 1) {
                                    File schematic = new File(Bukkit.getPluginManager().getPlugin("WorldEdit").getDataFolder(), "schematics/" + args[1] + ".schematic");

                                    if (schematic.isFile()) {

                                        EmbedBuilder embed = new EmbedBuilder();
                                        embed.setColor(new Color(0, 255, 42));
                                        embed.addField("Se ha enviado el archivo a tus mensajes privados.", "Si no has recibido nada, asegúrate de tener la opción \"Permitir mensajes directos de miembros del servidor\" activada.", false);
                                        e.getTextChannel().sendMessageEmbeds(embed.build()).reference(e.getMessage()).mentionRepliedUser(false).queue();

                                        e.getAuthor().openPrivateChannel().queue(privateChannel -> privateChannel.sendFile(schematic).queue());
                                    } else {
                                        EmbedBuilder embed = new EmbedBuilder();
                                        embed.setColor(new Color(255, 0, 0));
                                        embed.setAuthor("El schematic introducido no existe.");
                                        e.getTextChannel().sendMessageEmbeds(embed.build()).reference(e.getMessage()).mentionRepliedUser(false).queue();
                                    }
                                } else {
                                    EmbedBuilder embed = new EmbedBuilder();
                                    embed.setColor(new Color(255, 0, 0));
                                    embed.setAuthor("Ingresa el nombre de un schematic.");
                                    e.getTextChannel().sendMessageEmbeds(embed.build()).reference(e.getMessage()).mentionRepliedUser(false).queue();
                                }
                            } else {
                                EmbedBuilder embed = new EmbedBuilder();
                                embed.setColor(new Color(255, 0, 0));
                                embed.setAuthor("Solo jugadores con rango CONSTRUCTOR o mayor pueden usar este comando.");
                                e.getTextChannel().sendMessageEmbeds(embed.build()).reference(e.getMessage()).mentionRepliedUser(false).queue();
                            }
                        } catch (Exception exception) {
                            EmbedBuilder embed = new EmbedBuilder();
                            embed.setColor(new Color(255, 0, 0));
                            embed.setAuthor("Conecta tu cuenta de Discord con tu cuenta de Minecraft para usar esta función.");
                            e.getTextChannel().sendMessageEmbeds(embed.build()).reference(e.getMessage()).mentionRepliedUser(false).queue();
                        }
                    }
                }
            }
        }
    }
}

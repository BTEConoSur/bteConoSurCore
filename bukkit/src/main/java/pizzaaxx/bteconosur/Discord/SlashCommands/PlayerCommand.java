package pizzaaxx.bteconosur.Discord.SlashCommands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSur;
import pizzaaxx.bteconosur.Chat.Chat;
import pizzaaxx.bteconosur.Countries.Country;
import pizzaaxx.bteconosur.Player.Managers.DiscordManager;
import pizzaaxx.bteconosur.Player.ServerPlayer;
import pizzaaxx.bteconosur.Utils.DiscordUtils;

import java.awt.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static net.dv8tion.jda.api.EmbedBuilder.ZERO_WIDTH_SPACE;

public class PlayerCommand extends ListenerAdapter {

    private final BTEConoSur plugin;

    public PlayerCommand(BTEConoSur plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {

        if (event.getName().equals("player")) {

            ServerPlayer target;

            String subcommandName = event.getSubcommandName();

            if (subcommandName == null) {
                DiscordUtils.respondError(event, "Ha ocurrido un error.");
                return;
            }

            if (event.getSubcommandName().equals("name")) {

                OptionMapping nameMapping = event.getOption("nombre");

                if (nameMapping == null) {
                    DiscordUtils.respondError(event, "Ha ocurrido un error.");
                    return;
                }

                try {
                    if (plugin.getPlayerRegistry().hasPlayedBefore(nameMapping.getAsString())) {
                        target = plugin.getPlayerRegistry().get(nameMapping.getAsString());
                    } else {
                        DiscordUtils.respondError(event, "El jugador introducido jamás ha entrado al servidor.");
                        return;
                    }
                } catch (SQLException | IOException e) {
                    DiscordUtils.respondError(event, "Ha ocurrido un error en la base de datos.");
                    return;
                }

            } else if (event.getSubcommandName().equals("user")) {

                OptionMapping userMapping = event.getOption("usuario");

                if (userMapping == null) {
                    DiscordUtils.respondError(event, "Ha ocurrido un error.");
                    return;
                }

                User user = userMapping.getAsUser();

                try {
                    if (DiscordManager.isLinked(plugin, user.getId())) {
                        target = plugin.getPlayerRegistry().get(DiscordManager.getUUID(plugin, user.getId()));
                    } else {
                        DiscordUtils.respondError(event, "El usuario introducido no tiene una cuenta de Minecraft conectada.");
                        return;
                    }
                } catch (SQLException | IOException e) {
                    DiscordUtils.respondError(event, "Ha ocurrido un error en la base de datos.");
                    return;
                }

            } else {
                DiscordUtils.respondError(event, "Ha ocurrido un error.");
                return;
            }

            if (target == null) {
                DiscordUtils.respondError(event, "Ha ocurrido un error.");
                return;
            }

            // NAME

            // STATUS   CHAT    BLANK
            // RANK     ROLES   DISCORD

            //

            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle(target.getName());
            builder.setThumbnail("https://mc-heads.net/head/" + target.getUUID().toString());

            List<MessageEmbed.Field> fields = new ArrayList<>();

            ServerPlayer.BuilderRank rank = target.getBuilderRank();

            fields.add(new MessageEmbed.Field(
                    "Rango:",
                    "**" + rank.getDiscordPrefix() + "**" + StringUtils.capitalize(rank.toString()),
                    true
            ));

            List<ServerPlayer.SecondaryRoles> roles = target.getSecondaryRoles();

            if (!roles.isEmpty()) {

                List<String> roleLines = new ArrayList<>();
                for (ServerPlayer.SecondaryRoles role : roles) {
                    roleLines.add("• **" + role.getDiscordPrefix() + "**" + StringUtils.capitalize(role.toString()));
                }

                fields.add(new MessageEmbed.Field(
                        "Roles:",
                        String.join("\n", roleLines),
                        true
                ));
            } else {
                fields.add(
                        new MessageEmbed.Field(
                                ZERO_WIDTH_SPACE, ZERO_WIDTH_SPACE, true
                        )
                );
            }

            DiscordManager discordManager = target.getDiscordManager();

            if (discordManager.isLinked()) {
                fields.add(
                        new MessageEmbed.Field(
                                "Discord:",
                                "<@" + discordManager.getId() + ">",
                                true
                        )
                );
            } else {
                fields.add(
                        new MessageEmbed.Field(
                                "Discord:",
                                "No conectado",
                                true
                        )
                );
            }

            if (Bukkit.getOfflinePlayer(target.getUUID()).isOnline()) {

                Player player = Bukkit.getPlayer(target.getUUID());

                builder.setColor(Color.GREEN);
                fields.add(0, new MessageEmbed.Field(
                        "Status:",
                        "🟢 Online",
                        true
                ));

                try {
                    Chat chat = target.getChatManager().getCurrentChat();

                    fields.add(
                            1,
                            new MessageEmbed.Field(
                                    "Chat:",
                                    chat.getEmoji() + " " + chat.getDisplayName(),
                                    true
                            )
                    );

                } catch (SQLException e) {
                    DiscordUtils.respondError(event, "Ha ocurrido un error con la base de datos.");
                    return;
                }

                fields.add(
                        2,
                        new MessageEmbed.Field(
                                ZERO_WIDTH_SPACE, ZERO_WIDTH_SPACE, true
                        )
                );

                Location loc = player.getLocation();
                Country country = plugin.getCountryManager().getCountryAt(loc);

                fields.add(new MessageEmbed.Field(
                        "Coordenadas:",
                        (country == null ? ":globe_with_meridians: " : ":flag_" + country.getAbbreviation() + ": ") + loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ(),
                        false
                ));

            } else {

                builder.setColor(Color.RED);
                fields.add(0, new MessageEmbed.Field(
                        "Status:",
                        "🔴 Offline",
                        true
                ));

                fields.add(
                        1,
                        new MessageEmbed.Field(
                                ZERO_WIDTH_SPACE, ZERO_WIDTH_SPACE, true
                        )
                );

                fields.add(
                        2,
                        new MessageEmbed.Field(
                                ZERO_WIDTH_SPACE, ZERO_WIDTH_SPACE, true
                        )
                );

            }

            String[][] table = new String[4][4];
        }
    }
}

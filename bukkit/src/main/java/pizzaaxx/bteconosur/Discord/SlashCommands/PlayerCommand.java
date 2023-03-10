package pizzaaxx.bteconosur.Discord.SlashCommands;

import com.sk89q.worldguard.util.net.HttpRequest;
import de.vandermeer.asciitable.AT_Renderer;
import de.vandermeer.asciitable.AsciiTable;
import de.vandermeer.asciithemes.TA_Grid;
import de.vandermeer.asciithemes.TA_GridThemeOptions;
import de.vandermeer.asciithemes.TA_GridThemes;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.callbacks.IMessageEditCallback;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.requests.restaction.interactions.MessageEditCallbackAction;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import net.dv8tion.jda.api.utils.FileUpload;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSur;
import pizzaaxx.bteconosur.Chat.Chat;
import pizzaaxx.bteconosur.Countries.Country;
import pizzaaxx.bteconosur.Geo.Coords2D;
import pizzaaxx.bteconosur.Player.Managers.ChatManager;
import pizzaaxx.bteconosur.Player.Managers.DiscordManager;
import pizzaaxx.bteconosur.Player.Managers.ProjectManager;
import pizzaaxx.bteconosur.Player.ServerPlayer;
import pizzaaxx.bteconosur.Projects.Project;
import pizzaaxx.bteconosur.SQL.Columns.SQLColumnSet;
import pizzaaxx.bteconosur.SQL.Conditions.SQLANDConditionSet;
import pizzaaxx.bteconosur.SQL.Conditions.SQLJSONArrayCondition;
import pizzaaxx.bteconosur.SQL.Conditions.SQLORConditionSet;
import pizzaaxx.bteconosur.SQL.Conditions.SQLOperatorCondition;
import pizzaaxx.bteconosur.SQL.Ordering.SQLOrderExpression;
import pizzaaxx.bteconosur.SQL.Ordering.SQLOrderSet;
import pizzaaxx.bteconosur.Utils.DiscordUtils;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static pizzaaxx.bteconosur.SQL.Ordering.SQLOrderExpression.Order.DESC;

public class PlayerCommand extends ListenerAdapter implements SlashCommandContainer {

    private final BTEConoSur plugin;

    public PlayerCommand(BTEConoSur plugin) {
        this.plugin = plugin;
        plugin.getBot().upsertCommand(
                Commands.user("Ver información del jugador")
        ).queue();
    }

    @Override
    public void onUserContextInteraction(@NotNull UserContextInteractionEvent event) {

        if (event.getName().equals("Ver información del jugador")) {

            User user = event.getTarget();

            if (!plugin.getLinksRegistry().isLinked(user.getId())) {
                DiscordUtils.respondError(event, "El usuario introducido no tiene una cuenta de Minecraft conectada.");
                return;
            }

            ServerPlayer s  = plugin.getPlayerRegistry().get(plugin.getLinksRegistry().get(user.getId()));

            this.respondPlayerInfoEmbed(s, event);
        }

    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {

        if (event.getName().equals("player")) {

            String subcommandName = event.getSubcommandName();

            if (subcommandName == null) {
                return;
            }

            ServerPlayer s;
            if (subcommandName.equals("name")) {

                OptionMapping nameMapping = event.getOption("nombre");
                assert nameMapping != null;
                String name = nameMapping.getAsString();

                try {
                    s = plugin.getPlayerRegistry().get(name);

                    if (s == null) {
                        DiscordUtils.respondError(event, "El jugador introducido jamás ha entrado al servidor o no existe.");
                        return;
                    }
                } catch (SQLException | IOException e) {
                    DiscordUtils.respondError(event, "Ha ocurrido un error en la base de datos.");
                    return;
                }

            } else {

                OptionMapping userMapping = event.getOption("usuario");
                assert userMapping != null;
                User user = userMapping.getAsUser();

                if (!plugin.getLinksRegistry().isLinked(user.getId())) {
                    DiscordUtils.respondError(event, "El usuario introducido no tiene una cuenta de Minecraft conectada.");
                    return;
                }

                s = plugin.getPlayerRegistry().get(plugin.getLinksRegistry().get(user.getId()));

            }

            this.respondPlayerInfoEmbed(s, event);
        }
    }

    public void respondPlayerInfoEmbed(@NotNull ServerPlayer s, IReplyCallback event) {

        OfflinePlayer p = Bukkit.getOfflinePlayer(s.getUUID());

        try {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setThumbnail("https://mc-heads.net/head/" + s.getUUID());
            builder.setTitle(s.getName());

            builder.addField(
                    "Rango:",
                    s.getBuilderRank().getDiscordPrefix() + StringUtils.capitalize(s.getBuilderRank().toString().toLowerCase()),
                    true
            );

            if (!s.getSecondaryRoles().isEmpty()) {
                List<String> lines = new ArrayList<>();
                s.getSecondaryRoles().forEach(
                        role -> lines.add("• " + role.getDiscordPrefix() + role)
                );

                builder.addField(
                        "Roles:",
                        String.join("\n", lines),
                        true
                );
            } else {
                builder.addBlankField(true);
            }

            DiscordManager discordManager = s.getDiscordManager();

            if (discordManager.isLinked()) {
                builder.addField(
                        "Discord:",
                        "<@" + discordManager.getId() + ">",
                        true
                );
            } else {
                builder.addBlankField(true);
            }

            InputStream is = null;
            if (p.isOnline()) {

                Player player = Bukkit.getPlayer(p.getUniqueId());

                builder.setColor(Color.GREEN);
                builder.setDescription(":green_circle: Online");

                ChatManager chatManager = s.getChatManager();
                Chat chat = chatManager.getCurrentChat();
                builder.addField(
                        "Chat:",
                        chat.getEmoji() + " " + chat.getDisplayName(),
                        true
                );

                Location loc = player.getLocation();
                Coords2D coords = new Coords2D(plugin, loc);

                builder.addField(
                        "Coordenadas:",
                        ":round_pushpin: [" + loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ() + "](https://www.google.com/maps/@" + coords.getLat() + "," + coords.getLon() + ",19z" + ")",
                        true
                );

                URL url = new URL("https://open.mapquestapi.com/staticmap/v4/getmap?key=" + plugin.getSatMapHandler().getKey() + "&size=1280,720&type=sat&scalebar=false&imagetype=png&center=" + coords.getLat() + "," + coords.getLon() + "&zoom=18&xis=https://cravatar.eu/helmavatar/" + player.getName() + "/64.png,1,c," + coords.getLat() + "," + coords.getLon());
                is = HttpRequest.get(url).execute().getInputStream();

                builder.setImage("attachment://map.png");

            } else {

                builder.setColor(Color.RED);
                builder.setDescription(":red_circle: Offline");

                long lastDisconnected = s.getLastDisconnected() / 1000;

                builder.addField(
                        "Última vez conectado:",
                        ":timer: <t:" + lastDisconnected + ":R>",
                        false
                );

            }

            ProjectManager projectManager = s.getProjectManager();

            AsciiTable table = new AsciiTable();
            table.addRule();
            table.addRow("País", "Puntos", "Proy. terminados");
            table.addRule();
            for (Country country : plugin.getCountryManager().getAllCountries()) {
                int finishedProjects = projectManager.getFinishedProjects(country);

                if (finishedProjects > 0) {
                    table.addRow(country.getDisplayName(), projectManager.getPoints(country), finishedProjects);
                }
            }
            table.addRule();
            table.setPaddingLeft(1);

            builder.addField(
                    "Proyectos:",
                    "```\n" + String.join("\n", table.renderAsCollection(40)) + "\n```",
                    false
            );

            if (event instanceof SlashCommandInteractionEvent) {

                ReplyCallbackAction action = event.replyEmbeds(builder.build())
                        .addComponents(
                                ActionRow.of(
                                        Button.of(
                                                ButtonStyle.SUCCESS,
                                                "playerCommandViewProjects?user=" + event.getUser().getId(),
                                                "Ver proyectos",
                                                Emoji.fromUnicode("U+1F5FA")
                                        ),
                                        plugin.getDiscordHandler().getDeleteButton(event.getUser())
                                )
                        );

                if (is != null) {
                    action.setFiles(
                            FileUpload.fromData(is, "map.png")
                    );
                }

                action.queue(
                        msg -> msg.deleteOriginal().queueAfter(10, TimeUnit.MINUTES)
                );
            } else if (event instanceof IMessageEditCallback) {

                IMessageEditCallback editCallback = (IMessageEditCallback) event;

                MessageEditCallbackAction action = editCallback.editMessageEmbeds(builder.build())
                        .setComponents(
                                ActionRow.of(
                                        Button.of(
                                                ButtonStyle.SUCCESS,
                                                "playerCommandViewProjects?user=" + event.getUser().getId(),
                                                "Ver proyectos",
                                                Emoji.fromUnicode("U+1F5FA")
                                        ),
                                        plugin.getDiscordHandler().getDeleteButton(event.getUser())
                                )
                        );

                if (is != null) {
                    action.setFiles(
                            FileUpload.fromData(is, "map.png")
                    );
                }

                action.queue(
                        msg -> msg.deleteOriginal().queueAfter(10, TimeUnit.MINUTES)
                );

            }

        } catch (Exception e) {
            e.printStackTrace();
            DiscordUtils.respondError(event, "Ha ocurrido un error en la base de datos.");
        }

    }

    public void respondPlayerProjectsEmbed(ServerPlayer s, IReplyCallback event, int page) {

        try {
            ResultSet countSet = plugin.getSqlManager().select(
                    "projects",
                    new SQLColumnSet("COUNT(id) AS count"),
                    new SQLORConditionSet(
                            new SQLOperatorCondition(
                                    "owner", "=", s.getUUID()
                            ),
                            new SQLJSONArrayCondition(
                                    "members", s.getUUID()
                            )
                    )
            ).retrieve();

            countSet.next();
            int count = countSet.getInt("count");

            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle("Proyectos de " + s.getName());

            ResultSet set = plugin.getSqlManager().select(
                    "projects",
                    new SQLColumnSet("id"),
                    new SQLORConditionSet(
                            new SQLOperatorCondition(
                                    "owner", "=", s.getUUID()
                            ),
                            new SQLJSONArrayCondition(
                                    "members", s.getUUID()
                            )
                    ),
                    new SQLOrderSet(
                            new SQLOrderExpression(
                                    "(owner == null)", DESC
                            ),
                            new SQLOrderExpression(
                                    "points", DESC
                            )
                    )
            ).addText(
                    " LIMIT 25 OFFSET " + ((page - 1) * 25)
            ).retrieve();

            while (set.next()) {
                Project project = plugin.getProjectRegistry().get(set.getString("id"));

                List<String> lines = new ArrayList<>();

                if (project.hasCustomName()) {
                    lines.add("**• ID:** `" + project.getId() + "`");
                }

                lines.add("**• País:** :flag_" + project.getCountry().getAbbreviation() + ": " + project.getCountry().getDisplayName());

                lines.add(
                        "**• Tipo:** " + project.getType().getDisplayName() + " (" + project.getPoints() + " puntos)"
                );

                lines.add(
                        "**• Líder:** " + plugin.getPlayerRegistry().get(project.getOwner()).getName()
                );

                lines.add("**• Miembros:** " + project.getMembers().size());

                builder.addField(
                        "Proyecto " + project.getDisplayName(),
                        String.join("\n", lines),
                        true
                );
            }



        } catch (Exception e) {
            e.printStackTrace();
            DiscordUtils.respondError(event, "Ha ocurrido un error en la base de datos.");
        }


    }

    @Override
    public CommandData getCommandData() {
        return Commands.slash(
                "player",
                "Obtén información sobre un jugador."
        ).setNameLocalization(
                DiscordLocale.SPANISH,
                "jugador"
        ).addSubcommands(
                new SubcommandData(
                        "name",
                        "Encuentra un jugador usando su nombre de Minecraft."
                ).setNameLocalization(
                        DiscordLocale.SPANISH,
                        "nombre"
                ).addOption(
                        OptionType.STRING,
                        "nombre",
                        "El nombre del jugador.",
                        true
                ),
                new SubcommandData(
                        "user",
                        "Encuentra un jugador usando su usuario de Discord."
                ).setNameLocalization(
                        DiscordLocale.SPANISH,
                        "usuario"
                ).addOption(
                        OptionType.USER,
                        "usuario",
                        "El usuario del jugador.",
                        true
                )
        );
    }

    @Override
    public JDA getJDA() {
        return plugin.getBot();
    }
}

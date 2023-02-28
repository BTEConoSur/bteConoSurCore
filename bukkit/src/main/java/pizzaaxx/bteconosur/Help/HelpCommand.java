package pizzaaxx.bteconosur.Help;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSur;
import pizzaaxx.bteconosur.Discord.SlashCommands.SlashCommandContainer;
import pizzaaxx.bteconosur.SQL.Columns.SQLColumnSet;
import pizzaaxx.bteconosur.SQL.Conditions.SQLANDConditionSet;
import pizzaaxx.bteconosur.SQL.Conditions.SQLLikeCondition;
import pizzaaxx.bteconosur.SQL.Conditions.SQLOperatorCondition;
import pizzaaxx.bteconosur.SQL.Ordering.SQLOrderExpression;
import pizzaaxx.bteconosur.SQL.Ordering.SQLOrderSet;
import pizzaaxx.bteconosur.Utils.DiscordUtils;
import pizzaaxx.bteconosur.Utils.StringUtils;

import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class HelpCommand extends ListenerAdapter implements SlashCommandContainer, CommandExecutor {

    private final BTEConoSur plugin;

    public HelpCommand(BTEConoSur plugin) {
        this.plugin = plugin;
    }

    @Override
    public void checkCommand() {
        plugin.getBot().retrieveCommands().queue(
                commands -> {
                    boolean found = false;
                    for (Command command : commands) {
                        if (command.getName().equals("help")) {
                            found = true;
                            break;
                        }
                    }

                    if (found) {
                        plugin.getBot().upsertCommand(
                                "help",
                                "Obtén información sobre alguno de los comandos."
                        ).addSubcommands(
                                new SubcommandData(
                                        "minecraft",
                                        "Comandos que se pueden usar dentro del servidor de Minecraft."
                                ).addOption(
                                        OptionType.STRING,
                                        "comando",
                                        "El comando a buscar",
                                        false
                                ),
                                new SubcommandData(
                                        "discord",
                                        "Comandos que se pueden usar dentro del servidor de Discord."
                                ).addOption(
                                        OptionType.STRING,
                                        "comando",
                                        "El comando a buscar",
                                        false
                                )
                        ).setNameLocalization(DiscordLocale.SPANISH, "ayuda").queue();
                    }
                }
        );
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {

        if (event.getName().equals("help")) {

            OptionMapping mapping = event.getOption("comando");
            if (mapping != null) {
                String path = mapping.getAsString();
            } else {



            }

        }

    }

    public void replyHelpListEmbed(IReplyCallback event, @NotNull HelpRecord.Platform platform, int pageNumber) throws SQLException {

        ResultSet countSet = plugin.getSqlManager().select(
                "commands",
                new SQLColumnSet("COUNT(path) AS count"),
                new SQLANDConditionSet(
                        new SQLLikeCondition(
                                "path", false, "% %"
                        ),
                        new SQLOperatorCondition(
                                "platform", "=", platform.toString().toLowerCase()
                        )
                )
        ).retrieve();

        countSet.next();

        int size = countSet.getInt("count");

        ResultSet set = plugin.getSqlManager().select(
                "commands",
                new SQLColumnSet("path", "description"),
                new SQLANDConditionSet(
                        new SQLLikeCondition(
                                "path", false, "% %"
                        ),
                        new SQLOperatorCondition(
                                "platform", "=", platform.toString().toLowerCase()
                        )
                ),
                new SQLOrderSet(
                        new SQLOrderExpression(
                                "path",
                                SQLOrderExpression.Order.ASC
                        )
                )
        ).addText("LIMIT 10 OFFSET " + ((pageNumber - 1) * 10)).retrieve();

        Map<String, String> descriptions = new HashMap<>();
        Map<Character, List<String>> fields = new HashMap<>();
        while (set.next()) {

            String path = set.getString("path");
            List<String> list = fields.getOrDefault(path.toLowerCase().charAt(0), new ArrayList<>());
            list.add(path);
            fields.put(path.toLowerCase().charAt(0), list);

            descriptions.put(path, set.getString("description"));

        }

        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Color.GREEN);
        builder.setTitle("Ayuda de comandos de " + platform.toString().toLowerCase());
        builder.setDescription("Usa `/help [comando]` para obtener más información de cada comando.");

        for (Character character : fields.keySet()) {

            String emoji;
            switch (character.toString().toLowerCase()) {
                case "0": {
                    emoji = ":zero:";break;
                }
                case "1": {
                    emoji = ":one:";break;
                }
                case "2": {
                    emoji = ":two:";break;
                }
                case "3": {
                    emoji = ":three:";break;
                }
                case "4": {
                    emoji = ":four:";break;
                }
                case "5": {
                    emoji = ":five:";break;
                }
                case "6": {
                    emoji = ":six:";break;
                }
                case "7": {
                    emoji = ":seven:";break;
                }
                case "8": {
                    emoji = ":eight:";break;
                }
                case "9": {
                    emoji = ":nine:";break;
                }
                default: {
                    emoji = ":regional_indicator_" + character.toString().toLowerCase() + ":";
                }
            }

            List<String> lines = new ArrayList<>();
            for (String path : fields.get(character)) {
                lines.add("• `" + path + "`: " + descriptions.get(path));
            }

            builder.addField(
                    emoji,
                    String.join("\n", lines),
                    false
            );

        }

        String nextID = "helpListNext?platform=" + platform.toString().toLowerCase() + "?currentPage=" + pageNumber;
        String previousID = "helpListPrevious?platform=" + platform.toString().toLowerCase() + "?currentPage=" + pageNumber;
        if (event instanceof SlashCommandInteractionEvent) {
            event.replyEmbeds(
                            builder.build()
                    )
                    .addComponents(
                            ActionRow.of(
                                    Button.of(
                                            ButtonStyle.PRIMARY,
                                            previousID,
                                            "Pág. anterior",
                                            Emoji.fromUnicode("U+2B05")
                                    ).withDisabled(pageNumber <= 1),
                                    Button.of(
                                            ButtonStyle.SECONDARY,
                                            "counter",
                                            pageNumber + "/" + (Math.floorDiv(size, 10) + 1)
                                    ),
                                    Button.of(
                                            ButtonStyle.PRIMARY,
                                            nextID,
                                            "Pág. siguiente",
                                            Emoji.fromUnicode("U+27A1")
                                    ).withDisabled(pageNumber >= (Math.floorDiv(size, 10) + 1))
                            )
                    )
                    .queue(
                            interaction -> {
                                interaction.deleteOriginal().queueAfter(10, TimeUnit.MINUTES, i -> plugin.messagesToDelete.remove(interaction));
                                plugin.messagesToDelete.add(interaction);
                            }
                    );
        } else if (event instanceof ButtonInteractionEvent) {
            ButtonInteractionEvent buttonEvent = (ButtonInteractionEvent) event;
            buttonEvent.editMessageEmbeds(
                    builder.build()
            ).setComponents(
                    ActionRow.of(
                            Button.of(
                                    ButtonStyle.PRIMARY,
                                    previousID,
                                    "Pág. anterior",
                                    Emoji.fromUnicode("U+2B05")
                            ).withDisabled(pageNumber <= 1),
                            Button.of(
                                    ButtonStyle.SECONDARY,
                                    "counter",
                                    pageNumber + "/" + (Math.floorDiv(size, 10) + 1)
                            ),
                            Button.of(
                                    ButtonStyle.PRIMARY,
                                    nextID,
                                    "Pág. siguiente",
                                    Emoji.fromUnicode("U+27A1")
                            ).withDisabled(pageNumber >= (Math.floorDiv(size, 10) + 1))
                    )
            ).queue(
                    interaction -> {
                        interaction.deleteOriginal().queueAfter(10, TimeUnit.MINUTES, i -> plugin.messagesToDelete.remove(interaction));
                        plugin.messagesToDelete.add(interaction);
                    }
            );
        }
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        assert event.getButton().getId() != null;
        if (event.getButton().getId().startsWith("helpListNext") || event.getButton().getId().startsWith("helpListPrevious")) {
            assert event.getMessage().getInteraction() != null;
            if (!event.getMessage().getInteraction().getUser().getId().equals(event.getUser().getId())) {
                DiscordUtils.respondError(event, "Solo quien usó este comando puede usar los botones.");
                return;
            }

            Map<String, String> query = StringUtils.getQuery(event.getButton().getId().split("\\?")[1]);
            HelpRecord.Platform platform = HelpRecord.Platform.valueOf(query.get("platform").toUpperCase());
            int currentPage = Integer.parseInt(query.get("currentPage"));

            int targetPage;
            if (event.getButton().getId().startsWith("helpListNext")) {
                targetPage = currentPage + 1;
            } else {
                targetPage = currentPage - 1;
            }

            try {
                this.replyHelpListEmbed(event, platform, targetPage);
            } catch (SQLException e) {
                DiscordUtils.respondError(event, "Ha ocurrido un error en la base de datos.");
            }
        }
    }

    public void replyHelpEmbed(IReplyCallback event, String path, HelpRecord.Platform platform) {



    }


    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {



        return true;
    }


}

package pizzaaxx.bteconosur.discord;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pizzaaxx.bteconosur.BTEConoSurPlugin;
import pizzaaxx.bteconosur.countries.Country;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class DiscordConnector {

    private final BTEConoSurPlugin plugin;
    public static JDA BOT;
    private final List<Object> listeners = new ArrayList<>();

    public DiscordConnector(BTEConoSurPlugin plugin) {
        this.plugin = plugin;
    }

    public void startBot(String token) throws InterruptedException {
        JDABuilder builder = JDABuilder.createDefault(token);
        builder.setStatus(OnlineStatus.ONLINE);
        builder.setActivity(Activity.playing("bteconosur.com"));
        builder.addEventListeners(listeners.toArray());
        BOT = builder.build().awaitReady();

        for (Country country : plugin.getCountriesRegistry().getCountries()) {
            country.getChat().sendMessageEmbeds(
                    new EmbedBuilder()
                            .setColor(Color.GREEN)
                            .setTitle("¡El servidor está online!")
                            .setDescription(":link: IP: bteconosur.com")
                            .build()
            ).queue();
        }
    }

    public void stopBot() {

        for (Country country : plugin.getCountriesRegistry().getCountries()) {
            country.getChat().sendMessageEmbeds(
                    new EmbedBuilder()
                            .setColor(Color.RED)
                            .setTitle("El servidor se ha apagado.")
                            .setDescription("Te esperamos cuando vuelva a estar disponible.")
                            .build()
            ).queue();
        }

        BOT.shutdown();
    }

    public void registerListeners(Object... listeners) {
        this.listeners.addAll(Arrays.asList(listeners));
    }

    public static void respondError(@NotNull IReplyCallback event, String error) {
        event.replyEmbeds(
                new EmbedBuilder()
                        .setColor(Color.RED)
                        .setTitle(error)
                        .build()
        ).setEphemeral(true).queue();
    }

    public static void respondErrorNonEphemeral(@NotNull IReplyCallback event, String error) {
        event.replyEmbeds(
                new EmbedBuilder()
                        .setColor(Color.RED)
                        .setTitle(error)
                        .build()
        ).queue(
                msg -> msg.deleteOriginal().queueAfter(20, TimeUnit.SECONDS)
        );
    }

    public static void respondSuccess(@NotNull IReplyCallback event, String success) {
        event.replyEmbeds(
                new EmbedBuilder()
                        .setColor(Color.GREEN)
                        .setTitle(success)
                        .build()
        ).queue();
    }

    public static void respondSuccessEphemeral(@NotNull IReplyCallback event, String success) {
        event.replyEmbeds(
                new EmbedBuilder()
                        .setColor(Color.GREEN)
                        .setTitle(success)
                        .build()
        ).setEphemeral(true).queue();
    }

    public static void respondSuccess(@NotNull IReplyCallback event, String success, int deleteAfter) {
        event.replyEmbeds(
                new EmbedBuilder()
                        .setColor(Color.GREEN)
                        .setTitle(success)
                        .build()
        ).queue(
                msg -> msg.deleteOriginal().queueAfter(deleteAfter, TimeUnit.SECONDS)
        );
    }

    @NotNull
    public static MessageEmbed fastEmbed(Color color, String title) {
        return fastEmbed(color, title, null);
    }

    @NotNull
    public static MessageEmbed fastEmbed(Color color, String title, @Nullable String description) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(color);
        builder.setTitle(title);
        if (description != null) {
            builder.setDescription(description);
        }
        return builder.build();
    }

    public static boolean compareCommands(@NotNull Command command, @NotNull CommandData data) {

        if (command.isGuildOnly() != data.isGuildOnly()) {
            return false;
        }

        if (command.isNSFW() != data.isNSFW()) {
            return false;
        }

        if (command.getType() != data.getType()) {
            return false;
        }

        if (!command.getName().equals(data.getName())) {
            return false;
        }

        if (command.getType() == Command.Type.SLASH) {

            SlashCommandData slashData = (SlashCommandData) data;

            if (!command.getDescription().equals(slashData.getDescription())) {
                return false;
            }

            boolean equalsGroups = true;
            boolean equalsSubcommands = true;
            boolean equalsOptions = true;

            for (Command.SubcommandGroup group1 : command.getSubcommandGroups()) {
                boolean found = false;
                for (SubcommandGroupData group2Data : slashData.getSubcommandGroups()) {
                    Command.SubcommandGroup group2 = new Command.SubcommandGroup(command, group2Data.toData());

                    if (group1.equals(group2)) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    equalsGroups = false;
                    break;
                }
            }

            for (Command.Subcommand subcommand1 : command.getSubcommands()) {
                boolean found = false;
                for (SubcommandData subcommand2Data : slashData.getSubcommands()) {
                    Command.Subcommand subcommand2 = new Command.Subcommand(command, subcommand2Data.toData());

                    if (subcommand1.equals(subcommand2)) {
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    equalsSubcommands = false;
                    break;
                }
            }

            for (Command.Option option1 : command.getOptions()) {

                boolean found = false;
                for (OptionData option2Data : slashData.getOptions()) {
                    Command.Option option2 = new Command.Option(option2Data.toData());

                    if (option1.equals(option2)) {
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    equalsOptions = false;
                    break;
                }

            }



            return equalsGroups && equalsSubcommands && equalsOptions;

        }

        return true;

    }

}

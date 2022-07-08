package pizzaaxx.bteconosur;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class UpdateSlashCommands implements CommandExecutor {

    private final JDA bot;

    public UpdateSlashCommands(JDA bot) {
        this.bot = bot;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender.hasPermission("bteConoSur.updateSlashCommands")) {

            bot.upsertCommand("online", "Obtén un recuento de los jugadores en el servidor").queue();
            bot.upsertCommand("where", "Obtén la posición de los juagdores en el Cono Sur y los países en los que se encuentran").queue();
            bot.upsertCommand("schematic", "Descarga un schematic desde el servidor. Requiere ser constructor").addOption(OptionType.STRING, "schematic", "Nombre del schematic", true).queue();
            bot.upsertCommand("help", "Obtén ayuda con los comandos de Discord o Minecraft").addSubcommands(
                    new SubcommandData("minecraft", "Obtén ayuda con los comandos de Minecraft").addOption(OptionType.STRING, "comando", "(Opcional) El comando con el que necesitas ayuda (puede incluir subcomandos)", false),
                    new SubcommandData("discord", "Obtén ayuda con los comandos de Discord").addOption(OptionType.STRING, "comando", "(Opcional) El comando con el que necesitas ayuda (puede incluir subcomandos)", false)
            ).queue();
            bot.upsertCommand("player", "Obtén información sobre un jugador (online u offline)").addSubcommands(
                    new SubcommandData("user", "Obtén información sobre un jugador usando su usuario de Discord").addOption(OptionType.USER, "usuario", "El usuario que se quiere buscar", true),
                    new SubcommandData("player", "Obtén información sobre un jugador usando su nombre de Minecraft").addOption(OptionType.STRING, "nombre", "Nombre de usuario del jugador", true)
            ).queue();
            bot.upsertCommand("project", "Obtén información sobre un proyecto de cualquier país").addOption(OptionType.STRING, "id", "Id del proyecto", true).queue();
            bot.upsertCommand("mods", "Obtén un archivo con los mods del servidor").queue();
            bot.upsertCommand("scoreboard", "Obtén los 10 jugadores con mayor puntaje de construcción").addSubcommands(
                    new SubcommandData("bolivia", "Scoreboard de Bolivia"),
                    new SubcommandData("chile", "Scoreboard de Chile"),
                    new SubcommandData("paraguay", "Scoreboard de Paraguay"),
                    new SubcommandData("perú", "Scoreboard de Perú"),
                    new SubcommandData("uruguay", "Scoreboard de Uruguay"),
                    new SubcommandData("global", "Scoreboard global")
            ).queue();
            bot.upsertCommand("link", "Conecta tu cuenta de Discord con tu cuenta de Minecraft").addOption(OptionType.STRING, "código", "Código obtenido en Minecraft", false).queue();
            bot.upsertCommand("unlink", "Desconecta tu cuenta de Discord de tu cuenta de Minecraft").queue();
            bot.upsertCommand("findcolor", "Encuentra bloques con texturas cercanas a un color introducido").addSubcommands(
                    new SubcommandData("code", "Introduce un código hexadecimal para buscar").addOption(OptionType.STRING, "hex", "El código hexadecimal de color deseado", true),
                    new SubcommandData("image", "Adjunta una imagen para buscar el color promedio de esta").addOption(OptionType.ATTACHMENT, "imagen", "La imagen a buscar", true)
            ).queue();
            bot.upsertCommand("projecttag", "Administra la etiqueta de un proyecto").addOption(OptionType.STRING, "id", "ID del proyecto a manejar", true).queue();
            bot.upsertCommand("evento", "Obtén información sobre un evento").addSubcommands(
                new SubcommandData("argentina", "Evento de Argentina"),
                new SubcommandData("bolivia", "Evento de Bolivia"),
                new SubcommandData("chile", "Evento de Chile"),
                new SubcommandData("paraguay", "Evento de Paraguay"),
                new SubcommandData("perú", "Evento de Perú"),
                new SubcommandData("uruguay", "Evento de Uruguay"),
                new SubcommandData("global", "Evento global")
            ).queue();
            bot.upsertCommand("altura", "¿Cómo obtengo la altura en Google Earth?").queue();
            bot.upsertCommand("prioridad", "¿Cómo funciona la prioridad de entrada al servidor?").queue();
            bot.upsertCommand("premium", "¿Es el servidor premium?").queue();
            bot.upsertCommand("bedrock", "Información sobre el soporte de Bedrock.").queue();
            bot.upsertCommand("ipfix", "Obtén la IP alternativa.").queue();
            bot.upsertCommand("ip", "Obtén la IP del servidor.").queue();
            bot.upsertCommand(Commands.user("Perfil de Minecraft")).queue();
            bot.upsertCommand("pattern", "Obtén una previsualización de un patrón de bloques de WorldEdit.").addOption(OptionType.STRING, "patrón", "El patrón a usar.", true).queue();
        }

        return true;
    }
}

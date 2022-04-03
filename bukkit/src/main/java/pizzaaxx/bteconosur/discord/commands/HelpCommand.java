package pizzaaxx.bteconosur.discord.commands;

import com.google.common.collect.Lists;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.ButtonStyle;


import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.yaml.Configuration;
import xyz.upperlevel.spigot.book.BookUtil;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static pizzaaxx.bteconosur.misc.Misc.getSimplePrefix;

public class HelpCommand implements EventListener, CommandExecutor {

    private final Plugin plugin;

    public HelpCommand(Plugin plugin) {
        this.plugin = plugin;
    }

    public String helpPrefix = getSimplePrefix("help", "6");

    @Override
    public void onEvent(@NotNull GenericEvent event) {
        if (event instanceof MessageReceivedEvent) {
            MessageReceivedEvent e = (MessageReceivedEvent) event;
            if (e.getMessage().getContentRaw().startsWith("/")) {
                String fullCommand = e.getMessage().getContentRaw();
                String[] args = fullCommand.replaceFirst("/", "").split(" ");
                if (args.length > 0) {
                    if (args[0].equals("help")) {
                        if (args.length == 1) {
                            EmbedBuilder choose = new EmbedBuilder();
                            choose.setColor(new Color(0,255,42));
                            choose.setTitle("Elige qué comandos quieres ver.");
                            ActionRow actionRow = ActionRow.of(
                                    Button.of(ButtonStyle.SECONDARY, "minecraft", "Minecraft", Emoji.fromMarkdown("<:abcdefg:941030570048258048>")),
                                    Button.of(ButtonStyle.SECONDARY, "discord", "Discord", Emoji.fromMarkdown("<:qwertyu:853810678774759434>"))
                            );

                            e.getTextChannel().sendMessageEmbeds(choose.build()).setActionRows(actionRow).reference(e.getMessage()).mentionRepliedUser(false).queue();
                        } else {
                            Configuration yaml = new Configuration(plugin, "help");
                            String platform = null;
                            if (yaml.getConfigurationSection("minecraft").contains(args[1])) {
                                platform = "minecraft";
                            } else if (yaml.getConfigurationSection("discord").contains(args[1])) {
                                platform = "discord";
                            }
                            String path = platform + "." + String.join(".subcommands.", Arrays.asList(args).subList(1, args.length));
                            Map<String, Object> command = new HashMap<>();
                            ConfigurationSection commandSection = yaml.getConfigurationSection(path);
                            for (String key : commandSection.getKeys(false)) {
                                command.put(key, commandSection.get(key));
                            }

                            if (platform != null && command != null) {
                                EmbedBuilder help = new EmbedBuilder();
                                help.setColor(new Color(0, 255, 42));
                                help.setTitle((String) command.get("usage"));

                                help.addField(":open_file_folder: Descripción:", (String) command.get("description"), false);
                                help.addField(":label: Tiene(n) permisos:", (String) command.get("permission"), false);

                                if (command.containsKey("aliases")) {
                                    List<String> aliases = new ArrayList<>();
                                    for (String alias : (List<String>) command.get("aliases")) {
                                        aliases.add("• `" + alias + "`");
                                    }
                                    help.addField(":paperclip: Aliases:", String.join("\n", aliases), false);
                                }

                                if (command.containsKey("note")) {
                                    help.addField(":notepad_spiral: Nota:", (String) command.get("note"), false);
                                }

                                if (command.containsKey("examples")) {
                                    List<String> examples = new ArrayList<>();
                                    for (String alias : (List<String>) command.get("examples")) {
                                        examples.add("• `" + alias + "`");
                                    }
                                    help.addField(":placard: Ejemplos:", String.join("\n", examples), false);
                                }

                                if (command.containsKey("parameters")) {
                                    Map<String, String> parameters = (Map<String, String>) command.get("parameters");
                                    List<String> params = new ArrayList<>();
                                    for (Map.Entry<String, String> parameter : parameters.entrySet()) {
                                        params.add("• **" + parameter.getKey() + ":** " + parameter.getValue());
                                    }
                                    help.addField(":ledger: Parámetros:", String.join("\n", params), false);
                                }

                                if (command.containsKey("subcommands")) {
                                    Map<String, Object> subs = (Map<String, Object>) command.get("subcommands");
                                    List<String> subcommands = new ArrayList<>();
                                    for (Map.Entry<String, Object> subcommand : subs.entrySet()) {
                                        subcommands.add("• `" + subcommand.getKey() + "`");
                                    }
                                    help.addField(":notebook_with_decorative_cover: Subcomandos:", String.join("\n", subcommands), false);
                                }

                                if (command.containsKey("image")) {
                                    help.setImage((String) command.get("image"));
                                }

                                e.getTextChannel().sendMessageEmbeds(help.build()).reference(e.getMessage()).mentionRepliedUser(false).queue(msg -> msg.delete().queueAfter(10, TimeUnit.MINUTES));
                                e.getMessage().delete().queueAfter(10, TimeUnit.MINUTES);

                            } else {
                                EmbedBuilder error = new EmbedBuilder();
                                error.setColor(new Color(255, 0,0));
                                error.setTitle("El comando/subcomando introducido no existe.");
                                e.getTextChannel().sendMessageEmbeds(error.build()).reference(e.getMessage()).mentionRepliedUser(false).queue(msg -> msg.delete().queueAfter(1, TimeUnit.MINUTES));
                                e.getMessage().delete().queueAfter(1, TimeUnit.MINUTES);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;

            Configuration data = new Configuration(Bukkit.getPluginManager().getPlugin("bteConoSur"), "help");
            ConfigurationSection minecraft = data.getConfigurationSection("minecraft");
            if (args.length > 0) {
                String path =  String.join(".subcommands.", Arrays.asList(args));
                if (minecraft.contains(path)) {
                    p.closeInventory();
                    ConfigurationSection cmd = minecraft.getConfigurationSection(path);
                    p.sendMessage(">+------------+[-< §6AYUDA §f>-]+------------+<");

                    p.sendMessage("§a§lUso: §f" + cmd.getString("usage"));
                    p.sendMessage("§a§lDescripición: §f" + cmd.getString("description"));
                    p.sendMessage("§a§lTiene(n) permisos: §f" + cmd.getString("permission"));
                    if (cmd.contains("aliases")) {
                        p.sendMessage("§a§lAliases: §f");
                        cmd.getList("aliases").forEach(alias -> p.sendMessage("- " + alias));
                    }
                    if (cmd.contains("note")) {
                        p.sendMessage("§a§lNota: §f" + cmd.getString("note"));
                    }
                    if (cmd.contains("examples")) {
                        p.sendMessage("§a§lEjemplos: §f");
                        cmd.getList("examples").forEach(example -> p.sendMessage("- " + example));
                    }
                    if (cmd.contains("parameters")) {
                        p.sendMessage("§a§lParámetros: §f");
                        ConfigurationSection parameters = cmd.getConfigurationSection("parameters");
                        for (String key : parameters.getKeys(false)) {
                            p.sendMessage("- §a" + key + ": §f" + parameters.getString(key));
                        }
                    }

                    if (cmd.contains("subcommands")) {
                        p.sendMessage("§a§lSubcomandos: §f");
                        ConfigurationSection subcommands = cmd.getConfigurationSection("subcommands");
                        for (String key : subcommands.getKeys(false)) {
                            p.sendMessage(BookUtil.TextBuilder.of("§f- ").build(), BookUtil.TextBuilder.of("§a" + key)
                                    .onHover(BookUtil.HoverAction.showText("Haz click para obtener más información."))
                                    .onClick(BookUtil.ClickAction.runCommand("/help " + String.join(" ", Arrays.asList(args)) + " " + key))
                                    .build());
                        }
                    }

                    p.sendMessage(">+------------+[-< ===== >-]+------------+<");
                } else {
                    p.sendMessage(helpPrefix + "El comando/subcomando introducido no existe.");
                }
            } else {
                BookUtil.BookBuilder builder = BookUtil.writtenBook();

                List<String> keys = new ArrayList<>(minecraft.getKeys(false));
                Collections.sort(keys);
                List<List<String>> sublists =  Lists.partition(keys, 12);

                int i = 1;
                int j = 1;
                int total = sublists.size();

                List<BaseComponent[]> pages = new ArrayList<>();

                for (List<String> list : sublists) {
                    BookUtil.PageBuilder page = new BookUtil.PageBuilder();
                    page.add("----[ COMANDOS ]----");
                    page.newLine();
                    page.add("§7------[  " + j + "/" + total + "  ]------§r");
                    page.newLine();
                    for (String cmd : list) {
                        page.add(BookUtil.TextBuilder.of("§7" + i + ". ").build(), BookUtil.TextBuilder.of("§r/" + cmd)
                                .onClick(BookUtil.ClickAction.runCommand("/help " + cmd))
                                .onHover(BookUtil.HoverAction.showText("§fHaz click para obtener más información."))
                                .build());

                        i++;
                        page.newLine();
                    }
                    j++;
                    pages.add(page.build());
                }

                builder.pages(pages);

                BookUtil.openPlayer(p, builder.build());
            }
        }
        return true;
    }
}

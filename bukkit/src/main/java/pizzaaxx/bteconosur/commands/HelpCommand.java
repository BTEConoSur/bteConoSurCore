package pizzaaxx.bteconosur.commands;

import com.google.common.collect.Lists;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import pizzaaxx.bteconosur.configuration.Configuration;
import xyz.upperlevel.spigot.book.BookUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static pizzaaxx.bteconosur.misc.Misc.getSimplePrefix;

public class HelpCommand implements CommandExecutor {

    private final Configuration data;

    public HelpCommand(Configuration configuration) {
        this.data = configuration;
    }

    public String helpPrefix = getSimplePrefix("help", "6");

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;

            ConfigurationSection minecraft = data.getConfigurationSection("minecraft");
            if (args.length > 0) {
                String path =  String.join(".subcommands.", Arrays.asList(args));
                if (minecraft.contains(path)) {
                    p.closeInventory();
                    ConfigurationSection cmd = minecraft.getConfigurationSection(path);
                    p.sendMessage(">+------------+[-< §6AYUDA §f>-]+------------+<");

                    p.sendMessage("§a§lUso: §f" + cmd.getString("usage"));
                    p.sendMessage("§a§lDescripción: §f" + cmd.getString("description"));
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

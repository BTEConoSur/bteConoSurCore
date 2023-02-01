package pizzaaxx.bteconosur.Chat.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSur;
import pizzaaxx.bteconosur.Chat.Chat;
import pizzaaxx.bteconosur.Chat.Prefixable;
import pizzaaxx.bteconosur.Player.Managers.ChatManager;
import pizzaaxx.bteconosur.Player.ServerPlayer;

import java.sql.SQLException;

public class ChatCommand implements CommandExecutor, Prefixable {

    private final BTEConoSur plugin;

    public ChatCommand(BTEConoSur plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, @NotNull String[] args) {

        if (!(sender instanceof Player)) {
            return true;
        }

        Player p = (Player) sender;
        ServerPlayer s = plugin.getPlayerRegistry().get(p.getUniqueId());
        ChatManager chatManager = s.getChatManager();

        if (args.length < 1) {
            try {
                Chat chat = chatManager.getCurrentChat();
                p.sendMessage(getPrefix() + "Tu chat actual es §a" + chat.getDisplayName() + "§f.");
            } catch (SQLException e) {
                p.sendMessage(getPrefix() + "Ha ocurrido un error en la base de datos.");
                return true;
            }
        } else {
            Player target = plugin.getOnlinePlayer(args[0]);
            if (target != null) {
                ChatManager targetChatManager = plugin.getPlayerRegistry().get(target.getUniqueId()).getChatManager();
                try {
                    Chat chat = targetChatManager.getCurrentChat();
                    p.sendMessage(getPrefix() + "El chat actual de §a" + target.getName() + "§f es §a" + chat.getDisplayName() + "§f.");
                } catch (SQLException e) {
                    p.sendMessage(getPrefix() + "Ha ocurrido un error en la base de datos.");
                    return true;
                }
            }
        }
        return true;
    }

    @Override
    public String getPrefix() {
        return "§f[§aCHAT§f] §7>> §f";
    }
}

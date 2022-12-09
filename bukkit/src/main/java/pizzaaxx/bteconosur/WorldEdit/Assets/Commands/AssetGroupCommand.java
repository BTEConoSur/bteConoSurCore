package pizzaaxx.bteconosur.WorldEdit.Assets.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import pizzaaxx.bteconosur.BTEConoSur;

public class AssetGroupCommand implements CommandExecutor {

    private final BTEConoSur plugin;

    public AssetGroupCommand(BTEConoSur plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {



        return true;
    }
}

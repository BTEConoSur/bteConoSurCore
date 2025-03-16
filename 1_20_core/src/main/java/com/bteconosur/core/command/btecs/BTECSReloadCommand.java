package com.bteconosur.core.command.btecs;

import com.bteconosur.core.command.BaseCommand;
import com.bteconosur.core.config.ConfigHandler;
import org.bukkit.command.CommandSender;

public class BTECSReloadCommand extends BaseCommand {
    public BTECSReloadCommand() {
        super("reload", CommandMode.CONSOLE_ONLY);
    }

    @Override
    protected boolean onCommand(CommandSender sender, String[] args) {
        ConfigHandler configHandler = ConfigHandler.getInstance();
        configHandler.reload();
        sender.sendMessage(configHandler.getLang().getString("config-reloaded", "Config reloaded."));
        return true;
    }
}

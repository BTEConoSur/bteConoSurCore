package com.bteconosur.core.command.btecs;

import com.bteconosur.core.command.BaseCommand;
import org.bukkit.command.CommandSender;

public class BTECSCommand extends BaseCommand {
    public BTECSCommand() {
        super("btecs");
        this.addSubcommand(new BTECSReloadCommand());
    }

    @Override
    protected boolean onCommand(CommandSender sender, String[] args) {
        sender.sendMessage("Command not implemented yet.");
        return true;
    }
}

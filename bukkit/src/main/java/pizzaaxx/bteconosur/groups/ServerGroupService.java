package pizzaaxx.bteconosur.groups;

import org.bukkit.command.CommandSender;
import pizzaaxx.bteconosur.server.player.NewServerPlayer;

public interface ServerGroupService {

    void updatePrimaryGroup(CommandSender sender, NewServerPlayer newServerPlayer,
                            ServerGroup serverGroup);

    void addSecondaryGroup(CommandSender sender, NewServerPlayer newServerPlayer,
                           ServerGroup serverGroup);

    void deleteSecondaryGroup(CommandSender sender, NewServerPlayer newServerPlayer,
                              ServerGroup serverGroup);

    ServerGroup getGroup(int identifier);

    ServerGroup getPrimaryGroup(int identifier);

    ServerGroup getSecondaryGroup(int identifier);

    void registerGroup(ServerGroup serverGroup);

}

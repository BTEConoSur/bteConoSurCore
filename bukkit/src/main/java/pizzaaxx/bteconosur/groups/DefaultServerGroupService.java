package pizzaaxx.bteconosur.groups;

import org.bukkit.command.CommandSender;
import pizzaaxx.bteconosur.server.player.NewServerPlayer;

import java.util.HashMap;
import java.util.Map;

public class DefaultServerGroupService implements ServerGroupService {

    private final Map<Integer, ServerGroup> primaryGroups = new HashMap<>();
    private final Map<Integer, ServerGroup> secondsGroups = new HashMap<>();

    @Override
    public void updatePrimaryGroup(CommandSender sender, NewServerPlayer newServerPlayer, ServerGroup serverGroup) {
        newServerPlayer.setPrimaryGroup(serverGroup.getIdentifier());

        if (sender != null) {
            sender.sendMessage("Has actualizado el grupo");
        }

    }

    @Override
    public void addSecondaryGroup(CommandSender sender, NewServerPlayer newServerPlayer, ServerGroup serverGroup) {
        newServerPlayer.addSecondaryGroup(serverGroup.getIdentifier());

        if (sender != null) {
            sender.sendMessage("Has actualizado el grupo");
        }

    }

    @Override
    public void deleteSecondaryGroup(CommandSender sender, NewServerPlayer newServerPlayer, ServerGroup serverGroup) {

    }

    @Override
    public ServerGroup getGroup(int identifier) {
        ServerGroup serverGroup = getPrimaryGroup(identifier);

        if (serverGroup != null) {
            return serverGroup;
        }

        return getSecondaryGroup(identifier);
    }

    @Override
    public ServerGroup getPrimaryGroup(int identifier) {
        return primaryGroups.get(identifier);
    }

    @Override
    public ServerGroup getSecondaryGroup(int identifier) {
        return secondsGroups.get(identifier);
    }

    @Override
    public void registerGroup(ServerGroup serverGroup) {
        if (serverGroup.isPrimary()) {
            primaryGroups.put(serverGroup.getIdentifier(), serverGroup);
            return;
        }
        secondsGroups.put(serverGroup.getIdentifier(), serverGroup);
    }

}

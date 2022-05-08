package pizzaaxx.bteconosur.groups;

import pizzaaxx.bteconosur.server.player.Identifiable;

import java.awt.Color;

public class ServerGroup implements Identifiable {

    private final String name;
    private final int identifier;
    private final Color color;
    private final boolean isPrimary;

    public ServerGroup(String name, int identifier, Color color, boolean isPrimary) {
        this.name = name;
        this.identifier = identifier;
        this.color = color;
        this.isPrimary = isPrimary;
    }

    public String getName() {
        return name;
    }

    @Override
    public String getId() {
        return Integer.toString(identifier);
    }

    public Color getColor() {
        return color;
    }

    public boolean isPrimary() {
        return isPrimary;
    }

}

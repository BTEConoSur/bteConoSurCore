package pizzaaxx.bteconosur.serverPlayer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GroupsManager {

    public enum PrimaryGroup {
        DEFAULT(1), POSTULANTE(2), BUILDER(3), MOD(4), ADMIN(5);

        private final int priority;

        PrimaryGroup(Integer priority) {
            this.priority = priority;
        }

        public int getPriority() {
            return priority;
        }

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    public enum SecondaryGroup {
        EVENTO, DONADOR, STREAMER;

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    private final DataManager data;
    private final ServerPlayer serverPlayer;
    private PrimaryGroup primaryGroup;
    private final Set<SecondaryGroup> secondaryGroups = new HashSet<>();

    public GroupsManager(ServerPlayer s) {
        serverPlayer = s;
        data = s.getDataManager();
        primaryGroup = PrimaryGroup.valueOf(data.getString("primaryGroup").toUpperCase());
        if (data.contains("secondaryGroups")) {
            for (String group : (List<String>) data.getList("secondaryGroups")) {
                secondaryGroups.add(SecondaryGroup.valueOf(group));
            }
        }
    }

}

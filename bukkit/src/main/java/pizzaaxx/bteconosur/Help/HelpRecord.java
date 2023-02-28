package pizzaaxx.bteconosur.Help;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSur;
import pizzaaxx.bteconosur.SQL.Columns.SQLColumnSet;
import pizzaaxx.bteconosur.SQL.Conditions.SQLANDConditionSet;
import pizzaaxx.bteconosur.SQL.Conditions.SQLOperatorCondition;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HelpRecord {

    public enum Platform {
        MINECRAFT, DISCORD;
    }

    @NotNull
    @Contract("_, _, _ -> new")
    public static HelpRecord from(@NotNull BTEConoSur plugin, String path, @NotNull Platform platform) throws IllegalArgumentException, SQLException, JsonProcessingException {

        ResultSet set = plugin.getSqlManager().select(
                "commands",
                new SQLColumnSet("*"),
                new SQLANDConditionSet(
                        new SQLOperatorCondition(
                                "platform", "=", platform.toString().toLowerCase()
                        ),
                        new SQLOperatorCondition(
                                "path", "=", path
                        )
                )
        ).retrieve();

        if (set.next()) {
            return new HelpRecord(plugin, set);
        } else {
            throw new IllegalArgumentException();
        }

    }

    private final String path;

    private final String description;
    private final String commandUsage;
    private final String permission;
    private final List<String> aliases;
    private final List<String> examples;
    private final String note;
    private final Map<String, String> parameters;
    private final List<String> subcommands;
    private HelpRecord(BTEConoSur plugin, @NotNull ResultSet set) throws SQLException, JsonProcessingException {

        path = set.getString("path");
        description = set.getString("description");
        commandUsage = set.getString("command_usage");
        permission = set.getString("permission");

        String aliasesString = set.getString("aliases");
        aliases = (aliasesString != null ? plugin.getJSONMapper().readValue(aliasesString, ArrayList.class) : null);

        String examplesString = set.getString("examples");
        examples = (examplesString != null ? plugin.getJSONMapper().readValue(examplesString, ArrayList.class) : null);

        note = set.getString("note");

        String parametersString = set.getString("parameters");
        if (parametersString != null) {
            Map<String, Object> rawParametersMap = plugin.getJSONMapper().readValue(parametersString, HashMap.class);
            parameters = new HashMap<>();
            for (String key : rawParametersMap.keySet()) {
                parameters.put(key, rawParametersMap.get(key).toString());
            }
        } else {
            parameters = null;
        }

        String subcommandsString = set.getString("subcommands");
        subcommands = (subcommandsString != null ? plugin.getJSONMapper().readValue(subcommandsString, ArrayList.class) : null);

    }

    public String getPath() {
        return path;
    }

    public String getDescription() {
        return description;
    }

    public String getCommandUsage() {
        return commandUsage;
    }

    public String getPermission() {
        return permission;
    }

    public List<String> getAliases() {
        return aliases;
    }

    public List<String> getExamples() {
        return examples;
    }

    public String getNote() {
        return note;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public List<String> getSubcommands() {
        return subcommands;
    }
}

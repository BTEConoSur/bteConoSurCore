package pizzaaxx.bteconosur.country.ProjectTypes.Points;

import org.bukkit.configuration.ConfigurationSection;

public interface ProjectPointType {

    public static ProjectPointType getInstance(ConfigurationSection section) throws ProjectPointsTypeParseException {
        if (section.contains("type")) {
            switch (section.getString("type")) {
                case "static":
                    if (section.contains("amount")) {
                        return new StaticPoints(section.getInt("amount"));
                    }
                    throw new ProjectPointsTypeParseException("Missing \"amount\" field on project type points configuration.");
                case "options":
                    if (section.contains("options")) {
                        return new OptionPoints(section.getIntegerList("options"));
                    }
                    throw new ProjectPointsTypeParseException("Missing \"options\" field on project type points configuration.");
                case "range":
                    if (section.contains("min") && section.contains("max")) {
                        return new RangePoints(section.getInt("min"), section.getInt("max"));
                    }
                    throw new ProjectPointsTypeParseException("Missing \"min\" or \"max\" field on project type points configuration.");
                default:
                    throw new ProjectPointsTypeParseException("Unknown points type on project type points configuration.");
            }
        }
        throw new ProjectPointsTypeParseException("Missing \"type\" field on project type points configuration.");
    }

    boolean isValid(int amount);

}

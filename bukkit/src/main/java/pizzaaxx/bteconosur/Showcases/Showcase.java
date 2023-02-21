package pizzaaxx.bteconosur.Showcases;

public class Showcase {

    private final String messageID;
    private final String projectIDString;

    public Showcase(String messageID, String projectIDString) {
        this.messageID = messageID;
        this.projectIDString = projectIDString;
    }

    public String getMessageID() {
        return messageID;
    }

    public String getProjectIDString() {
        return projectIDString;
    }
}

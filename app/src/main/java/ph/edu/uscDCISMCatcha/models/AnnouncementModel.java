package ph.edu.uscDCISMCatcha.models;

public class AnnouncementModel {
    private String title;
    private String message;
    private boolean sendPush;

    public AnnouncementModel(String title, String message, boolean sendPush) {
        this.title = title;
        this.message = message;
        this.sendPush = sendPush;
    }

    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public boolean isSendPush() { return sendPush; }
}
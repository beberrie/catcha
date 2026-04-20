package ph.edu.uscDCISMCatcha.models;

public class AnnouncementModel {
    private String title;
    private String message;
    private boolean sendPush;
    private long createdAt;

    public AnnouncementModel() {}

    public AnnouncementModel(String title, String message, boolean sendPush) {
        this.title = title;
        this.message = message;
        this.sendPush = sendPush;
        this.createdAt = System.currentTimeMillis();
    }

    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public boolean isSendPush() { return sendPush; }
    public long getCreatedAt() { return createdAt; }
}
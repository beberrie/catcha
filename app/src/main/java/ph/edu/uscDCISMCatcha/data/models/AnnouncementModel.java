package ph.edu.uscDCISMCatcha.data.models;

import com.google.firebase.Timestamp;

public class AnnouncementModel {
    private String announcementId;
    private String title;
    private String content;
    private String authorUid;
    private Timestamp timestamp;

    public AnnouncementModel() {}

    // Getters
    public String getAnnouncementId() { return announcementId; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getAuthorUid() { return authorUid; }
    public Timestamp getTimestamp() { return timestamp; }

    // Setters
    public void setAnnouncementId(String announcementId) { this.announcementId = announcementId; }
    public void setTitle(String title) { this.title = title; }
    public void setContent(String content) { this.content = content; }
    public void setAuthorUid(String authorUid) { this.authorUid = authorUid; }
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }
}
package ph.edu.uscDCISMCatcha.models;

import com.google.firebase.Timestamp;

public class AnnouncementModel {
    private String announcementId;
    private String title;
    private String content;
    private String authorUid;
    private String orgName;
    private String imageUrl;
    private Timestamp timestamp;

    public AnnouncementModel() {}

    public AnnouncementModel(String title, String content, String authorUid) {
        this.title     = title;
        this.content   = content;
        this.authorUid = authorUid;
        this.timestamp = Timestamp.now();
    }

    // Getters
    public String getAnnouncementId() { return announcementId; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getAuthorUid() { return authorUid; }
    public String getOrgName() { return orgName; }
    public String getImageUrl() { return imageUrl; }
    public Timestamp getTimestamp() { return timestamp; }

    // Setters
    public void setAnnouncementId(String announcementId) { this.announcementId = announcementId; }
    public void setTitle(String title) { this.title = title; }
    public void setContent(String content) { this.content = content; }
    public void setAuthorUid(String authorUid) { this.authorUid = authorUid; }
    public void setOrgName(String orgName) { this.orgName = orgName; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }
}
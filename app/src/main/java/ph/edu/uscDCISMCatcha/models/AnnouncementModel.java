package ph.edu.uscDCISMCatcha.data.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.ServerTimestamp;

public class AnnouncementModel {
    private String id;
    private String orgId;
    private String orgName;
    private String title;
    private String content;
    private String authorName;
    private String authorUid;

    @ServerTimestamp
    private Timestamp timestamp;

    // 1. No-arg constructor for Firestore
    public AnnouncementModel() {}

    // 2. Full constructor for Repository
    public AnnouncementModel(String id, String orgId, String orgName, String title, String content, String authorName, String authorUid) {
        this.id = id;
        this.orgId = orgId;
        this.orgName = orgName;
        this.title = title;
        this.content = content;
        this.authorName = authorName;
        this.authorUid = authorUid;
    }

    // 3. ADDED: 3-arg constructor for ViewModel dummy data
    public AnnouncementModel(String title, String content, String authorUid) {
        this.title = title;
        this.content = content;
        this.authorUid = authorUid;
    }

    // --- ID Methods (Aliased for compatibility) ---
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public void setAnnouncementId(String id) { this.id = id; }
    public String getAnnouncementId() { return id; }

    // --- Standard Getters and Setters ---
    public String getOrgId() { return orgId; }
    public void setOrgId(String orgId) { this.orgId = orgId; }
    public String getOrgName() { return orgName; }
    public void setOrgName(String orgName) { this.orgName = orgName; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }
    public String getAuthorUid() { return authorUid; }
    public void setAuthorUid(String authorUid) { this.authorUid = authorUid; }
    public Timestamp getTimestamp() { return timestamp; }
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }
}
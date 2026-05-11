package ph.edu.uscDCISMCatcha.data.models;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.io.Serializable;
import java.util.Date;

@IgnoreExtraProperties
public class AnnouncementModel implements Serializable {
    private String announcementId;
    private String title;
    private String content;
    private String authorUid;
    private String orgName;
    private String imageUrl;
    @ServerTimestamp
    private Date timestamp;

    public AnnouncementModel() {}

    public AnnouncementModel(String title, String content, String authorUid) {
        this.title = title;
        this.content = content;
        this.authorUid = authorUid;
    }

    @Exclude
    public String getAnnouncementId() { return announcementId; }
    public void setAnnouncementId(String announcementId) { this.announcementId = announcementId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getAuthorUid() { return authorUid; }
    public void setAuthorUid(String authorUid) { this.authorUid = authorUid; }
    public String getOrgName() { return orgName; }
    public void setOrgName(String orgName) { this.orgName = orgName; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
}

package ph.edu.uscDCISMCatcha.data.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

@IgnoreExtraProperties
public class AnnouncementModel {
    private String title;
    private String content;
    private String authorUid;
    @ServerTimestamp
    private Timestamp timestamp;

    public AnnouncementModel() {}

    public AnnouncementModel(String title, String content, String authorUid) {
        this.title = title;
        this.content = content;
        this.authorUid = authorUid;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getAuthorUid() { return authorUid; }
    public void setAuthorUid(String authorUid) { this.authorUid = authorUid; }
    public Timestamp getTimestamp() { return timestamp; }
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }
}

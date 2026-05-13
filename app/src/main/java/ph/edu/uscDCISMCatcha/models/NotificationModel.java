package ph.edu.uscDCISMCatcha.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;
import java.text.SimpleDateFormat;
import java.util.Locale;
import ph.edu.uscDCISMCatcha.data.models.EventModel;

@IgnoreExtraProperties
public class NotificationModel {
    public enum Type { EVENT, ANNOUNCEMENT }
    public enum Urgency { NORMAL, HOURS_24, HOUR_1 }

    private String id;
    private Type type;
    private Urgency urgency;
    private boolean isRead;
    @ServerTimestamp
    private Timestamp createdAt;

    private String title;
    private String content;
    private String authorUid;
    private String orgId;
    private String orgName;
    private String location;
    private Timestamp startDateTime;
    private Timestamp endDateTime;
    private String university;
    private String imageUrl;
    private String createdBy;
    private String postedByName;
    private String postedByPosition;
    private String sentTime;
    private int followersCount;
    private int eventGoing;

    public NotificationModel() {}

    public static NotificationModel fromAnnouncement(String id, AnnouncementModel a, String postedByName, String postedByPosition, int followersCount) {
        NotificationModel n = new NotificationModel();
        n.id = id;
        n.type = Type.ANNOUNCEMENT;
        n.urgency = Urgency.NORMAL;
        n.isRead = false;
        n.title = a.getTitle();
        n.content = a.getContent();
        n.authorUid = a.getAuthorUid();
        n.createdAt = a.getTimestamp();
        n.postedByName = postedByName;
        n.postedByPosition = postedByPosition;
        n.followersCount = followersCount;
        return n;
    }

    public static NotificationModel fromEvent(String id, EventModel e, int eventGoing) {
        NotificationModel n = new NotificationModel();
        n.id = id;
        n.type = Type.EVENT;
        n.isRead = false;
        n.title = e.getTitle();
        n.content = e.getDescription();
        n.orgId = e.getOrgId();
        n.orgName = e.getOrgName();
        n.location = e.getLocation();
        if (e.getStartDateTime() != null) n.startDateTime = new Timestamp(e.getStartDateTime());
        if (e.getEndDateTime() != null) n.endDateTime = new Timestamp(e.getEndDateTime());
        if (e.getCreatedAt() != null) n.createdAt = new Timestamp(e.getCreatedAt());
        n.university = e.getUniversity();
        n.imageUrl = e.getImageUrl();
        n.createdBy = e.getCreatedBy();
        n.eventGoing = eventGoing;
        n.computeUrgency();
        return n;
    }

    public void computeUrgency() {
        if (type != Type.EVENT || startDateTime == null) {
            urgency = Urgency.NORMAL; return;
        }
        long diff = startDateTime.toDate().getTime() - System.currentTimeMillis();
        long hours = diff / (1000 * 60 * 60);
        if (hours <= 1) { urgency = Urgency.HOUR_1; return; }
        if (hours <= 24) { urgency = Urgency.HOURS_24; return; }
        urgency = Urgency.NORMAL;
    }

    public String getTimeAgo() {
        if (createdAt == null) return "Just Now";
        long diff = System.currentTimeMillis() - createdAt.toDate().getTime();
        long s = diff / 1000;
        if (s < 60) return "Just Now";
        long m = s / 60;
        if (m < 60) return m + " min ago";
        long h = m / 60;
        if (h < 24) return h + (h == 1 ? " hr ago" : " hrs ago");
        return new SimpleDateFormat("MMM d", Locale.getDefault()).format(createdAt.toDate());
    }

    public String getEventDateFormatted() {
        if (startDateTime == null) return "";
        return new SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(startDateTime.toDate());
    }

    public String getEventTimeFormatted() {
        if (startDateTime == null) return "";
        return new SimpleDateFormat("h:mm a", Locale.getDefault()).format(startDateTime.toDate());
    }

    public String getEventEndTimeFormatted() {
        if (endDateTime == null) return "";
        return new SimpleDateFormat("h:mm a", Locale.getDefault()).format(endDateTime.toDate());
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public Type getType() { return type; }
    public void setType(Type type) { this.type = type; }
    public Urgency getUrgency() { return urgency; }
    public void setUrgency(Urgency urgency) { this.urgency = urgency; }
    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getAuthorUid() { return authorUid; }
    public void setAuthorUid(String authorUid) { this.authorUid = authorUid; }
    public String getOrgId() { return orgId; }
    public void setOrgId(String orgId) { this.orgId = orgId; }
    public String getOrgName() { return orgName; }
    public void setOrgName(String orgName) { this.orgName = orgName; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public Timestamp getStartDateTime() { return startDateTime; }
    public void setStartDateTime(Timestamp startDateTime) { this.startDateTime = startDateTime; }
    public Timestamp getEndDateTime() { return endDateTime; }
    public void setEndDateTime(Timestamp endDateTime) { this.endDateTime = endDateTime; }
    public String getUniversity() { return university; }
    public void setUniversity(String university) { this.university = university; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public String getPostedByName() { return postedByName; }
    public void setPostedByName(String postedByName) { this.postedByName = postedByName; }
    public String getPostedByPosition() { return postedByPosition; }
    public void setPostedByPosition(String postedByPosition) { this.postedByPosition = postedByPosition; }
    public String getSentTime() { return sentTime; }
    public void setSentTime(String sentTime) { this.sentTime = sentTime; }
    public int getFollowersCount() { return followersCount; }
    public void setFollowersCount(int followersCount) { this.followersCount = followersCount; }
    public int getEventGoing() { return eventGoing; }
    public void setEventGoing(int eventGoing) { this.eventGoing = eventGoing; }
    
    public String getNotificationId() { return id; }
}

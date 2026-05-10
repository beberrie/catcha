package ph.edu.uscDCISMCatcha.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

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

    // From AnnouncementModel
    private String title;
    private String content;
    private String authorUid;

    // From EventModel
    private String orgId;
    private String orgName;
    private String location;
    private Timestamp startDateTime;
    private Timestamp endDateTime;
    private String university;
    private String imageUrl;
    private String createdBy;

    // Display fields
    private String postedByName;
    private String postedByPosition;
    private String sentTime;
    private int followersCount;
    private int eventGoing;

    public NotificationModel() {}

    public static NotificationModel fromAnnouncement(
            String id,
            AnnouncementModel a,
            String postedByName,
            String postedByPosition,
            int followersCount) {
        NotificationModel n = new NotificationModel();
        n.id               = id;
        n.type             = Type.ANNOUNCEMENT;
        n.urgency          = Urgency.NORMAL;
        n.isRead           = false;
        n.title            = a.getTitle();
        n.content          = a.getContent();
        n.authorUid        = a.getAuthorUid();
        n.createdAt        = a.getTimestamp();
        n.postedByName     = postedByName;
        n.postedByPosition = postedByPosition;
        n.followersCount   = followersCount;
        return n;
    }

    public static NotificationModel fromEvent(
            String id, EventModel e, int eventGoing) {
        NotificationModel n = new NotificationModel();
        n.id            = id;
        n.type          = Type.EVENT;
        n.isRead        = false;
        n.title         = e.getTitle();
        n.content       = e.getDescription();
        n.orgId         = e.getOrgId();
        n.orgName       = e.getOrgName();
        n.location      = e.getLocation();
        n.startDateTime = e.getStartDateTime();
        n.endDateTime   = e.getEndDateTime();
        n.university    = e.getUniversity();
        n.imageUrl      = e.getImageUrl();
        n.createdBy     = e.getCreatedBy();
        n.createdAt     = e.getCreatedAt();
        n.eventGoing    = eventGoing;
        n.computeUrgency();
        return n;
    }

    public void computeUrgency() {
        if (type != Type.EVENT || startDateTime == null) {
            urgency = Urgency.NORMAL; return;
        }
        long diff = startDateTime.toDate().getTime()
                - System.currentTimeMillis();
        long hours = diff / (1000 * 60 * 60);
        if (hours <= 1)  { urgency = Urgency.HOUR_1;   return; }
        if (hours <= 24) { urgency = Urgency.HOURS_24; return; }
        urgency = Urgency.NORMAL;
    }

    public String getTimeAgo() {
        if (createdAt == null) return "Just Now";
        long diff = System.currentTimeMillis()
                - createdAt.toDate().getTime();
        long s = diff / 1000;
        if (s < 60) return "Just Now";
        long m = s / 60;
        if (m < 60) return m + " min ago";
        long h = m / 60;
        if (h < 24) return h + (h == 1 ? " hour ago" : " hours ago");
        long d = h / 24;
        if (d == 1) return "Yesterday";
        if (d < 7)  return d + " days ago";
        return new java.text.SimpleDateFormat("MMM d",
                java.util.Locale.getDefault())
                .format(createdAt.toDate());
    }

    public String getEventDateFormatted() {
        if (startDateTime == null) return "";
        return new java.text.SimpleDateFormat("MMM d, yyyy",
                java.util.Locale.getDefault())
                .format(startDateTime.toDate());
    }

    public String getEventTimeFormatted() {
        if (startDateTime == null) return "";
        return new java.text.SimpleDateFormat("h:mm a",
                java.util.Locale.getDefault())
                .format(startDateTime.toDate());
    }

    public String getEventEndTimeFormatted() {
        if (endDateTime == null) return "";
        return new java.text.SimpleDateFormat("h:mm a",
                java.util.Locale.getDefault())
                .format(endDateTime.toDate());
    }

    // Getters
    public String getId() { return id; }
    public Type getType() { return type; }
    public Urgency getUrgency() { return urgency; }
    public boolean isRead() { return isRead; }
    public Timestamp getCreatedAt() { return createdAt; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getAuthorUid() { return authorUid; }
    public String getOrgId() { return orgId; }
    public String getOrgName() { return orgName; }
    public String getLocation() { return location; }
    public Timestamp getStartDateTime() { return startDateTime; }
    public Timestamp getEndDateTime() { return endDateTime; }
    public String getUniversity() { return university; }
    public String getImageUrl() { return imageUrl; }
    public String getCreatedBy() { return createdBy; }
    public String getPostedByName() { return postedByName; }
    public String getPostedByPosition() { return postedByPosition; }
    public String getSentTime() { return sentTime; }
    public int getFollowersCount() { return followersCount; }
    public int getEventGoing() { return eventGoing; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setType(Type type) { this.type = type; }
    public void setUrgency(Urgency urgency) { this.urgency = urgency; }
    public void setRead(boolean read) { isRead = read; }
    public void setCreatedAt(Timestamp t) { this.createdAt = t; }
    public void setTitle(String title) { this.title = title; }
    public void setContent(String content) { this.content = content; }
    public void setAuthorUid(String uid) { this.authorUid = uid; }
    public void setOrgId(String orgId) { this.orgId = orgId; }
    public void setOrgName(String orgName) { this.orgName = orgName; }
    public void setLocation(String location) { this.location = location; }
    public void setStartDateTime(Timestamp t) { this.startDateTime = t; }
    public void setEndDateTime(Timestamp t) { this.endDateTime = t; }
    public void setUniversity(String university) { this.university = university; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public void setPostedByName(String name) { this.postedByName = name; }
    public void setPostedByPosition(String pos) { this.postedByPosition = pos; }
    public void setSentTime(String sentTime) { this.sentTime = sentTime; }
    public void setFollowersCount(int count) { this.followersCount = count; }
    public void setEventGoing(int going) { this.eventGoing = going; }
}
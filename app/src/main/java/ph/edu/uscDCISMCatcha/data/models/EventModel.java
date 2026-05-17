package ph.edu.uscDCISMCatcha.data.models;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@IgnoreExtraProperties
public class EventModel implements Serializable {

    @DocumentId
    private String id;
    private String eventId;
    private String orgId;
    private String orgName;
    private String title;
    private String description;
    private String location;
    private Timestamp startDateTime;
    private Timestamp endDateTime;

    private String university;
    private String imageUrl;
    private String createdBy;
    private String registrationUrl;
    private int maxCapacity = 0;
    private int currentRsvpCount = 0;
    private int interestedCount = 0;
    private int goingCount = 0;


    private String category;
    private List<String> categories = new ArrayList<>();

    private String status;

    @ServerTimestamp
    private Timestamp createdAt;

    public EventModel() {}


    public EventModel(String orgId, String orgName, String title, String description, String location,
                      Timestamp startDateTime, Timestamp endDateTime, String university, String imageUrl, String createdBy) {
        this.orgId = orgId;
        this.orgName = orgName;
        this.title = title;
        this.description = description;
        this.location = location;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.university = university;
        this.imageUrl = imageUrl;
        this.createdBy = createdBy;
    }

    public EventModel(String orgId, String orgName, String title,
                      String description, String location,
                      String category,
                      Timestamp startDateTime,
                      Timestamp endDateTime,
                      String university, String imageUrl,
                      String createdBy) {
        this.orgId         = orgId;
        this.orgName       = orgName;
        this.title         = title;
        this.description   = description;
        this.location      = location;
        this.category      = category;
        this.startDateTime = startDateTime;
        this.endDateTime   = endDateTime;
        this.university    = university;
        this.imageUrl      = imageUrl;
        this.createdBy     = createdBy;
        this.status        = "Upcoming";
        this.goingCount    = 0;
    }

    @Exclude
    public String getDateTimeFormatted() {
        if (startDateTime == null) return "";
        java.text.SimpleDateFormat sdf =
                new java.text.SimpleDateFormat(
                        "MMM d · h:mm a",
                        java.util.Locale.getDefault());
        return sdf.format(startDateTime.toDate());
    }

    @Exclude
    public String getTimeAgo() {
        if (createdAt == null) return "Just now";
        long diff = System.currentTimeMillis()
                - createdAt.toDate().getTime();
        long minutes = diff / (1000 * 60);
        if (minutes < 1)  return "Just now";
        if (minutes < 60) return minutes + "m";
        long hours = minutes / 60;
        if (hours < 24)   return hours + "h";
        long days = hours / 24;
        return days + "d";
    }


    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    @Exclude
    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public String getOrgId() { return orgId; }
    public void setOrgId(String orgId) { this.orgId = orgId; }

    public String getOrgName() { return orgName; }
    public void setOrgName(String orgName) { this.orgName = orgName; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

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

    public String getRegistrationUrl() { return registrationUrl; }
    public void setRegistrationUrl(String registrationUrl) { this.registrationUrl = registrationUrl; }

    public int getMaxCapacity() { return maxCapacity; }
    public void setMaxCapacity(int maxCapacity) { this.maxCapacity = maxCapacity; }

    public int getCurrentRsvpCount() { return currentRsvpCount; }
    public void setCurrentRsvpCount(int currentRsvpCount) { this.currentRsvpCount = currentRsvpCount; }

    public int getInterestedCount() { return interestedCount; }
    public void setInterestedCount(int interestedCount) { this.interestedCount = interestedCount; }

    public int getGoingCount() { return goingCount; }
    public void setGoingCount(int goingCount) { this.goingCount = goingCount; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public List<String> getCategories() { return categories; }
    public void setCategories(List<String> categories) { this.categories = categories; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
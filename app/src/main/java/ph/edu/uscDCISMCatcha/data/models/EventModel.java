package ph.edu.uscDCISMCatcha.data.models;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@IgnoreExtraProperties
public class EventModel implements Serializable {
    private String eventId; // Added for easier document reference
    private String orgId;
    private String orgName;
    private String title;
    private String description;
    private String location;
    private Date startDateTime;
    private Date endDateTime;
    private String university;
    private String imageUrl;
    private String createdBy;
    private String registrationUrl;
    private int maxCapacity = 0; // 0 means unlimited if not specified
    private int currentRsvpCount = 0; // Specifically for "Going"
    private int interestedCount = 0;
    private List<String> categories = new ArrayList<>();
    
    @ServerTimestamp
    private Date createdAt;

    public EventModel() {}

    public EventModel(String orgId, String orgName, String title, String description, String location, 
                      Date startDateTime, Date endDateTime, String university, String imageUrl, String createdBy) {
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

    // Getters and Setters
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
    public Date getStartDateTime() { return startDateTime; }
    public void setStartDateTime(Date startDateTime) { this.startDateTime = startDateTime; }
    public Date getEndDateTime() { return endDateTime; }
    public void setEndDateTime(Date endDateTime) { this.endDateTime = endDateTime; }
    public String getUniversity() { return university; }
    public void setUniversity(String university) { this.university = university; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public String getRegistrationUrl() { return registrationUrl; }
    public void setRegistrationUrl(String registrationUrl) { this.registrationUrl = registrationUrl; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public int getMaxCapacity() { return maxCapacity; }
    public void setMaxCapacity(int maxCapacity) { this.maxCapacity = maxCapacity; }
    public int getCurrentRsvpCount() { return currentRsvpCount; }
    public void setCurrentRsvpCount(int currentRsvpCount) { this.currentRsvpCount = currentRsvpCount; }
    public int getInterestedCount() { return interestedCount; }
    public void setInterestedCount(int interestedCount) { this.interestedCount = interestedCount; }

    public List<String> getCategories() { return categories; }
    public void setCategories(List<String> categories) { this.categories = categories; }
}

package ph.edu.uscDCISMCatcha.data.models;

import com.google.firebase.Timestamp;

public class EventModel {
    private String id;
    private String title;
    private String location;
    private String description;
    private String imageUrl;
    private String status;
    private String university;

    // Fields required by NotificationModel & Repository
    private String orgId;
    private String orgName;
    private String createdBy;
    private Timestamp startDateTime;
    private Timestamp endDateTime;
    private Timestamp createdAt;
    private long maxCapacity;
    private long currentRsvpCount;
    private long interestedCount;

    public EventModel() {}

    // Getters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getLocation() { return location; }
    public String getDescription() { return description; }
    public String getImageUrl() { return imageUrl; }
    public String getStatus() { return status; }
    public String getUniversity() { return university; }
    public String getOrgId() { return orgId; }
    public String getOrgName() { return orgName; }
    public String getCreatedBy() { return createdBy; }
    public Timestamp getStartDateTime() { return startDateTime; }
    public Timestamp getEndDateTime() { return endDateTime; }
    public Timestamp getCreatedAt() { return createdAt; }
    public long getMaxCapacity() { return maxCapacity; }
    public long getCurrentRsvpCount() { return currentRsvpCount; }
    public long getInterestedCount() { return interestedCount; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setLocation(String location) { this.location = location; }
    public void setDescription(String description) { this.description = description; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setStatus(String status) { this.status = status; }
    public void setUniversity(String university) { this.university = university; }
    public void setOrgId(String orgId) { this.orgId = orgId; }
    public void setOrgName(String orgName) { this.orgName = orgName; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public void setStartDateTime(Timestamp startDateTime) { this.startDateTime = startDateTime; }
    public void setEndDateTime(Timestamp endDateTime) { this.endDateTime = endDateTime; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    public void setMaxCapacity(long maxCapacity) { this.maxCapacity = maxCapacity; }
    public void setCurrentRsvpCount(long currentRsvpCount) { this.currentRsvpCount = currentRsvpCount; }
    public void setInterestedCount(long interestedCount) { this.interestedCount = interestedCount; }
}
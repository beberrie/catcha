package ph.edu.uscDCISMCatcha.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

@IgnoreExtraProperties
public class EventModel {
    private String eventId;
    private String orgId;
    private String title;
    private String description;
    private String location;
    private String date;
    private String startTime;
    private String endTime;
    private String university;
    private String department;
    private int capacityLimit;
    private int currentRsvpCount;
    private String imageUrl;
    @ServerTimestamp
    private Timestamp createdAt;

    public EventModel() {}

    public EventModel(String eventId, String orgId, String title, String description, String location, 
                      String date, String startTime, String endTime, String university, 
                      String department, int capacityLimit, String imageUrl) {
        this.eventId = eventId;
        this.orgId = orgId;
        this.title = title;
        this.description = description;
        this.location = location;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.university = university;
        this.department = department;
        this.capacityLimit = capacityLimit;
        this.currentRsvpCount = 0;
        this.imageUrl = imageUrl;
    }

    // Getters and Setters
    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }
    public String getOrgId() { return orgId; }
    public void setOrgId(String orgId) { this.orgId = orgId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }
    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
    public String getUniversity() { return university; }
    public void setUniversity(String university) { this.university = university; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public int getCapacityLimit() { return capacityLimit; }
    public void setCapacityLimit(int capacityLimit) { this.capacityLimit = capacityLimit; }
    public int getCurrentRsvpCount() { return currentRsvpCount; }
    public void setCurrentRsvpCount(int currentRsvpCount) { this.currentRsvpCount = currentRsvpCount; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}

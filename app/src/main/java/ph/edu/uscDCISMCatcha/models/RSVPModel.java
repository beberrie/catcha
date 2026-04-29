package ph.edu.uscDCISMCatcha.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

@IgnoreExtraProperties
public class RSVPModel {
    private String rsvpId;
    private String userId;
    private String eventId;
    private String eventTitle;
    private String status; // "Going", "Interested", "Not Going"
    @ServerTimestamp
    private Timestamp timestamp;

    public RSVPModel() {}

    public RSVPModel(String userId, String eventId, String eventTitle, String status) {
        this.userId = userId;
        this.eventId = eventId;
        this.eventTitle = eventTitle;
        this.status = status;
    }

    @Exclude
    public String getRsvpId() { return rsvpId; }
    public void setRsvpId(String rsvpId) { this.rsvpId = rsvpId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }
    public String getEventTitle() { return eventTitle; }
    public void setEventTitle(String eventTitle) { this.eventTitle = eventTitle; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Timestamp getTimestamp() { return timestamp; }
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }
}

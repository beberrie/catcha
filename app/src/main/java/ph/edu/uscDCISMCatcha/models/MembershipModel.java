package ph.edu.uscDCISMCatcha.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

@IgnoreExtraProperties
public class MembershipModel {
    private String userId;
    private String orgId;
    private String orgName; // Denormalized for quick UI display
    private String role;    // "Member", "Officer", "President"
    private String status;  // "Active", "Pending"
    @ServerTimestamp
    private Timestamp joinedAt;

    public MembershipModel() {}

    public MembershipModel(String userId, String orgId, String orgName, String role, String status) {
        this.userId = userId;
        this.orgId = orgId;
        this.orgName = orgName;
        this.role = role;
        this.status = status;
    }

    // Getters and Setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getOrgId() { return orgId; }
    public void setOrgId(String orgId) { this.orgId = orgId; }
    public String getOrgName() { return orgName; }
    public void setOrgName(String orgName) { this.orgName = orgName; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Timestamp getJoinedAt() { return joinedAt; }
    public void setJoinedAt(Timestamp joinedAt) { this.joinedAt = joinedAt; }
}

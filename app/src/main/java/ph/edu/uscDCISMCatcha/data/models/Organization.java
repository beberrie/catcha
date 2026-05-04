package ph.edu.uscDCISMCatcha.data.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

@IgnoreExtraProperties
public class Organization {
    private String name;
    private String university;
    private String school; // e.g., School of Arts and Sciences
    private String department; // e.g., DCISM
    private String description;
    private String category;
    private String profileImageUrl;
    private String ownerUid;
    @ServerTimestamp
    private Timestamp createdAt;

    public Organization() {}

    public Organization(String name, String university, String school, String department, String description, String category, String profileImageUrl, String ownerUid) {
        this.name = name;
        this.university = university;
        this.school = school;
        this.department = department;
        this.description = description;
        this.category = category;
        this.profileImageUrl = profileImageUrl;
        this.ownerUid = ownerUid;
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getUniversity() { return university; }
    public void setUniversity(String university) { this.university = university; }
    public String getSchool() { return school; }
    public void setSchool(String school) { this.school = school; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }
    public String getOwnerUid() { return ownerUid; }
    public void setOwnerUid(String ownerUid) { this.ownerUid = ownerUid; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}

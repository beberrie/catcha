package ph.edu.uscDCISMCatcha.models;

import com.google.firebase.firestore.IgnoreExtraProperties;
import java.util.ArrayList;
import java.util.List;

@IgnoreExtraProperties
public class Organization {
    private String orgId;
    private String name;
    private String university;
    private String department;
    private String description;
    private String profileImageUrl;
    private List<String> handlerUids;
    private List<String> categories; // e.g., "Hackathon", "Robotics", "Arts"
    private boolean isOpenForApplications;

    public Organization() {
        this.categories = new ArrayList<>();
    }

    public Organization(String orgId, String name, String university, String department, 
                        String description, String profileImageUrl, List<String> handlerUids) {
        this.orgId = orgId;
        this.name = name;
        this.university = university;
        this.department = department;
        this.description = description;
        this.profileImageUrl = profileImageUrl;
        this.handlerUids = handlerUids;
        this.isOpenForApplications = false;
        this.categories = new ArrayList<>();
    }

    // Getters and Setters
    public String getOrgId() { return orgId; }
    public void setOrgId(String orgId) { this.orgId = orgId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getUniversity() { return university; }
    public void setUniversity(String university) { this.university = university; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }
    public List<String> getHandlerUids() { return handlerUids; }
    public void setHandlerUids(List<String> handlerUids) { this.handlerUids = handlerUids; }
    public List<String> getCategories() { return categories; }
    public void setCategories(List<String> categories) { this.categories = categories; }
    public boolean isOpenForApplications() { return isOpenForApplications; }
    public void setOpenForApplications(boolean openForApplications) { isOpenForApplications = openForApplications; }
}

package ph.edu.uscDCISMCatcha.data.models;

import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@IgnoreExtraProperties
public class UserModel implements Serializable {
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String university;
    private String department;
    private String role; // "Student", "OrgHandler", "Admin"
    private boolean interestsSelected;
    private List<String> interests;
    private Map<String, String> fcmTokens;
    @ServerTimestamp
    private Date createdAt;

    public UserModel() {
        this.fcmTokens = new HashMap<>();
        this.interests = new ArrayList<>();
        this.interestsSelected = false;
    }

    public UserModel(String firstName, String lastName, String username, String email, String university, String department, String role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.email = email;
        this.university = university;
        this.department = department;
        this.role = role;
        this.fcmTokens = new HashMap<>();
        this.interests = new ArrayList<>();
        this.interestsSelected = false;
    }

    // Getters and Setters
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getUniversity() { return university; }
    public void setUniversity(String university) { this.university = university; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public boolean isInterestsSelected() { return interestsSelected; }
    public void setInterestsSelected(boolean interestsSelected) { this.interestsSelected = interestsSelected; }
    public List<String> getInterests() { return interests; }
    public void setInterests(List<String> interests) { this.interests = interests; }
    public Map<String, String> getFcmTokens() { return fcmTokens; }
    public void setFcmTokens(Map<String, String> fcmTokens) { this.fcmTokens = fcmTokens; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public String getName() { return firstName + " " + lastName; }
}

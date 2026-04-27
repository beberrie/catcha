package ph.edu.uscDCISMCatcha.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;
import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class UserModel {
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String university;
    private String department;
    private String role; // "Student", "OrgHandler", "Admin"
    private Map<String, String> fcmTokens;
    @ServerTimestamp
    private Timestamp createdAt;

    public UserModel() {
        this.fcmTokens = new HashMap<>();
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
    public Map<String, String> getFcmTokens() { return fcmTokens; }
    public void setFcmTokens(Map<String, String> fcmTokens) { this.fcmTokens = fcmTokens; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}

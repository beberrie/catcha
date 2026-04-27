package ph.edu.uscDCISMCatcha.models;

import com.google.firebase.firestore.IgnoreExtraProperties;
import java.util.ArrayList;
import java.util.List;

@IgnoreExtraProperties
public class UserModel {
    private String uid;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String university;
    private String department;
    private String role; // "Student", "OrgHandler", "Admin"
    private String fcmToken;
    private List<String> interests;

    public UserModel() {
        this.interests = new ArrayList<>();
    }

    public UserModel(String uid, String firstName, String lastName, String username, String email, String university, String department, String role) {
        this.uid = uid;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.email = email;
        this.university = university;
        this.department = department;
        this.role = role;
        this.interests = new ArrayList<>();
    }

    // Getters and Setters
    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }
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
    public String getFcmToken() { return fcmToken; }
    public void setFcmToken(String fcmToken) { this.fcmToken = fcmToken; }
    public List<String> getInterests() { return interests; }
    public void setInterests(List<String> interests) { this.interests = interests; }
}

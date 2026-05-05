package ph.edu.uscDCISMCatcha.models;

public class UserModel {
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String university;
    private String department;
    private String role;

    public UserModel() {}

    public UserModel(String firstName, String lastName, String username,
                     String email, String university, String department,
                     String role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.email = email;
        this.university = university;
        this.department = department;
        this.role = role;
    }

    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getUniversity() { return university; }
    public String getDepartment() { return department; }
    public String getRole() { return role; }
    public String getName() { return firstName + " " + lastName; }
}
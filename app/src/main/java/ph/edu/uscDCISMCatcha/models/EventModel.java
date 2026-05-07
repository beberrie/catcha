package ph.edu.uscDCISMCatcha.models;

public class EventModel {
    private String id;
    private String title;
    private String location;
    private String date;
    private String time;
    private String description;
    private String imageUrl;
    private String status;
    private String university;

    public EventModel() {}

    public EventModel(String id, String title, String location, String date,
                      String time, String description, String status, String university) {
        this.id = id;
        this.title = title;
        this.location = location;
        this.date = date;
        this.time = time;
        this.description = description;
        this.status = status;
        this.university = university;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getLocation() { return location; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public String getDescription() { return description; }
    public String getStatus() { return status; }
    public String getUniversity() { return university; }
    public String getImageUrl() { return imageUrl; }
}
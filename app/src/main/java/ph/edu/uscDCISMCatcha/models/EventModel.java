package ph.edu.uscDCISMCatcha.models;

public class EventModel {
    private String title, date, time, endTime, location, description;
    private boolean autoReminders;

    public EventModel(String title, String date, String time, String endTime,
                      String location, String description, boolean autoReminders) {
        this.title = title;
        this.date = date;
        this.time = time;
        this.endTime = endTime;
        this.location = location;
        this.description = description;
        this.autoReminders = autoReminders;
    }

    // Getters...
    public String getTitle() { return title; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public String getLocation() { return location; }
}
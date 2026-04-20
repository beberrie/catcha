package ph.edu.uscDCISMCatcha.models;

public class EventModel {
    private String title;
    private String date;
    private String time;
    private String endTime;
    private String location;
    private String description;
    private boolean autoReminders;
    private long createdAt;

    public EventModel() {}

    public EventModel(String title, String date, String time, String endTime,
                      String location, String description, boolean autoReminders) {
        this.title = title;
        this.date = date;
        this.time = time;
        this.endTime = endTime;
        this.location = location;
        this.description = description;
        this.autoReminders = autoReminders;
        this.createdAt = System.currentTimeMillis();
    }

    public String getTitle() { return title; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public String getEndTime() { return endTime; }
    public String getLocation() { return location; }
    public String getDescription() { return description; }
    public boolean isAutoReminders() { return autoReminders; }
    public long getCreatedAt() { return createdAt; }
}
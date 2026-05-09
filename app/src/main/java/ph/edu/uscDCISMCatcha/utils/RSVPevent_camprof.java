package ph.edu.uscDCISMCatcha.utils;

public class RSVPevent_camprof {
    public enum AttendanceStatus {
        ATTENDED,
        NO_SHOW,
        PENDING
    }

    private String           title;
    private String           date;
    private String           category;
    private AttendanceStatus status;

    public RSVPevent_camprof(String title, String date, String category, AttendanceStatus status) {
        this.title    = title;
        this.date     = date;
        this.category = category;
        this.status   = status;
    }

    public String           getTitle()    { return title; }
    public String           getDate()     { return date; }
    public String           getCategory() { return category; }
    public AttendanceStatus getStatus()   { return status; }

    public String getStatusLabel() {
        switch (status) {
            case ATTENDED: return "Attended";
            case NO_SHOW:  return "No-show";
            case PENDING:  return "Pending";
            default:       return "";
        }
    }
}
package ph.edu.uscDCISMCatcha.utils;

public class Constants {
    // Firestore Collections
    public static final String COL_USERS = "users";
    public static final String COL_EVENTS = "events";
    public static final String COL_ORGANIZATIONS = "organizations";
    public static final String COL_RSVPS = "rsvps";
    public static final String COL_ANNOUNCEMENTS = "announcements";
    public static final String COL_SYSTEM_METRICS = "system_metrics";
    public static final String COL_ORG_METRICS = "org_metrics";
    public static final String COL_DAILY_METRICS = "daily_metrics";

    // Document IDs
    public static final String DOC_GLOBAL_METRICS = "global";

    // RSVP Status
    public static final String STATUS_GOING = "Going";
    public static final String STATUS_INTERESTED = "Interested";

    // Intent Extras
    public static final String EXTRA_EVENT_ID = "EVENT_ID";
    public static final String EXTRA_EVENT_TITLE = "EVENT_TITLE";
    public static final String EXTRA_EVENT_HOST = "EVENT_HOST";
    public static final String EXTRA_EVENT_LOCATION = "EVENT_LOCATION";
    public static final String EXTRA_EVENT_DATETIME = "EVENT_DATETIME";
    public static final String EXTRA_EVENT_DESCRIPTION = "EVENT_DESCRIPTION";
    public static final String EXTRA_EVENT_STATUS = "EVENT_STATUS";
    public static final String EXTRA_EVENT_STATUS_COLOR = "EVENT_STATUS_COLOR";
}

package ph.edu.uscDCISMCatcha.utils;

public class CategoryBreakdown {
    private String category;
    private int percentage;
    private int eventCount;

    public CategoryBreakdown(String category, int percentage, int eventCount) {
        this.category   = category;
        this.percentage = percentage;
        this.eventCount = eventCount;
    }

    public String getCategory()   { return category; }
    public int    getPercentage() { return percentage; }
    public int    getEventCount() { return eventCount; }
}
package ph.edu.uscDCISMCatcha.models;

public class RecommendationModel {
    public enum Type { ORG, EVENT }

    private String initials;
    private String title;
    private String subtitle;
    private int followers;
    private int matchPercent;
    private String[] tags;
    private Type type;

    public RecommendationModel() {}

    public RecommendationModel(String initialsOrSubtitle, String title, int followers,
                               int matchPercent, String[] tags, Type type) {
        if (type == Type.ORG) {
            this.initials = initialsOrSubtitle;
        } else {
            this.subtitle = initialsOrSubtitle;
        }
        this.title = title;
        this.followers = followers;
        this.matchPercent = matchPercent;
        this.tags = tags;
        this.type = type;
    }

    public String getInitials() { return initials; }
    public String getTitle() { return title; }
    public String getSubtitle() { return subtitle; }
    public int getFollowers() { return followers; }
    public int getMatchPercent() { return matchPercent; }
    public String[] getTags() { return tags; }
    public Type getType() { return type; }
}
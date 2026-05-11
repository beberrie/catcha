package ph.edu.uscDCISMCatcha.models;

public class RecommendationModel {

    public enum Type { ORG, EVENT }

    private String id;
    private Type type;
    private String title;
    private String subtitle;
    private int matchPercent;
    private String[] tags;
    private int followers;
    private String initials;

    public RecommendationModel() {}

    public RecommendationModel(String id, Type type, String title,
                               String subtitle, int matchPercent,
                               String[] tags, int followers, String initials) {
        this.id           = id;
        this.type         = type;
        this.title        = title;
        this.subtitle     = subtitle;
        this.matchPercent = matchPercent;
        this.tags         = tags;
        this.followers    = followers;
        this.initials     = initials;
    }

    public String getId() { return id; }
    public Type getType() { return type; }
    public String getTitle() { return title; }
    public String getSubtitle() { return subtitle; }
    public int getMatchPercent() { return matchPercent; }
    public String[] getTags() { return tags; }
    public int getFollowers() { return followers; }
    public String getInitials() { return initials; }

    public void setId(String id) { this.id = id; }
    public void setType(Type type) { this.type = type; }
    public void setTitle(String title) { this.title = title; }
    public void setSubtitle(String subtitle) { this.subtitle = subtitle; }
    public void setMatchPercent(int matchPercent) { this.matchPercent = matchPercent; }
    public void setTags(String[] tags) { this.tags = tags; }
    public void setFollowers(int followers) { this.followers = followers; }
    public void setInitials(String initials) { this.initials = initials; }
<<<<<<< HEAD
}
=======
}
>>>>>>> 61f9bf6689b019dcfe76283eac605b9dca98bb21

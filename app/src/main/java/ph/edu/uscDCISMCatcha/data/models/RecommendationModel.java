package ph.edu.uscDCISMCatcha.models;

import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class RecommendationModel {

    public enum Type { ORG }

    private String id;
    private String name;
    private String school;
    private String department;
    private String category;
    private String bannerImageUrl;
    private int matchPercent;
    private Type type;

    public RecommendationModel() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    // Getters and Setters for Compatibility
    public String getTitle() { return name; }
    public void setTitle(String title) { this.name = title; }

    public String[] getTags() {
        return category != null ? new String[]{category} : new String[0];
    }

    public String getSchool() { return school; }
    public void setSchool(String school) { this.school = school; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getBannerImageUrl() { return bannerImageUrl; }
    public void setBannerImageUrl(String bannerImageUrl) { this.bannerImageUrl = bannerImageUrl; }
    public int getMatchPercent() { return matchPercent; }
    public void setMatchPercent(int matchPercent) { this.matchPercent = matchPercent; }
    public Type getType() { return type; }
    public void setType(Type type) { this.type = type; }
}
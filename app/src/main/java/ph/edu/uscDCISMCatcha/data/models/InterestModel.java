package ph.edu.uscDCISMCatcha.models;

import java.util.Map;

public class InterestModel {

    private String userId;
    private Map<String, Double> tagWeights;
    private long lastUpdated;

    public InterestModel() {}

    public InterestModel(String userId,
                         Map<String, Double> tagWeights) {
        this.userId      = userId;
        this.tagWeights  = tagWeights;
        this.lastUpdated = System.currentTimeMillis();
    }

    public String getTopTag() {
        if (tagWeights == null || tagWeights.isEmpty()) return "";
        return tagWeights.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("");
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public Map<String, Double> getTagWeights() { return tagWeights; }
    public void setTagWeights(Map<String, Double> tagWeights) {
        this.tagWeights = tagWeights;
    }
    public long getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
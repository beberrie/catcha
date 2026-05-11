package ph.edu.uscDCISMCatcha.models;

import com.google.firebase.firestore.IgnoreExtraProperties;
import java.util.Map;

@IgnoreExtraProperties
public class InterestModel {

    private String userId;
    private Map<String, Double> tagWeights;
    private long lastUpdated;

    public InterestModel() {}

    public InterestModel(String userId, Map<String, Double> tagWeights) {
        this.userId     = userId;
        this.tagWeights = tagWeights;
        this.lastUpdated = System.currentTimeMillis();
    }

    // Get top tag name by highest weight
    public String getTopTag() {
        if (tagWeights == null || tagWeights.isEmpty()) return "";
        return tagWeights.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("");
    }

    // Compute match score against another tag map (0.0 - 1.0)
    public double computeMatchScore(Map<String, Double> targetTags) {
        if (tagWeights == null || targetTags == null) return 0.0;
        double score = 0.0;
        for (Map.Entry<String, Double> entry : targetTags.entrySet()) {
            Double userWeight = tagWeights.get(entry.getKey());
            if (userWeight != null) {
                score += userWeight * entry.getValue();
            }
        }
        return Math.min(score, 1.0);
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public Map<String, Double> getTagWeights() { return tagWeights; }
    public void setTagWeights(Map<String, Double> tagWeights) { this.tagWeights = tagWeights; }
    public long getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(long lastUpdated) { this.lastUpdated = lastUpdated; }
}
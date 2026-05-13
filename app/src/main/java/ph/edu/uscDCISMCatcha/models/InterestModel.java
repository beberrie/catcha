package ph.edu.uscDCISMCatcha.models;

import java.util.Map;

public class InterestModel {
    private String userId;
    private Map<String, Double> tagWeights;

    public InterestModel() {}

    public InterestModel(String userId, Map<String, Double> tagWeights) {
        this.userId = userId;
        this.tagWeights = tagWeights;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Map<String, Double> getTagWeights() {
        return tagWeights;
    }

    public void setTagWeights(Map<String, Double> tagWeights) {
        this.tagWeights = tagWeights;
    }
}
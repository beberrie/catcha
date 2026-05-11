package ph.edu.uscDCISMCatcha.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ph.edu.uscDCISMCatcha.models.InterestModel;
import ph.edu.uscDCISMCatcha.models.RecommendationModel;

public class InterestViewModel extends ViewModel {

    private final MutableLiveData<InterestModel> interestProfile = new MutableLiveData<>();
    private final MutableLiveData<List<RecommendationModel>> recommendations = new MutableLiveData<>();

    public LiveData<InterestModel> getInterestProfile() {
        return interestProfile;
    }

    public LiveData<List<RecommendationModel>> getRecommendations() {
        return recommendations;
    }

    // FIX: Added the missing method required by InterestProfileFragment
    public void loadUserContent(String userId) {
        InterestModel dummyProfile = new InterestModel();
        Map<String, Double> weights = new HashMap<>();

        // Simulating data for your Interest Profile
        weights.put("Coding", 0.95);
        weights.put("Technology", 0.85);
        weights.put("Android", 0.75);
        weights.put("Gaming", 0.60);
        weights.put("Leadership", 0.45);
        weights.put("UI/UX", 0.30);

        dummyProfile.setUserId(userId);
        dummyProfile.setTagWeights(weights);

        interestProfile.setValue(dummyProfile);
    }

    public void loadDummyRecommendations() {
        // (Existing recommendation logic goes here)
    }

    public void updateTagWeight(String userId, String tag, double increment) {
    }
}
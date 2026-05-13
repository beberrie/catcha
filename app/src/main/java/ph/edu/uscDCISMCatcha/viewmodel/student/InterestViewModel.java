package ph.edu.uscDCISMCatcha.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import ph.edu.uscDCISMCatcha.data.models.InterestModel;
import ph.edu.uscDCISMCatcha.models.RecommendationModel;

public class InterestViewModel extends ViewModel {

    private final MutableLiveData<InterestModel> interestProfile = new MutableLiveData<>();
    private final MutableLiveData<List<RecommendationModel>> recommendations = new MutableLiveData<>();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public LiveData<InterestModel> getInterestProfile() { return interestProfile; }
    public LiveData<List<RecommendationModel>> getRecommendations() { return recommendations; }

    public void loadDummyRecommendations() {
        // Using a default user ID for dummy loading
        loadUserContent("user_001");
    }

    // This method replaces your dummy loaders
    public void loadUserContent(String userId) {
        db.collection("users").document(userId)
                .addSnapshotListener((documentSnapshot, e) -> {
                    if (e != null || documentSnapshot == null || !documentSnapshot.exists()) return;

                    // Getting the "interests" array from your screenshot
                    List<String> userInterests = (List<String>) documentSnapshot.get("interests");

                    if (userInterests != null) {
                        Map<String, Double> tagWeights = new LinkedHashMap<>();
                        for (String interest : userInterests) {
                            tagWeights.put(interest, 1.0);
                        }
                        interestProfile.setValue(new InterestModel(userId, tagWeights));

                        // Fetch matching content
                        fetchMatchesFromFirestore(userInterests);
                    }
                });
    }

    public void updateTagWeight(String userId, String tag, double increment) {
        // TODO: Implement actual tag weight update logic in Firestore if needed
        // For now, this stub prevents compilation errors in RecommendationFragment
    }

    private void fetchMatchesFromFirestore(List<String> userInterests) {
        if (userInterests.isEmpty()) return;

        List<RecommendationModel> combinedResults = new ArrayList<>();

        // Match "Category" in events
        db.collection("events")
                .whereIn("Category", userInterests)
                .get()
                .addOnSuccessListener(eventSnapshots -> {
                    for (DocumentSnapshot doc : eventSnapshots) {
                        RecommendationModel event = doc.toObject(RecommendationModel.class);
                        if (event != null) {
                            event.setType(RecommendationModel.Type.EVENT);
                            combinedResults.add(event);
                        }
                    }

                    // Match "category" in organizations
                    db.collection("organizations")
                            .whereIn("category", userInterests)
                            .get()
                            .addOnSuccessListener(orgSnapshots -> {
                                for (DocumentSnapshot doc : orgSnapshots) {
                                    RecommendationModel org = doc.toObject(RecommendationModel.class);
                                    if (org != null) {
                                        org.setType(RecommendationModel.Type.ORG);
                                        combinedResults.add(org);
                                    }
                                }
                                recommendations.setValue(combinedResults);
                            });
                });
    }
}
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
import ph.edu.uscDCISMCatcha.models.InterestModel;
import ph.edu.uscDCISMCatcha.models.RecommendationModel;

public class InterestViewModel extends ViewModel {

    private final MutableLiveData<InterestModel> interestProfile = new MutableLiveData<>();
    private final MutableLiveData<List<RecommendationModel>> recommendations = new MutableLiveData<>();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public LiveData<InterestModel> getInterestProfile() { return interestProfile; }
    public LiveData<List<RecommendationModel>> getRecommendations() { return recommendations; }

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

    public void loadDummyRecommendations() {
        List<RecommendationModel> dummyList = new ArrayList<>();

        dummyList.add(new RecommendationModel(
                "org_1",
                RecommendationModel.Type.ORG,
                "Google Developer Student Club",
                "Technology & Programming",
                95,
                new String[]{"Tech", "Mobile", "Web"},
                1200,
                "GDSC"
        ));

        dummyList.add(new RecommendationModel(
                "event_1",
                RecommendationModel.Type.EVENT,
                "Android Dev Summit",
                "May 20, 2024 • Online",
                88,
                new String[]{"Android", "Kotlin"},
                0,
                ""
        ));

        recommendations.setValue(dummyList);
    }

    public void updateTagWeight(String userId, String tag, double increment) {
        // Implementation for updating tag weight in Firestore or local model
        // This is a placeholder to resolve the "cannot find symbol" error
    }
}

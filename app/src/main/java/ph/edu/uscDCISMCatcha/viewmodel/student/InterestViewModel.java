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

    private final MutableLiveData<InterestModel> interestProfile
            = new MutableLiveData<>();

    // ✅ Only ORG recommendations — events removed
    private final MutableLiveData<List<RecommendationModel>>
            orgRecommendations = new MutableLiveData<>();

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public LiveData<InterestModel> getInterestProfile() {
        return interestProfile;
    }

    // ✅ Renamed to orgRecommendations
    public LiveData<List<RecommendationModel>> getOrgRecommendations() {
        return orgRecommendations;
    }

    // Called from OrgDashboardFragment
    public void loadUserContent(String userId) {
        db.collection("users").document(userId)
                .addSnapshotListener((documentSnapshot, e) -> {
                    if (e != null
                            || documentSnapshot == null
                            || !documentSnapshot.exists()) return;

                    List<String> userInterests =
                            (List<String>) documentSnapshot.get("interests");

                    if (userInterests != null && !userInterests.isEmpty()) {
                        // Build interest profile
                        Map<String, Double> tagWeights =
                                new LinkedHashMap<>();
                        for (String interest : userInterests) {
                            tagWeights.put(interest, 1.0);
                        }
                        interestProfile.setValue(
                                new InterestModel(userId, tagWeights));

                        // ✅ Fetch only org matches
                        fetchOrgMatchesFromFirestore(userInterests);
                    }
                });
    }

    // ✅ Only fetches orgs — no events
    private void fetchOrgMatchesFromFirestore(List<String> userInterests) {
        if (userInterests.isEmpty()) return;

        db.collection("organizations")
                .whereIn("category", userInterests)
                .get()
                .addOnSuccessListener(orgSnapshots -> {
                    List<RecommendationModel> orgList = new ArrayList<>();

                    for (DocumentSnapshot doc : orgSnapshots.getDocuments()) {
                        RecommendationModel org =
                                doc.toObject(RecommendationModel.class);
                        if (org != null) {
                            org.setId(doc.getId()); // ✅ Set the Firestore ID
                            org.setType(RecommendationModel.Type.ORG);
                            orgList.add(org);
                        }
                    }

                    orgRecommendations.setValue(orgList);
                })
                .addOnFailureListener(e ->
                        orgRecommendations.setValue(new ArrayList<>()));
    }

    // ✅ Update tag weight when user follows an org
    public void updateTagWeight(String userId, String tag,
                                double delta) {
        InterestModel current = interestProfile.getValue();
        if (current == null
                || current.getTagWeights() == null) return;

        Map<String, Double> weights = current.getTagWeights();
        double currentWeight = weights.containsKey(tag)
                ? weights.get(tag) : 0.0;
        double newWeight = Math.min(currentWeight + delta, 1.0);
        weights.put(tag, newWeight);
        current.setLastUpdated(System.currentTimeMillis());
        interestProfile.setValue(current);

        // Re-fetch org recommendations after weight update
        List<String> tags = new ArrayList<>(weights.keySet());
        if (!tags.isEmpty()) fetchOrgMatchesFromFirestore(tags);
    }
}
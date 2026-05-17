package ph.edu.uscDCISMCatcha.viewmodel.student;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import ph.edu.uscDCISMCatcha.data.models.InterestModel;
import ph.edu.uscDCISMCatcha.data.models.RecommendationModel;

public class InterestViewModel extends ViewModel {

    private final MutableLiveData<InterestModel> interestProfile = new MutableLiveData<>();
    private final MutableLiveData<List<RecommendationModel>> orgRecommendations = new MutableLiveData<>();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "InterestViewModel";

    public LiveData<InterestModel> getInterestProfile() {
        return interestProfile;
    }

    public LiveData<List<RecommendationModel>> getOrgRecommendations() {
        return orgRecommendations;
    }

    public void loadUserContent(String userId) {
        if (userId == null) return;

        db.collection("users").document(userId)
                .addSnapshotListener((documentSnapshot, e) -> {
                    if (e != null) {
                        Log.e(TAG, "Snapshot error", e);
                        return;
                    }

                    if (documentSnapshot == null || !documentSnapshot.exists()) {
                        Log.d(TAG, "User document does not exist");
                        orgRecommendations.setValue(new ArrayList<>());
                        return;
                    }

                    @SuppressWarnings("unchecked")
                    List<String> userInterests = (List<String>) documentSnapshot.get("interests");

                    if (userInterests != null && !userInterests.isEmpty()) {
                        Log.d(TAG, "User interests: " + userInterests);
                        Map<String, Double> tagWeights = new LinkedHashMap<>();
                        for (String interest : userInterests) {
                            tagWeights.put(interest, 1.0);
                        }
                        interestProfile.setValue(new InterestModel(userId, tagWeights));
                        fetchOrgMatchesFromFirestore(userInterests);
                    } else {
                        Log.d(TAG, "User has no interests selected.");
                        orgRecommendations.setValue(new ArrayList<>());
                    }
                });
    }

    private void fetchOrgMatchesFromFirestore(List<String> userInterests) {
        if (userInterests == null || userInterests.isEmpty()) {
            orgRecommendations.setValue(new ArrayList<>());
            return;
        }

        // Limit query list to 10 for Firestore compatibility
        List<String> queryList = userInterests.size() > 10 ? userInterests.subList(0, 10) : userInterests;

        // Perform sequential queries (Category then Department) and merge
        db.collection("organizations")
                .whereIn("category", queryList)
                .get()
                .addOnSuccessListener(catSnapshots -> {
                    final List<RecommendationModel> combinedList = new ArrayList<>();
                    final Set<String> addedIds = new HashSet<>();

                    // Add category matches
                    for (DocumentSnapshot doc : catSnapshots.getDocuments()) {
                        RecommendationModel org = doc.toObject(RecommendationModel.class);
                        if (org != null) {
                            org.setId(doc.getId());
                            combinedList.add(org);
                            addedIds.add(doc.getId());
                        }
                    }

                    // Add department matches (avoiding duplicates)
                    db.collection("organizations")
                            .whereIn("department", queryList)
                            .get()
                            .addOnSuccessListener(deptSnapshots -> {
                                for (DocumentSnapshot doc : deptSnapshots.getDocuments()) {
                                    if (!addedIds.contains(doc.getId())) {
                                        RecommendationModel org = doc.toObject(RecommendationModel.class);
                                        if (org != null) {
                                            org.setId(doc.getId());
                                            combinedList.add(org);
                                            addedIds.add(doc.getId());
                                        }
                                    }
                                }
                                Log.d(TAG, "Final merged recommendation count: " + combinedList.size());
                                orgRecommendations.setValue(combinedList);
                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "Dept query error", e);
                                orgRecommendations.setValue(combinedList); // Return category results at least
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Category query error", e);
                    orgRecommendations.setValue(new ArrayList<>());
                });
    }

    public void updateTagWeight(String userId, String tag, double delta) {
        InterestModel current = interestProfile.getValue();
        if (current == null || current.getTagWeights() == null) return;

        Map<String, Double> weights = current.getTagWeights();
        double currentWeight = weights.getOrDefault(tag, 0.0);
        double newWeight = Math.min(currentWeight + delta, 1.0);
        weights.put(tag, newWeight);
        current.setLastUpdated(System.currentTimeMillis());
        interestProfile.setValue(current);

        List<String> tags = new ArrayList<>(weights.keySet());
        if (!tags.isEmpty()) fetchOrgMatchesFromFirestore(tags);
    }
}

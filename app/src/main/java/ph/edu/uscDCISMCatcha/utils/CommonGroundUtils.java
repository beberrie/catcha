package ph.edu.uscDCISMCatcha.utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import ph.edu.uscDCISMCatcha.models.UserModel;

public class CommonGroundUtils {

    public interface CommonGroundCallback {
        void onResult(List<UserModel> friendsAttending);
    }

    public static void getFriendsAttending(String eventId, CommonGroundCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("users").document(currentUserId).get()
                .addOnSuccessListener(currentUserDoc -> {
                    String currentUniversity = currentUserDoc.getString("university");

                    db.collection("rsvps")
                            .whereEqualTo("eventId", eventId)
                            .whereEqualTo("status", "Going")
                            .get()
                            .addOnSuccessListener(rsvpSnapshots -> {

                                List<String> attendingUserIds = new ArrayList<>();
                                for (QueryDocumentSnapshot rsvp : rsvpSnapshots) {
                                    String userId = rsvp.getString("userId");
                                    if (userId != null && !userId.equals(currentUserId)) {
                                        attendingUserIds.add(userId);
                                    }
                                }

                                if (attendingUserIds.isEmpty()) {
                                    callback.onResult(new ArrayList<>());
                                    return;
                                }

                                db.collection("users")
                                        .whereEqualTo("university", currentUniversity)
                                        .get()
                                        .addOnSuccessListener(userSnapshots -> {
                                            List<UserModel> commonGround = new ArrayList<>();

                                            for (QueryDocumentSnapshot userDoc : userSnapshots) {
                                                String userId = userDoc.getId();
                                                if (attendingUserIds.contains(userId)) {
                                                    UserModel user = userDoc.toObject(UserModel.class);
                                                    commonGround.add(user);
                                                }
                                            }
                                            callback.onResult(commonGround);
                                        })
                                        .addOnFailureListener(e -> callback.onResult(new ArrayList<>()));
                            })
                            .addOnFailureListener(e -> callback.onResult(new ArrayList<>()));
                })
                .addOnFailureListener(e -> callback.onResult(new ArrayList<>()));
    }
}
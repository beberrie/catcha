package ph.edu.uscDCISMCatcha.utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import ph.edu.uscDCISMCatcha.data.models.UserModel;

public class CommonGroundUtils {

    public interface CommonGroundCallback {
        void onResult(List<UserModel> friendsAttending);
    }

    public static void getFriendsAttending(String eventId, CommonGroundCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("users").document(currentUserId).get()
                .addOnSuccessListener(currentUserDoc -> {
                    List<String> currentInterests = (List<String>) currentUserDoc.get("interests");
                    String currentUniversity = currentUserDoc.getString("university");

                    android.util.Log.d("CommonGround", "Current interests: " + currentInterests);
                    android.util.Log.d("CommonGround", "Current university: " + currentUniversity);

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

                                android.util.Log.d("CommonGround", "RSVPs found: " + attendingUserIds.size());

                                if (attendingUserIds.isEmpty()) {
                                    callback.onResult(new ArrayList<>());
                                    return;
                                }

                                db.collection("users").get()
                                        .addOnSuccessListener(userSnapshots -> {
                                            List<UserModel> commonGround = new ArrayList<>();

                                            for (QueryDocumentSnapshot userDoc : userSnapshots) {
                                                String userId = userDoc.getId();

                                                if (userId.equals(currentUserId)) continue;
                                                if (!attendingUserIds.contains(userId)) continue;
                                                // Skip private users
                                                Boolean isPrivate = userDoc.getBoolean("isPrivate");
                                                if (isPrivate != null && isPrivate) continue;

                                                String theirUniversity = userDoc.getString("university");
                                                boolean sameUniversity = currentUniversity != null
                                                        && currentUniversity.equals(theirUniversity);

                                                List<String> theirInterests = (List<String>) userDoc.get("interests");
                                                boolean hasSharedInterest = false;
                                                if (currentInterests != null && theirInterests != null) {
                                                    for (String interest : currentInterests) {
                                                        if (theirInterests.contains(interest)) {
                                                            hasSharedInterest = true;
                                                            break;
                                                        }
                                                    }
                                                }

                                                android.util.Log.d("CommonGround", "User: " + userId + " sameUni: " + sameUniversity + " sharedInterest: " + hasSharedInterest);

                                                if (sameUniversity || hasSharedInterest) {
                                                    UserModel user = userDoc.toObject(UserModel.class);
                                                    commonGround.add(user);
                                                }
                                            }

                                            android.util.Log.d("CommonGround", "Final result: " + commonGround.size());
                                            callback.onResult(commonGround);
                                        })
                                        .addOnFailureListener(e -> callback.onResult(new ArrayList<>()));
                            })
                            .addOnFailureListener(e -> callback.onResult(new ArrayList<>()));
                })
                .addOnFailureListener(e -> callback.onResult(new ArrayList<>()));
    }
}
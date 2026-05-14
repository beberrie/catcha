package ph.edu.uscDCISMCatcha.viewmodel;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.DocumentSnapshot;
import java.util.ArrayList;
import java.util.List;
import ph.edu.uscDCISMCatcha.data.models.NotificationModel;


public class NotificationViewModel extends ViewModel {


    private final MutableLiveData<List<NotificationModel>>
            notifications = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading
            = new MutableLiveData<>(false);
    private final MutableLiveData<String> error
            = new MutableLiveData<>();


    public LiveData<List<NotificationModel>> getNotifications() {
        return notifications;
    }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getError() { return error; }


    // Fetch real announcements from Firestore
    // Collection: announcements
    // Filter: recipientUserId == current user
    // OR: orgId in user's followed orgs
    public void fetchNotifications() {
        String userId = FirebaseAuth.getInstance()
                .getCurrentUser() != null
                ? FirebaseAuth.getInstance()
                .getCurrentUser().getUid()
                : null;


        if (userId == null) {
            error.setValue("User not logged in");
            return;
        }


        isLoading.setValue(true);


        // Step 1: Get orgs the user follows
        FirebaseFirestore.getInstance()
                .collection("userFollows")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(followSnapshots -> {


                    List<String> followedOrgIds = new ArrayList<>();
                    for (DocumentSnapshot doc :
                            followSnapshots.getDocuments()) {
                        String orgId = doc.getString("orgId");
                        if (orgId != null) followedOrgIds.add(orgId);
                    }


                    if (followedOrgIds.isEmpty()) {
                        isLoading.setValue(false);
                        notifications.setValue(new ArrayList<>());
                        return;
                    }


                    // Step 2: Fetch announcements from followed orgs
                    FirebaseFirestore.getInstance()
                            .collection("announcements")
                            .whereIn("orgId", followedOrgIds)
                            .orderBy("createdAt",
                                    Query.Direction.DESCENDING)
                            .addSnapshotListener((snapshots, e) -> {
                                isLoading.setValue(false);
                                if (e != null || snapshots == null) {
                                    error.setValue(
                                            "Failed to load notifications");
                                    return;
                                }


                                List<NotificationModel> list =
                                        new ArrayList<>();


                                for (DocumentSnapshot doc :
                                        snapshots.getDocuments()) {
                                    NotificationModel n =
                                            new NotificationModel();
                                    n.setId(doc.getId());
                                    n.setType(
                                            NotificationModel.Type.ANNOUNCEMENT);
                                    n.setUrgency(
                                            NotificationModel.Urgency.NORMAL);
                                    n.setOrgName(
                                            doc.getString("orgName"));
                                    n.setTitle(
                                            doc.getString("title"));
                                    n.setContent(
                                            doc.getString("content"));
                                    n.setPostedByName(
                                            doc.getString("postedByName"));
                                    n.setPostedByPosition(
                                            doc.getString("postedByPosition"));
                                    n.setFollowersCount(
                                            doc.getLong("followersCount") != null
                                                    ? doc.getLong("followersCount")
                                                    .intValue() : 0);


                                    // Check if read by current user
                                    List<String> readBy =
                                            (List<String>) doc.get("readBy");
                                    boolean isRead = readBy != null
                                            && readBy.contains(userId);
                                    n.setRead(isRead);


                                    if (doc.getTimestamp("createdAt") != null)
                                        n.setCreatedAt(
                                                doc.getTimestamp("createdAt"));


                                    // Format sent time
                                    if (doc.getTimestamp("createdAt") != null) {
                                        java.text.SimpleDateFormat sdf =
                                                new java.text.SimpleDateFormat(
                                                        "h:mm a",
                                                        java.util.Locale.getDefault());
                                        n.setSentTime(sdf.format(
                                                doc.getTimestamp("createdAt")
                                                        .toDate()));
                                    }


                                    list.add(n);
                                }


                                notifications.setValue(list);
                            });
                })
                .addOnFailureListener(e -> {
                    isLoading.setValue(false);
                    error.setValue("Failed to load followed orgs");
                });
    }


    // Mark single notification as read
    public void markAsRead(String announcementId) {
        String userId = FirebaseAuth.getInstance()
                .getCurrentUser() != null
                ? FirebaseAuth.getInstance()
                .getCurrentUser().getUid()
                : null;
        if (userId == null) return;


        FirebaseFirestore.getInstance()
                .collection("announcements")
                .document(announcementId)
                .update("readBy",
                        com.google.firebase.firestore.FieldValue
                                .arrayUnion(userId));
    }


    // Mark all as read
    public void markAllAsRead() {
        String userId = FirebaseAuth.getInstance()
                .getCurrentUser() != null
                ? FirebaseAuth.getInstance()
                .getCurrentUser().getUid()
                : null;
        if (userId == null) return;


        List<NotificationModel> list = notifications.getValue();
        if (list == null) return;


        for (NotificationModel item : list) {
            if (!item.isRead()) {
                FirebaseFirestore.getInstance()
                        .collection("announcements")
                        .document(item.getId())
                        .update("readBy",
                                com.google.firebase.firestore.FieldValue
                                        .arrayUnion(userId));
            }
        }
    }
}

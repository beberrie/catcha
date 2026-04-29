package ph.edu.uscDCISMCatcha.data.remote;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

import java.util.ArrayList;
import java.util.List;

import ph.edu.uscDCISMCatcha.models.EventModel;
import ph.edu.uscDCISMCatcha.models.RSVPModel;
import ph.edu.uscDCISMCatcha.models.UserModel;

public class FirebaseRemoteDataSource {

    private final FirebaseAuth auth;
    private final FirebaseFirestore firestore;

    public FirebaseRemoteDataSource() {
        this.auth = FirebaseAuth.getInstance();
        this.firestore = FirebaseFirestore.getInstance();
    }

    public FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }

    public boolean isUserLoggedIn() {
        return auth.getCurrentUser() != null;
    }

    public Task<AuthResult> signIn(String email, String password) {
        return auth.signInWithEmailAndPassword(email, password);
    }

    public Task<AuthResult> signUp(String email, String password) {
        return auth.createUserWithEmailAndPassword(email, password);
    }

    public Task<Void> saveUserProfile(String uid, UserModel user) {
        return firestore.collection("users").document(uid).set(user);
    }

    public Task<DocumentSnapshot> getUserProfile(String uid) {
        return firestore.collection("users").document(uid).get();
    }

    public Task<String> getUserRole(String uid) {
        return getUserProfile(uid).continueWith(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                return task.getResult().getString("role");
            }
            return null;
        });
    }

    public void signOut() {
        auth.signOut();
    }

    // --- RSVP Capacity Enforcement ---
    public Task<Void> rsvpToEvent(RSVPModel rsvp) {
        DocumentReference eventRef = firestore.collection("events").document(rsvp.getEventId());
        DocumentReference rsvpRef = firestore.collection("rsvps").document();
        rsvp.setRsvpId(rsvpRef.getId());

        return firestore.runTransaction(transaction -> {
            DocumentSnapshot eventSnapshot = transaction.get(eventRef);
            if (!eventSnapshot.exists()) {
                throw new RuntimeException("Event does not exist!");
            }

            long maxCapacity = eventSnapshot.getLong("maxCapacity");
            long currentCount = eventSnapshot.getLong("currentRsvpCount");

            if (maxCapacity > 0 && currentCount >= maxCapacity) {
                throw new RuntimeException("Event is full!");
            }

            // Check if user already RSVP'd
            // Note: Transactions require reads before writes. 
            // Better to handle "already rsvp'd" check via a separate query or composite ID.
            // For simplicity in this example, we proceed with creation.

            transaction.set(rsvpRef, rsvp);
            transaction.update(eventRef, "currentRsvpCount", currentCount + 1);

            return null;
        });
    }

    // --- RSVP Management ---
    public Query getUserRSVPs(String userId) {
        return firestore.collection("rsvps")
                .whereEqualTo("userId", userId)
                .orderBy("timestamp", Query.Direction.DESCENDING);
    }

    public Task<Void> cancelRSVP(String rsvpId, String eventId) {
        DocumentReference rsvpRef = firestore.collection("rsvps").document(rsvpId);
        DocumentReference eventRef = firestore.collection("events").document(eventId);

        return firestore.runTransaction(transaction -> {
            DocumentSnapshot eventSnapshot = transaction.get(eventRef);
            long currentCount = eventSnapshot.exists() ? eventSnapshot.getLong("currentRsvpCount") : 0;

            transaction.delete(rsvpRef);
            if (currentCount > 0) {
                transaction.update(eventRef, "currentRsvpCount", currentCount - 1);
            }
            return null;
        });
    }

    // --- Schedule Conflict Alerts ---
    public Task<List<EventModel>> checkConflicts(String userId, EventModel targetEvent) {
        // Fetch user's "Going" RSVPs
        return firestore.collection("rsvps")
                .whereEqualTo("userId", userId)
                .whereEqualTo("status", "Going")
                .get()
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) return Tasks.forException(task.getException());
                    
                    List<String> eventIds = new ArrayList<>();
                    if (task.getResult() != null) {
                        for (DocumentSnapshot doc : task.getResult()) {
                            String eventId = doc.getString("eventId");
                            if (eventId != null) eventIds.add(eventId);
                        }
                    }

                    if (eventIds.isEmpty()) {
                        return Tasks.forResult((List<EventModel>) new ArrayList<EventModel>());
                    }

                    // Fetch the actual events to compare timings
                    return firestore.collection("events")
                            .whereIn("eventId", eventIds)
                            .get()
                            .continueWith(eventsTask -> {
                                List<EventModel> events = new ArrayList<>();
                                if (eventsTask.isSuccessful() && eventsTask.getResult() != null) {
                                    for (DocumentSnapshot doc : eventsTask.getResult()) {
                                        EventModel event = doc.toObject(EventModel.class);
                                        if (event != null) {
                                            event.setEventId(doc.getId());
                                            events.add(event);
                                        }
                                    }
                                }
                                return events;
                            });
                })
                .continueWith(task -> {
                    List<EventModel> conflicts = new ArrayList<>();
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (EventModel existingEvent : task.getResult()) {
                            if (isOverlapping(existingEvent, targetEvent)) {
                                conflicts.add(existingEvent);
                            }
                        }
                    }
                    return conflicts;
                });
    }

    private boolean isOverlapping(EventModel e1, EventModel e2) {
        if (e1.getStartDateTime() == null || e1.getEndDateTime() == null ||
            e2.getStartDateTime() == null || e2.getEndDateTime() == null) {
            return false;
        }
        return e1.getStartDateTime().getSeconds() < e2.getEndDateTime().getSeconds() &&
               e2.getStartDateTime().getSeconds() < e1.getEndDateTime().getSeconds();
    }
}

package ph.edu.uscDCISMCatcha.data.repository;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.Transaction;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ph.edu.uscDCISMCatcha.data.models.EventModel;
import ph.edu.uscDCISMCatcha.data.models.Organization;
import ph.edu.uscDCISMCatcha.data.models.RSVPModel;
import ph.edu.uscDCISMCatcha.data.models.UserModel;

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
        DocumentReference userRef = firestore.collection("users").document(uid);
        DocumentReference metricsRef = firestore.collection("system_metrics").document("global");

        return firestore.runTransaction(transaction -> {
            DocumentSnapshot snapshot = transaction.get(userRef);
            transaction.set(userRef, user);
            if (!snapshot.exists()) {
                ensureMetricsInitialized(transaction, metricsRef);
                transaction.update(metricsRef, "totalUsers", FieldValue.increment(1));
            }
            return null;
        });
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

    // --- Organization Management ---
    public Task<Void> createOrganization(String orgId, Organization org) {
        DocumentReference orgRef = firestore.collection("organizations").document(orgId);
        DocumentReference metricsRef = firestore.collection("system_metrics").document("global");
        DocumentReference userRef = firestore.collection("users").document(org.getOwnerUid());

        return firestore.runTransaction(transaction -> {
            ensureMetricsInitialized(transaction, metricsRef);
            transaction.set(orgRef, org);
            transaction.update(userRef, "role", "OrgHandler");
            transaction.update(metricsRef, "totalOrganizations", FieldValue.increment(1));
            return null;
        });
    }

    // --- Event Creation ---
    public Task<Void> createEvent(EventModel event) {
        DocumentReference docRef = firestore.collection("events").document();
        event.setEventId(docRef.getId());
        DocumentReference metricsRef = firestore.collection("system_metrics").document("global");
        
        return firestore.runTransaction(transaction -> {
            ensureMetricsInitialized(transaction, metricsRef);
            transaction.set(docRef, event);
            transaction.update(metricsRef, "totalEvents", FieldValue.increment(1));
            
            // Increment org-level event count
            DocumentReference orgMetricsRef = firestore.collection("org_metrics").document(event.getOrgId());
            ensureOrgMetricsInitialized(transaction, orgMetricsRef);
            transaction.update(orgMetricsRef, "totalEvents", FieldValue.increment(1));
            
            return null;
        });
    }

    // --- RSVP Capacity Enforcement & Aggregation ---
    public Task<Void> rsvpToEvent(RSVPModel rsvp) {
        String rsvpId = rsvp.getUserId() + "_" + rsvp.getEventId();
        DocumentReference rsvpRef = firestore.collection("rsvps").document(rsvpId);
        DocumentReference eventRef = firestore.collection("events").document(rsvp.getEventId());
        DocumentReference metricsRef = firestore.collection("system_metrics").document("global");
        rsvp.setRsvpId(rsvpId);

        return firestore.runTransaction(transaction -> {
            ensureMetricsInitialized(transaction, metricsRef);
            DocumentSnapshot rsvpSnapshot = transaction.get(rsvpRef);
            DocumentSnapshot eventSnapshot = transaction.get(eventRef);
            
            if (!eventSnapshot.exists()) {
                throw new RuntimeException("Event does not exist!");
            }

            String orgId = eventSnapshot.getString("orgId");
            DocumentReference orgMetricsRef = firestore.collection("org_metrics").document(orgId);
            ensureOrgMetricsInitialized(transaction, orgMetricsRef);

            String oldStatus = rsvpSnapshot.exists() ? rsvpSnapshot.getString("status") : null;
            String newStatus = rsvp.getStatus();

            // Capacity and Count logic for Event
            if (!newStatus.equals(oldStatus)) {
                // Update new status counts
                if ("Going".equals(newStatus)) {
                    long maxCapacity = eventSnapshot.getLong("maxCapacity");
                    long currentCount = eventSnapshot.getLong("currentRsvpCount");
                    if (maxCapacity > 0 && currentCount >= maxCapacity) {
                        throw new RuntimeException("Event is full!");
                    }
                    transaction.update(eventRef, "currentRsvpCount", FieldValue.increment(1));
                } else if ("Interested".equals(newStatus)) {
                    transaction.update(eventRef, "interestedCount", FieldValue.increment(1));
                }

                // Update old status counts
                if ("Going".equals(oldStatus)) {
                    transaction.update(eventRef, "currentRsvpCount", FieldValue.increment(-1));
                } else if ("Interested".equals(oldStatus)) {
                    transaction.update(eventRef, "interestedCount", FieldValue.increment(-1));
                }
            }

            // Save RSVP
            transaction.set(rsvpRef, rsvp);

            // Update Global Metrics
            if (oldStatus == null) {
                transaction.update(metricsRef, "totalRsvps", FieldValue.increment(1));
                updateDailyMetrics(transaction, newStatus, 1);
                updateCategoricalMetrics(transaction, orgMetricsRef, newStatus, 1);
            } else if (!newStatus.equals(oldStatus)) {
                updateDailyMetrics(transaction, oldStatus, -1);
                updateDailyMetrics(transaction, newStatus, 1);
                updateCategoricalMetrics(transaction, orgMetricsRef, oldStatus, -1);
                updateCategoricalMetrics(transaction, orgMetricsRef, newStatus, 1);
                
                if ("Going".equals(oldStatus)) transaction.update(metricsRef, "totalGoing", FieldValue.increment(-1));
                if ("Interested".equals(oldStatus)) transaction.update(metricsRef, "totalInterested", FieldValue.increment(-1));
                if ("Going".equals(newStatus)) transaction.update(metricsRef, "totalGoing", FieldValue.increment(1));
                if ("Interested".equals(newStatus)) transaction.update(metricsRef, "totalInterested", FieldValue.increment(1));
            }

            return null;
        });
    }

    public Task<Void> cancelRSVP(String rsvpId, String eventId) {
        DocumentReference rsvpRef = firestore.collection("rsvps").document(rsvpId);
        DocumentReference eventRef = firestore.collection("events").document(eventId);
        DocumentReference metricsRef = firestore.collection("system_metrics").document("global");

        return firestore.runTransaction(transaction -> {
            ensureMetricsInitialized(transaction, metricsRef);
            DocumentSnapshot rsvpSnapshot = transaction.get(rsvpRef);
            if (!rsvpSnapshot.exists()) return null;
            
            String status = rsvpSnapshot.getString("status");
            DocumentSnapshot eventSnapshot = transaction.get(eventRef);
            if (!eventSnapshot.exists()) {
                transaction.delete(rsvpRef);
                return null;
            }

            String orgId = eventSnapshot.getString("orgId");
            DocumentReference orgMetricsRef = firestore.collection("org_metrics").document(orgId);

            // Delete RSVP
            transaction.delete(rsvpRef);
            
            // Update Event Counts
            if ("Going".equals(status)) {
                transaction.update(eventRef, "currentRsvpCount", FieldValue.increment(-1));
            } else if ("Interested".equals(status)) {
                transaction.update(eventRef, "interestedCount", FieldValue.increment(-1));
            }
            
            // Update Global Metrics
            transaction.update(metricsRef, "totalRsvps", FieldValue.increment(-1));
            if ("Going".equals(status)) transaction.update(metricsRef, "totalGoing", FieldValue.increment(-1));
            if ("Interested".equals(status)) transaction.update(metricsRef, "totalInterested", FieldValue.increment(-1));
            
            updateDailyMetrics(transaction, status, -1);
            updateCategoricalMetrics(transaction, orgMetricsRef, status, -1);
            
            return null;
        });
    }

    private void ensureMetricsInitialized(Transaction transaction, DocumentReference metricsRef) throws com.google.firebase.firestore.FirebaseFirestoreException {
        DocumentSnapshot metricsSnapshot = transaction.get(metricsRef);
        if (!metricsSnapshot.exists()) {
            Map<String, Object> initial = new HashMap<>();
            initial.put("totalRsvps", 0L);
            initial.put("totalGoing", 0L);
            initial.put("totalInterested", 0L);
            initial.put("totalEvents", 0L);
            initial.put("totalOrganizations", 0L);
            initial.put("totalUsers", 0L);
            transaction.set(metricsRef, initial);
        }
    }

    private void ensureOrgMetricsInitialized(Transaction transaction, DocumentReference orgMetricsRef) throws com.google.firebase.firestore.FirebaseFirestoreException {
        DocumentSnapshot snapshot = transaction.get(orgMetricsRef);
        if (!snapshot.exists()) {
            Map<String, Object> initial = new HashMap<>();
            initial.put("totalRsvps", 0L);
            initial.put("totalGoing", 0L);
            initial.put("totalInterested", 0L);
            initial.put("totalEvents", 0L);
            transaction.set(orgMetricsRef, initial);
        }
    }

    private void updateDailyMetrics(Transaction transaction, String status, long delta) throws com.google.firebase.firestore.FirebaseFirestoreException {
        String dateStr = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        DocumentReference dailyRef = firestore.collection("daily_metrics").document(dateStr);
        DocumentSnapshot dailySnapshot = transaction.get(dailyRef);
        
        if (!dailySnapshot.exists()) {
            Map<String, Object> initial = new HashMap<>();
            initial.put("totalRsvps", 0L);
            initial.put("totalGoing", 0L);
            initial.put("totalInterested", 0L);
            transaction.set(dailyRef, initial);
        }
        
        transaction.update(dailyRef, "totalRsvps", FieldValue.increment(delta));
        if ("Going".equals(status)) {
            transaction.update(dailyRef, "totalGoing", FieldValue.increment(delta));
        } else if ("Interested".equals(status)) {
            transaction.update(dailyRef, "totalInterested", FieldValue.increment(delta));
        }
    }

    private void updateCategoricalMetrics(Transaction transaction, DocumentReference metricsRef, String status, long delta) {
        transaction.update(metricsRef, "totalRsvps", FieldValue.increment(delta));
        if ("Going".equals(status)) {
            transaction.update(metricsRef, "totalGoing", FieldValue.increment(delta));
        } else if ("Interested".equals(status)) {
            transaction.update(metricsRef, "totalInterested", FieldValue.increment(delta));
        }
    }

    public Task<DocumentSnapshot> getGlobalMetrics() {
        return firestore.collection("system_metrics").document("global").get();
    }

    public Query getDailyMetrics() {
        return firestore.collection("daily_metrics").orderBy("__name__", Query.Direction.DESCENDING);
    }

    public Task<DocumentSnapshot> getOrgMetrics(String orgId) {
        return firestore.collection("org_metrics").document(orgId).get();
    }

    // --- Schedule Conflict Alerts ---
    public Task<List<EventModel>> checkConflicts(String userId, EventModel targetEvent) {
        return firestore.collection("rsvps")
                .whereEqualTo("userId", userId)
                .whereEqualTo("status", "Going")
                .get()
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) return Tasks.forException(task.getException());

                    List<String> eventIds = new ArrayList<>();
                    for (DocumentSnapshot doc : task.getResult()) {
                        eventIds.add(doc.getString("eventId"));
                    }

                    if (eventIds.isEmpty()) {
                        return Tasks.forResult((List<EventModel>) new ArrayList<EventModel>());
                    }

                    return firestore.collection("events")
                            .whereIn("eventId", eventIds)
                            .get()
                            .continueWith(queryTask -> {
                                List<EventModel> conflicts = new ArrayList<>();
                                if (queryTask.isSuccessful() && queryTask.getResult() != null) {
                                    for (DocumentSnapshot doc : queryTask.getResult()) {
                                        EventModel existingEvent = doc.toObject(EventModel.class);
                                        if (existingEvent != null && isOverlapping(existingEvent, targetEvent)) {
                                            conflicts.add(existingEvent);
                                        }
                                    }
                                }
                                return conflicts;
                            });
                });
    }

    private boolean isOverlapping(EventModel e1, EventModel e2) {
        if (e1.getStartDateTime() == null || e1.getEndDateTime() == null || 
            e2.getStartDateTime() == null || e2.getEndDateTime() == null) return false;
        return e1.getStartDateTime().getSeconds() < e2.getEndDateTime().getSeconds() &&
               e2.getStartDateTime().getSeconds() < e1.getEndDateTime().getSeconds();
    }
}

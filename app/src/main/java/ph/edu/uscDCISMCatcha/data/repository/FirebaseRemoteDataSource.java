package ph.edu.uscDCISMCatcha.data.repository;

import android.net.Uri;

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
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

// FIXED: Corrected package path for AnnouncementModel
import ph.edu.uscDCISMCatcha.models.AnnouncementModel;
import ph.edu.uscDCISMCatcha.data.models.EventModel;
import ph.edu.uscDCISMCatcha.data.models.Organization;
import ph.edu.uscDCISMCatcha.data.models.RSVPModel;
import ph.edu.uscDCISMCatcha.data.models.UserModel;
import ph.edu.uscDCISMCatcha.utils.Constants;
import ph.edu.uscDCISMCatcha.utils.DateUtils;

public class FirebaseRemoteDataSource {

    private final FirebaseAuth auth;
    private final FirebaseFirestore firestore;
    private final FirebaseStorage storage;

    public FirebaseRemoteDataSource() {
        this.auth = FirebaseAuth.getInstance();
        this.firestore = FirebaseFirestore.getInstance();
        this.storage = FirebaseStorage.getInstance();
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
        DocumentReference userRef = firestore.collection(Constants.COL_USERS).document(uid);
        DocumentReference metricsRef = firestore.collection(Constants.COL_SYSTEM_METRICS).document(Constants.DOC_GLOBAL_METRICS);

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
        return firestore.collection(Constants.COL_USERS).document(uid).get();
    }

    public Task<String> getUserRole(String uid) {
        return getUserProfile(uid).continueWith(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                return task.getResult().getString("role");
            }
            return null;
        });
    }

    public Task<QuerySnapshot> getOrganizationByOwner(String uid) {
        return firestore.collection(Constants.COL_ORGANIZATIONS)
                .whereEqualTo("ownerUid", uid)
                .limit(1)
                .get();
    }

    public void signOut() {
        auth.signOut();
    }

    // --- Image Upload ---
    public Task<String> uploadImage(Uri imageUri, String folder) {
        String fileName = UUID.randomUUID().toString();
        StorageReference ref = storage.getReference().child(folder + "/" + fileName);
        return ref.putFile(imageUri).continueWithTask(task -> {
            if (!task.isSuccessful()) {
                throw task.getException();
            }
            return ref.getDownloadUrl();
        }).continueWith(task -> task.getResult().toString());
    }

    // --- Organization Management ---
    public Task<Void> createOrganization(String orgId, Organization org) {
        DocumentReference orgRef = firestore.collection(Constants.COL_ORGANIZATIONS).document(orgId);
        DocumentReference metricsRef = firestore.collection(Constants.COL_SYSTEM_METRICS).document(Constants.DOC_GLOBAL_METRICS);
        DocumentReference userRef = firestore.collection(Constants.COL_USERS).document(org.getOwnerUid());

        return firestore.runTransaction(transaction -> {
            ensureMetricsInitialized(transaction, metricsRef);
            transaction.set(orgRef, org);
            transaction.update(userRef, "role", "OrgHandler");
            transaction.update(metricsRef, "totalOrganizations", FieldValue.increment(1));
            return null;
        });
    }


    public Task<Void> createEvent(EventModel event) {
        DocumentReference docRef = firestore.collection(Constants.COL_EVENTS).document();
        event.setEventId(docRef.getId());
        DocumentReference metricsRef = firestore.collection(Constants.COL_SYSTEM_METRICS).document(Constants.DOC_GLOBAL_METRICS);

        return firestore.runTransaction(transaction -> {
            ensureMetricsInitialized(transaction, metricsRef);
            transaction.set(docRef, event);
            transaction.update(metricsRef, "totalEvents", FieldValue.increment(1));

            // Increment org-level event count
            DocumentReference orgMetricsRef = firestore.collection(Constants.COL_ORG_METRICS).document(event.getOrgId());
            ensureOrgMetricsInitialized(transaction, orgMetricsRef);
            transaction.update(orgMetricsRef, "totalEvents", FieldValue.increment(1));

            return null;
        });
    }

    public Task<Void> createAnnouncement(AnnouncementModel announcement) {
        DocumentReference docRef = firestore.collection(Constants.COL_ANNOUNCEMENTS).document();
        announcement.setAnnouncementId(docRef.getId());
        return docRef.set(announcement);
    }

    public Task<Void> rsvpToEvent(RSVPModel rsvp) {
        String rsvpId = rsvp.getUserId() + "_" + rsvp.getEventId();
        DocumentReference rsvpRef = firestore.collection(Constants.COL_RSVPS).document(rsvpId);
        DocumentReference eventRef = firestore.collection(Constants.COL_EVENTS).document(rsvp.getEventId());
        DocumentReference metricsRef = firestore.collection(Constants.COL_SYSTEM_METRICS).document(Constants.DOC_GLOBAL_METRICS);
        rsvp.setRsvpId(rsvpId);

        return firestore.runTransaction(transaction -> {
            ensureMetricsInitialized(transaction, metricsRef);
            DocumentSnapshot rsvpSnapshot = transaction.get(rsvpRef);
            DocumentSnapshot eventSnapshot = transaction.get(eventRef);

            if (!eventSnapshot.exists()) {
                throw new RuntimeException("Event does not exist!");
            }

            String orgId = eventSnapshot.getString("orgId");
            DocumentReference orgMetricsRef = firestore.collection(Constants.COL_ORG_METRICS).document(orgId);
            ensureOrgMetricsInitialized(transaction, orgMetricsRef);

            String oldStatus = rsvpSnapshot.exists() ? rsvpSnapshot.getString("status") : null;
            String newStatus = rsvp.getStatus();

            // Capacity and Count logic for Event
            if (!newStatus.equals(oldStatus)) {
                // Update new status counts
                if (Constants.STATUS_GOING.equals(newStatus)) {
                    long maxCapacity = eventSnapshot.getLong("maxCapacity");
                    long currentCount = eventSnapshot.getLong("currentRsvpCount");
                    if (maxCapacity > 0 && currentCount >= maxCapacity) {
                        throw new RuntimeException("Event is full!");
                    }
                    transaction.update(eventRef, "currentRsvpCount", FieldValue.increment(1));
                } else if (Constants.STATUS_INTERESTED.equals(newStatus)) {
                    transaction.update(eventRef, "interestedCount", FieldValue.increment(1));
                }

                // Update old status counts
                if (Constants.STATUS_GOING.equals(oldStatus)) {
                    transaction.update(eventRef, "currentRsvpCount", FieldValue.increment(-1));
                } else if (Constants.STATUS_INTERESTED.equals(oldStatus)) {
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

                if (Constants.STATUS_GOING.equals(oldStatus)) transaction.update(metricsRef, "totalGoing", FieldValue.increment(-1));
                if (Constants.STATUS_INTERESTED.equals(oldStatus)) transaction.update(metricsRef, "totalInterested", FieldValue.increment(-1));
                if (Constants.STATUS_GOING.equals(newStatus)) transaction.update(metricsRef, "totalGoing", FieldValue.increment(1));
                if (Constants.STATUS_INTERESTED.equals(newStatus)) transaction.update(metricsRef, "totalInterested", FieldValue.increment(1));
            }

            return null;
        });
    }

    public Task<Void> cancelRSVP(String rsvpId, String eventId) {
        DocumentReference rsvpRef = firestore.collection(Constants.COL_RSVPS).document(rsvpId);
        DocumentReference eventRef = firestore.collection(Constants.COL_EVENTS).document(eventId);
        DocumentReference metricsRef = firestore.collection(Constants.COL_SYSTEM_METRICS).document(Constants.DOC_GLOBAL_METRICS);

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
            DocumentReference orgMetricsRef = firestore.collection(Constants.COL_ORG_METRICS).document(orgId);

            // Delete RSVP
            transaction.delete(rsvpRef);

            // Update Event Counts
            if (Constants.STATUS_GOING.equals(status)) {
                transaction.update(eventRef, "currentRsvpCount", FieldValue.increment(-1));
            } else if (Constants.STATUS_INTERESTED.equals(status)) {
                transaction.update(eventRef, "interestedCount", FieldValue.increment(-1));
            }

            // Update Global Metrics
            transaction.update(metricsRef, "totalRsvps", FieldValue.increment(-1));
            if (Constants.STATUS_GOING.equals(status)) transaction.update(metricsRef, "totalGoing", FieldValue.increment(-1));
            if (Constants.STATUS_INTERESTED.equals(status)) transaction.update(metricsRef, "totalInterested", FieldValue.increment(-1));

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
        DocumentReference dailyRef = firestore.collection(Constants.COL_DAILY_METRICS).document(dateStr);
        DocumentSnapshot dailySnapshot = transaction.get(dailyRef);

        if (!dailySnapshot.exists()) {
            Map<String, Object> initial = new HashMap<>();
            initial.put("totalRsvps", 0L);
            initial.put("totalGoing", 0L);
            initial.put("totalInterested", 0L);
            transaction.set(dailyRef, initial);
        }

        transaction.update(dailyRef, "totalRsvps", FieldValue.increment(delta));
        if (Constants.STATUS_GOING.equals(status)) {
            transaction.update(dailyRef, "totalGoing", FieldValue.increment(delta));
        } else if (Constants.STATUS_INTERESTED.equals(status)) {
            transaction.update(dailyRef, "totalInterested", FieldValue.increment(delta));
        }
    }

    private void updateCategoricalMetrics(Transaction transaction, DocumentReference metricsRef, String status, long delta) {
        transaction.update(metricsRef, "totalRsvps", FieldValue.increment(delta));
        if (Constants.STATUS_GOING.equals(status)) {
            transaction.update(metricsRef, "totalGoing", FieldValue.increment(delta));
        } else if (Constants.STATUS_INTERESTED.equals(status)) {
            transaction.update(metricsRef, "totalInterested", FieldValue.increment(delta));
        }
    }

    public Task<DocumentSnapshot> getGlobalMetrics() {
        return firestore.collection(Constants.COL_SYSTEM_METRICS).document(Constants.DOC_GLOBAL_METRICS).get();
    }

    public Query getDailyMetrics() {
        return firestore.collection(Constants.COL_DAILY_METRICS).orderBy("__name__", Query.Direction.DESCENDING);
    }

    public Task<DocumentSnapshot> getOrgMetrics(String orgId) {
        return firestore.collection(Constants.COL_ORG_METRICS).document(orgId).get();
    }

    // --- Schedule Conflict Alerts ---
    public Task<List<EventModel>> checkConflicts(String userId, EventModel targetEvent) {
        return firestore.collection(Constants.COL_RSVPS)
                .whereEqualTo("userId", userId)
                .whereEqualTo("status", Constants.STATUS_GOING)
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

                    return firestore.collection(Constants.COL_EVENTS)
                            .whereIn("eventId", eventIds)
                            .get()
                            .continueWith(queryTask -> {
                                List<EventModel> conflicts = new ArrayList<>();
                                if (queryTask.isSuccessful() && queryTask.getResult() != null) {
                                    for (DocumentSnapshot doc : queryTask.getResult()) {
                                        EventModel existingEvent = doc.toObject(EventModel.class);
                                        if (existingEvent != null && DateUtils.isOverlapping(existingEvent, targetEvent)) {
                                            conflicts.add(existingEvent);
                                        }
                                    }
                                }
                                return conflicts;
                            });
                });
    }
}
package ph.edu.uscDCISMCatcha.viewmodel.org;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import java.util.ArrayList;
import java.util.List;
import ph.edu.uscDCISMCatcha.data.models.EventModel;

public class LeaderEventViewModel extends ViewModel {

    private final MutableLiveData<List<EventModel>> events
            = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<String> statusMessage
            = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading
            = new MutableLiveData<>(false);

    private final FirebaseFirestore db =
            FirebaseFirestore.getInstance();

    public LiveData<List<EventModel>> getEvents() { return events; }
    public LiveData<String> getStatusMessage() { return statusMessage; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }

    // Fetch all events for this org
    public void fetchOrgEvents(String orgId) {
        isLoading.setValue(true);
        db.collection("events")
                .whereEqualTo("orgId", orgId)
                .orderBy("startDateTime", Query.Direction.ASCENDING)
                .addSnapshotListener((snapshots, error) -> {
                    isLoading.setValue(false);
                    if (error != null || snapshots == null) {
                        statusMessage.setValue("Failed to load events");
                        return;
                    }
                    List<EventModel> list = new ArrayList<>();
                    list.addAll(snapshots.toObjects(EventModel.class));
                    events.setValue(list);
                });
    }

    public void createEvent(
            String orgId, String orgName,
            String title, String description,
            String location, String category,
            Timestamp startDateTime, Timestamp endDateTime,
            String university, boolean autoReminders) {

        String userId = FirebaseAuth.getInstance()
                .getCurrentUser() != null
                ? FirebaseAuth.getInstance()
                .getCurrentUser().getUid() : "";

        isLoading.setValue(true);

        EventModel event = new EventModel(
                orgId, orgName, title, description,
                location, category, startDateTime,
                endDateTime, university, null, userId);

        db.collection("events")
                .add(event)
                .addOnSuccessListener(ref -> {
                    isLoading.setValue(false);
                    statusMessage.setValue(
                            "Event created successfully!");

                    if (autoReminders) {
                        scheduleReminders(ref.getId(), title,
                                startDateTime);
                    }
                })
                .addOnFailureListener(e -> {
                    isLoading.setValue(false);
                    statusMessage.setValue(
                            "Failed to create event");
                });
    }

    public void updateEvent(
            String eventId, String title,
            String description, String location,
            Timestamp startDateTime, Timestamp endDateTime) {

        isLoading.setValue(true);

        java.util.Map<String, Object> updates =
                new java.util.HashMap<>();
        updates.put("title",         title);
        updates.put("description",   description);
        updates.put("location",      location);
        updates.put("startDateTime", startDateTime);
        updates.put("endDateTime",   endDateTime);

        db.collection("events")
                .document(eventId)
                .update(updates)
                .addOnSuccessListener(unused -> {
                    isLoading.setValue(false);
                    statusMessage.setValue(
                            "Event updated successfully!");
                })
                .addOnFailureListener(e -> {
                    isLoading.setValue(false);
                    statusMessage.setValue(
                            "Failed to update event");
                });
    }

    public void deleteEvent(String eventId) {
        isLoading.setValue(true);
        db.collection("events")
                .document(eventId)
                .delete()
                .addOnSuccessListener(unused -> {
                    isLoading.setValue(false);
                    statusMessage.setValue(
                            "Event deleted successfully!");
                })
                .addOnFailureListener(e -> {
                    isLoading.setValue(false);
                    statusMessage.setValue(
                            "Failed to delete event");
                });
    }
    private void scheduleReminders(String eventId,
                                   String title,
                                   Timestamp startDateTime) {
        java.util.Map<String, Object> reminder =
                new java.util.HashMap<>();
        reminder.put("eventId",       eventId);
        reminder.put("title",         title);
        reminder.put("startDateTime", startDateTime);
        reminder.put("createdAt",     Timestamp.now());

        db.collection("reminderJobs").add(reminder);
    }

    public void clearStatus() {
        statusMessage.setValue(null);
    }
}
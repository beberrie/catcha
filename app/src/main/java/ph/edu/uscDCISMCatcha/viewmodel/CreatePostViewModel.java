package ph.edu.uscDCISMCatcha.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class CreatePostViewModel extends ViewModel {

    private final MutableLiveData<String> statusMessage = new MutableLiveData<>();
    public LiveData<String> getStatusMessage() { return statusMessage; }

    public void postAnnouncement(String title, String message, boolean sendPush) {
        Map<String, Object> data = new HashMap<>();
        data.put("title", title);
        data.put("message", message);
        data.put("sendPush", sendPush);
        data.put("createdAt", System.currentTimeMillis());
        data.put("type", "announcement");

        FirebaseFirestore.getInstance()
                .collection("announcements")
                .add(data)
                .addOnSuccessListener(ref -> {
                    if (sendPush) {
                        sendPushToFollowers(title, message);
                    }
                    statusMessage.setValue("Announcement sent!");
                })
                .addOnFailureListener(e ->
                        statusMessage.setValue("Failed: " + e.getMessage())
                );
    }

    public void createEvent(String title, String date, String time, String endTime,
                            String location, String description, boolean autoReminders) {
        Map<String, Object> data = new HashMap<>();
        data.put("title", title);
        data.put("date", date);
        data.put("time", time);
        data.put("endTime", endTime);
        data.put("location", location);
        data.put("description", description);
        data.put("autoReminders", autoReminders);
        data.put("createdAt", System.currentTimeMillis());
        data.put("type", "event");

        FirebaseFirestore.getInstance()
                .collection("events")
                .add(data)
                .addOnSuccessListener(ref -> {
                    if (autoReminders) {
                        scheduleReminders(ref.getId(), title, date, time);
                    }
                    statusMessage.setValue("Event created!");
                })
                .addOnFailureListener(e ->
                        statusMessage.setValue("Failed: " + e.getMessage())
                );
    }

    private void sendPushToFollowers(String title, String message) {
        // Trigger your Cloud Function via Firestore write
        // The Cloud Function listens to this collection and sends FCM
        Map<String, Object> pushJob = new HashMap<>();
        pushJob.put("title", title);
        pushJob.put("body", message);
        pushJob.put("type", "announcement");
        pushJob.put("createdAt", System.currentTimeMillis());

        FirebaseFirestore.getInstance()
                .collection("pushJobs")
                .add(pushJob);
    }

    private void scheduleReminders(String eventId, String title, String date, String time) {
        // Trigger your Cloud Function to schedule 24h and 1h reminders
        Map<String, Object> reminderJob = new HashMap<>();
        reminderJob.put("eventId", eventId);
        reminderJob.put("title", title);
        reminderJob.put("date", date);
        reminderJob.put("time", time);
        reminderJob.put("createdAt", System.currentTimeMillis());

        FirebaseFirestore.getInstance()
                .collection("reminderJobs")
                .add(reminderJob);
    }
}

package ph.edu.uscDCISMCatcha.viewmodel.org;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ph.edu.uscDCISMCatcha.data.repository.FirebaseRemoteDataSource;
import ph.edu.uscDCISMCatcha.data.models.EventModel;
import ph.edu.uscDCISMCatcha.data.models.AnnouncementModel;

public class CreatePostViewModel extends ViewModel {

    private final FirebaseRemoteDataSource dataSource;
    private final FirebaseFirestore db;
    private final MutableLiveData<String> statusMessage = new MutableLiveData<>();
    private final MutableLiveData<AnnouncementModel> existingAnnouncement = new MutableLiveData<>();
    private final MutableLiveData<EventModel> existingEvent = new MutableLiveData<>();
    private final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.getDefault());

    public CreatePostViewModel() {
        this.dataSource = new FirebaseRemoteDataSource();
        this.db = FirebaseFirestore.getInstance();
    }

    public LiveData<String> getStatusMessage() { return statusMessage; }
    public LiveData<AnnouncementModel> getExistingAnnouncement() { return existingAnnouncement; }
    public LiveData<EventModel> getExistingEvent() { return existingEvent; }

    public void fetchAnnouncement(String id) {
        db.collection("announcements").document(id).get().addOnSuccessListener(doc -> {
            if (doc.exists()) {
                existingAnnouncement.setValue(doc.toObject(AnnouncementModel.class));
            }
        });
    }

    public void fetchEvent(String id) {
        db.collection("events").document(id).get().addOnSuccessListener(doc -> {
            if (doc.exists()) {
                existingEvent.setValue(doc.toObject(EventModel.class));
            }
        });
    }

    public void postAnnouncement(String title, String message, boolean sendPush) {
        if (!dataSource.isUserLoggedIn()) {
            statusMessage.setValue("Error: User not logged in");
            return;
        }

        AnnouncementModel announcement = new AnnouncementModel(title, message, dataSource.getCurrentUser().getUid());
        db.collection("announcements").add(announcement).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                statusMessage.setValue("Announcement created successfully!");
            } else {
                statusMessage.setValue("Error: " + task.getException().getMessage());
            }
        });
    }

    public void updateAnnouncement(String id, String title, String message) {
        db.collection("announcements").document(id)
                .update("title", title, "content", message)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        statusMessage.setValue("Announcement updated successfully!");
                    } else {
                        statusMessage.setValue("Error: " + task.getException().getMessage());
                    }
                });
    }

    public void createEvent(String title, String date, String time, String endTime,
                            String location, String description, int capacity, boolean autoReminders) {
        
        if (!dataSource.isUserLoggedIn()) {
            statusMessage.setValue("Error: User not logged in");
            return;
        }

        try {
            Date startD = dateTimeFormat.parse(date + " " + time);
            Date endD = dateTimeFormat.parse(date + " " + endTime);

            EventModel event = new EventModel();
            event.setTitle(title);
            event.setDescription(description);
            event.setLocation(location);
            event.setStartDateTime(new Timestamp(startD));
            event.setEndDateTime(new Timestamp(endD));
            event.setMaxCapacity(capacity);
            event.setCurrentRsvpCount(0);
            event.setOrgId(dataSource.getCurrentUser().getUid());
            event.setOrgName("Organization"); // Should ideally be fetched
            event.setCreatedBy(dataSource.getCurrentUser().getUid());

            dataSource.createEvent(event).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    statusMessage.setValue("Event created successfully!");
                } else {
                    statusMessage.setValue("Error: " + task.getException().getMessage());
                }
            });

        } catch (ParseException e) {
            statusMessage.setValue("Error parsing date/time");
        }
    }

    public void updateEvent(String id, String title, String date, String time, String endTime,
                            String location, String description, int capacity) {
        try {
            Date startD = dateTimeFormat.parse(date + " " + time);
            Date endD = dateTimeFormat.parse(date + " " + endTime);

            db.collection("events").document(id)
                    .update("title", title,
                            "startDateTime", new Timestamp(startD),
                            "endDateTime", new Timestamp(endD),
                            "location", location,
                            "description", description,
                            "maxCapacity", capacity)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            statusMessage.setValue("Event updated successfully!");
                        } else {
                            statusMessage.setValue("Error: " + task.getException().getMessage());
                        }
                    });
        } catch (ParseException e) {
            statusMessage.setValue("Error parsing date/time");
        }
    }

    public void clearStatus() {
        statusMessage.setValue(null);
    }
}

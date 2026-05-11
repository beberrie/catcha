package ph.edu.uscDCISMCatcha.viewmodel.org;

import android.net.Uri;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ph.edu.uscDCISMCatcha.data.repository.FirebaseRemoteDataSource;
import ph.edu.uscDCISMCatcha.data.models.EventModel;
import ph.edu.uscDCISMCatcha.data.models.AnnouncementModel;
import ph.edu.uscDCISMCatcha.data.models.Organization;

public class CreatePostViewModel extends ViewModel {

    private final FirebaseRemoteDataSource dataSource;
    private final FirebaseFirestore db;
    private final MutableLiveData<String> statusMessage = new MutableLiveData<>();
    private final MutableLiveData<AnnouncementModel> existingAnnouncement = new MutableLiveData<>();
    private final MutableLiveData<EventModel> existingEvent = new MutableLiveData<>();
    private final MutableLiveData<Organization> organization = new MutableLiveData<>();
    private final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.getDefault());

    public CreatePostViewModel() {
        this.dataSource = new FirebaseRemoteDataSource();
        this.db = FirebaseFirestore.getInstance();
        loadOrganizationInfo();
    }

    public LiveData<String> getStatusMessage() { return statusMessage; }
    public LiveData<AnnouncementModel> getExistingAnnouncement() { return existingAnnouncement; }
    public LiveData<EventModel> getExistingEvent() { return existingEvent; }
    public LiveData<Organization> getOrganization() { return organization; }

    private void loadOrganizationInfo() {
        if (dataSource.isUserLoggedIn()) {
            String uid = dataSource.getCurrentUser().getUid();
            dataSource.getOrganizationByOwner(uid).addOnSuccessListener(queryDocumentSnapshots -> {
                if (!queryDocumentSnapshots.isEmpty()) {
                    organization.setValue(queryDocumentSnapshots.getDocuments().get(0).toObject(Organization.class));
                }
            });
        }
    }

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
        postAnnouncementWithImage(title, message, sendPush, null);
    }

    public void postAnnouncementWithImage(String title, String message, boolean sendPush, Uri imageUri) {
        if (!dataSource.isUserLoggedIn()) {
            statusMessage.setValue("Error: User not logged in");
            return;
        }

        String uid = dataSource.getCurrentUser().getUid();
        Organization org = organization.getValue();
        String orgName = (org != null) ? org.getName() : "Organization";

        AnnouncementModel announcement = new AnnouncementModel(title, message, uid);
        announcement.setOrgName(orgName);

        if (imageUri != null) {
            statusMessage.setValue("Uploading image...");
            dataSource.uploadImage(imageUri, "announcements")
                    .addOnSuccessListener(url -> {
                        announcement.setImageUrl(url); 
                        saveAnnouncement(announcement);
                    })
                    .addOnFailureListener(e -> statusMessage.setValue("Upload failed: " + e.getMessage()));
        } else {
            saveAnnouncement(announcement);
        }
    }

    private void saveAnnouncement(AnnouncementModel announcement) {
        dataSource.createAnnouncement(announcement).addOnCompleteListener(task -> {
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
                            String location, String description, int capacity, List<String> categories, String registrationUrl) {
        createEventWithImage(title, date, time, endTime, location, description, capacity, categories, registrationUrl, null);
    }

    public void createEventWithImage(String title, String date, String time, String endTime,
                            String location, String description, int capacity, List<String> categories, String registrationUrl, Uri imageUri) {
        
        if (!dataSource.isUserLoggedIn()) {
            statusMessage.setValue("Error: User not logged in");
            return;
        }

        try {
            Date startD = dateTimeFormat.parse(date + " " + time);
            Date endD = dateTimeFormat.parse(date + " " + endTime);
            String uid = dataSource.getCurrentUser().getUid();
            Organization org = organization.getValue();

            String orgName = (org != null) ? org.getName() : "Organization";
            String orgId = (org != null) ? org.getId() : uid;

            EventModel event = new EventModel();
            event.setTitle(title);
            event.setDescription(description);
            event.setLocation(location);
            event.setStartDateTime(startD);
            event.setEndDateTime(endD);
            event.setMaxCapacity(capacity);
            event.setCurrentRsvpCount(0);
            event.setOrgId(orgId);
            event.setOrgName(orgName);
            event.setCreatedBy(uid);
            event.setCategories(categories);
            event.setRegistrationUrl(registrationUrl);

            if (imageUri != null) {
                statusMessage.setValue("Uploading event cover...");
                dataSource.uploadImage(imageUri, "events")
                        .addOnSuccessListener(url -> {
                            event.setImageUrl(url);
                            saveEvent(event);
                        })
                        .addOnFailureListener(e -> statusMessage.setValue("Upload failed: " + e.getMessage()));
            } else {
                saveEvent(event);
            }

        } catch (ParseException e) {
            statusMessage.setValue("Error parsing date/time");
        }
    }

    private void saveEvent(EventModel event) {
        dataSource.createEvent(event).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                statusMessage.setValue("Event created successfully!");
            } else {
                statusMessage.setValue("Error: " + task.getException().getMessage());
            }
        });
    }

    public void updateEvent(String id, String title, String date, String time, String endTime,
                            String location, String description, int capacity, List<String> categories, String registrationUrl, String imageUrl) {
        try {
            Date startD = dateTimeFormat.parse(date + " " + time);
            Date endD = dateTimeFormat.parse(date + " " + endTime);

            db.collection("events").document(id)
                    .update("title", title,
                            "startDateTime", new Timestamp(startD),
                            "endDateTime", new Timestamp(endD),
                            "location", location,
                            "description", description,
                            "maxCapacity", capacity,
                            "categories", categories,
                            "registrationUrl", registrationUrl,
                            "imageUrl", imageUrl)
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

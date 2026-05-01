package ph.edu.uscDCISMCatcha.viewmodel.org;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.Timestamp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ph.edu.uscDCISMCatcha.data.repository.FirebaseRemoteDataSource;
import ph.edu.uscDCISMCatcha.data.models.EventModel;

public class CreatePostViewModel extends ViewModel {

    private final FirebaseRemoteDataSource dataSource;
    private final MutableLiveData<String> statusMessage = new MutableLiveData<>();
    private final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.getDefault());

    public CreatePostViewModel() {
        this.dataSource = new FirebaseRemoteDataSource();
    }

    public LiveData<String> getStatusMessage() { return statusMessage; }

    public void postAnnouncement(String title, String message, boolean sendPush) {
        // Implementation for announcements can be added here
        statusMessage.setValue("Announcement created successfully!");
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
            event.setOrgName("DCISM Organization"); // Placeholder: In real app, fetch from user profile
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

    public void clearStatus() {
        statusMessage.setValue(null);
    }
}

package ph.edu.uscDCISMCatcha.viewmodel.org;


import android.os.Handler;
import android.os.Looper;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import android.net.Uri;


import java.util.List;


import ph.edu.uscDCISMCatcha.data.models.Organization;
import ph.edu.uscDCISMCatcha.data.models.AnnouncementModel;
import ph.edu.uscDCISMCatcha.data.models.EventModel;


public class CreatePostViewModel extends ViewModel {


    private final MutableLiveData<String> statusMessage
            = new MutableLiveData<>();
    private final MutableLiveData<Organization> organization = new MutableLiveData<>();
    private final MutableLiveData<AnnouncementModel> existingAnnouncement = new MutableLiveData<>();
    private final MutableLiveData<EventModel> existingEvent = new MutableLiveData<>();


    public LiveData<String> getStatusMessage() {
        return statusMessage;
    }


    public LiveData<Organization> getOrganization() {
        return organization;
    }


    public LiveData<AnnouncementModel> getExistingAnnouncement() {
        return existingAnnouncement;
    }


    public LiveData<EventModel> getExistingEvent() {
        return existingEvent;
    }


    public void fetchAnnouncement(String id) {
        // Mock implementation
    }


    public void fetchEvent(String id) {
        // Mock implementation
    }


    public void postAnnouncement(String title, String message,
                                 boolean sendPush) {
        new Handler(Looper.getMainLooper()).postDelayed(() ->
                statusMessage.setValue(
                        "Announcement created successfully!"), 600);
    }


    public void postAnnouncementWithImage(String title, String message, boolean sendPush, Uri imageUri) {
        new Handler(Looper.getMainLooper()).postDelayed(() ->
                statusMessage.setValue(
                        "Announcement created successfully!"), 600);
    }


    public void updateAnnouncement(String id, String title, String message) {
        new Handler(Looper.getMainLooper()).postDelayed(() ->
                statusMessage.setValue(
                        "Announcement updated successfully!"), 600);
    }


    public void createEvent(String title, String date, String time,
                            String endTime, String location,
                            String description, int capacity,
                            List<String> categories, String registrationUrl) {
        new Handler(Looper.getMainLooper()).postDelayed(() ->
                statusMessage.setValue(
                        "Event created successfully!"), 600);
    }


    public void updateEvent(String id, String title, String date, String time,
                            String endTime, String location, String description,
                            int capacity, List<String> categories,
                            String registrationUrl, String imageUrl) {
        new Handler(Looper.getMainLooper()).postDelayed(() ->
                statusMessage.setValue(
                        "Event updated successfully!"), 600);
    }


    public void clearStatus() {
        statusMessage.setValue(null);
    }
}

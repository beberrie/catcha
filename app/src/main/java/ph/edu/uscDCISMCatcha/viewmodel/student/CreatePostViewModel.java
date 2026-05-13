package ph.edu.uscDCISMCatcha.viewmodel.student;

import android.os.Handler;
import android.os.Looper;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CreatePostViewModel extends ViewModel {

    private final MutableLiveData<String> statusMessage
            = new MutableLiveData<>();

    public LiveData<String> getStatusMessage() {
        return statusMessage;
    }

    public void postAnnouncement(String title, String message,
                                 boolean sendPush) {
        new Handler(Looper.getMainLooper()).postDelayed(() ->
                statusMessage.setValue(
                        "Announcement created successfully!"), 600);
    }

    public void createEvent(String title, String date, String time,
                            String endTime, String location,
                            String description,
                            boolean autoReminders) {
        new Handler(Looper.getMainLooper()).postDelayed(() ->
                statusMessage.setValue(
                        "Event created successfully!"), 600);
    }

    public void clearStatus() {
        statusMessage.setValue(null);
    }
}
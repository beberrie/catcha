package ph.edu.uscDCISMCatcha.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.google.firebase.Timestamp;
import ph.edu.uscDCISMCatcha.models.NotificationModel;

public class SharedAnnouncementViewModel extends ViewModel {

    private final MutableLiveData<NotificationModel> newAnnouncement
            = new MutableLiveData<>();

    public LiveData<NotificationModel> getNewAnnouncement() {
        return newAnnouncement;
    }

    public void broadcastAnnouncement(
            String title,
            String message,
            String postedByName,
            String postedByPosition,
            String orgName,
            int followersCount) {

        NotificationModel n = new NotificationModel();
        n.setId("notif_" + System.currentTimeMillis());
        n.setType(NotificationModel.Type.ANNOUNCEMENT);
        n.setUrgency(NotificationModel.Urgency.NORMAL);
        n.setOrgName(orgName);
        n.setTitle(title);
        n.setContent(message);
        n.setRead(false);
        n.setFollowersCount(followersCount);
        n.setPostedByName(postedByName);
        n.setPostedByPosition(postedByPosition);
        n.setCreatedAt(Timestamp.now());

        java.text.SimpleDateFormat sdf =
                new java.text.SimpleDateFormat(
                        "h:mm a", java.util.Locale.getDefault());
        n.setSentTime(sdf.format(new java.util.Date()));

        newAnnouncement.setValue(n);
    }

    public void clearNewAnnouncement() {
        newAnnouncement.setValue(null);
    }
}
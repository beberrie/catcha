package ph.edu.uscDCISMCatcha.viewmodel;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import ph.edu.uscDCISMCatcha.data.models.NotificationModel;


public class SharedAnnouncementViewModel extends ViewModel {


    // Holds ALL announcements created this session
    private final MutableLiveData<List<NotificationModel>>
            announcementList = new MutableLiveData<>(new ArrayList<>());


    // Holds only the LATEST one for banner pop-up
    private final MutableLiveData<NotificationModel>
            newAnnouncement = new MutableLiveData<>();


    public LiveData<List<NotificationModel>> getAnnouncementList() {
        return announcementList;
    }


    public LiveData<NotificationModel> getNewAnnouncement() {
        return newAnnouncement;
    }


    // Called from CreatePostFragment when Broadcast is tapped
    public void broadcastAnnouncement(
            String title,
            String content,
            String orgName,
            String postedByName,
            String postedByPosition,
            int followersCount) {


        // Build the notification model from real input
        NotificationModel n = new NotificationModel();
        n.setId("notif_" + System.currentTimeMillis());
        n.setType(NotificationModel.Type.ANNOUNCEMENT);
        n.setUrgency(NotificationModel.Urgency.NORMAL);
        n.setOrgName(orgName);
        n.setTitle(title);
        n.setContent(content);
        n.setRead(false);
        n.setFollowersCount(followersCount);
        n.setPostedByName(postedByName);
        n.setPostedByPosition(postedByPosition);


        // Format current time as sent time
        SimpleDateFormat sdf = new SimpleDateFormat(
                "h:mm a", Locale.getDefault());
        n.setSentTime(sdf.format(new Date()));


        // Add to the full list (insert at top)
        List<NotificationModel> current =
                announcementList.getValue();
        if (current == null) current = new ArrayList<>();
        current.add(0, n);
        announcementList.setValue(current);


        // Also set as latest for banner trigger
        newAnnouncement.setValue(n);
    }


    // Called after NotificationFragment consumes the banner
    public void clearNewAnnouncement() {
        newAnnouncement.setValue(null);
    }


    // Mark one as read
    public void markAsRead(String id) {
        List<NotificationModel> current =
                announcementList.getValue();
        if (current == null) return;
        for (NotificationModel item : current) {
            if (item.getId().equals(id)) {
                item.setRead(true);
                break;
            }
        }
        announcementList.setValue(current);
    }


    // Mark all as read
    public void markAllAsRead() {
        List<NotificationModel> current =
                announcementList.getValue();
        if (current == null) return;
        for (NotificationModel item : current) {
            item.setRead(true);
        }
        announcementList.setValue(current);
    }
}

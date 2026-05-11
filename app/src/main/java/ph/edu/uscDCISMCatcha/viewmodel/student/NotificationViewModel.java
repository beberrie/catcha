package ph.edu.uscDCISMCatcha.viewmodel;

import android.os.Handler;
import android.os.Looper;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.google.firebase.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

// FIXED IMPORTS
import ph.edu.uscDCISMCatcha.data.models.EventModel;
import ph.edu.uscDCISMCatcha.models.AnnouncementModel;
import ph.edu.uscDCISMCatcha.models.NotificationModel;

public class NotificationViewModel extends ViewModel {

    private final MutableLiveData<List<NotificationModel>> notifications
            = new MutableLiveData<>();

    public LiveData<List<NotificationModel>> getNotifications() {
        return notifications;
    }

    public void loadDummyNotifications() {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            List<NotificationModel> list = new ArrayList<>();

            // Announcement 1 - unread
            AnnouncementModel ann1 = new AnnouncementModel();
            ann1.setTitle("No classes on Wednesday to Friday");
            ann1.setContent("In observance of Holy Week, there will be no class from Wednesday to Friday.");
            ann1.setAuthorUid("user_001");
            ann1.setTimestamp(new Timestamp(new Date(System.currentTimeMillis() - 1000 * 60 * 2)));

            NotificationModel n1 = NotificationModel.fromAnnouncement(
                    "notif_001", ann1,
                    "Don Joseph Gesta", "CISCO President", 312);
            n1.setSentTime("2:15 PM");
            list.add(n1);

            // Announcement 2 - read
            AnnouncementModel ann2 = new AnnouncementModel();
            ann2.setTitle("Reminder: CCS Week starts tomorrow!");
            ann2.setContent("Make sure to attend the opening ceremony.");
            ann2.setAuthorUid("user_001");
            ann2.setTimestamp(new Timestamp(new Date(System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 5)));

            NotificationModel n2 = NotificationModel.fromAnnouncement(
                    "notif_002", ann2,
                    "Don Joseph Gesta", "CISCO President", 312);
            n2.setSentTime("9:00 AM");
            n2.setRead(true);
            list.add(n2);

            // Event 1 - 1 hour
            // Note: Adjusting constructor to match the EventModel structure
            EventModel ev1 = new EventModel();
            ev1.setTitle("CCS Week: Day 1 Opening");
            ev1.setDescription("Join us for the opening ceremony of CCS Week.");
            ev1.setLocation("Gymnasium, Bldg A");
            ev1.setUniversity("USC");
            // Assuming your EventModel handles Timestamps for Notification logic:
            // ev1.setStartDateTime(...)

            NotificationModel n3 = NotificationModel.fromEvent("notif_003", ev1, 312);
            list.add(n3);

            notifications.setValue(list);
        }, 300);
    }

    public void markAsRead(String notificationId) {
        // TODO: connect to Firestore later
    }

    public void markAllAsRead() {
        // TODO: connect to Firestore later
    }
}
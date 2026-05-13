package ph.edu.uscDCISMCatcha.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.ArrayList;
import java.util.List;
import ph.edu.uscDCISMCatcha.models.NotificationModel;

public class NotificationViewModel extends ViewModel {

    private final MutableLiveData<List<NotificationModel>> notifications = new MutableLiveData<>(new ArrayList<>());

    public LiveData<List<NotificationModel>> getNotifications() {
        return notifications;
    }

    /**
     * Adds a new notification to the top of the local list.
     */
    public void addNotification(NotificationModel newNotif) {
        List<NotificationModel> currentList = notifications.getValue();
        List<NotificationModel> updatedList = new ArrayList<>();

        if (currentList != null) {
            updatedList.addAll(currentList);
        }

        updatedList.add(0, newNotif); // Add to top
        notifications.setValue(updatedList);
    }

    /**
     * Updates a specific notification's status to 'read' locally.
     */
    public void markAsRead(String notificationId) {
        List<NotificationModel> currentList = notifications.getValue();
        if (currentList != null) {
            List<NotificationModel> updatedList = new ArrayList<>(currentList);
            for (NotificationModel notification : updatedList) {
                // Ensure notificationId matches your Model's getter
                if (notification.getNotificationId().equals(notificationId)) {
                    notification.setRead(true);
                    break;
                }
            }
            notifications.setValue(updatedList);
        }
    }

    public void markAllAsRead() {
        List<NotificationModel> currentList = notifications.getValue();
        if (currentList != null) {
            List<NotificationModel> updatedList = new ArrayList<>(currentList);
            for (NotificationModel notification : updatedList) {
                notification.setRead(true);
            }
            notifications.setValue(updatedList);
        }
    }
}
package ph.edu.uscDCISMCatcha.viewmodel.student;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import ph.edu.uscDCISMCatcha.data.repository.FirebaseRemoteDataSource;
import ph.edu.uscDCISMCatcha.data.models.EventModel;
import ph.edu.uscDCISMCatcha.data.models.RSVPModel;

public class EventViewModel extends ViewModel {

    private final FirebaseRemoteDataSource dataSource;
    
    private final MutableLiveData<String> _rsvpStatus = new MutableLiveData<>();
    public LiveData<String> rsvpStatus = _rsvpStatus;

    private final MutableLiveData<List<EventModel>> _conflicts = new MutableLiveData<>();
    public LiveData<List<EventModel>> conflicts = _conflicts;

    public EventViewModel() {
        this.dataSource = new FirebaseRemoteDataSource();
    }

    public void rsvpToEvent(EventModel event, String status) {
        if (!dataSource.isUserLoggedIn()) {
            _rsvpStatus.setValue("Error: User not logged in");
            return;
        }

        String userId = dataSource.getCurrentUser().getUid();
        RSVPModel rsvp = new RSVPModel(userId, event.getEventId(), event.getTitle(), status);

        // First check for conflicts if status is "Going"
        if ("Going".equals(status)) {
            dataSource.checkConflicts(userId, event).addOnCompleteListener(task -> {
                if (task.isSuccessful() && !task.getResult().isEmpty()) {
                    _conflicts.setValue(task.getResult());
                } else {
                    // No conflicts or error checking conflicts, proceed with RSVP
                    performRSVP(rsvp);
                }
            });
        } else {
            performRSVP(rsvp);
        }
    }

    private void performRSVP(RSVPModel rsvp) {
        dataSource.rsvpToEvent(rsvp).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                _rsvpStatus.setValue("RSVP Successful!");
            } else {
                _rsvpStatus.setValue("Error: " + task.getException().getMessage());
            }
        });
    }
    
    public void clearConflictState() {
        _conflicts.setValue(null);
    }
}

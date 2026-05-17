package ph.edu.uscDCISMCatcha.ui.student;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ph.edu.uscDCISMCatcha.R;
import ph.edu.uscDCISMCatcha.data.models.EventModel;
import ph.edu.uscDCISMCatcha.ui.student.NotificationFragment;
import ph.edu.uscDCISMCatcha.ui.adapters.EventAdapter;
import ph.edu.uscDCISMCatcha.ui.chat.ChatBotFragment;
import ph.edu.uscDCISMCatcha.databinding.FragmentEventsBinding;

public class EventFragment extends Fragment implements EventAdapter.OnEventClickListener {

    private FragmentEventsBinding binding;
    private EventAdapter adapter;
    private List<EventModel> allEvents = new ArrayList<>();
    private List<EventModel> filteredEvents = new ArrayList<>();
    private List<String> joinedEventIds = new ArrayList<>();
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentEventsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        binding.rvEvents.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new EventAdapter(filteredEvents, this);
        binding.rvEvents.setAdapter(adapter);

        // Set default text
        binding.tvSelectedDate.setText(dateFormat.format(new Date()));

        binding.calendarView.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
            Calendar selected = Calendar.getInstance();
            selected.set(year, month, dayOfMonth);
            updateFilteredEvents(selected.getTime());
        });

        setupHeader();
        setupChatBot();
        loadJoinedEvents();
        loadAllEvents();
    }

    private void setupHeader() {
        binding.header.ivSearchHeader.setOnClickListener(v -> navigateTo(new SearchFragment()));
        
        binding.header.ivNotificationsHeader.setOnClickListener(v -> navigateTo(new NotificationFragment()));

        binding.header.ivUserAvatarHeader.setOnClickListener(v -> navigateTo(new UserProfileFragment()));
    }

    private void setupChatBot() {
        binding.fabChatBot.setOnClickListener(v -> navigateTo(new ChatBotFragment()));
    }

    private void loadJoinedEvents() {
        if (auth.getCurrentUser() == null) return;

        db.collection("rsvps")
                .whereEqualTo("userId", auth.getCurrentUser().getUid())
                .whereEqualTo("status", "Going")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    joinedEventIds.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        joinedEventIds.add(doc.getString("eventId"));
                    }
                    adapter.setJoinedEventIds(joinedEventIds);
                });
    }

    private void loadAllEvents() {
        binding.progressBar.setVisibility(View.VISIBLE);
        db.collection("events").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (binding == null) return;
                    allEvents.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        EventModel event = doc.toObject(EventModel.class);
                        event.setEventId(doc.getId());
                        allEvents.add(event);
                    }
                    binding.progressBar.setVisibility(View.GONE);
                    // Filter for today by default
                    updateFilteredEvents(new Date(binding.calendarView.getDate()));
                })
                .addOnFailureListener(e -> {
                    if (binding == null) return;
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Failed to load events", Toast.LENGTH_SHORT).show();
                });
    }

    private void updateFilteredEvents(Date date) {
        binding.tvSelectedDate.setText(dateFormat.format(date));
        filteredEvents.clear();

        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date);

        for (EventModel event : allEvents) {
            if (event.getStartDateTime() != null) {
                Calendar cal2 = Calendar.getInstance();
                cal2.setTime(event.getStartDateTime().toDate());

                if (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                    cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)) {
                    filteredEvents.add(event);
                }
            }
        }

        if (filteredEvents.isEmpty()) {
            binding.tvNoEvents.setVisibility(View.VISIBLE);
            binding.rvEvents.setVisibility(View.GONE);
        } else {
            binding.tvNoEvents.setVisibility(View.GONE);
            binding.rvEvents.setVisibility(View.VISIBLE);
        }
        adapter.notifyDataSetChanged();
    }

    private void navigateTo(Fragment fragment) {
        getParentFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onEventClick(EventModel event) {
        Intent intent = new Intent(getActivity(), EventDetailsActivity.class);
        intent.putExtra("EVENT_ID", event.getEventId());
        intent.putExtra("EVENT_DATA", event);
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

package ph.edu.uscDCISMCatcha.ui.student;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import ph.edu.uscDCISMCatcha.ui.adapters.EventAdapter;
import ph.edu.uscDCISMCatcha.ui.chat.ChatBotFragment;

public class EventFragment extends Fragment implements EventAdapter.OnEventClickListener {

    private CalendarView calendarView;
    private RecyclerView rvEvents;
    private TextView tvSelectedDate, tvNoEvents;
    private ProgressBar progressBar;
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
        View view = inflater.inflate(R.layout.fragment_events, container, false);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        calendarView = view.findViewById(R.id.calendarView);
        rvEvents = view.findViewById(R.id.rvEvents);
        tvSelectedDate = view.findViewById(R.id.tvSelectedDate);
        tvNoEvents = view.findViewById(R.id.tvNoEvents);
        progressBar = view.findViewById(R.id.progressBar);

        rvEvents.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new EventAdapter(filteredEvents, this);
        rvEvents.setAdapter(adapter);

        // Set default text
        tvSelectedDate.setText(dateFormat.format(new Date()));

        calendarView.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
            Calendar selected = Calendar.getInstance();
            selected.set(year, month, dayOfMonth);
            updateFilteredEvents(selected.getTime());
        });

        loadJoinedEvents();
        loadAllEvents();
        setupChatBot(view);

        return view;
    }

    private void setupChatBot(View view) {
        FloatingActionButton fabChatBot = view.findViewById(R.id.fabChatBot);
        if (fabChatBot != null) {
            fabChatBot.setOnClickListener(v -> {
                getParentFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                        .replace(R.id.fragment_container, new ChatBotFragment())
                        .addToBackStack(null)
                        .commit();
            });
        }
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
        progressBar.setVisibility(View.VISIBLE);
        db.collection("events").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    allEvents.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        EventModel event = doc.toObject(EventModel.class);
                        event.setEventId(doc.getId());
                        allEvents.add(event);
                    }
                    progressBar.setVisibility(View.GONE);
                    // Filter for today by default
                    updateFilteredEvents(new Date(calendarView.getDate()));
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Failed to load events", Toast.LENGTH_SHORT).show();
                });
    }

    private void updateFilteredEvents(Date date) {
        tvSelectedDate.setText(dateFormat.format(date));
        filteredEvents.clear();

        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date);

        for (EventModel event : allEvents) {
            if (event.getStartDateTime() != null) {
                Calendar cal2 = Calendar.getInstance();
                cal2.setTime(event.getStartDateTime());

                if (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                    cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)) {
                    filteredEvents.add(event);
                }
            }
        }

        if (filteredEvents.isEmpty()) {
            tvNoEvents.setVisibility(View.VISIBLE);
            rvEvents.setVisibility(View.GONE);
        } else {
            tvNoEvents.setVisibility(View.GONE);
            rvEvents.setVisibility(View.VISIBLE);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onEventClick(EventModel event) {
        Intent intent = new Intent(getActivity(), EventDetailsActivity.class);
        intent.putExtra("EVENT_ID", event.getEventId());
        intent.putExtra("EVENT_DATA", event);
        startActivity(intent);
    }
}
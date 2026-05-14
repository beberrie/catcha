package ph.edu.uscDCISMCatcha.ui.student;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import ph.edu.uscDCISMCatcha.R;
import ph.edu.uscDCISMCatcha.adapters.CommonGroundAdapter;
import ph.edu.uscDCISMCatcha.data.models.EventModel;
import ph.edu.uscDCISMCatcha.databinding.ItemTrendingCardBinding;
import ph.edu.uscDCISMCatcha.databinding.OtherEventsCardBinding;
import ph.edu.uscDCISMCatcha.databinding.TrendingEventsBinding;
import ph.edu.uscDCISMCatcha.ui.chat.ChatBotFragment;
import ph.edu.uscDCISMCatcha.ui.student.NotificationFragment;
import ph.edu.uscDCISMCatcha.utils.Constants;

public class TrendingEventsFragment extends Fragment {

    private TrendingEventsBinding binding;
    private FirebaseFirestore db;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = TrendingEventsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();

        setupHeader();
        setupChatBot();
        fetchEvents();
    }

    private void setupHeader() {
        binding.header.ivSearchHeader.setOnClickListener(v -> navigateTo(new SearchFragment()));
        binding.header.ivNotificationsHeader.setOnClickListener(v -> navigateTo(new NotificationFragment()));
        binding.header.ivUserAvatarHeader.setOnClickListener(v -> navigateTo(new UserProfileFragment()));
    }

    private void setupChatBot() {
        binding.fabChatBot.setOnClickListener(v -> navigateTo(new ChatBotFragment()));
    }

    private void fetchEvents() {
        db.collection("events").get()
                .addOnSuccessListener(querySnapshots -> {
                    if (binding == null) return;
                    
                    List<EventModel> allEvents = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : querySnapshots) {
                        EventModel event = doc.toObject(EventModel.class);
                        event.setEventId(doc.getId());
                        allEvents.add(event);
                    }

                    // Sort by popularity: (RSVP * 2) + Interested
                    allEvents.sort((a, b) -> {
                        int scoreA = (a.getCurrentRsvpCount() * 2) + a.getInterestedCount();
                        int scoreB = (b.getCurrentRsvpCount() * 2) + b.getInterestedCount();
                        return Integer.compare(scoreB, scoreA);
                    });

                    populateUI(allEvents);
                });
    }

    private void populateUI(List<EventModel> events) {
        if (binding == null) return;

        binding.llFeaturedContainer.removeAllViews();
        binding.llEventCards.removeAllViews();

        // Top 5 or less for Trending
        int trendingCount = Math.min(events.size(), 5);
        for (int i = 0; i < trendingCount; i++) {
            addTrendingCard(events.get(i));
        }

        // The rest for Other Events
        for (int j = trendingCount; j < events.size(); j++) {
            addOtherEventCard(events.get(j));
        }
        
        // If no other events, hide the label
        binding.tvOtherEvents.setVisibility(events.size() > trendingCount ? View.VISIBLE : View.GONE);
    }

    private void addTrendingCard(EventModel event) {
        ItemTrendingCardBinding cardBinding = ItemTrendingCardBinding.inflate(
                getLayoutInflater(), binding.llFeaturedContainer, false);

        cardBinding.tvEventTitle.setText(event.getTitle());
        cardBinding.tvOrganizer.setText("by " + (event.getOrgName() != null ? event.getOrgName() : "Organization"));
        cardBinding.tvLocation.setText(event.getLocation());
        
        if (event.getStartDateTime() != null) {
            cardBinding.tvDateTime.setText(dateFormat.format(event.getStartDateTime()));
        }

        cardBinding.btnAction.setOnClickListener(v -> openEventDetails(event));
        cardBinding.getRoot().setOnClickListener(v -> openEventDetails(event));

        // TODO: Populate Common Ground for real if needed
        // For now, hide it as it requires more complex queries
        cardBinding.tvCommonGroundLabel.setVisibility(View.GONE);
        cardBinding.rvCommonGround.setVisibility(View.GONE);

        binding.llFeaturedContainer.addView(cardBinding.getRoot());
    }

    private void addOtherEventCard(EventModel event) {
        OtherEventsCardBinding cardBinding = OtherEventsCardBinding.inflate(
                getLayoutInflater(), binding.llEventCards, false);

        cardBinding.tvEventTitle.setText(event.getTitle());
        cardBinding.tvDescription.setText(event.getDescription());
        cardBinding.tvLocation.setText(event.getLocation());
        
        if (event.getStartDateTime() != null) {
            cardBinding.tvDate.setText(dateFormat.format(event.getStartDateTime()));
        }

        cardBinding.tvCapacity.setText(String.format(Locale.getDefault(), "%d/%d slots",
                event.getCurrentRsvpCount(), event.getMaxCapacity()));

        cardBinding.btnAction.setOnClickListener(v -> openEventDetails(event));
        cardBinding.getRoot().setOnClickListener(v -> openEventDetails(event));

        binding.llEventCards.addView(cardBinding.getRoot());
    }

    private void openEventDetails(EventModel event) {
        Intent intent = new Intent(getActivity(), EventDetailsActivity.class);
        intent.putExtra(Constants.EXTRA_EVENT_ID, event.getEventId());
        intent.putExtra(Constants.EXTRA_EVENT_TITLE, event.getTitle());
        intent.putExtra(Constants.EXTRA_EVENT_HOST, event.getOrgName());
        intent.putExtra(Constants.EXTRA_EVENT_LOCATION, event.getLocation());
        intent.putExtra(Constants.EXTRA_EVENT_DESCRIPTION, event.getDescription());
        intent.putExtra(Constants.EXTRA_EVENT_REGISTRATION_URL, event.getRegistrationUrl());
        
        if (event.getStartDateTime() != null) {
            intent.putExtra(Constants.EXTRA_EVENT_DATETIME, dateFormat.format(event.getStartDateTime()));
        }
        
        // Basic status logic
        intent.putExtra(Constants.EXTRA_EVENT_STATUS, "UPCOMING");
        intent.putExtra(Constants.EXTRA_EVENT_STATUS_COLOR, R.color.yellow);
        
        // Also pass the whole object just in case
        intent.putExtra("EVENT_DATA", event);
        startActivity(intent);
    }

    private void navigateTo(Fragment fragment) {
        getParentFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

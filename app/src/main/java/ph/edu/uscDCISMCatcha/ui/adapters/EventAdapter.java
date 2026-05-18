package ph.edu.uscDCISMCatcha.ui.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ph.edu.uscDCISMCatcha.R;
import ph.edu.uscDCISMCatcha.databinding.OtherEventsCardBinding;
import ph.edu.uscDCISMCatcha.data.models.EventModel;
import ph.edu.uscDCISMCatcha.utils.CommonGroundUtils;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private final List<EventModel> eventList;
    private final OnEventClickListener listener;
    private List<String> joinedEventIds = new ArrayList<>();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());

    public interface OnEventClickListener {
        void onEventClick(EventModel event);
    }

    public EventAdapter(List<EventModel> eventList, OnEventClickListener listener) {
        this.eventList = eventList;
        this.listener = listener;
    }

    public void setJoinedEventIds(List<String> joinedEventIds) {
        this.joinedEventIds = joinedEventIds;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        OtherEventsCardBinding binding = OtherEventsCardBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new EventViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        holder.bind(eventList.get(position));
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    class EventViewHolder extends RecyclerView.ViewHolder {
        private final OtherEventsCardBinding binding;

        public EventViewHolder(OtherEventsCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(EventModel event) {
            final String currentEventId = event.getEventId();
            binding.tvEventTitle.setText(event.getTitle());
            binding.tvDescription.setText(event.getDescription());
            binding.tvLocation.setText(event.getLocation());

            if (event.getStartDateTime() != null) {
                binding.tvDate.setText(dateFormat.format(event.getStartDateTime()));
                binding.tvTime.setText(timeFormat.format(event.getStartDateTime()));
            }

            if (event.getMaxCapacity() > 0) {
                String capacityText = event.getCurrentRsvpCount() + "/" + event.getMaxCapacity() + " slots";
                binding.tvCapacity.setText(capacityText);

                if (event.getCurrentRsvpCount() >= event.getMaxCapacity()) {
                    binding.tvCapacity.setTextColor(Color.RED);
                    binding.tvCapacity.setText("EVENT FULL (" + event.getMaxCapacity() + ")");
                } else {
                    binding.tvCapacity.setTextColor(Color.parseColor("#556077"));
                }
            } else {
                binding.tvCapacity.setText("Unlimited slots");
            }

            // Initially hide common ground to prevent flicker from recycled views
            binding.rvCommonGround.setVisibility(View.GONE);
            binding.tvCommonGroundLabel.setVisibility(View.GONE);

            /* Disabled until Friend feature is implemented
            CommonGroundUtils.getFriendsAttending(
                    currentEventId,
                    friendsAttending -> {
                        // Safety check: ensure ViewHolder is still showing the same event
                        if (currentEventId.equals(event.getEventId()) && !friendsAttending.isEmpty()) {
                            binding.tvCommonGroundLabel.setVisibility(View.VISIBLE);
                            binding.rvCommonGround.setVisibility(View.VISIBLE);

                            ph.edu.uscDCISMCatcha.adapters.CommonGroundAdapter adapter =
                                    new ph.edu.uscDCISMCatcha.adapters.CommonGroundAdapter(
                                            itemView.getContext(), friendsAttending);
                            binding.rvCommonGround.setLayoutManager(
                                    new LinearLayoutManager(
                                            itemView.getContext(),
                                            LinearLayoutManager.HORIZONTAL, false));
                            binding.rvCommonGround.setAdapter(adapter);
                        }
                    }
            );
            */

            if (joinedEventIds != null && joinedEventIds.contains(event.getEventId())) {
                binding.tvUpcoming.setText("JOINED");
                binding.tvUpcoming.setBackgroundResource(R.drawable.bg_btn_yellow);
                binding.tvUpcoming.setVisibility(View.VISIBLE);
            } else {
                binding.tvUpcoming.setText("UPCOMING");
                binding.tvUpcoming.setBackgroundResource(R.drawable.bg_btn_yellow);
                binding.tvUpcoming.setVisibility(View.VISIBLE);
            }

            itemView.setOnClickListener(v -> listener.onEventClick(event));
            binding.btnAction.setOnClickListener(v -> listener.onEventClick(event));
        }
    }
}

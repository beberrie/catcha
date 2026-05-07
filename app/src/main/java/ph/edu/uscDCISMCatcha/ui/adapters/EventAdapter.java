package ph.edu.uscDCISMCatcha.ui.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import ph.edu.uscDCISMCatcha.databinding.OtherEventsCardBinding;
import ph.edu.uscDCISMCatcha.data.models.EventModel;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private final List<EventModel> eventList;
    private final OnEventClickListener listener;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());

    public interface OnEventClickListener {
        void onEventClick(EventModel event);
    }

    public EventAdapter(List<EventModel> eventList, OnEventClickListener listener) {
        this.eventList = eventList;
        this.listener = listener;
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

            itemView.setOnClickListener(v -> listener.onEventClick(event));
        }
    }
}
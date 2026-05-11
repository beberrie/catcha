package ph.edu.uscDCISMCatcha.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import ph.edu.uscDCISMCatcha.data.models.EventModel;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private List<EventModel> eventList;

    public EventAdapter(List<EventModel> eventList) {
        this.eventList = eventList;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Replace 'R.layout.item_event' with your actual layout file name
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_2, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        EventModel event = eventList.get(position);
        // Bind your data here
        holder.titleText.setText(event.getTitle());
        holder.locationText.setText(event.getLocation());
    }

    @Override
    public int getItemCount() {
        return eventList != null ? eventList.size() : 0;
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView titleText, locationText;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize your views here
            titleText = itemView.findViewById(android.R.id.text1);
            locationText = itemView.findViewById(android.R.id.text2);
        }
    }
}
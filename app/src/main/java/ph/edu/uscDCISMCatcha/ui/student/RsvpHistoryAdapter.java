package ph.edu.uscDCISMCatcha.ui.student;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ph.edu.uscDCISMCatcha.R;
import ph.edu.uscDCISMCatcha.utils.RSVPevent_camprof;

public class RsvpHistoryAdapter extends RecyclerView.Adapter<RsvpHistoryAdapter.ViewHolder> {

    private final List<RSVPevent_camprof> items;

    public RsvpHistoryAdapter(List<RSVPevent_camprof> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_rsvp_event, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RSVPevent_camprof event = items.get(position);

        holder.tvTitle.setText(event.getTitle());
        holder.tvDate.setText(event.getDate());
        holder.tvCategory.setText(event.getCategory());
        holder.tvStatus.setText(event.getStatusLabel());

        switch (event.getStatus()) {
            case ATTENDED:
                holder.tvStatus.setTextColor(Color.parseColor("#4CAF50"));
                break;
            case NO_SHOW:
                holder.tvStatus.setTextColor(Color.parseColor("#E53935"));
                break;
            case PENDING:
                holder.tvStatus.setTextColor(Color.parseColor("#FF9800"));
                break;
        }

        holder.tvCategory.setBackgroundTintList(
                ColorStateList.valueOf(getCategoryColor(event.getCategory())));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private int getCategoryColor(String category) {
        if (category == null) return Color.GRAY;
        switch (category) {
            case "Tech":       return Color.parseColor("#1A2B4A");
            case "Leadership": return Color.parseColor("#C8A84B");
            case "Service":    return Color.parseColor("#4CAF50");
            case "Arts":       return Color.parseColor("#E57373");
            default:           return Color.GRAY;
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDate, tvCategory, tvStatus;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle    = itemView.findViewById(R.id.tvEventTitle);
            tvDate     = itemView.findViewById(R.id.tvEventDate);
            tvCategory = itemView.findViewById(R.id.tvEventCategory);
            tvStatus   = itemView.findViewById(R.id.tvEventStatus);
        }
    }
}
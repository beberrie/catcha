package ph.edu.uscDCISMCatcha.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import ph.edu.uscDCISMCatcha.models.NotificationModel;
import ph.edu.uscDCISMCatcha.R;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private List<NotificationModel> notifications;
    private final OnMarkAsReadListener listener;

    // Interface required by NotificationFragment
    public interface OnMarkAsReadListener {
        void onMarkAsRead(NotificationModel item, int position);
    }

    public NotificationAdapter(List<NotificationModel> notifications, OnMarkAsReadListener listener) {
        this.notifications = notifications;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NotificationModel item = notifications.get(position);

        holder.titleText.setText(item.getTitle());
        holder.contentText.setText(item.getContent());
        holder.timeText.setText(item.getTimeAgo());

        // Handle unread/read state (visual feedback)
        if (item.isRead()) {
            holder.itemView.setAlpha(0.6f);
        } else {
            holder.itemView.setAlpha(1.0f);
        }

        // Set click listener for marking as read
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onMarkAsRead(item, position);
            }
        });
    }

    public void updateItems(List<NotificationModel> newItems) {
        this.notifications = newItems;
        notifyDataSetChanged();
    }

    public void markAllAsRead() {
        for (NotificationModel n : notifications) {
            n.setRead(true);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleText, contentText, timeText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.tvHeadline);
            contentText = itemView.findViewById(R.id.tvBody);
            timeText = itemView.findViewById(R.id.tvTime);
        }
    }
}

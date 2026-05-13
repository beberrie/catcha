package ph.edu.uscDCISMCatcha.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import ph.edu.uscDCISMCatcha.R;
import ph.edu.uscDCISMCatcha.data.models.NotificationModel;

public class NotificationAdapter extends
        RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private List<NotificationModel> items;
    private final OnMarkAsReadListener listener;

    public interface OnMarkAsReadListener {
        void onMarkAsRead(NotificationModel item, int position);
    }

    public NotificationAdapter(List<NotificationModel> items,
                               OnMarkAsReadListener listener) {
        this.items    = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                         int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        NotificationModel item = items.get(position);

        h.tvOrgName.setText(
                item.getType() == NotificationModel.Type.EVENT
                        ? item.getOrgName()
                        : (item.getPostedByName() != null
                        ? item.getPostedByName()
                        : "Organization"));

        h.tvTime.setText(item.getTimeAgo());
        h.tvHeadline.setText(item.getTitle());
        h.tvBody.setText(item.getContent());

        if (item.getType() == NotificationModel.Type.EVENT) {
            switch (item.getUrgency()) {
                case HOUR_1:
                    h.tvTag.setText("Event in 1 hour");
                    h.tvTag.setTextColor(
                            Color.parseColor("#E53935"));
                    break;
                case HOURS_24:
                    h.tvTag.setText("Event in 24 hours");
                    h.tvTag.setTextColor(
                            Color.parseColor("#F57C00"));
                    break;
                default:
                    h.tvTag.setText("Upcoming Event");
                    h.tvTag.setTextColor(
                            Color.parseColor("#888888"));
                    break;
            }
            h.layoutEventCard.setVisibility(View.VISIBLE);
            h.layoutAnnouncementCard.setVisibility(View.GONE);
            h.tvEventTitle.setText(item.getTitle());
            h.tvEventDateTime.setText(
                    item.getEventDateFormatted()
                            + " · " + item.getEventTimeFormatted()
                            + " – " + item.getEventEndTimeFormatted());
            h.tvEventLocation.setText(item.getLocation());
            h.tvEventGoing.setText(
                    item.getEventGoing() + " Going");
        } else {
            h.tvTag.setText("Announcement");
            h.tvTag.setTextColor(Color.parseColor("#185FA5"));
            h.layoutAnnouncementCard.setVisibility(View.VISIBLE);
            h.layoutEventCard.setVisibility(View.GONE);
            h.tvOrgFollowers.setText(
                    item.getOrgName()
                            + " · " + item.getFollowersCount()
                            + " Followers · You're a member");
            h.tvAnnouncementHeadline.setText(item.getTitle());
            h.tvPostedBy.setText(
                    "Sent by: " + item.getPostedByName()
                            + " (" + item.getPostedByPosition() + ")"
                            + " · " + item.getSentTime());
        }

        h.itemView.setAlpha(item.isRead() ? 0.5f : 1.0f);
        h.tvMarkAsRead.setVisibility(
                item.isRead() ? View.GONE : View.VISIBLE);
        h.tvMarkAsRead.setOnClickListener(v -> {
            if (listener != null)
                listener.onMarkAsRead(item, h.getAdapterPosition());
        });
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    public void updateItems(List<NotificationModel> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    public void markAllAsRead() {
        for (NotificationModel item : items) item.setRead(true);
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrgName, tvTag, tvTime, tvHeadline,
                tvBody, tvMarkAsRead;
        LinearLayout layoutEventCard, layoutAnnouncementCard;
        TextView tvEventTitle, tvEventDateTime,
                tvEventLocation, tvEventGoing;
        TextView tvOrgFollowers, tvAnnouncementHeadline, tvPostedBy;

        ViewHolder(@NonNull View v) {
            super(v);
            tvOrgName              = v.findViewById(R.id.tvOrgName);
            tvTag                  = v.findViewById(R.id.tvTag);
            tvTime                 = v.findViewById(R.id.tvTime);
            tvHeadline             = v.findViewById(R.id.tvHeadline);
            tvBody                 = v.findViewById(R.id.tvBody);
            tvMarkAsRead           = v.findViewById(R.id.tvMarkAsRead);
            layoutEventCard        = v.findViewById(R.id.layoutEventCard);
            layoutAnnouncementCard = v.findViewById(R.id.layoutAnnouncementCard);
            tvEventTitle           = v.findViewById(R.id.tvEventTitle);
            tvEventDateTime        = v.findViewById(R.id.tvEventDateTime);
            tvEventLocation        = v.findViewById(R.id.tvEventLocation);
            tvEventGoing           = v.findViewById(R.id.tvEventGoing);
            tvOrgFollowers         = v.findViewById(R.id.tvOrgFollowers);
            tvAnnouncementHeadline = v.findViewById(R.id.tvAnnouncementHeadline);
            tvPostedBy             = v.findViewById(R.id.tvPostedBy);
        }
    }
}
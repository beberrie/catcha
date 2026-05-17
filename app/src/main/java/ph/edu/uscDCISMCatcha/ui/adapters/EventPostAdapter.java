package ph.edu.uscDCISMCatcha.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import ph.edu.uscDCISMCatcha.R;
import ph.edu.uscDCISMCatcha.databinding.DialogDeleteEventBinding;
import ph.edu.uscDCISMCatcha.data.models.EventModel;

public class EventPostAdapter extends
        RecyclerView.Adapter<EventPostAdapter.ViewHolder> {

    private List<EventModel> items;
    private final boolean isLeader;
    private final OnEventMenuListener listener;

    public interface OnEventMenuListener {
        void onEdit(EventModel event);
        void onDelete(EventModel event);
        void onClose(EventModel event, int position);
    }

    public EventPostAdapter(List<EventModel> items,
                            boolean isLeader,
                            OnEventMenuListener listener) {
        this.items    = items;
        this.isLeader = isLeader;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                         int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event_post, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        EventModel event = items.get(position);

        // Org avatar initials
        String name = event.getOrgName() != null
                ? event.getOrgName() : "ORG";
        String initials = name.length() >= 2
                ? name.substring(0, 2).toUpperCase()
                : name.toUpperCase();
        h.tvOrgInitials.setText(initials);
        h.tvOrgName.setText(event.getOrgName());
        h.tvPostTime.setText(event.getTimeAgo());

        // Post body
        h.tvPostBody.setText(event.getTitle()
                + " — " + event.getDescription());

        // Event card
        h.layoutEventCard.setVisibility(View.VISIBLE);
        h.tvEventCardTitle.setText(event.getTitle());
        h.tvEventCardDetails.setText(
                event.getDateTimeFormatted()
                        + " · " + event.getLocation()
                        + " · " + event.getGoingCount() + " going");

        // Show 3-dot + X only for leaders
        h.btnPostMenu.setVisibility(
                isLeader ? View.VISIBLE : View.GONE);
        h.btnClosePost.setVisibility(
                isLeader ? View.VISIBLE : View.GONE);

        // Close popup when clicking elsewhere
        h.layoutPopupMenu.setVisibility(View.GONE);

        // 3-dot → toggle popup menu
        h.btnPostMenu.setOnClickListener(v -> {
            boolean isVisible = h.layoutPopupMenu.getVisibility()
                    == View.VISIBLE;
            h.layoutPopupMenu.setVisibility(
                    isVisible ? View.GONE : View.VISIBLE);
        });

        // Close post
        h.btnClosePost.setOnClickListener(v -> {
            h.layoutPopupMenu.setVisibility(View.GONE);
            if (listener != null)
                listener.onClose(event, h.getAdapterPosition());
        });

        // Edit option
        h.menuItemEdit.setOnClickListener(v -> {
            h.layoutPopupMenu.setVisibility(View.GONE);
            if (listener != null) listener.onEdit(event);
        });

        // Delete option → show confirmation dialog
        h.menuItemDelete.setOnClickListener(v -> {
            h.layoutPopupMenu.setVisibility(View.GONE);
            showDeleteDialog(h, event);
        });
    }

    private void showDeleteDialog(@NonNull ViewHolder h,
                                  EventModel event) {
        View dialogView = LayoutInflater.from(
                        h.itemView.getContext())
                .inflate(R.layout.dialog_delete_event, null);

        DialogDeleteEventBinding db =
                DialogDeleteEventBinding.bind(dialogView);

        db.tvDeleteEventName.setText(
                "\"" + event.getTitle() + "\"");

        AlertDialog dialog = new AlertDialog.Builder(
                h.itemView.getContext())
                .setView(dialogView)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow()
                    .setBackgroundDrawableResource(
                            android.R.color.transparent);
        }

        db.btnCancelDelete.setOnClickListener(v ->
                dialog.dismiss());

        db.btnConfirmDelete.setOnClickListener(v -> {
            dialog.dismiss();
            if (listener != null) listener.onDelete(event);
        });

        dialog.show();
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    public void updateItems(List<EventModel> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        if (position >= 0 && position < items.size()) {
            items.remove(position);
            notifyItemRemoved(position);
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrgInitials, tvOrgName, tvPostTime,
                tvPostBody, tvEventCardTitle, tvEventCardDetails;
        ImageView btnPostMenu, btnClosePost;
        LinearLayout layoutEventCard, layoutPopupMenu,
                menuItemEdit, menuItemDelete;

        ViewHolder(@NonNull View v) {
            super(v);
            tvOrgInitials      = v.findViewById(R.id.tvOrgInitials);
            tvOrgName          = v.findViewById(R.id.tvOrgName);
            tvPostTime         = v.findViewById(R.id.tvPostTime);
            tvPostBody         = v.findViewById(R.id.tvPostBody);
            tvEventCardTitle   = v.findViewById(R.id.tvEventCardTitle);
            tvEventCardDetails = v.findViewById(R.id.tvEventCardDetails);
            btnPostMenu        = v.findViewById(R.id.btnPostMenu);
            btnClosePost       = v.findViewById(R.id.btnClosePost);
            layoutEventCard    = v.findViewById(R.id.layoutEventCard);
            layoutPopupMenu    = v.findViewById(R.id.layoutPopupMenu);
            menuItemEdit       = v.findViewById(R.id.menuItemEdit);
            menuItemDelete     = v.findViewById(R.id.menuItemDelete);
        }
    }
}
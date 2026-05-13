package ph.edu.uscDCISMCatcha.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import ph.edu.uscDCISMCatcha.R;

public class FeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_PROMPT = 0;
    private static final int VIEW_TYPE_POST   = 1;

    private List<Object> items;
    private final boolean isOrgMember;
    private final OnCreatePostClickListener listener;

    public interface OnCreatePostClickListener {
        void onCreatePostClick(boolean startOnAnnouncement);
    }

    public FeedAdapter(List<Object> posts, boolean isOrgMember,
                       OnCreatePostClickListener listener) {
        this.isOrgMember = isOrgMember;
        this.listener    = listener;
        this.items       = new ArrayList<>();
        this.items.add("PROMPT");
        this.items.addAll(posts);
    }

    public void updatePosts(List<Object> newPosts) {
        this.items.clear();
        this.items.add("PROMPT");
        this.items.addAll(newPosts);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? VIEW_TYPE_PROMPT : VIEW_TYPE_POST;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                      int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_TYPE_PROMPT) {
            View v = inflater.inflate(
                    R.layout.org_home_page, parent, false);
            return new PromptViewHolder(v);
        }
        View v = inflater.inflate(
                R.layout.org_home_page, parent, false);
        return new PromptViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder,
                                 int position) {
        if (holder instanceof PromptViewHolder && position == 0) {
            bindPrompt((PromptViewHolder) holder);
        }
    }

    private void bindPrompt(PromptViewHolder h) {
        if (!isOrgMember) {
            h.itemView.setVisibility(View.GONE);
            h.itemView.getLayoutParams().height = 0;
            return;
        }
        h.itemView.setVisibility(View.VISIBLE);
        h.itemView.getLayoutParams().height =
                ViewGroup.LayoutParams.WRAP_CONTENT;

        h.btnOpenCreatePost.setOnClickListener(v -> {
            if (listener != null) listener.onCreatePostClick(true);
        });
        h.btnShortcutAnnouncement.setOnClickListener(v -> {
            if (listener != null) listener.onCreatePostClick(true);
        });
        h.btnShortcutEvent.setOnClickListener(v -> {
            if (listener != null) listener.onCreatePostClick(false);
        });
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class PromptViewHolder extends RecyclerView.ViewHolder {
        ImageView    ivUserAvatar;
        LinearLayout btnOpenCreatePost;
        LinearLayout btnShortcutAnnouncement;
        LinearLayout btnShortcutEvent;

        PromptViewHolder(@NonNull View v) {
            super(v);
            ivUserAvatar             = v.findViewById(R.id.ivUserAvatar);
            btnOpenCreatePost        = v.findViewById(R.id.btnOpenCreatePost);
            btnShortcutAnnouncement  = v.findViewById(R.id.btnShortcutAnnouncement);
            btnShortcutEvent         = v.findViewById(R.id.btnShortcutEvent);
        }
    }
}
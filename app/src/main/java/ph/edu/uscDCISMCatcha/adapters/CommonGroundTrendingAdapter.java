package ph.edu.uscDCISMCatcha.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ph.edu.uscDCISMCatcha.R;
import ph.edu.uscDCISMCatcha.data.models.UserModel;

public class CommonGroundTrendingAdapter extends RecyclerView.Adapter<CommonGroundTrendingAdapter.AvatarViewHolder> {

    private List<UserModel> users;
    private Context context;

    public CommonGroundTrendingAdapter(Context context, List<UserModel> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public AvatarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_avatar_trending, parent, false);
        return new AvatarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AvatarViewHolder holder, int position) {
        UserModel user = users.get(position);
        holder.tvName.setText(user.getFirstName());
        holder.ivAvatar.setImageResource(R.drawable.ic_profile);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    static class AvatarViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAvatar;
        TextView tvName;

        AvatarViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.ivAvatar);
            tvName = itemView.findViewById(R.id.tvAvatarName);
        }
    }
}
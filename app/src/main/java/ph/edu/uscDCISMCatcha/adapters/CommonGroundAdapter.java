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
import ph.edu.uscDCISMCatcha.models.UserModel;

public class CommonGroundAdapter extends RecyclerView.Adapter<CommonGroundAdapter.AvatarViewHolder> {

    private List<UserModel> users;
    private Context context;

    public CommonGroundAdapter(Context context, List<UserModel> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public AvatarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_avatar, parent, false);
        return new AvatarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AvatarViewHolder holder, int position) {
        UserModel user = users.get(position);

        String firstName = user.getFirstName();
        holder.tvName.setText(firstName);

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



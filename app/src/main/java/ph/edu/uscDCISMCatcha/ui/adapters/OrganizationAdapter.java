package ph.edu.uscDCISMCatcha.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;

import java.util.List;

import ph.edu.uscDCISMCatcha.R;
import ph.edu.uscDCISMCatcha.data.models.Organization;

public class OrganizationAdapter extends RecyclerView.Adapter<OrganizationAdapter.OrgViewHolder> {

    private final List<Organization> orgList;

    public OrganizationAdapter(List<Organization> orgList) {
        this.orgList = orgList;
    }

    @NonNull
    @Override
    public OrgViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_org_card_main, parent, false);
        return new OrgViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrgViewHolder holder, int position) {
        Organization org = orgList.get(position);
        holder.bind(org);
    }

    @Override
    public int getItemCount() {
        return orgList.size();
    }

    static class OrgViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrgName, tvDescription;
        Chip chipSchool, chipDept;

        public OrgViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrgName = itemView.findViewById(R.id.tvOrgNameMain);
            tvDescription = itemView.findViewById(R.id.tvDescriptionMain);
            chipSchool = itemView.findViewById(R.id.chipSchool);
            chipDept = itemView.findViewById(R.id.chipDept);

            // Hide the join button if used in Admin side, or just leave it for now.
            View joinButton = itemView.findViewById(R.id.joinButton);
            if (joinButton != null) joinButton.setVisibility(View.GONE);
        }

        public void bind(Organization org) {
            tvOrgName.setText(org.getName());
            if (tvDescription != null) tvDescription.setText(org.getDescription());

            if (chipSchool != null) {
                if (org.getSchool() != null && !org.getSchool().isEmpty()) {
                    chipSchool.setVisibility(View.VISIBLE);
                    chipSchool.setText(org.getSchool());
                } else {
                    chipSchool.setVisibility(View.GONE);
                }
            }

            if (chipDept != null) {
                if (org.getDepartment() != null && !org.getDepartment().isEmpty()) {
                    chipDept.setVisibility(View.VISIBLE);
                    chipDept.setText(org.getDepartment());
                } else {
                    chipDept.setVisibility(View.GONE);
                }
            }
        }
    }
}

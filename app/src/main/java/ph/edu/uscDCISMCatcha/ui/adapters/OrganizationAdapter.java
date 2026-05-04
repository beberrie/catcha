package ph.edu.uscDCISMCatcha.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;

import java.util.List;

import ph.edu.uscDCISMCatcha.R;
import ph.edu.uscDCISMCatcha.data.models.Organization;

public class OrganizationAdapter extends RecyclerView.Adapter<OrganizationAdapter.OrgViewHolder> {

    private final List<Organization> orgList;
    private final boolean isAdmin;
    private OnOrgActionListener listener;

    public interface OnOrgActionListener {
        void onEdit(Organization org);
        void onDelete(Organization org);
        void onJoin(Organization org);
    }

    public OrganizationAdapter(List<Organization> orgList) {
        this(orgList, false);
    }

    public OrganizationAdapter(List<Organization> orgList, boolean isAdmin) {
        this.orgList = orgList;
        this.isAdmin = isAdmin;
    }

    public void setOnOrgActionListener(OnOrgActionListener listener) {
        this.listener = listener;
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
        holder.bind(org, isAdmin, listener);
    }

    @Override
    public int getItemCount() {
        return orgList.size();
    }

    static class OrgViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrgName, tvDescription;
        Chip chipSchool, chipDept;
        View adminActions;
        ImageButton btnEdit, btnDelete;
        View btnJoin;

        public OrgViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrgName = itemView.findViewById(R.id.tvOrgNameMain);
            tvDescription = itemView.findViewById(R.id.tvDescriptionMain);
            chipSchool = itemView.findViewById(R.id.chipSchool);
            chipDept = itemView.findViewById(R.id.chipDept);
            adminActions = itemView.findViewById(R.id.adminActions);
            btnEdit = itemView.findViewById(R.id.btnEditOrg);
            btnDelete = itemView.findViewById(R.id.btnDeleteOrg);
            btnJoin = itemView.findViewById(R.id.btnJoin);
        }

        public void bind(Organization org, boolean isAdmin, OnOrgActionListener listener) {
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

            if (isAdmin) {
                if (adminActions != null) adminActions.setVisibility(View.VISIBLE);
                if (btnJoin != null) btnJoin.setVisibility(View.GONE);

                if (btnEdit != null) {
                    btnEdit.setOnClickListener(v -> {
                        if (listener != null) listener.onEdit(org);
                    });
                }

                if (btnDelete != null) {
                    btnDelete.setOnClickListener(v -> {
                        if (listener != null) listener.onDelete(org);
                    });
                }
            } else {
                if (adminActions != null) adminActions.setVisibility(View.GONE);
                if (btnJoin != null) {
                    btnJoin.setVisibility(View.VISIBLE);
                    btnJoin.setOnClickListener(v -> {
                        if (listener != null) listener.onJoin(org);
                    });
                }
            }
        }
    }
}

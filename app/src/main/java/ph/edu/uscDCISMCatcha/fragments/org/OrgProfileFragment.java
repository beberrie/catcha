package ph.edu.uscDCISMCatcha.fragments.org;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.chip.Chip;
import ph.edu.uscDCISMCatcha.R;

public class OrgProfileFragment extends Fragment {

    private Button btnBack;
    private Button btnJoin;
    private TextView tvOrgName;
    private LinearLayout joinedStatusContainer;
    private Chip filterButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.org_profile, container, false);

        btnBack = view.findViewById(R.id.backButton);
        btnJoin = view.findViewById(R.id.joinButton);
        tvOrgName = view.findViewById(R.id.orgName);
        joinedStatusContainer = view.findViewById(R.id.joinedStatusContainer);
        filterButton = view.findViewById(R.id.filterButton);

        // Retrieve the organization name from arguments
        if (getArguments() != null) {
            String orgName = getArguments().getString("ORG_NAME");
            if (orgName != null && tvOrgName != null) {
                tvOrgName.setText(orgName);
            }
        }

        // Handle Back Navigation
        btnBack.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        // Example: Toggling Join Status
        btnJoin.setOnClickListener(v -> {
            btnJoin.setVisibility(View.GONE);
            joinedStatusContainer.setVisibility(View.VISIBLE);
        });

        // Show Event Filters Bottom Sheet
        filterButton.setOnClickListener(v -> {
            EventFiltersBottomSheet bottomSheet = new EventFiltersBottomSheet();
            bottomSheet.show(getChildFragmentManager(), "EventFiltersBottomSheet");
        });

        return view;
    }
}
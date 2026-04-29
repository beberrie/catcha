package ph.edu.uscDCISMCatcha.fragments.org;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.chip.Chip;
import ph.edu.uscDCISMCatcha.R;
import ph.edu.uscDCISMCatcha.fragments.home.UserProfileFragment;

public class OrgDashboardFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.org_dashboard, container, false);

        LinearLayout suggestedContainer = view.findViewById(R.id.suggestedOrgsContainer);
        LinearLayout allOrgsContainer = view.findViewById(R.id.allOrgsContainer);
        Chip chipFilters = view.findViewById(R.id.chipFilters);

        // Header Icons
        ImageView ivUserAvatar = view.findViewById(R.id.ivUserAvatarHeader);
        ImageView ivNotifications = view.findViewById(R.id.ivNotificationsHeader);
        ImageView ivSearch = view.findViewById(R.id.ivSearchHeader);

        // Add dummy data for suggested organizations
        addDummySuggestedOrgs(inflater, suggestedContainer);

        // Add dummy data for all organizations
        addDummyAllOrgs(inflater, allOrgsContainer);

        // Setup filter button click
        if (chipFilters != null) {
            chipFilters.setOnClickListener(v -> {
                OrgFiltersBottomSheet bottomSheet = new OrgFiltersBottomSheet();
                bottomSheet.show(getParentFragmentManager(), "OrgFiltersBottomSheet");
            });
        }

        // Setup avatar click to go to profile
        if (ivUserAvatar != null) {
            ivUserAvatar.setOnClickListener(v -> {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new UserProfileFragment())
                        .addToBackStack(null)
                        .commit();
            });
        }

        // Setup notifications click
        if (ivNotifications != null) {
            ivNotifications.setOnClickListener(v -> {
                Toast.makeText(getContext(), "Notifications coming soon!", Toast.LENGTH_SHORT).show();
            });
        }

        // Setup search click
        if (ivSearch != null) {
            ivSearch.setOnClickListener(v -> {
                Toast.makeText(getContext(), "Search functionality coming soon!", Toast.LENGTH_SHORT).show();
            });
        }

        return view;
    }

    private void addDummySuggestedOrgs(LayoutInflater inflater, LinearLayout container) {
        String[] orgNames = {"GDG Campus", "Google Student", "DSC Cebu", "USC Tech"};
        for (String name : orgNames) {
            View card = inflater.inflate(R.layout.fragment_org_card_suggest, container, false);
            TextView tvName = card.findViewById(R.id.tvOrgNameSuggest);
            if (tvName != null) tvName.setText(name);
            
            card.setOnClickListener(v -> navigateToOrgProfile(name));
            
            container.addView(card);
        }
    }

    private void addDummyAllOrgs(LayoutInflater inflater, LinearLayout container) {
        String[] orgNames = {"Google Developer Group", "Student Council", "Cebu Tech Club"};
        for (String name : orgNames) {
            View card = inflater.inflate(R.layout.fragment_org_card_main, container, false);
            TextView tvName = card.findViewById(R.id.tvOrgNameMain);
            if (tvName != null) tvName.setText(name);

            card.setOnClickListener(v -> navigateToOrgProfile(name));

            container.addView(card);
        }
    }

    private void navigateToOrgProfile(String orgName) {
        OrgProfileFragment fragment = new OrgProfileFragment();
        Bundle args = new Bundle();
        args.putString("ORG_NAME", orgName);
        fragment.setArguments(args);

        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}

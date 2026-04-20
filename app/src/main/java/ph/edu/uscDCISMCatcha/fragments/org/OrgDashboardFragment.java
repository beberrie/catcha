package ph.edu.uscDCISMCatcha.fragments.org;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import ph.edu.uscDCISMCatcha.R;

public class OrgDashboardFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.org_dashboard, container, false);

        LinearLayout suggestedContainer = view.findViewById(R.id.suggestedOrgsContainer);
        LinearLayout allOrgsContainer = view.findViewById(R.id.allOrgsContainer);

        // Add dummy data for suggested organizations
        addDummySuggestedOrgs(inflater, suggestedContainer);

        // Add dummy data for all organizations
        addDummyAllOrgs(inflater, allOrgsContainer);

        return view;
    }

    private void addDummySuggestedOrgs(LayoutInflater inflater, LinearLayout container) {
        String[] orgNames = {"GDG Campus", "Google Student", "DSC Cebu", "USC Tech"};
        for (String name : orgNames) {
            View card = inflater.inflate(R.layout.fragment_org_card_suggest, container, false);
            TextView tvName = card.findViewById(R.id.tvOrgNameSuggest);
            if (tvName != null) tvName.setText(name);
            
            // Set click listener to navigate to profile with the org name
            card.setOnClickListener(v -> navigateToProfile(name));
            
            container.addView(card);
        }
    }

    private void addDummyAllOrgs(LayoutInflater inflater, LinearLayout container) {
        String[] orgNames = {"Google Developer Group", "Student Council", "Cebu Tech Club"};
        for (String name : orgNames) {
            View card = inflater.inflate(R.layout.fragment_org_card_main, container, false);
            TextView tvName = card.findViewById(R.id.tvOrgNameMain);
            if (tvName != null) tvName.setText(name);

            // Set click listener to navigate to profile with the org name
            card.setOnClickListener(v -> navigateToProfile(name));

            container.addView(card);
        }
    }

    private void navigateToProfile(String orgName) {
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

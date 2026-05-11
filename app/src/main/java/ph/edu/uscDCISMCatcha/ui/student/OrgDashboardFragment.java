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
<<<<<<< HEAD:app/src/main/java/ph/edu/uscDCISMCatcha/ui/student/OrgDashboardFragment.java
import ph.edu.uscDCISMCatcha.fragments.NotificationFragment;
import ph.edu.uscDCISMCatcha.ui.student.UserProfileFragment;
import ph.edu.uscDCISMCatcha.ui.student.OrgFiltersBottomSheet;
import ph.edu.uscDCISMCatcha.ui.student.OrgProfileFragment;
=======
import ph.edu.uscDCISMCatcha.fragments.NotificationFragment; // ✅ changed
import ph.edu.uscDCISMCatcha.fragments.home.UserProfileFragment;
>>>>>>> 61f9bf6689b019dcfe76283eac605b9dca98bb21:app/src/main/java/ph/edu/uscDCISMCatcha/fragments/org/OrgDashboardFragment.java

public class OrgDashboardFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.org_dashboard, container, false);

        // UI References
        LinearLayout suggestedContainer = view.findViewById(R.id.suggestedOrgsContainer);
        LinearLayout allOrgsContainer   = view.findViewById(R.id.allOrgsContainer);
        Chip chipFilters                = view.findViewById(R.id.chipFilters);

        ImageView ivUserAvatar    = view.findViewById(R.id.ivUserAvatarHeader);
        ImageView ivNotifications = view.findViewById(R.id.ivNotificationsHeader);
        ImageView ivSearch        = view.findViewById(R.id.ivSearchHeader);

        // Initialize Content
<<<<<<< HEAD:app/src/main/java/ph/edu/uscDCISMCatcha/ui/student/OrgDashboardFragment.java
        if (suggestedContainer != null) addDummySuggestedOrgs(inflater, suggestedContainer);
        if (allOrgsContainer != null) addDummyAllOrgs(inflater, allOrgsContainer);
=======
        addDummySuggestedOrgs(inflater, suggestedContainer);
        addDummyAllOrgs(inflater, allOrgsContainer);
>>>>>>> 61f9bf6689b019dcfe76283eac605b9dca98bb21:app/src/main/java/ph/edu/uscDCISMCatcha/fragments/org/OrgDashboardFragment.java

        // Filter chip
        if (chipFilters != null) {
            chipFilters.setOnClickListener(v -> {
                OrgFiltersBottomSheet bottomSheet = new OrgFiltersBottomSheet();
                bottomSheet.show(getParentFragmentManager(), "OrgFiltersBottomSheet");
            });
        }

        // Header Navigation
        if (ivUserAvatar != null) {
<<<<<<< HEAD:app/src/main/java/ph/edu/uscDCISMCatcha/ui/student/OrgDashboardFragment.java
            ivUserAvatar.setOnClickListener(v -> navigateToFragment(new UserProfileFragment()));
        }

        if (ivNotifications != null) {
            ivNotifications.setOnClickListener(v -> navigateToFragment(new NotificationFragment()));
=======
            ivUserAvatar.setOnClickListener(v ->
                    navigateToFragment(new UserProfileFragment()));
        }

        if (ivNotifications != null) {
            ivNotifications.setOnClickListener(v ->
                    navigateToFragment(new NotificationFragment()));
>>>>>>> 61f9bf6689b019dcfe76283eac605b9dca98bb21:app/src/main/java/ph/edu/uscDCISMCatcha/fragments/org/OrgDashboardFragment.java
        }

        if (ivSearch != null) {
            ivSearch.setOnClickListener(v ->
<<<<<<< HEAD:app/src/main/java/ph/edu/uscDCISMCatcha/ui/student/OrgDashboardFragment.java
                    Toast.makeText(getContext(), "Search coming soon!", Toast.LENGTH_SHORT).show());
=======
                    Toast.makeText(getContext(),
                            "Search functionality coming soon!",
                            Toast.LENGTH_SHORT).show());
>>>>>>> 61f9bf6689b019dcfe76283eac605b9dca98bb21:app/src/main/java/ph/edu/uscDCISMCatcha/fragments/org/OrgDashboardFragment.java
        }

        return view;
    }

    private void navigateToFragment(Fragment fragment) {
<<<<<<< HEAD:app/src/main/java/ph/edu/uscDCISMCatcha/ui/student/OrgDashboardFragment.java
        if (fragment != null && getActivity() != null) {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    private void addDummySuggestedOrgs(LayoutInflater inflater, LinearLayout container) {
=======
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void addDummySuggestedOrgs(LayoutInflater inflater,
                                       LinearLayout container) {
>>>>>>> 61f9bf6689b019dcfe76283eac605b9dca98bb21:app/src/main/java/ph/edu/uscDCISMCatcha/fragments/org/OrgDashboardFragment.java
        String[] orgNames = {"GDG Campus", "Google Student", "DSC Cebu", "USC Tech"};
        for (String name : orgNames) {
            View card = inflater.inflate(
                    R.layout.fragment_org_card_suggest, container, false);
            TextView tvName = card.findViewById(R.id.tvOrgNameSuggest);
            if (tvName != null) tvName.setText(name);
            card.setOnClickListener(v -> navigateToOrgProfile(name));
            container.addView(card);
        }
    }

    private void addDummyAllOrgs(LayoutInflater inflater,
                                 LinearLayout container) {
        String[] orgNames = {
                "Google Developer Group", "Student Council", "Cebu Tech Club"
        };
        for (String name : orgNames) {
            View card = inflater.inflate(
                    R.layout.fragment_org_card_main, container, false);
            TextView tvName = card.findViewById(R.id.tvOrgNameMain);
            if (tvName != null) tvName.setText(name);
            card.setOnClickListener(v -> navigateToOrgProfile(name));
            container.addView(card);
        }
    }

    private void navigateToOrgProfile(String orgName) {
        // This is where the "cannot find symbol" was triggered
        OrgProfileFragment fragment = new OrgProfileFragment();
        Bundle args = new Bundle();
        args.putString("ORG_NAME", orgName);
        fragment.setArguments(args);
        navigateToFragment(fragment);
    }
}
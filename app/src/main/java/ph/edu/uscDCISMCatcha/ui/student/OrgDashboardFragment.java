package ph.edu.uscDCISMCatcha.ui.student;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import ph.edu.uscDCISMCatcha.R;

public class OrgDashboardFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // 1. Inflate the dashboard layout
        View view = inflater.inflate(R.layout.org_dashboard, container, false);

        // 2. Initialize the Header Icons
        // If your header is inside an <include>, this will still work as long as
        // the ID ivNotificationsHeader is unique.
        ImageView ivNotifications = view.findViewById(R.id.ivNotificationsHeader);
        ImageView ivUserAvatar    = view.findViewById(R.id.ivUserAvatarHeader);
        ImageView ivSearch        = view.findViewById(R.id.ivSearchHeader);

        // 3. Set up the Notification Click Listener
        if (ivNotifications != null) {
            ivNotifications.setOnClickListener(v -> {
                Log.d("NAVIGATION_LOG", "Notification Icon Clicked!");
                navigateToFragment(new NotificationFragment());
            });
        } else {
            // If you see this in Logcat, your R.id.ivNotificationsHeader is wrong
            Log.e("NAVIGATION_LOG", "Error: ivNotificationsHeader NOT FOUND in layout");
        }

        // 4. Set up Avatar/Profile Click
        if (ivUserAvatar != null) {
            ivUserAvatar.setOnClickListener(v -> navigateToFragment(new UserProfileFragment()));
        }

        // Initialize your dummy content (Suggested and All Orgs)
        setupDummyData(view, inflater);

        return view;
    }

    private void navigateToFragment(Fragment fragment) {
        // Ensure R.id.fragment_container matches the ID in activity_main.xml
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void setupDummyData(View view, LayoutInflater inflater) {
        LinearLayout suggested = view.findViewById(R.id.suggestedOrgsContainer);
        LinearLayout allOrgs = view.findViewById(R.id.allOrgsContainer);

        // Suggested Orgs
        String[] suggestions = {"GDG Campus", "USC Tech", "Google Student"};
        for (String name : suggestions) {
            View card = inflater.inflate(R.layout.fragment_org_card_suggest, suggested, false);
            TextView tvName = card.findViewById(R.id.tvOrgNameSuggest);
            if (tvName != null) tvName.setText(name);
            card.setOnClickListener(v -> navigateToOrgProfile(name));
            suggested.addView(card);
        }

        // All Orgs
        String[] all = {"Student Council", "Cebu Tech Club", "Google Developer Group"};
        for (String name : all) {
            View card = inflater.inflate(R.layout.fragment_org_card_main, allOrgs, false);
            TextView tvName = card.findViewById(R.id.tvOrgNameMain);
            if (tvName != null) tvName.setText(name);
            card.setOnClickListener(v -> navigateToOrgProfile(name));
            allOrgs.addView(card);
        }
    }

    private void navigateToOrgProfile(String orgName) {
        OrgProfileFragment fragment = new OrgProfileFragment();
        Bundle args = new Bundle();
        args.putString("ORG_NAME", orgName);
        fragment.setArguments(args);
        navigateToFragment(fragment);
    }
}
package ph.edu.uscDCISMCatcha.ui.student;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import ph.edu.uscDCISMCatcha.R;
import ph.edu.uscDCISMCatcha.data.models.Organization;
import ph.edu.uscDCISMCatcha.databinding.OrgDashboardBinding;
import ph.edu.uscDCISMCatcha.databinding.FragmentOrgCardMainBinding;

public class OrgDashboardFragment extends Fragment {

    private OrgDashboardBinding binding;
    private FirebaseFirestore db;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = OrgDashboardBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupHeader();
        setupFilters();
        
        // Suggested Orgs - Clear for now as requested
        binding.suggestedOrgsContainer.removeAllViews();

        fetchOrganizations();
    }

    private void setupHeader() {
        binding.header.ivUserAvatarHeader.setOnClickListener(v ->
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new UserProfileFragment())
                        .addToBackStack(null)
                        .commit());

        binding.header.ivNotificationsHeader.setOnClickListener(v ->
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new PushSetupFragment())
                        .addToBackStack(null)
                        .commit());

        binding.header.ivSearchHeader.setOnClickListener(v ->
                Toast.makeText(getContext(), "Search functionality coming soon!", Toast.LENGTH_SHORT).show());
    }

    private void setupFilters() {
        binding.chipFilters.setOnClickListener(v -> {
            OrgFiltersBottomSheet bottomSheet = new OrgFiltersBottomSheet();
            bottomSheet.show(getParentFragmentManager(), "OrgFiltersBottomSheet");
        });
    }

    private void fetchOrganizations() {
        db.collection("organizations")
                .orderBy("name", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        if (getContext() != null) {
                            Toast.makeText(getContext(), "Error fetching organizations", Toast.LENGTH_SHORT).show();
                        }
                        return;
                    }

                    if (binding != null) {
                        binding.allOrgsContainer.removeAllViews();
                        if (value != null) {
                            for (QueryDocumentSnapshot doc : value) {
                                Organization org = doc.toObject(Organization.class);
                                addOrgCard(org, doc.getId());
                            }
                        }
                    }
                });
    }

    private void addOrgCard(Organization org, String orgId) {
        FragmentOrgCardMainBinding cardBinding = FragmentOrgCardMainBinding.inflate(
                getLayoutInflater(), binding.allOrgsContainer, false);

        cardBinding.tvOrgNameMain.setText(org.getName());
        
        // Populate school and department tags
        cardBinding.chipSchool.setText(org.getSchool());
        cardBinding.chipDept.setText(org.getDepartment());

        if (org.getCategory() != null && !org.getCategory().isEmpty()) {
            cardBinding.chipCategory.setText(org.getCategory());
            cardBinding.chipCategory.setVisibility(View.VISIBLE);
        } else {
            cardBinding.chipCategory.setVisibility(View.GONE);
        }

        // For now, let's keep members count simple or hide if not in model
        cardBinding.tvMemberCount.setText("View Profile");

        cardBinding.btnJoin.setOnClickListener(v -> navigateToOrgProfile(org.getName(), orgId));
        cardBinding.getRoot().setOnClickListener(v -> navigateToOrgProfile(org.getName(), orgId));

        binding.allOrgsContainer.addView(cardBinding.getRoot());
    }

    private void navigateToOrgProfile(String orgName, String orgId) {
        OrgProfileFragment fragment = new OrgProfileFragment();
        Bundle args = new Bundle();
        args.putString("ORG_NAME", orgName);
        args.putString("ORG_ID", orgId);
        fragment.setArguments(args);

        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

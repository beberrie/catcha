package ph.edu.uscDCISMCatcha.ui.student;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.chip.Chip;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import ph.edu.uscDCISMCatcha.R;
import ph.edu.uscDCISMCatcha.data.models.Organization;
import ph.edu.uscDCISMCatcha.databinding.FragmentOrgCardMainBinding;
import ph.edu.uscDCISMCatcha.databinding.FragmentOrgCardSuggestBinding;
import ph.edu.uscDCISMCatcha.databinding.OrgDashboardBinding;
import ph.edu.uscDCISMCatcha.models.RecommendationModel;
import ph.edu.uscDCISMCatcha.ui.chat.ChatBotFragment;
import ph.edu.uscDCISMCatcha.viewmodel.InterestViewModel;

public class OrgDashboardFragment extends Fragment {

    private OrgDashboardBinding binding;
    private FirebaseFirestore db;
    private InterestViewModel interestViewModel;
    private String currentUserId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) currentUserId = user.getUid();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = OrgDashboardBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        interestViewModel = new ViewModelProvider(this)
                .get(InterestViewModel.class);

        setupHeader();
        setupFilters();
        setupChatBot();

        // ✅ Observe org recommendations from InterestViewModel
        // These show in the "Suggested for you" horizontal container
        observeOrgRecommendations();

        // Fetch all orgs for the vertical "Organizations" list
        fetchOrganizations();

        // Load user interests from Firestore → triggers recommendations
        if (currentUserId != null) {
            interestViewModel.loadUserContent(currentUserId);
        }
    }

    // ✅ Merged from RecommendationFragment — now lives here
    private void observeOrgRecommendations() {
        interestViewModel.getOrgRecommendations().observe(
                getViewLifecycleOwner(), recommendations -> {
                    if (recommendations == null
                            || binding == null) return;

                    binding.suggestedOrgsContainer.removeAllViews();

                    if (recommendations.isEmpty()) {
                        // Hide the suggested section if no matches
                        binding.suggestedOrgsContainer
                                .setVisibility(View.GONE);
                        return;
                    }

                    binding.suggestedOrgsContainer
                            .setVisibility(View.VISIBLE);

                    for (RecommendationModel rec : recommendations) {
                        addSuggestedOrgCard(rec);
                    }
                });
    }

    // ✅ Builds each suggested org card using fragment_org_card_suggest.xml
    private void addSuggestedOrgCard(RecommendationModel rec) {
        FragmentOrgCardSuggestBinding cardBinding =
                FragmentOrgCardSuggestBinding.inflate(
                        getLayoutInflater(),
                        binding.suggestedOrgsContainer, false);

        // Keep horizontal scroll card width at 82% of screen
        ViewGroup.LayoutParams params =
                cardBinding.getRoot().getLayoutParams();
        params.width = (int) (getResources()
                .getDisplayMetrics().widthPixels * 0.82);
        cardBinding.getRoot().setLayoutParams(params);

        cardBinding.tvOrgNameSuggest.setText(rec.getTitle());
        cardBinding.chipSchoolSuggest.setText(rec.getSchool());
        cardBinding.chipDeptSuggest.setText(rec.getDepartment());
        cardBinding.chipCategorySuggest.setText(rec.getCategory());

        cardBinding.getRoot().setOnClickListener(v -> {
            // ✅ Update interest weight when user taps
            if (rec.getTags() != null
                    && rec.getTags().length > 0
                    && currentUserId != null) {
                interestViewModel.updateTagWeight(
                        currentUserId, rec.getTags()[0], 0.08);
            }
            // ✅ Pass the correct Firestore ID for navigation
            navigateToOrgProfile(rec.getTitle(), rec.getId());
        });

        binding.suggestedOrgsContainer.addView(
                cardBinding.getRoot());
    }

    // Fetches all orgs for the main vertical list
    private void fetchOrganizations() {
        db.collection("organizations")
                .orderBy("name", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null || binding == null) return;
                    binding.allOrgsContainer.removeAllViews();
                    if (value != null) {
                        for (QueryDocumentSnapshot doc : value) {
                            Organization org =
                                    doc.toObject(Organization.class);
                            addAllOrgCard(org, doc.getId());
                        }
                    }
                });
    }

    private void addAllOrgCard(Organization org, String orgId) {
        FragmentOrgCardMainBinding cardBinding =
                FragmentOrgCardMainBinding.inflate(
                        getLayoutInflater(),
                        binding.allOrgsContainer, false);

        cardBinding.tvOrgNameMain.setText(org.getName());
        cardBinding.chipSchool.setText(org.getSchool());
        cardBinding.chipDept.setText(org.getDepartment());
        cardBinding.tvMemberCount.setText("View Details");

        View.OnClickListener listener = v ->
                navigateToOrgProfile(org.getName(), orgId);
        cardBinding.btnJoin.setOnClickListener(listener);
        cardBinding.getRoot().setOnClickListener(listener);

        binding.allOrgsContainer.addView(cardBinding.getRoot());
    }

    private void setupHeader() {
        binding.header.ivUserAvatarHeader.setOnClickListener(v ->
                navigateTo(new UserProfileFragment()));
        binding.header.ivNotificationsHeader.setOnClickListener(v ->
                navigateTo(new NotificationFragment()));
        binding.header.ivSearchHeader.setOnClickListener(v ->
                navigateTo(new SearchFragment()));
    }

    private void setupFilters() {
        binding.chipFilters.setOnClickListener(v ->
                new OrgFiltersBottomSheet().show(
                        getParentFragmentManager(), "Filters"));
    }

    private void setupChatBot() {
        binding.fabChatBot.setOnClickListener(v ->
                navigateTo(new ChatBotFragment()));
    }

    private void navigateTo(Fragment fragment) {
        getParentFragmentManager().beginTransaction()
                .setCustomAnimations(
                        R.anim.slide_in_right,
                        R.anim.slide_out_left,
                        R.anim.slide_in_left,
                        R.anim.slide_out_right)
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void navigateToOrgProfile(String name, String id) {
        OrgProfileFragment fragment = new OrgProfileFragment();
        Bundle args = new Bundle();
        args.putString("ORG_NAME", name);
        args.putString("ORG_ID", id);
        fragment.setArguments(args);
        navigateTo(fragment);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
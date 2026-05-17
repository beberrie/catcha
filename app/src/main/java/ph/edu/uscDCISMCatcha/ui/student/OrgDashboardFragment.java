package ph.edu.uscDCISMCatcha.ui.student;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.bumptech.glide.Glide;
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
import ph.edu.uscDCISMCatcha.data.models.RecommendationModel;
import ph.edu.uscDCISMCatcha.ui.chat.ChatBotFragment;
import ph.edu.uscDCISMCatcha.viewmodel.student.InterestViewModel;

public class OrgDashboardFragment extends Fragment {

    private OrgDashboardBinding binding;
    private FirebaseFirestore db;
    private InterestViewModel interestViewModel;
    private static final String TAG = "OrgDashboardFragment";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
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

        // 1. Observe org recommendations
        observeOrgRecommendations();

        // 2. Fetch all orgs for the main list
        fetchOrganizations();

        // 3. Load user context to trigger recommendations
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            interestViewModel.loadUserContent(user.getUid());
        }
    }

    private void observeOrgRecommendations() {
        interestViewModel.getOrgRecommendations().observe(
                getViewLifecycleOwner(), recommendations -> {
                    if (binding == null) return;

                    binding.suggestedOrgsContainer.removeAllViews();

                    // Always show the suggested section layout
                    binding.layoutSuggestedSection.setVisibility(View.VISIBLE);
                    binding.suggestedDivider.setVisibility(View.VISIBLE);

                    if (recommendations == null || recommendations.isEmpty()) {
                        Log.d(TAG, "No matching organizations found for suggestions.");
                        // Show "No suggestions" message and hide the scroll view
                        binding.tvNoSuggestions.setVisibility(View.VISIBLE);
                        binding.hsvSuggestedOrgs.setVisibility(View.GONE);
                        binding.tvSeeAllSuggested.setVisibility(View.GONE);
                    } else {
                        Log.d(TAG, "Displaying " + recommendations.size() + " suggestion cards.");
                        // Hide "No suggestions" message and show the scroll view
                        binding.tvNoSuggestions.setVisibility(View.GONE);
                        binding.hsvSuggestedOrgs.setVisibility(View.VISIBLE);
                        binding.tvSeeAllSuggested.setVisibility(View.VISIBLE);

                        for (RecommendationModel rec : recommendations) {
                            addSuggestedOrgCard(rec);
                        }
                    }
                });
    }

    private void addSuggestedOrgCard(RecommendationModel rec) {
        FragmentOrgCardSuggestBinding cardBinding =
                FragmentOrgCardSuggestBinding.inflate(
                        getLayoutInflater(),
                        binding.suggestedOrgsContainer, false);

        // Keep horizontal cards at a fixed comfortable width
        ViewGroup.LayoutParams params = cardBinding.getRoot().getLayoutParams();
        params.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.82);
        cardBinding.getRoot().setLayoutParams(params);

        cardBinding.tvOrgNameSuggest.setText(rec.getTitle());
        cardBinding.chipSchoolSuggest.setText(rec.getSchool());
        cardBinding.chipDeptSuggest.setText(rec.getDepartment());
        cardBinding.chipCategorySuggest.setText(rec.getCategory());

        // Load banner image if available
        if (rec.getBannerImageUrl() != null && !rec.getBannerImageUrl().isEmpty()) {
            Glide.with(this)
                    .load(rec.getBannerImageUrl())
                    .placeholder(R.drawable.qr_dummy_data)
                    .centerCrop()
                    .into(cardBinding.ivOrgBannerSuggest);
        }

        cardBinding.getRoot().setOnClickListener(v -> navigateToOrgProfile(rec.getTitle(), rec.getId()));

        binding.suggestedOrgsContainer.addView(cardBinding.getRoot());
    }

    private void fetchOrganizations() {
        db.collection("organizations")
                .orderBy("name", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null || binding == null) return;
                    binding.allOrgsContainer.removeAllViews();
                    if (value != null) {
                        for (QueryDocumentSnapshot doc : value) {
                            Organization org = doc.toObject(Organization.class);
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
        cardBinding.chipCategory.setText(org.getCategory());
        cardBinding.tvDescriptionMain.setText(org.getDescription());
        
        // Handle Join Button visibility based on org status
        if (org.isJoinEnabled()) {
            cardBinding.btnJoin.setVisibility(View.VISIBLE);
            cardBinding.btnJoin.setText("Join");
            cardBinding.tvMemberCount.setText("Recruitment Open");
        } else {
            cardBinding.btnJoin.setVisibility(View.GONE);
            cardBinding.tvMemberCount.setText("Recruitment Closed");
        }

        // Load banner image
        if (org.getBannerImageUrl() != null && !org.getBannerImageUrl().isEmpty()) {
            Glide.with(this)
                    .load(org.getBannerImageUrl())
                    .placeholder(R.drawable.qr_dummy_data)
                    .centerCrop()
                    .into(cardBinding.ivOrgBanner);
        }

        // Load logo
        if (org.getProfileImageUrl() != null && !org.getProfileImageUrl().isEmpty()) {
            Glide.with(this)
                    .load(org.getProfileImageUrl())
                    .placeholder(R.drawable.qr_dummy_data)
                    .circleCrop()
                    .into(cardBinding.ivOrgLogoMain);
        }

        View.OnClickListener listener = v -> navigateToOrgProfile(org.getName(), orgId);
        cardBinding.btnJoin.setOnClickListener(listener);
        cardBinding.getRoot().setOnClickListener(listener);

        binding.allOrgsContainer.addView(cardBinding.getRoot());
    }

    private void setupHeader() {
        binding.header.ivUserAvatarHeader.setOnClickListener(v -> navigateTo(new UserProfileFragment()));
        binding.header.ivNotificationsHeader.setOnClickListener(v -> navigateTo(new NotificationFragment()));
        binding.header.ivSearchHeader.setOnClickListener(v -> navigateTo(new SearchFragment()));
    }

    private void setupFilters() {
        binding.chipFilters.setOnClickListener(v ->
                new OrgFiltersBottomSheet().show(getParentFragmentManager(), "Filters"));
    }

    private void setupChatBot() {
        binding.fabChatBot.setOnClickListener(v -> navigateTo(new ChatBotFragment()));
    }

    private void navigateTo(Fragment fragment) {
        getParentFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
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

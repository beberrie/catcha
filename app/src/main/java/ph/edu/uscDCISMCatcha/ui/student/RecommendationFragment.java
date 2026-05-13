package ph.edu.uscDCISMCatcha.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.chip.Chip;
import java.util.List;
import ph.edu.uscDCISMCatcha.R;
// Updated to singular to match image_1fd5af.png
import ph.edu.uscDCISMCatcha.databinding.FragmentRecommendationBinding;
import ph.edu.uscDCISMCatcha.models.RecommendationModel;
import ph.edu.uscDCISMCatcha.viewmodel.InterestViewModel;


public class RecommendationFragment extends Fragment {


    // Updated to singular
    private FragmentRecommendationBinding binding;
    private InterestViewModel viewModel;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        // Updated to singular
        binding = FragmentRecommendationBinding.inflate(
                inflater, container, false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        viewModel = new ViewModelProvider(this).get(InterestViewModel.class);


        observeRecommendations();
        viewModel.loadDummyRecommendations();
    }


    private void observeRecommendations() {
        viewModel.getRecommendations().observe(getViewLifecycleOwner(),
                list -> {
                    if (list == null) return;
                    binding.layoutRecommendedOrgs.removeAllViews();
                    binding.layoutRecommendedEvents.removeAllViews();


                    for (RecommendationModel item : list) {
                        if (item.getType() == RecommendationModel.Type.ORG) {
                            addOrgCard(item);
                        } else {
                            addEventCard(item);
                        }
                    }
                });
    }


    private void addOrgCard(RecommendationModel item) {
        View card = LayoutInflater.from(requireContext())
                .inflate(R.layout.item_recommended_org,
                        binding.layoutRecommendedOrgs, false);


        TextView tvInitials = card.findViewById(R.id.tvOrgInitials);
        TextView tvName     = card.findViewById(R.id.tvOrgName);
        TextView tvFollowers = card.findViewById(R.id.tvOrgFollowers);
        TextView tvMatch    = card.findViewById(R.id.tvMatchPercent);
        com.google.android.material.chip.ChipGroup chipGroup =
                card.findViewById(R.id.chipGroupOrgTags);
        Button btnView   = card.findViewById(R.id.btnViewOrg);
        Button btnFollow = card.findViewById(R.id.btnFollowOrg);


        tvInitials.setText(item.getInitials());
        tvName.setText(item.getTitle());
        tvFollowers.setText(item.getFollowers() + " followers");
        tvMatch.setText(item.getMatchPercent() + "% match");


        for (String tag : item.getTags()) {
            Chip chip = new Chip(requireContext());
            chip.setText(tag);
            chip.setClickable(false);
            chipGroup.addView(chip);
        }


        btnView.setOnClickListener(v ->
                Toast.makeText(requireContext(),
                        "Opening " + item.getTitle(),
                        Toast.LENGTH_SHORT).show());


        btnFollow.setOnClickListener(v -> {
            btnFollow.setText("Following");
            btnFollow.setEnabled(false);
            // Update interest weight when user follows
            viewModel.updateTagWeight("user_001",
                    item.getTags()[0], 0.08);
        });


        binding.layoutRecommendedOrgs.addView(card);
    }


    private void addEventCard(RecommendationModel item) {
        View card = LayoutInflater.from(requireContext())
                .inflate(R.layout.item_recommended_event,
                        binding.layoutRecommendedEvents, false);


        TextView tvTitle   = card.findViewById(R.id.tvEventTitle);
        TextView tvDateTime = card.findViewById(R.id.tvEventDateTime);
        TextView tvMatch   = card.findViewById(R.id.tvEventMatchPercent);
        com.google.android.material.chip.ChipGroup chipGroup =
                card.findViewById(R.id.chipGroupEventTags);
        Button btnInterested = card.findViewById(R.id.btnInterestedEvent);


        tvTitle.setText(item.getTitle());
        tvDateTime.setText(item.getSubtitle());
        tvMatch.setText(item.getMatchPercent() + "%");


        for (String tag : item.getTags()) {
            Chip chip = new Chip(requireContext());
            chip.setText(tag);
            chip.setClickable(false);
            chipGroup.addView(chip);
        }


        btnInterested.setOnClickListener(v -> {
            btnInterested.setText("Interested ✓");
            btnInterested.setEnabled(false);
            // Update interest weight when user marks interested
            viewModel.updateTagWeight("user_001",
                    item.getTags()[0], 0.10);
        });


        binding.layoutRecommendedEvents.addView(card);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

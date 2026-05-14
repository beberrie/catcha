package ph.edu.uscDCISMCatcha.ui.student;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view. View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.chip.Chip;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.Map;
import ph.edu.uscDCISMCatcha.R;
import ph.edu.uscDCISMCatcha.databinding.FragmentInterestProfileBinding;
import ph.edu.uscDCISMCatcha.data.models.InterestModel;
import ph.edu.uscDCISMCatcha.viewmodel.student.InterestViewModel;

public class InterestProfileFragment extends Fragment {

    private FragmentInterestProfileBinding binding;
    private InterestViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentInterestProfileBinding.inflate(
                inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this)
                .get(InterestViewModel.class);

        observeInterestProfile();

        FirebaseUser currentUser =
                FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            viewModel.loadUserContent(currentUser.getUid());
        }
    }

    private void observeInterestProfile() {
        viewModel.getInterestProfile().observe(
                getViewLifecycleOwner(), profile -> {
                    if (profile == null) return;
                    renderInterestTags(profile);
                    renderTagWeights(profile);
                    renderStrengthBadge(profile);
                });
    }

    private void renderStrengthBadge(InterestModel profile) {
        int count = profile.getTagWeights() != null
                ? profile.getTagWeights().size() : 0;
        String strength = count >= 6 ? "Strong"
                : count >= 3 ? "Moderate" : "Building";
        binding.tvProfileStrength.setText(
                "Profile strength: " + strength
                        + " · " + count + " tags tracked");
    }

    private void renderInterestTags(InterestModel profile) {
        if (profile.getTagWeights() == null) return;
        binding.chipGroupInterests.removeAllViews();

        for (Map.Entry<String, Double> entry :
                profile.getTagWeights().entrySet()) {
            Chip chip = new Chip(requireContext());
            chip.setText(entry.getKey()
                    + " " + getDotsForWeight(entry.getValue()));
            chip.setChipBackgroundColorResource(
                    android.R.color.transparent);
            chip.setChipStrokeWidth(1f);
            chip.setClickable(false);
            binding.chipGroupInterests.addView(chip);
        }
    }

    private void renderTagWeights(InterestModel profile) {
        if (profile.getTagWeights() == null) return;
        binding.layoutTagWeights.removeAllViews();

        int[] colors = {
                Color.parseColor("#185FA5"),
                Color.parseColor("#F59E0B"),
                Color.parseColor("#22C55E"),
                Color.parseColor("#8B5CF6"),
                Color.parseColor("#EC4899"),
                Color.parseColor("#F97316")
        };
        int colorIndex = 0;

        for (Map.Entry<String, Double> entry :
                profile.getTagWeights().entrySet()) {
            View itemView = LayoutInflater.from(requireContext())
                    .inflate(R.layout.item_tag_weight,
                            binding.layoutTagWeights, false);

            TextView tvName =
                    itemView.findViewById(R.id.tvTagName);
            TextView tvWeight =
                    itemView.findViewById(R.id.tvTagWeight);
            ProgressBar progress =
                    itemView.findViewById(R.id.progressWeight);

            tvName.setText(entry.getKey());
            tvWeight.setText(String.format(
                    "%.2f", entry.getValue()));

            int color = colors[colorIndex % colors.length];
            tvWeight.setTextColor(color);
            progress.setProgressTintList(
                    android.content.res.ColorStateList
                            .valueOf(color));
            progress.setProgress(
                    (int) (entry.getValue() * 100));

            binding.layoutTagWeights.addView(itemView);
            colorIndex++;
        }
    }

    private String getDotsForWeight(double weight) {
        int filled = (int) Math.round(weight * 5);
        StringBuilder dots = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            dots.append(i < filled ? "●" : "○");
        }
        return dots.toString();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

package ph.edu.uscDCISMCatcha.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import ph.edu.uscDCISMCatcha.R;

public class InterestsActivity extends AppCompatActivity {

    // Main interest chips
    private Chip chipCreativeArts, chipTechInnovation, chipAcademicCareer,
            chipSportsWellness, chipCommunityService, chipFaithCulture;

    // Sub-category containers
    private FlexboxLayout subCreativeArts, subTechInnovation, subAcademicCareer,
            subSportsWellness, subCommunityService, subFaithCulture;

    // Track which sub-panel is currently open (null = none)
    private FlexboxLayout currentOpenSub = null;
    private Chip currentCheckedChip = null;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interests);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();
        TextView tvUserName = findViewById(R.id.tv_user_name);
        if (user != null && user.getDisplayName() != null && !user.getDisplayName().isEmpty()) {
            tvUserName.setText("OUR DEAR " + user.getDisplayName().toUpperCase() + " !");
        } else {
            // If display name is not set, try to fetch from Firestore or just use a generic name
            fetchUsernameAndSet(tvUserName);
        }

        chipCreativeArts     = findViewById(R.id.chip_creative_arts);
        chipTechInnovation   = findViewById(R.id.chip_tech_innovation);
        chipAcademicCareer   = findViewById(R.id.chip_academic_career);
        chipSportsWellness   = findViewById(R.id.chip_sports_wellness);
        chipCommunityService = findViewById(R.id.chip_community_service);
        chipFaithCulture     = findViewById(R.id.chip_faith_culture);

        subCreativeArts     = findViewById(R.id.sub_creative_arts);
        subTechInnovation   = findViewById(R.id.sub_tech_innovation);
        subAcademicCareer   = findViewById(R.id.sub_academic_career);
        subSportsWellness   = findViewById(R.id.sub_sports_wellness);
        subCommunityService = findViewById(R.id.sub_community_service);
        subFaithCulture     = findViewById(R.id.sub_faith_culture);

        setupChip(chipCreativeArts,     subCreativeArts);
        setupChip(chipTechInnovation,   subTechInnovation);
        setupChip(chipAcademicCareer,   subAcademicCareer);
        setupChip(chipSportsWellness,   subSportsWellness);
        setupChip(chipCommunityService, subCommunityService);
        setupChip(chipFaithCulture,     subFaithCulture);

        MaterialButton btnContinue = findViewById(R.id.btn_continue);
        TextView tvSkip = findViewById(R.id.tv_skip);

        btnContinue.setOnClickListener(v -> saveInterestsAndContinue());

        tvSkip.setOnClickListener(v -> {
            Intent intent = new Intent(InterestsActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void fetchUsernameAndSet(TextView tvUserName) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;
        db.collection("users").document(user.getUid()).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        String firstName = doc.getString("firstName");
                        if (firstName != null) {
                            tvUserName.setText("OUR DEAR " + firstName.toUpperCase() + " !");
                        }
                    }
                });
    }

    private void setupChip(Chip chip, FlexboxLayout subContainer) {
        chip.setOnClickListener(v -> {
            boolean isSameChip = currentCheckedChip == chip;

            // Close whatever is currently open
            if (currentOpenSub != null) {
                currentOpenSub.setVisibility(View.GONE);
            }
            if (currentCheckedChip != null) {
                currentCheckedChip.setChecked(false);
            }

            if (isSameChip) {
                currentOpenSub = null;
                currentCheckedChip = null;
            } else {
                subContainer.setVisibility(View.VISIBLE);
                chip.setChecked(true);
                currentOpenSub = subContainer;
                currentCheckedChip = chip;
            }
        });
    }

    private void saveInterestsAndContinue() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
            return;
        }

        List<String> selectedInterests = new ArrayList<>();
        // Collect all checked chips from all FlexboxLayouts
        collectSelectedChips(subCreativeArts, selectedInterests);
        collectSelectedChips(subTechInnovation, selectedInterests);
        collectSelectedChips(subAcademicCareer, selectedInterests);
        collectSelectedChips(subSportsWellness, selectedInterests);
        collectSelectedChips(subCommunityService, selectedInterests);
        collectSelectedChips(subFaithCulture, selectedInterests);

        if (selectedInterests.isEmpty()) {
            Toast.makeText(this, "Please select at least one interest or skip", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("users").document(user.getUid())
                .update("interests", selectedInterests, "interestsSelected", true)
                .addOnSuccessListener(aVoid -> {
                    Intent intent = new Intent(InterestsActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to save interests: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    // Fallback to home anyway
                    startActivity(new Intent(this, HomeActivity.class));
                    finish();
                });
    }

    private void collectSelectedChips(FlexboxLayout layout, List<String> list) {
        for (int i = 0; i < layout.getChildCount(); i++) {
            View v = layout.getChildAt(i);
            if (v instanceof Chip) {
                Chip chip = (Chip) v;
                if (chip.isChecked()) {
                    list.add(chip.getText().toString());
                }
            }
        }
    }
}

package ph.edu.uscDCISMCatcha.ui.student;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import ph.edu.uscDCISMCatcha.R;
import ph.edu.uscDCISMCatcha.ui.admin.AdminHomeActivity;
import ph.edu.uscDCISMCatcha.ui.org.OrgHomeActivity;

public class InterestsActivity extends AppCompatActivity {
    // Main interest chips
    private Chip chipCreativeArts, chipTechInnovation, chipAcademicCareer,
            chipSportsWellness, chipCommunityService, chipFaithCulture;
    // Sub-category containers
    private FlexboxLayout subCreativeArts, subTechInnovation, subAcademicCareer,
            subSportsWellness, subCommunityService, subFaithCulture;

    private TextView tvInterestsHint;
    private MaterialButton btnContinue;
    private final Set<String> selectedInterests = new HashSet<>();

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

        tvInterestsHint = findViewById(R.id.tv_interests_hint);
        btnContinue = findViewById(R.id.btn_continue);
        TextView tvSkip = findViewById(R.id.tv_skip);

        setupChip(chipCreativeArts,     subCreativeArts);
        setupChip(chipTechInnovation,   subTechInnovation);
        setupChip(chipAcademicCareer,   subAcademicCareer);
        setupChip(chipSportsWellness,   subSportsWellness);
        setupChip(chipCommunityService, subCommunityService);
        setupChip(chipFaithCulture,     subFaithCulture);

        // Setup sub-chips for all containers
        setupSubChips(subCreativeArts);
        setupSubChips(subTechInnovation);
        setupSubChips(subAcademicCareer);
        setupSubChips(subSportsWellness);
        setupSubChips(subCommunityService);
        setupSubChips(subFaithCulture);

        btnContinue.setOnClickListener(v -> saveInterestsAndNavigate());
        tvSkip.setOnClickListener(v -> saveInterestsAndNavigate());

        updateValidation();
    }

    private void saveInterestsAndNavigate() {
        String uid = mAuth.getUid();
        if (uid == null) {
            navigateToDashboard("Student");
            return;
        }

        btnContinue.setEnabled(false);
        btnContinue.setText("Saving...");

        db.collection("users").document(uid)
                .update("interests", new ArrayList<>(selectedInterests), "interestsSelected", true)
                .addOnCompleteListener(task -> {
                    db.collection("users").document(uid).get()
                            .addOnSuccessListener(doc -> {
                                String role = doc.getString("role");
                                navigateToDashboard(role);
                            })
                            .addOnFailureListener(e -> {
                                navigateToDashboard("Student");
                            });
                });
    }

    private void navigateToDashboard(String role) {
        Intent intent;
        if ("Admin".equalsIgnoreCase(role)) {
            intent = new Intent(this, AdminHomeActivity.class);
        } else if ("OrgHandler".equalsIgnoreCase(role) || "Organization".equalsIgnoreCase(role) || "Org".equalsIgnoreCase(role)) {
            intent = new Intent(this, OrgHomeActivity.class);
        } else {
            intent = new Intent(this, HomeActivity.class);
        }
        startActivity(intent);
        finish();
    }

    private void setupChip(Chip chip, FlexboxLayout subContainer) {
        if (chip == null) return;
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
                if (subContainer != null) {
                    subContainer.setVisibility(View.VISIBLE);
                }
                chip.setChecked(true);
                currentOpenSub = subContainer;
                currentCheckedChip = chip;
            }
        });
    }

    private void setupSubChips(FlexboxLayout container) {
        if (container == null) return;
        for (int i = 0; i < container.getChildCount(); i++) {
            View v = container.getChildAt(i);
            if (v instanceof Chip) {
                Chip subChip = (Chip) v;
                subChip.setCheckable(true);
                subChip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    String interest = subChip.getText().toString();
                    if (isChecked) {
                        selectedInterests.add(interest);
                        subChip.setChipBackgroundColorResource(R.color.soft_yellow);
                    } else {
                        selectedInterests.remove(interest);
                        subChip.setChipBackgroundColorResource(R.color.off_white);
                    }
                    updateValidation();
                });
            }
        }
    }

    private void updateValidation() {
        int count = selectedInterests.size();
        boolean isReady = count >= 3;
        btnContinue.setEnabled(isReady);

        if (isReady) {
            btnContinue.setAlpha(1.0f);
            tvInterestsHint.setVisibility(View.GONE);
            // Apply gold glow effect
            int glowColor = ContextCompat.getColor(this, R.color.soft_yellow);
            btnContinue.setStrokeColor(ColorStateList.valueOf(glowColor));
            btnContinue.setStrokeWidth((int) (3 * getResources().getDisplayMetrics().density));
            btnContinue.setTextColor(Color.BLACK);
        } else {
            btnContinue.setAlpha(0.5f);
            tvInterestsHint.setVisibility(View.VISIBLE);
            // Default greyed out state
            btnContinue.setStrokeColor(ColorStateList.valueOf(Color.parseColor("#CCCCCC")));
            btnContinue.setStrokeWidth((int) (1.5 * getResources().getDisplayMetrics().density));
            btnContinue.setTextColor(Color.parseColor("#888888"));
        }
    }
}

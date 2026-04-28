package ph.edu.uscDCISMCatcha.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interests);

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

        btnContinue.setOnClickListener(v -> {
            Intent intent = new Intent(InterestsActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        });

        tvSkip.setOnClickListener(v -> {
            Intent intent = new Intent(InterestsActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
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
}
package ph.edu.uscDCISMCatcha.ui.student;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
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

    private TextView tvInterestsHint;
    private MaterialButton btnContinue;
    private final Set<String> selectedInterests = new HashSet<>();

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

        tvInterestsHint = findViewById(R.id.tv_interests_hint);
        btnContinue = findViewById(R.id.btn_continue);
        TextView tvSkip = findViewById(R.id.tv_skip);

        // SAS Departments
        setupDept(R.id.chip_dcism, R.id.sub_dcism);
        setupDept(R.id.chip_dmb,   R.id.sub_dmb);
        setupDept(R.id.chip_dcomm, R.id.sub_dcomm);
        setupDept(R.id.chip_dpa,   R.id.sub_dpa);
        setupDept(R.id.chip_dpsy,  R.id.sub_dpsy);
        setupDept(R.id.chip_dphi,  R.id.sub_dphi);

        // SOE Departments
        setupDept(R.id.chip_dcpe,  R.id.sub_dcpe);
        setupDept(R.id.chip_dce,   R.id.sub_dce);
        setupDept(R.id.chip_dmme,  R.id.sub_dmme);
        setupDept(R.id.chip_dche,  R.id.sub_dche);
        setupDept(R.id.chip_die,   R.id.sub_die);
        setupDept(R.id.chip_deee,  R.id.sub_deee);

        btnContinue.setOnClickListener(v -> saveInterestsAndNavigate());
        tvSkip.setOnClickListener(v -> saveInterestsAndNavigate());

        updateValidation();
    }

    private void setupDept(int chipId, int subId) {
        Chip chip = findViewById(chipId);
        FlexboxLayout subContainer = findViewById(subId);

        if (chip != null) {
            chip.setOnClickListener(v -> {
                boolean isSameChip = currentCheckedChip == chip;
                if (currentOpenSub != null) {
                    currentOpenSub.setVisibility(View.GONE);
                }
                
                // Uncheck all main chips in the same group or globally
                // For simplicity, just uncheck the previously checked one
                if (currentCheckedChip != null) {
                    currentCheckedChip.setChecked(false);
                }

                if (isSameChip) {
                    currentOpenSub = null;
                    currentCheckedChip = null;
                    chip.setChecked(false);
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

        if (subContainer != null) {
            setupSubChips(subContainer);
        }
    }

    private void setupSubChips(FlexboxLayout container) {
        for (int i = 0; i < container.getChildCount(); i++) {
            View v = container.getChildAt(i);
            if (v instanceof Chip) {
                Chip subChip = (Chip) v;
                subChip.setCheckable(true);
                subChip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    String interest = subChip.getText().toString();
                    if (isChecked) {
                        selectedInterests.add(interest);
                    } else {
                        selectedInterests.remove(interest);
                    }
                    updateValidation();
                });
            }
        }
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

    private void updateValidation() {
        int count = selectedInterests.size();
        boolean isReady = count >= 3;
        btnContinue.setEnabled(isReady);

        if (isReady) {
            btnContinue.setAlpha(1.0f);
            tvInterestsHint.setVisibility(View.GONE);
            int glowColor = ContextCompat.getColor(this, R.color.soft_yellow);
            btnContinue.setStrokeColor(ColorStateList.valueOf(glowColor));
            btnContinue.setStrokeWidth((int) (3 * getResources().getDisplayMetrics().density));
            btnContinue.setTextColor(Color.BLACK);
        } else {
            btnContinue.setAlpha(0.5f);
            tvInterestsHint.setVisibility(View.VISIBLE);
            btnContinue.setStrokeColor(ColorStateList.valueOf(Color.parseColor("#CCCCCC")));
            btnContinue.setStrokeWidth((int) (1.5 * getResources().getDisplayMetrics().density));
            btnContinue.setTextColor(Color.parseColor("#888888"));
        }
    }
}

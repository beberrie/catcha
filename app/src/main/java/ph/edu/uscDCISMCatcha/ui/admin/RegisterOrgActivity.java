package ph.edu.uscDCISMCatcha.ui.admin;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.UUID;

import ph.edu.uscDCISMCatcha.R;
import ph.edu.uscDCISMCatcha.data.models.Organization;

public class RegisterOrgActivity extends AppCompatActivity {

    private TextInputEditText etOrgName, etDescription, etOwnerEmail, etSchool, etDepartment;
    private MaterialAutoCompleteTextView actvUniversity, actvCategory;
    private Button btnRegisterOrg;
    private TextView tvTitle;
    private TextInputLayout tilOwnerEmail;
    private FirebaseFirestore db;
    private Organization existingOrg;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_register_org);

        db = FirebaseFirestore.getInstance();

        tvTitle = findViewById(R.id.tvTitle);
        etOrgName = findViewById(R.id.etOrgName);
        etSchool = findViewById(R.id.etSchool);
        etDepartment = findViewById(R.id.etDepartment);
        etDescription = findViewById(R.id.etDescription);
        etOwnerEmail = findViewById(R.id.etOwnerEmail);
        tilOwnerEmail = (TextInputLayout) etOwnerEmail.getParent().getParent(); // Getting TextInputLayout
        actvUniversity = findViewById(R.id.actvUniversity);
        actvCategory = findViewById(R.id.actvCategory);
        btnRegisterOrg = findViewById(R.id.btnRegisterOrg);

        setupDropdowns();

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Check if we are in Edit Mode
        if (getIntent().hasExtra("ORGANIZATION")) {
            existingOrg = (Organization) getIntent().getSerializableExtra("ORGANIZATION");
            if (existingOrg != null) {
                isEditMode = true;
                populateFields();
            }
        }

        btnRegisterOrg.setOnClickListener(v -> {
            if (isEditMode) {
                updateOrganization();
            } else {
                validateAndRegister();
            }
        });
    }

    private void populateFields() {
        tvTitle.setText("Edit Organization");
        btnRegisterOrg.setText("Update Organization");
        
        etOrgName.setText(existingOrg.getName());
        actvUniversity.setText(existingOrg.getUniversity(), false);
        etSchool.setText(existingOrg.getSchool());
        etDepartment.setText(existingOrg.getDepartment());
        etDescription.setText(existingOrg.getDescription());
        actvCategory.setText(existingOrg.getCategory(), false);
        
        // Hide owner email in edit mode as we don't support changing owner here yet for simplicity,
        // or just disable it.
        tilOwnerEmail.setVisibility(View.GONE);
    }

    private void setupDropdowns() {
        String[] universities = getResources().getStringArray(R.array.universities);
        ArrayAdapter<String> uniAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, universities);
        actvUniversity.setAdapter(uniAdapter);

        String[] categories = getResources().getStringArray(R.array.interest_categories);
        ArrayAdapter<String> catAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, categories);
        actvCategory.setAdapter(catAdapter);
    }

    private void validateAndRegister() {
        String name = etOrgName.getText().toString().trim();
        String uni = actvUniversity.getText().toString().trim();
        String school = etSchool.getText().toString().trim();
        String dept = etDepartment.getText().toString().trim();
        String desc = etDescription.getText().toString().trim();
        String cat = actvCategory.getText().toString().trim();
        String email = etOwnerEmail.getText().toString().trim();

        if (name.isEmpty() || uni.isEmpty() || school.isEmpty() || dept.isEmpty() || desc.isEmpty() || cat.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        btnRegisterOrg.setEnabled(false);
        btnRegisterOrg.setText("Processing...");

        db.collection("users").whereEqualTo("email", email).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        QueryDocumentSnapshot userDoc = (QueryDocumentSnapshot) task.getResult().getDocuments().get(0);
                        String ownerUid = userDoc.getId();
                        createOrganization(name, uni, school, dept, desc, cat, ownerUid);
                    } else {
                        btnRegisterOrg.setEnabled(true);
                        btnRegisterOrg.setText("Create Organization");
                        Toast.makeText(this, "User email not found.", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(e -> {
                    btnRegisterOrg.setEnabled(true);
                    btnRegisterOrg.setText("Create Organization");
                    Toast.makeText(this, "Search error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void createOrganization(String name, String uni, String school, String dept, String desc, String cat, String ownerUid) {
        String orgId = UUID.randomUUID().toString();
        Organization newOrg = new Organization(name, uni, school, dept, desc, cat, "", ownerUid);

        db.collection("organizations").document(orgId).set(newOrg)
                .addOnSuccessListener(aVoid -> {
                    db.collection("users").document(ownerUid).update("role", "OrgHandler")
                            .addOnSuccessListener(aVoid2 -> {
                                Toast.makeText(this, name + " successfully registered!", Toast.LENGTH_LONG).show();
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Org created, but failed to promote owner.", Toast.LENGTH_LONG).show();
                                finish();
                            });
                })
                .addOnFailureListener(e -> {
                    btnRegisterOrg.setEnabled(true);
                    btnRegisterOrg.setText("Create Organization");
                    Toast.makeText(this, "Creation error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void updateOrganization() {
        String name = etOrgName.getText().toString().trim();
        String uni = actvUniversity.getText().toString().trim();
        String school = etSchool.getText().toString().trim();
        String dept = etDepartment.getText().toString().trim();
        String desc = etDescription.getText().toString().trim();
        String cat = actvCategory.getText().toString().trim();

        if (name.isEmpty() || uni.isEmpty() || school.isEmpty() || dept.isEmpty() || desc.isEmpty() || cat.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        btnRegisterOrg.setEnabled(false);
        btnRegisterOrg.setText("Updating...");

        existingOrg.setName(name);
        existingOrg.setUniversity(uni);
        existingOrg.setSchool(school);
        existingOrg.setDepartment(dept);
        existingOrg.setDescription(desc);
        existingOrg.setCategory(cat);

        db.collection("organizations").document(existingOrg.getId()).set(existingOrg)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Organization updated successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    btnRegisterOrg.setEnabled(true);
                    btnRegisterOrg.setText("Update Organization");
                    Toast.makeText(this, "Update error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}

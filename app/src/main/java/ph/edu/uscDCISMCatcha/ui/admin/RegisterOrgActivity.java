package ph.edu.uscDCISMCatcha.ui.admin;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.UUID;

import ph.edu.uscDCISMCatcha.R;
import ph.edu.uscDCISMCatcha.data.models.Organization;

public class RegisterOrgActivity extends AppCompatActivity {

    private TextInputEditText etOrgName, etDescription, etOwnerEmail;
    private MaterialAutoCompleteTextView actvUniversity, actvCategory;
    private Button btnRegisterOrg;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_register_org);

        db = FirebaseFirestore.getInstance();

        etOrgName = findViewById(R.id.etOrgName);
        etDescription = findViewById(R.id.etDescription);
        etOwnerEmail = findViewById(R.id.etOwnerEmail);
        actvUniversity = findViewById(R.id.actvUniversity);
        actvCategory = findViewById(R.id.actvCategory);
        btnRegisterOrg = findViewById(R.id.btnRegisterOrg);

        setupDropdowns();

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        btnRegisterOrg.setOnClickListener(v -> validateAndRegister());
    }

    private void setupDropdowns() {
        // Populating Universities from strings.xml
        String[] universities = getResources().getStringArray(R.array.universities);
        ArrayAdapter<String> uniAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, universities);
        actvUniversity.setAdapter(uniAdapter);

        // Populating Categories from interest_categories in strings.xml
        String[] categories = getResources().getStringArray(R.array.interest_categories);
        ArrayAdapter<String> catAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, categories);
        actvCategory.setAdapter(catAdapter);
    }

    private void validateAndRegister() {
        String name = etOrgName.getText().toString().trim();
        String uni = actvUniversity.getText().toString().trim();
        String desc = etDescription.getText().toString().trim();
        String cat = actvCategory.getText().toString().trim();
        String email = etOwnerEmail.getText().toString().trim();

        if (name.isEmpty() || uni.isEmpty() || desc.isEmpty() || cat.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        btnRegisterOrg.setEnabled(false);
        btnRegisterOrg.setText("Processing...");

        // 1. Find the User UID by Email in Firestore
        db.collection("users").whereEqualTo("email", email).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        // Get the first user found with this email
                        QueryDocumentSnapshot userDoc = (QueryDocumentSnapshot) task.getResult().getDocuments().get(0);
                        String ownerUid = userDoc.getId();

                        createOrganization(name, uni, desc, cat, ownerUid);
                    } else {
                        btnRegisterOrg.setEnabled(true);
                        btnRegisterOrg.setText("Create Organization");
                        Toast.makeText(this, "User email not found. They must register an account first.", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(e -> {
                    btnRegisterOrg.setEnabled(true);
                    btnRegisterOrg.setText("Create Organization");
                    Toast.makeText(this, "Search error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void createOrganization(String name, String uni, String desc, String cat, String ownerUid) {
        String orgId = UUID.randomUUID().toString();
        // Creating the Organization model object
        Organization newOrg = new Organization(name, uni, desc, cat, "", ownerUid);

        // 2. Save the Organization to Firestore
        db.collection("organizations").document(orgId).set(newOrg)
                .addOnSuccessListener(aVoid -> {
                    // 3. Promote the User to OrgHandler role
                    db.collection("users").document(ownerUid).update("role", "OrgHandler")
                            .addOnSuccessListener(aVoid2 -> {
                                Toast.makeText(this, name + " successfully registered!", Toast.LENGTH_LONG).show();
                                finish(); // Close activity on success
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Org created, but failed to promote owner: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                finish();
                            });
                })
                .addOnFailureListener(e -> {
                    btnRegisterOrg.setEnabled(true);
                    btnRegisterOrg.setText("Create Organization");
                    Toast.makeText(this, "Creation error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
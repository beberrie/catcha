package ph.edu.uscDCISMCatcha.ui.admin;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.UUID;

import ph.edu.uscDCISMCatcha.R;
import ph.edu.uscDCISMCatcha.data.models.Organization;
import ph.edu.uscDCISMCatcha.data.repository.FirebaseRemoteDataSource;

public class RegisterOrgActivity extends AppCompatActivity {

    private TextInputEditText etOrgName, etDescription, etOwnerEmail, etSchool, etDepartment;
    private TextInputEditText etProfileImageUrl, etBannerImageUrl;
    private MaterialAutoCompleteTextView actvUniversity, actvCategory;
    private Button btnRegisterOrg;
    private TextView tvTitle;
    private TextInputLayout tilOwnerEmail;
    private ImageView ivProfilePreview, ivBannerPreview;
    
    private FirebaseFirestore db;
    private FirebaseRemoteDataSource dataSource;
    private Organization existingOrg;
    private boolean isEditMode = false;
    
    private Uri profileUri, bannerUri;

    private final ActivityResultLauncher<String> profilePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    profileUri = uri;
                    Glide.with(this).load(uri).into(ivProfilePreview);
                }
            }
    );

    private final ActivityResultLauncher<String> bannerPickerLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    bannerUri = uri;
                    Glide.with(this).load(uri).into(ivBannerPreview);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_register_org);

        db = FirebaseFirestore.getInstance();
        dataSource = new FirebaseRemoteDataSource();

        tvTitle = findViewById(R.id.tvTitle);
        etOrgName = findViewById(R.id.etOrgName);
        etSchool = findViewById(R.id.etSchool);
        etDepartment = findViewById(R.id.etDepartment);
        etDescription = findViewById(R.id.etDescription);
        etOwnerEmail = findViewById(R.id.etOwnerEmail);
        etProfileImageUrl = findViewById(R.id.etProfileImageUrl);
        etBannerImageUrl = findViewById(R.id.etBannerImageUrl);
        tilOwnerEmail = findViewById(R.id.tilOwnerEmail);
        actvUniversity = findViewById(R.id.actvUniversity);
        actvCategory = findViewById(R.id.actvCategory);
        btnRegisterOrg = findViewById(R.id.btnRegisterOrg);
        ivProfilePreview = findViewById(R.id.ivProfilePreview);
        ivBannerPreview = findViewById(R.id.ivBannerPreview);

        setupDropdowns();

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        findViewById(R.id.btnPickProfile).setOnClickListener(v -> profilePickerLauncher.launch("image/*"));
        findViewById(R.id.btnPickBanner).setOnClickListener(v -> bannerPickerLauncher.launch("image/*"));

        // Check if we are in Edit Mode
        if (getIntent().hasExtra("ORGANIZATION")) {
            existingOrg = (Organization) getIntent().getSerializableExtra("ORGANIZATION");
            if (existingOrg != null) {
                isEditMode = true;
                populateFields();
            }
        }

        btnRegisterOrg.setOnClickListener(v -> {
            if (validateInputs()) {
                uploadImagesAndSubmit();
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
        etProfileImageUrl.setText(existingOrg.getProfileImageUrl());
        etBannerImageUrl.setText(existingOrg.getBannerImageUrl());

        if (existingOrg.getProfileImageUrl() != null && !existingOrg.getProfileImageUrl().isEmpty()) {
            Glide.with(this).load(existingOrg.getProfileImageUrl()).into(ivProfilePreview);
        }
        if (existingOrg.getBannerImageUrl() != null && !existingOrg.getBannerImageUrl().isEmpty()) {
            Glide.with(this).load(existingOrg.getBannerImageUrl()).into(ivBannerPreview);
        }

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

    private boolean validateInputs() {
        String name = etOrgName.getText().toString().trim();
        String uni = actvUniversity.getText().toString().trim();
        String school = etSchool.getText().toString().trim();
        String dept = etDepartment.getText().toString().trim();
        String desc = etDescription.getText().toString().trim();
        String cat = actvCategory.getText().toString().trim();
        String email = etOwnerEmail.getText().toString().trim();

        if (name.isEmpty() || uni.isEmpty() || school.isEmpty() || dept.isEmpty() || desc.isEmpty() || cat.isEmpty()) {
            Toast.makeText(this, "Organization details are required", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!isEditMode && email.isEmpty()) {
            Toast.makeText(this, "Owner email is required", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void uploadImagesAndSubmit() {
        if (profileUri != null) {
            btnRegisterOrg.setEnabled(false);
            btnRegisterOrg.setText("Uploading Profile...");
            dataSource.uploadImage(profileUri, "org_profiles")
                    .addOnSuccessListener(url -> {
                        etProfileImageUrl.setText(url);
                        profileUri = null;
                        uploadImagesAndSubmit();
                    })
                    .addOnFailureListener(this::handleUploadError);
            return;
        }

        if (bannerUri != null) {
            btnRegisterOrg.setEnabled(false);
            btnRegisterOrg.setText("Uploading Banner...");
            dataSource.uploadImage(bannerUri, "org_banners")
                    .addOnSuccessListener(url -> {
                        etBannerImageUrl.setText(url);
                        bannerUri = null;
                        uploadImagesAndSubmit();
                    })
                    .addOnFailureListener(this::handleUploadError);
            return;
        }

        if (isEditMode) {
            updateOrganization();
        } else {
            validateAndRegister();
        }
    }

    private void handleUploadError(Exception e) {
        btnRegisterOrg.setEnabled(true);
        btnRegisterOrg.setText(isEditMode ? "Update Organization" : "Create Organization");
        Toast.makeText(this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
    }

    private void validateAndRegister() {
        String email = etOwnerEmail.getText().toString().trim();
        
        btnRegisterOrg.setEnabled(false);
        btnRegisterOrg.setText("Processing...");

        db.collection("users").whereEqualTo("email", email).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        QueryDocumentSnapshot userDoc = (QueryDocumentSnapshot) task.getResult().getDocuments().get(0);
                        String ownerUid = userDoc.getId();

                        String name = etOrgName.getText().toString().trim();
                        String uni = actvUniversity.getText().toString().trim();
                        String school = etSchool.getText().toString().trim();
                        String dept = etDepartment.getText().toString().trim();
                        String desc = etDescription.getText().toString().trim();
                        String cat = actvCategory.getText().toString().trim();
                        String profileUrl = etProfileImageUrl.getText().toString().trim();
                        String bannerUrl = etBannerImageUrl.getText().toString().trim();

                        createOrganization(name, uni, school, dept, desc, cat, ownerUid, profileUrl, bannerUrl);
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

    private void createOrganization(String name, String uni, String school, String dept, String desc, String cat, String ownerUid, String profileUrl, String bannerUrl) {
        String orgId = UUID.randomUUID().toString();
        Organization newOrg = new Organization(name, uni, school, dept, desc, cat, profileUrl, ownerUid);
        newOrg.setBannerImageUrl(bannerUrl);
        newOrg.setId(orgId);

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
        String profileUrl = etProfileImageUrl.getText().toString().trim();
        String bannerUrl = etBannerImageUrl.getText().toString().trim();

        btnRegisterOrg.setEnabled(false);
        btnRegisterOrg.setText("Updating...");

        existingOrg.setName(name);
        existingOrg.setUniversity(uni);
        existingOrg.setSchool(school);
        existingOrg.setDepartment(dept);
        existingOrg.setDescription(desc);
        existingOrg.setCategory(cat);
        existingOrg.setProfileImageUrl(profileUrl);
        existingOrg.setBannerImageUrl(bannerUrl);

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

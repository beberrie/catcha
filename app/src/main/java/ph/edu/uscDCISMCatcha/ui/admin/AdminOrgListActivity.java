package ph.edu.uscDCISMCatcha.ui.admin;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import ph.edu.uscDCISMCatcha.R;
import ph.edu.uscDCISMCatcha.data.models.Organization;
import ph.edu.uscDCISMCatcha.ui.adapters.OrganizationAdapter;

public class AdminOrgListActivity extends AppCompatActivity implements OrganizationAdapter.OnOrgActionListener {

    private AutoCompleteTextView actvUniversity;
    private RecyclerView rvOrganizations;
    private TextView tvEmptyState;
    private FirebaseFirestore db;
    private List<Organization> organizationList;
    private OrganizationAdapter adapter;
    private String currentUniversity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_org_list);

        db = FirebaseFirestore.getInstance();
        organizationList = new ArrayList<>();
        adapter = new OrganizationAdapter(organizationList, true); // Pass true for isAdmin
        adapter.setOnOrgActionListener(this);

        actvUniversity = findViewById(R.id.actvUniversity);
        rvOrganizations = findViewById(R.id.rvOrganizations);
        tvEmptyState = findViewById(R.id.tvEmptyState);

        rvOrganizations.setLayoutManager(new LinearLayoutManager(this));
        rvOrganizations.setAdapter(adapter);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        setupUniversityDropdown();
    }

    private void setupUniversityDropdown() {
        String[] universities = getResources().getStringArray(R.array.universities);
        ArrayAdapter<String> uniAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, universities);
        actvUniversity.setAdapter(uniAdapter);

        actvUniversity.setOnItemClickListener((parent, view, position, id) -> {
            currentUniversity = (String) parent.getItemAtPosition(position);
            fetchOrganizations(currentUniversity);
        });
    }

    private void fetchOrganizations(String university) {
        db.collection("organizations")
                .whereEqualTo("university", university)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    organizationList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Organization org = document.toObject(Organization.class);
                        org.setId(document.getId());
                        organizationList.add(org);
                    }
                    adapter.notifyDataSetChanged();

                    if (organizationList.isEmpty()) {
                        tvEmptyState.setVisibility(View.VISIBLE);
                        rvOrganizations.setVisibility(View.GONE);
                    } else {
                        tvEmptyState.setVisibility(View.GONE);
                        rvOrganizations.setVisibility(View.VISIBLE);
                    }
                });
    }

    @Override
    public void onEdit(Organization org) {
        Intent intent = new Intent(this, RegisterOrgActivity.class);
        intent.putExtra("ORGANIZATION", org);
        startActivity(intent);
    }

    @Override
    public void onDelete(Organization org) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Organization")
                .setMessage("Are you sure you want to delete " + org.getName() + "? This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    db.collection("organizations").document(org.getId()).delete()
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(this, "Organization deleted", Toast.LENGTH_SHORT).show();
                                if (currentUniversity != null) {
                                    fetchOrganizations(currentUniversity);
                                }
                            })
                            .addOnFailureListener(e -> Toast.makeText(this, "Error deleting organization", Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onJoin(Organization org) {
        // Not used in Admin side
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (currentUniversity != null) {
            fetchOrganizations(currentUniversity);
        }
    }
}

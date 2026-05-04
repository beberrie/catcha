package ph.edu.uscDCISMCatcha.ui.admin;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

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

public class AdminOrgListActivity extends AppCompatActivity {

    private AutoCompleteTextView actvUniversity;
    private RecyclerView rvOrganizations;
    private TextView tvEmptyState;
    private FirebaseFirestore db;
    private List<Organization> organizationList;
    private OrganizationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_org_list);

        db = FirebaseFirestore.getInstance();
        organizationList = new ArrayList<>();
        adapter = new OrganizationAdapter(organizationList);

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
            String selectedUni = (String) parent.getItemAtPosition(position);
            fetchOrganizations(selectedUni);
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
}

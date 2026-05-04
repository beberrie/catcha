package ph.edu.uscDCISMCatcha.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import ph.edu.uscDCISMCatcha.R;
import ph.edu.uscDCISMCatcha.ui.auth.LoginActivity;

public class AdminHomeActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private TextView tvTotalOrgs, tvTotalEvents;
    private MaterialCardView cardAddOrg, cardViewOrgs, cardManageUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_home);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        tvTotalOrgs = findViewById(R.id.tvTotalOrgs);
        tvTotalEvents = findViewById(R.id.tvTotalEvents);
        cardAddOrg = findViewById(R.id.cardAddOrg);
        cardViewOrgs = findViewById(R.id.cardViewOrgs);
        cardManageUsers = findViewById(R.id.cardManageUsers);

        fetchStats();

        // Redirect to RegisterOrgActivity when clicking "Register New Org"
        cardAddOrg.setOnClickListener(v -> {
            Intent intent = new Intent(AdminHomeActivity.this, RegisterOrgActivity.class);
            startActivity(intent);
        });

        // Redirect to AdminOrgListActivity when clicking "Organization List"
        cardViewOrgs.setOnClickListener(v -> {
            Intent intent = new Intent(AdminHomeActivity.this, AdminOrgListActivity.class);
            startActivity(intent);
        });

        cardManageUsers.setOnClickListener(v -> {
            // TODO: Open User Management Activity
            Toast.makeText(this, "Opening User Management...", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.btnLogout).setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(AdminHomeActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void fetchStats() {
        // Fetch Total Organizations
        db.collection("organizations").addSnapshotListener((value, error) -> {
            if (value != null) {
                tvTotalOrgs.setText(String.valueOf(value.size()));
            }
        });

        // Fetch Total Events
        db.collection("events").addSnapshotListener((value, error) -> {
            if (value != null) {
                tvTotalEvents.setText(String.valueOf(value.size()));
            }
        });
    }
}

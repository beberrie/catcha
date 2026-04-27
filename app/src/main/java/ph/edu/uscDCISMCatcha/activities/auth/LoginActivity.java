package ph.edu.uscDCISMCatcha.activities.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import ph.edu.uscDCISMCatcha.R;
import ph.edu.uscDCISMCatcha.activities.HomeActivity;
import ph.edu.uscDCISMCatcha.activities.OrgHomeActivity;
import ph.edu.uscDCISMCatcha.activities.admin.AdminHomeActivity;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvSignupLink, tvResetLink, tvSkip;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.auth_activity_login);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvSignupLink = findViewById(R.id.tvSignupLink);
        tvResetLink = findViewById(R.id.tvResetLink);
        tvSkip = findViewById(R.id.tvSkip);

        btnLogin.setOnClickListener(v -> loginUser());
        tvSignupLink.setOnClickListener(v -> startActivity(new Intent(this, SignupActivity.class)));
        tvResetLink.setOnClickListener(v -> resetPassword());
        tvSkip.setOnClickListener(v -> {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        });
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        btnLogin.setEnabled(false);
        btnLogin.setText("Logging in...");

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        checkUserRoleAndNavigate();
                    } else {
                        btnLogin.setEnabled(true);
                        btnLogin.setText(R.string.login_button);
                        String errorMsg = task.getException() != null ? task.getException().getMessage() : "Authentication failed";
                        Toast.makeText(LoginActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkUserRoleAndNavigate() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        // Bypassing email verification check as requested
        db.collection("users").document(user.getUid()).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        navigateToDashboard(doc.getString("role"));
                    } else {
                        Toast.makeText(this, "User profile not found.", Toast.LENGTH_SHORT).show();
                        mAuth.signOut();
                        btnLogin.setEnabled(true);
                        btnLogin.setText(R.string.login_button);
                    }
                })
                .addOnFailureListener(e -> {
                    btnLogin.setEnabled(true);
                    btnLogin.setText(R.string.login_button);
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void resetPassword() {
        String email = etEmail.getText().toString().trim();
        if (email.isEmpty()) {
            Toast.makeText(this, "Enter your email to reset password", Toast.LENGTH_SHORT).show();
            return;
        }
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> Toast.makeText(this, 
                    task.isSuccessful() ? "Reset link sent!" : "Error: " + task.getException().getMessage(), 
                    Toast.LENGTH_SHORT).show());
    }

    private void navigateToDashboard(String role) {
        Intent intent;
        if ("Admin".equals(role)) intent = new Intent(this, AdminHomeActivity.class);
        else if ("OrgHandler".equals(role)) intent = new Intent(this, OrgHomeActivity.class);
        else intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}

package ph.edu.uscDCISMCatcha.ui.student;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import ph.edu.uscDCISMCatcha.R;
import ph.edu.uscDCISMCatcha.ui.auth.LoginActivity;

public class UserProfileFragment extends Fragment {

    private TextView tvUserName, tvUsernameHandle, tvUniversity, tvEmail, tvOrgCount, tvEventCount;
    private MaterialButton btnSettings;
    private SwitchMaterial switchPrivacy;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        tvUserName = view.findViewById(R.id.tvUserName);
        tvUsernameHandle = view.findViewById(R.id.tvUsernameHandle);
        tvUniversity = view.findViewById(R.id.tvUniversity);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvOrgCount = view.findViewById(R.id.tvOrgCount);
        tvEventCount = view.findViewById(R.id.tvEventCount);
        btnSettings = view.findViewById(R.id.btnSettings);
        switchPrivacy = view.findViewById(R.id.switchPrivacy);

        loadUserData();

        btnSettings.setOnClickListener(v -> showSettingsDialog());

        // Save privacy toggle to Firestore when changed
        switchPrivacy.setOnCheckedChangeListener((buttonView, isChecked) -> {
            FirebaseUser user = mAuth.getCurrentUser();
            if (user == null) return;

            Map<String, Object> update = new HashMap<>();
            update.put("isPrivate", isChecked);

            db.collection("users").document(user.getUid())
                    .update(update)
                    .addOnSuccessListener(a -> {
                        String msg = isChecked ? "Attendance hidden" : "Attendance visible";
                        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(getContext(), "Failed to update privacy", Toast.LENGTH_SHORT).show()
                    );
        });

        return view;
    }

    private void loadUserData() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        db.collection("users").document(user.getUid()).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        String firstName = doc.getString("firstName");
                        String lastName = doc.getString("lastName");
                        String username = doc.getString("username");
                        String university = doc.getString("university");
                        String email = doc.getString("email");

                        tvUserName.setText(firstName + " " + lastName);
                        tvUsernameHandle.setText("@" + username);
                        tvUniversity.setText(university);
                        tvEmail.setText(email);

                        // Load privacy toggle state
                        Boolean isPrivate = doc.getBoolean("isPrivate");
                        if (isPrivate != null) {
                            switchPrivacy.setChecked(isPrivate);
                        }
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Error loading profile", Toast.LENGTH_SHORT).show()
                );

        tvOrgCount.setText("3");
        tvEventCount.setText("5");
    }

    private void showSettingsDialog() {
        String[] options = {"Log Out", "Cancel"};
        new AlertDialog.Builder(requireContext())
                .setTitle("Settings")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) logout();
                })
                .show();
    }

    private void logout() {
        mAuth.signOut();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }
}
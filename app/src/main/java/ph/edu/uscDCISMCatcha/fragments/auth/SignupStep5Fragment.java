package ph.edu.uscDCISMCatcha.fragments.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import ph.edu.uscDCISMCatcha.R;
import ph.edu.uscDCISMCatcha.activities.InterestsActivity;
import ph.edu.uscDCISMCatcha.models.UserModel;
import ph.edu.uscDCISMCatcha.viewmodel.SignupViewModel;

public class SignupStep5Fragment extends Fragment {

    private TextInputEditText etUsername;
    private Button btnFinish;
    private TextView tvLoginLink;
    private SignupViewModel viewModel;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.auth_activity_signup_step5, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        viewModel = new ViewModelProvider(requireActivity()).get(SignupViewModel.class);

        etUsername = view.findViewById(R.id.etUsername);
        btnFinish = view.findViewById(R.id.btnFinish);
        tvLoginLink = view.findViewById(R.id.tvLoginLink);

        tvLoginLink.setOnClickListener(v -> requireActivity().finish());

        btnFinish.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();

            if (username.isEmpty()) {
                Toast.makeText(requireContext(), "Please choose a username", Toast.LENGTH_SHORT).show();
            } else {
                viewModel.setUsername(username);
                completeRegistration();
            }
        });

        return view;
    }

    private void completeRegistration() {
        btnFinish.setEnabled(false);
        btnFinish.setText("Creating Account...");

        String email = viewModel.getEmail().getValue();
        String password = viewModel.getPassword().getValue();

        if (email == null || password == null) {
            Toast.makeText(requireContext(), "Registration data missing. Please restart.", Toast.LENGTH_LONG).show();
            btnFinish.setEnabled(true);
            btnFinish.setText(R.string.finish_button);
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        saveUserToFirestore(user.getUid());
                    }
                } else {
                    btnFinish.setEnabled(true);
                    btnFinish.setText(R.string.finish_button);
                    String error = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                    Toast.makeText(requireContext(), "Auth Error: " + error, Toast.LENGTH_LONG).show();
                }
            });
    }

    private void saveUserToFirestore(String uid) {
        UserModel userProfile = new UserModel(
            viewModel.getFirstName().getValue(),
            viewModel.getLastName().getValue(),
            viewModel.getUsername().getValue(),
            viewModel.getEmail().getValue(),
            viewModel.getUniversity().getValue(),
            "General",
            "Student" 
        );

        db.collection("users").document(uid)
            .set(userProfile)
            .addOnSuccessListener(aVoid -> {
                Toast.makeText(requireContext(), "Welcome to Catcha!", Toast.LENGTH_LONG).show();
                // Navigate to InterestsActivity
                Intent intent = new Intent(requireActivity(), InterestsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                requireActivity().finish();
            })
            .addOnFailureListener(e -> {
                btnFinish.setEnabled(true);
                btnFinish.setText(R.string.finish_button);
                Toast.makeText(requireContext(), "Database Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
    }
}

package ph.edu.uscDCISMCatcha.fragments.auth;

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

import com.google.android.material.textfield.TextInputEditText;

import ph.edu.uscDCISMCatcha.R;
import ph.edu.uscDCISMCatcha.activities.auth.SignupActivity;

public class SignupStep5Fragment extends Fragment {

    private TextInputEditText etPassword, etConfirmPassword;
    private Button btnNext;
    private TextView tvLoginLink;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.auth_activity_signup_step5, container, false);

        etPassword = view.findViewById(R.id.etPassword);
        etConfirmPassword = view.findViewById(R.id.etConfirmPassword);
        btnNext = view.findViewById(R.id.btnNext);
        tvLoginLink = view.findViewById(R.id.tvLoginLink);

        tvLoginLink.setOnClickListener(v -> requireActivity().finish());

        btnNext.setOnClickListener(v -> {
            String password = etPassword.getText().toString();
            String confirmPassword = etConfirmPassword.getText().toString();

            if (password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            } else if (!password.equals(confirmPassword)) {
                Toast.makeText(requireContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
            } else if (password.length() < 6) {
                Toast.makeText(requireContext(), "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            } else {
                ((SignupActivity) requireActivity()).navigateToStep6();
            }
        });

        return view;
    }
}
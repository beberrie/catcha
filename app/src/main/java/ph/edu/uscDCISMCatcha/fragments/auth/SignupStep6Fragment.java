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

public class SignupStep6Fragment extends Fragment {

    private TextInputEditText etUsername;
    private Button btnFinish;
    private TextView tvLoginLink;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.auth_activity_signup_step6, container, false);

        etUsername = view.findViewById(R.id.etUsername);
        btnFinish = view.findViewById(R.id.btnFinish);
        tvLoginLink = view.findViewById(R.id.tvLoginLink);

        tvLoginLink.setOnClickListener(v -> requireActivity().finish());

        btnFinish.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();

            if (username.isEmpty()) {
                Toast.makeText(requireContext(), "Please choose a username", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "Registration Complete! Please login with your new account.", Toast.LENGTH_LONG).show();
                
                // Finish the SignupActivity which will return to LoginActivity (assuming it started it)
                requireActivity().finish();
            }
        });

        return view;
    }
}
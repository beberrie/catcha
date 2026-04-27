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
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;

import ph.edu.uscDCISMCatcha.R;
import ph.edu.uscDCISMCatcha.activities.auth.SignupActivity;
import ph.edu.uscDCISMCatcha.viewmodel.SignupViewModel;

public class SignupStep2Fragment extends Fragment {

    private TextInputEditText etSchoolEmail;
    private Button btnNext;
    private TextView tvLoginLink;
    private SignupViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.auth_activity_signup_step2, container, false);

        viewModel = new ViewModelProvider(requireActivity()).get(SignupViewModel.class);

        etSchoolEmail = view.findViewById(R.id.etSchoolEmail);
        btnNext = view.findViewById(R.id.btnNext);
        tvLoginLink = view.findViewById(R.id.tvLoginLink);

        // Pre-fill if already in viewModel
        if (viewModel.getEmail().getValue() != null) {
            etSchoolEmail.setText(viewModel.getEmail().getValue());
        }

        tvLoginLink.setOnClickListener(v -> requireActivity().finish());

        btnNext.setOnClickListener(v -> {
            String email = etSchoolEmail.getText().toString().trim();
            if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(requireContext(), "Please enter a valid school email", Toast.LENGTH_SHORT).show();
                return;
            }
            
            viewModel.setEmail(email);
            ((SignupActivity) requireActivity()).navigateToPersonalInfoStep();
        });

        return view;
    }
}

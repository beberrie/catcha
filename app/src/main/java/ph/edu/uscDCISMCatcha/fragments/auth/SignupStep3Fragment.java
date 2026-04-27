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

public class SignupStep3Fragment extends Fragment {

    private TextInputEditText etFirstName, etLastName;
    private Button btnNext;
    private TextView tvLoginLink;
    private SignupViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.auth_activity_signup_step3, container, false);

        viewModel = new ViewModelProvider(requireActivity()).get(SignupViewModel.class);

        etFirstName = view.findViewById(R.id.etFirstName);
        etLastName = view.findViewById(R.id.etLastName);
        btnNext = view.findViewById(R.id.btnNext);
        tvLoginLink = view.findViewById(R.id.tvLoginLink);

        // Load existing data from ViewModel if available
        if (viewModel.getFirstName().getValue() != null) {
            etFirstName.setText(viewModel.getFirstName().getValue());
        }
        if (viewModel.getLastName().getValue() != null) {
            etLastName.setText(viewModel.getLastName().getValue());
        }

        tvLoginLink.setOnClickListener(v -> requireActivity().finish());

        btnNext.setOnClickListener(v -> {
            String firstName = etFirstName.getText().toString().trim();
            String lastName = etLastName.getText().toString().trim();

            if (firstName.isEmpty() || lastName.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            } else {
                viewModel.setFirstName(firstName);
                viewModel.setLastName(lastName);
                ((SignupActivity) requireActivity()).navigateToPasswordStep();
            }
        });

        return view;
    }
}

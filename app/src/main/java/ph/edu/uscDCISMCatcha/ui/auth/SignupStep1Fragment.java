package ph.edu.uscDCISMCatcha.ui.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import ph.edu.uscDCISMCatcha.R;
import ph.edu.uscDCISMCatcha.viewmodel.auth.SignupViewModel;

public class SignupStep1Fragment extends Fragment {

    private SignupViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.auth_activity_signup_step1, container, false);

        viewModel = new ViewModelProvider(requireActivity()).get(SignupViewModel.class);

        AutoCompleteTextView actvUniversity = view.findViewById(R.id.actvUniversity);
        Button btnNext = view.findViewById(R.id.btnNext);
        TextView tvLoginLink = view.findViewById(R.id.tvLoginLink);

        // university dropdown
        String[] universities = getResources().getStringArray(R.array.universities);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, universities);
        actvUniversity.setAdapter(adapter);

        tvLoginLink.setOnClickListener(v -> requireActivity().finish());

        btnNext.setOnClickListener(v -> {
            String selectedUni = actvUniversity.getText().toString();
            if (selectedUni.isEmpty()) {
                Toast.makeText(requireContext(), "Please select a university", Toast.LENGTH_SHORT).show();
            } else {
                viewModel.setUniversity(selectedUni);
                viewModel.setRole("User"); // Default role as per requirement
                ((SignupActivity) requireActivity()).navigateToEmailStep();
            }
        });

        return view;
    }
}
package ph.edu.uscDCISMCatcha.fragments.auth;

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

import ph.edu.uscDCISMCatcha.R;
import ph.edu.uscDCISMCatcha.activities.auth.SignupActivity;

public class SignupStep2Fragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.auth_activity_signup_step2, container, false);

        AutoCompleteTextView actvRoleSignup = view.findViewById(R.id.actvRoleSignup);
        Button btnNext = view.findViewById(R.id.btnNext);
        TextView tvLoginLink = view.findViewById(R.id.tvLoginLink);

        // role dropdown
        String[] roles = getResources().getStringArray(R.array.signup_roles);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, roles);
        actvRoleSignup.setAdapter(adapter);

        tvLoginLink.setOnClickListener(v -> requireActivity().finish());

        btnNext.setOnClickListener(v -> {
            String selectedRole = actvRoleSignup.getText().toString();
            if (selectedRole.isEmpty()) {
                Toast.makeText(requireContext(), "Please select a role", Toast.LENGTH_SHORT).show();
            } else {
                ((SignupActivity) requireActivity()).navigateToStep3();
            }
        });

        return view;
    }
}
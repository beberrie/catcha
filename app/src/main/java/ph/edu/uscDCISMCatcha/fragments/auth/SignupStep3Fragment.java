package ph.edu.uscDCISMCatcha.fragments.auth;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;

import ph.edu.uscDCISMCatcha.R;
import ph.edu.uscDCISMCatcha.activities.auth.SignupActivity;

public class SignupStep3Fragment extends Fragment {

    private TextInputEditText etSchoolEmail;
    private EditText etOtp1, etOtp2, etOtp3, etOtp4;
    private Button btnNext;
    private TextView tvLoginLink;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.auth_activity_signup_step3, container, false);

        etSchoolEmail = view.findViewById(R.id.etSchoolEmail);
        etOtp1 = view.findViewById(R.id.etOtp1);
        etOtp2 = view.findViewById(R.id.etOtp2);
        etOtp3 = view.findViewById(R.id.etOtp3);
        etOtp4 = view.findViewById(R.id.etOtp4);
        btnNext = view.findViewById(R.id.btnNext);
        tvLoginLink = view.findViewById(R.id.tvLoginLink);

        setupOtpFocus();

        tvLoginLink.setOnClickListener(v -> requireActivity().finish());

        btnNext.setOnClickListener(v -> {
            String email = etSchoolEmail.getText().toString();
            String otp = etOtp1.getText().toString() + etOtp2.getText().toString() + 
                         etOtp3.getText().toString() + etOtp4.getText().toString();

            if (email.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter your school email", Toast.LENGTH_SHORT).show();
            } else if (otp.length() < 4) {
                Toast.makeText(requireContext(), "Please enter the full 4-digit code", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "Email validated!", Toast.LENGTH_SHORT).show();
                ((SignupActivity) requireActivity()).navigateToStep4();
            }
        });

        return view;
    }

    private void setupOtpFocus() {
        etOtp1.addTextChangedListener(new OtpTextWatcher(etOtp1, etOtp2));
        etOtp2.addTextChangedListener(new OtpTextWatcher(etOtp2, etOtp3));
        etOtp3.addTextChangedListener(new OtpTextWatcher(etOtp3, etOtp4));
        etOtp4.addTextChangedListener(new OtpTextWatcher(etOtp4, null));
    }

    private class OtpTextWatcher implements TextWatcher {
        private final View currentView;
        private final View nextView;

        public OtpTextWatcher(View currentView, View nextView) {
            this.currentView = currentView;
            this.nextView = nextView;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() == 1 && nextView != null) {
                nextView.requestFocus();
            }
        }
    }
}
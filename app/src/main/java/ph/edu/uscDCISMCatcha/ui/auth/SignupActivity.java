package ph.edu.uscDCISMCatcha.ui.auth;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;

import ph.edu.uscDCISMCatcha.R;
import ph.edu.uscDCISMCatcha.viewmodel.auth.SignupViewModel;

public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "SignupActivity";
    private FirebaseAuth mAuth;
    private SignupViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.auth_activity_signup);

        mAuth = FirebaseAuth.getInstance();
        viewModel = new ViewModelProvider(this).get(SignupViewModel.class);

        // Load Step 1 by default
        if (savedInstanceState == null) {
            loadFragment(new SignupStep1Fragment(), false);
        }
    }

    private void loadFragment(Fragment fragment, boolean addToBackStack) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    public void navigateToStep1() {
        loadFragment(new SignupStep1Fragment(), true);
    }

    public void navigateToEmailStep() {
        loadFragment(new SignupStep2Fragment(), true);
    }

    public void navigateToPersonalInfoStep() {
        loadFragment(new SignupStep3Fragment(), true);
    }

    public void navigateToPasswordStep() {
        loadFragment(new SignupStep4Fragment(), true);
    }

    public void navigateToUsernameStep() {
        loadFragment(new SignupStep5Fragment(), true);
    }
}

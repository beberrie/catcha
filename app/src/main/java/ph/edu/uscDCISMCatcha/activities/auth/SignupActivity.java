package ph.edu.uscDCISMCatcha.activities.auth;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import ph.edu.uscDCISMCatcha.R;
import ph.edu.uscDCISMCatcha.fragments.auth.SignupStep1Fragment;
import ph.edu.uscDCISMCatcha.fragments.auth.SignupStep2Fragment;
import ph.edu.uscDCISMCatcha.fragments.auth.SignupStep3Fragment;
import ph.edu.uscDCISMCatcha.fragments.auth.SignupStep4Fragment;
import ph.edu.uscDCISMCatcha.fragments.auth.SignupStep5Fragment;
import ph.edu.uscDCISMCatcha.fragments.auth.SignupStep6Fragment;

public class SignupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.auth_activity_signup);

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

    public void navigateToStep2() {
        loadFragment(new SignupStep2Fragment(), true);
    }

    public void navigateToStep3() {
        loadFragment(new SignupStep3Fragment(), true);
    }

    public void navigateToStep4() {
        loadFragment(new SignupStep4Fragment(), true);
    }

    public void navigateToStep5() {
        loadFragment(new SignupStep5Fragment(), true);
    }

    public void navigateToStep6() {
        loadFragment(new SignupStep6Fragment(), true);
    }
}
package ph.edu.uscDCISMCatcha.activities;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import ph.edu.uscDCISMCatcha.R;
import ph.edu.uscDCISMCatcha.fragments.org.OrgHomePageFragment;

public class OrgHomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.auth_activity_signup); // Using a container layout or activity_main

        if (savedInstanceState == null) {
            loadFragment(new OrgHomePageFragment());
        }
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }
}

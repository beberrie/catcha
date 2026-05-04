package ph.edu.uscDCISMCatcha.ui.org;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import ph.edu.uscDCISMCatcha.R;
import ph.edu.uscDCISMCatcha.ui.student.OrgProfileFragment;

public class OrgHomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.auth_activity_signup); // Using the layout with fragment_container

        if (savedInstanceState == null) {
            // TEMPORARY: Load OrgProfileFragment directly to test org_profile layout
            loadFragment(new OrgHomePageFragment());
        }
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }
}

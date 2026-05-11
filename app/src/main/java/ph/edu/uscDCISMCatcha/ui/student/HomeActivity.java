package ph.edu.uscDCISMCatcha.ui.student;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import ph.edu.uscDCISMCatcha.R;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        View mainView = findViewById(R.id.main);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);

                BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
                bottomNav.setPadding(0, 0, 0, systemBars.bottom);

                return insets;
            });
        }

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);

        // Load default fragment
        if (savedInstanceState == null) {
            loadFragment(new OrgDashboardFragment()); // Defaulting to Organizations for now
            bottomNav.setSelectedItemId(R.id.nav_orgs);
        }

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                // loadFragment(new HomeFragment());
            } else if (id == R.id.nav_orgs) {
                loadFragment(new OrgDashboardFragment());
            } else if (id == R.id.nav_events) {
                loadFragment(new TrendingEventsFragment());
            }
            return true;
        });
    }

     private void loadFragment(Fragment fragment) {
         getSupportFragmentManager()
             .beginTransaction()
             .replace(R.id.fragment_container, fragment)
             .commit();
     }
}

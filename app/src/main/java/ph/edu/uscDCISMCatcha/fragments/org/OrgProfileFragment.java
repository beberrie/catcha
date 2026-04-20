package ph.edu.uscDCISMCatcha.fragments.org;import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import ph.edu.uscDCISMCatcha.R;

public class OrgProfileFragment extends Fragment {

    private Button btnBack;
    private Button btnJoin;
    private LinearLayout joinedStatusContainer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.org_profile, container, false);

        btnBack = view.findViewById(R.id.backButton);
        btnJoin = view.findViewById(R.id.joinButton);
        joinedStatusContainer = view.findViewById(R.id.joinedStatusContainer);

        // Handle Back Navigation
        btnBack.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        // Example: Toggling Join Status
        btnJoin.setOnClickListener(v -> {
            btnJoin.setVisibility(View.GONE);
            joinedStatusContainer.setVisibility(View.VISIBLE);
        });

        return view;
    }
}
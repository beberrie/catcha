package ph.edu.uscDCISMCatcha.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import ph.edu.uscDCISMCatcha.R;
import ph.edu.uscDCISMCatcha.databinding.FragmentPushSetupSuccessBinding;

public class PushSetupSuccessFragment extends Fragment {

    private FragmentPushSetupSuccessBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPushSetupSuccessBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.tvTokenPreview.setText("eXaMpLeToKeN...abc123");

        binding.btnContinue.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new HomeFragment())
                        .commit());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
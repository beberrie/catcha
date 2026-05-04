package ph.edu.uscDCISMCatcha.ui.org;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import ph.edu.uscDCISMCatcha.R;
import ph.edu.uscDCISMCatcha.databinding.OrgHomePageBinding;

public class OrgHomePageFragment extends Fragment {

    private OrgHomePageBinding binding;

    private final boolean isOrgMember = true;

    public OrgHomePageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout using View Binding
        binding = OrgHomePageBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupHeader();
        setupCreatePostCard();
    }

    private void setupHeader() {
        // Navigate to Profile when avatar is clicked
        binding.header.ivUserAvatarHeader.setOnClickListener(v -> {
            if (getActivity() != null) {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new OrgHandlerProfileFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    private void setupCreatePostCard() {
        // Hide the card if the user is not an organization member
        if (!isOrgMember) {
            binding.createPostCard.setVisibility(View.GONE);
            return;
        } else {
            binding.createPostCard.setVisibility(View.VISIBLE);
        }

        binding.ivUserAvatar.setImageResource(R.drawable.bg_avatar_dark);

        // Click Listeners for all entry points to the Create Post screen
        binding.btnOpenCreatePost.setOnClickListener(v -> openCreatePost(true));
        binding.btnShortcutAnnouncement.setOnClickListener(v -> openCreatePost(true));
        binding.btnShortcutEvent.setOnClickListener(v -> openCreatePost(false));
        binding.btnAttachImage.setOnClickListener(v -> openCreatePost(true));
    }

    private void openCreatePost(boolean startOnAnnouncement) {
        // Prepare data to pass to the next fragment
        Bundle args = new Bundle();
        args.putBoolean("startOnAnnouncement", startOnAnnouncement);

        CreatePostFragment fragment = new CreatePostFragment();
        fragment.setArguments(args);

        if (getActivity() != null) {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

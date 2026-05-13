package ph.edu.uscDCISMCatcha.ui.student;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import ph.edu.uscDCISMCatcha.R;
import ph.edu.uscDCISMCatcha.adapters.CommonGroundAdapter;
import ph.edu.uscDCISMCatcha.data.models.UserModel;
import ph.edu.uscDCISMCatcha.ui.chat.ChatBotFragment;
import ph.edu.uscDCISMCatcha.databinding.TrendingEventsBinding;

public class TrendingEventsFragment extends Fragment {

    private TrendingEventsBinding binding;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = TrendingEventsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();

        setupHeader();
        setupChatBot();
        loadEventCards();
        setupDemoTrending();
    }

    private void setupHeader() {
        binding.header.ivSearchHeader.setOnClickListener(v -> navigateTo(new SearchFragment()));

        binding.header.ivNotificationsHeader.setOnClickListener(v -> navigateTo(new PushSetupFragment()));

        binding.header.ivUserAvatarHeader.setOnClickListener(v -> navigateTo(new UserProfileFragment()));
    }

    private void setupChatBot() {
        binding.fabChatBot.setOnClickListener(v -> navigateTo(new ChatBotFragment()));
    }

    private void loadEventCards() {
        db.collection("events").get()
                .addOnSuccessListener(querySnapshots -> {
                    if (binding == null) return;
                    binding.llEventCards.removeAllViews();
                    for (QueryDocumentSnapshot doc : querySnapshots) {
                        View eventCard = getLayoutInflater().inflate(R.layout.other_events_card, binding.llEventCards, false);
                        
                        RecyclerView rvCommonGround = eventCard.findViewById(R.id.rvCommonGround);
                        View tvCommonGroundLabel = eventCard.findViewById(R.id.tvCommonGroundLabel);

                        // HARDCODED FOR DEMO
                        List<UserModel> demoUsers = new ArrayList<>();
                        demoUsers.add(new UserModel("Beth", "Santos", "beth", "beth@usc.edu.ph", "USC", "DCISM", "Student"));
                        demoUsers.add(new UserModel("Carl", "Gomez", "carl", "carl@usc.edu.ph", "USC", "DCISM", "Student"));

                        if (tvCommonGroundLabel != null) tvCommonGroundLabel.setVisibility(View.VISIBLE);
                        if (rvCommonGround != null) {
                            rvCommonGround.setVisibility(View.VISIBLE);
                            rvCommonGround.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
                            rvCommonGround.setAdapter(new CommonGroundAdapter(requireContext(), demoUsers));
                        }

                        binding.llEventCards.addView(eventCard);
                    }
                });
    }

    private void setupDemoTrending() {
        // Trending Card 1
        List<UserModel> demoUsers1 = new ArrayList<>();
        demoUsers1.add(new UserModel("Beth", "Santos", "beth", "beth@usc.edu.ph", "USC", "DCISM", "Student"));
        demoUsers1.add(new UserModel("Carl", "Gomez", "carl", "carl@usc.edu.ph", "USC", "DCISM", "Student"));

        binding.rvCommonGround.setVisibility(View.VISIBLE);
        binding.tvCommonGroundLabel.setVisibility(View.VISIBLE);
        binding.rvCommonGround.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvCommonGround.setAdapter(new CommonGroundAdapter(requireContext(), demoUsers1));

        // Trending Card 2
        List<UserModel> demoUsers2 = new ArrayList<>();
        demoUsers2.add(new UserModel("Dana", "Cruz", "dana", "dana@usc.edu.ph", "USC", "DCISM", "Student"));

        binding.rvCommonGround2.setVisibility(View.VISIBLE);
        binding.tvCommonGroundLabel2.setVisibility(View.VISIBLE);
        binding.rvCommonGround2.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvCommonGround2.setAdapter(new CommonGroundAdapter(requireContext(), demoUsers2));
    }

    private void navigateTo(Fragment fragment) {
        getParentFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

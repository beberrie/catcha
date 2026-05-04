package ph.edu.uscDCISMCatcha.ui.org;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.text.SimpleDateFormat;
import java.util.Locale;
import ph.edu.uscDCISMCatcha.R;
import ph.edu.uscDCISMCatcha.data.models.AnnouncementModel;
import ph.edu.uscDCISMCatcha.data.models.EventModel;
import ph.edu.uscDCISMCatcha.databinding.ItemAnnouncementCardBinding;
import ph.edu.uscDCISMCatcha.databinding.ItemEventCardHandlerBinding;
import ph.edu.uscDCISMCatcha.databinding.OrgHomePageBinding;

public class OrgHomePageFragment extends Fragment {

    private OrgHomePageBinding binding;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault());

    public OrgHomePageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = OrgHomePageBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupHeader();
        setupCreatePostCard();
        fetchAnnouncements();
        fetchEvents();
    }

    private void setupHeader() {
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
        binding.ivUserAvatar.setImageResource(R.drawable.bg_avatar_dark);

        binding.btnOpenCreatePost.setOnClickListener(v -> openCreatePost(true));
        binding.btnShortcutAnnouncement.setOnClickListener(v -> openCreatePost(true));
        binding.btnShortcutEvent.setOnClickListener(v -> openCreatePost(false));
        binding.btnAttachImage.setOnClickListener(v -> openCreatePost(true));
    }

    private void fetchAnnouncements() {
        String uid = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : "";
        if (uid.isEmpty()) return;

        db.collection("announcements")
                .whereEqualTo("authorUid", uid)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        return;
                    }

                    if (binding != null) {
                        binding.announcementsContainer.removeAllViews();
                        if (value != null) {
                            for (QueryDocumentSnapshot doc : value) {
                                AnnouncementModel announcement = doc.toObject(AnnouncementModel.class);
                                addAnnouncementCard(announcement, doc.getId());
                            }
                        }
                    }
                });
    }

    private void addAnnouncementCard(AnnouncementModel announcement, String docId) {
        ItemAnnouncementCardBinding cardBinding = ItemAnnouncementCardBinding.inflate(
                getLayoutInflater(), binding.announcementsContainer, false);

        cardBinding.tvAnnouncementTitle.setText(announcement.getTitle());
        cardBinding.tvAnnouncementContent.setText(announcement.getContent());
        if (announcement.getTimestamp() != null) {
            cardBinding.tvAnnouncementDate.setText(dateFormat.format(announcement.getTimestamp()));
        }

        cardBinding.btnDeleteAnnouncement.setOnClickListener(v -> showDeleteConfirmation("announcements", docId));

        binding.announcementsContainer.addView(cardBinding.getRoot());
    }

    private void fetchEvents() {
        String uid = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : "";
        if (uid.isEmpty()) return;

        db.collection("events")
                .whereEqualTo("createdBy", uid)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        return;
                    }

                    if (binding != null) {
                        binding.eventsContainer.removeAllViews();
                        if (value != null) {
                            for (QueryDocumentSnapshot doc : value) {
                                EventModel event = doc.toObject(EventModel.class);
                                addEventCard(event, doc.getId());
                            }
                        }
                    }
                });
    }

    private void addEventCard(EventModel event, String docId) {
        ItemEventCardHandlerBinding cardBinding = ItemEventCardHandlerBinding.inflate(
                getLayoutInflater(), binding.eventsContainer, false);

        cardBinding.tvEventTitle.setText(event.getTitle());
        cardBinding.tvLocation.setText(event.getLocation());
        if (event.getStartDateTime() != null) {
            cardBinding.tvDate.setText(dateFormat.format(event.getStartDateTime()));
        }
        
        cardBinding.tvCapacity.setText(String.format(Locale.getDefault(), "%d/%d slots", 
                event.getCurrentRsvpCount(), event.getMaxCapacity()));

        cardBinding.btnDeleteEvent.setOnClickListener(v -> showDeleteConfirmation("events", docId));

        binding.eventsContainer.addView(cardBinding.getRoot());
    }

    private void showDeleteConfirmation(String collection, String docId) {
        new AlertDialog.Builder(getContext())
                .setTitle("Delete Post")
                .setMessage("Are you sure you want to delete this post?")
                .setPositiveButton("Delete", (dialog, which) -> deletePost(collection, docId))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deletePost(String collection, String docId) {
        db.collection(collection).document(docId).delete()
                .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Post deleted", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Error deleting post", Toast.LENGTH_SHORT).show());
    }

    private void openCreatePost(boolean startOnAnnouncement) {
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

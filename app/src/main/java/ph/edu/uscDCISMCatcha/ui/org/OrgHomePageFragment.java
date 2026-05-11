package ph.edu.uscDCISMCatcha.ui.org;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import ph.edu.uscDCISMCatcha.R;
import ph.edu.uscDCISMCatcha.data.models.AnnouncementModel;
import ph.edu.uscDCISMCatcha.data.models.EventModel;
import ph.edu.uscDCISMCatcha.databinding.ItemAnnouncementCardBinding;
import ph.edu.uscDCISMCatcha.databinding.ItemEventCardHandlerBinding;
import ph.edu.uscDCISMCatcha.databinding.OrgHomePageBinding;

public class OrgHomePageFragment extends Fragment {

    private static final String TAG = "OrgHomePageFragment";
    private OrgHomePageBinding binding;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault());
    private String organizationName;

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
        fetchOrganizationData();
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

        binding.btnOpenCreatePost.setOnClickListener(v -> openCreatePost(true, null));
        binding.btnShortcutAnnouncement.setOnClickListener(v -> openCreatePost(true, null));
        binding.btnShortcutEvent.setOnClickListener(v -> openCreatePost(false, null));
        binding.btnAttachImage.setOnClickListener(v -> openCreatePost(true, null));
    }

    private void fetchOrganizationData() {
        String uid = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : "";
        if (uid.isEmpty()) return;

        db.collection("organizations")
                .whereEqualTo("ownerUid", uid)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        organizationName = queryDocumentSnapshots.getDocuments().get(0).getString("name");
                        if (organizationName != null) {
                            fetchAnnouncements();
                            fetchEvents();
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error fetching org", e));
    }

    private void fetchAnnouncements() {
        if (organizationName == null || organizationName.isEmpty()) return;

        db.collection("announcements")
                .whereEqualTo("orgName", organizationName)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e(TAG, "Announcements error: " + error.getMessage());
                        return;
                    }

                    if (binding != null) {
                        binding.announcementsContainer.removeAllViews();
                        if (value != null) {
                            List<AnnouncementModel> list = new ArrayList<>();
                            for (QueryDocumentSnapshot doc : value) {
                                AnnouncementModel announcement = doc.toObject(AnnouncementModel.class);
                                if (announcement != null) {
                                    announcement.setAnnouncementId(doc.getId());
                                    list.add(announcement);
                                }
                            }

                            Collections.sort(list, (a, b) -> {
                                if (a.getTimestamp() == null || b.getTimestamp() == null) return 0;
                                return b.getTimestamp().compareTo(a.getTimestamp());
                            });

                            for (AnnouncementModel announcement : list) {
                                addAnnouncementCard(announcement, announcement.getAnnouncementId());
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
            cardBinding.tvAnnouncementDate.setText(dateFormat.format(announcement.getTimestamp().toDate()));
        }

        cardBinding.btnEditAnnouncement.setOnClickListener(v -> openCreatePost(true, docId));
        cardBinding.btnDeleteAnnouncement.setOnClickListener(v -> showDeleteConfirmation("announcements", docId));

        binding.announcementsContainer.addView(cardBinding.getRoot());
    }

    private void fetchEvents() {
        if (organizationName == null || organizationName.isEmpty()) return;

        db.collection("events")
                .whereEqualTo("orgName", organizationName)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e(TAG, "Events error: " + error.getMessage());
                        return;
                    }

                    if (binding != null) {
                        binding.eventsContainer.removeAllViews();
                        if (value != null) {
                            List<EventModel> list = new ArrayList<>();
                            for (QueryDocumentSnapshot doc : value) {
                                EventModel event = doc.toObject(EventModel.class);
                                if (event != null) {
                                    event.setId(doc.getId()); // FIXED: Changed setEventId to setId
                                    list.add(event);
                                }
                            }

                            Collections.sort(list, (a, b) -> {
                                if (a.getCreatedAt() == null || b.getCreatedAt() == null) return 0;
                                return b.getCreatedAt().compareTo(a.getCreatedAt());
                            });

                            for (EventModel event : list) {
                                addEventCard(event, event.getId()); // FIXED: Changed getEventId to getId
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
            cardBinding.tvDate.setText(dateFormat.format(event.getStartDateTime().toDate()));
        }

        cardBinding.tvCapacity.setText(String.format(Locale.getDefault(), "%d/%d slots",
                event.getCurrentRsvpCount(), event.getMaxCapacity()));

        cardBinding.btnEditEvent.setOnClickListener(v -> openCreatePost(false, docId));
        cardBinding.btnDeleteEvent.setOnClickListener(v -> showDeleteConfirmation("events", docId));

        binding.eventsContainer.addView(cardBinding.getRoot());
    }

    private void showDeleteConfirmation(String collection, String docId) {
        new AlertDialog.Builder(getContext())
                .setTitle("Delete Post")
                .setMessage("Are you sure you want to delete this post? This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> deletePost(collection, docId))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deletePost(String collection, String docId) {
        db.collection(collection).document(docId).delete()
                .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Post deleted successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Error deleting post", Toast.LENGTH_SHORT).show());
    }

    private void openCreatePost(boolean startOnAnnouncement, @Nullable String editId) {
        Bundle args = new Bundle();
        args.putBoolean("startOnAnnouncement", startOnAnnouncement);
        if (editId != null) {
            args.putString("EDIT_ID", editId);
        }

        ph.edu.uscDCISMCatcha.fragments.CreatePostFragment fragment = new ph.edu.uscDCISMCatcha.fragments.CreatePostFragment();
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
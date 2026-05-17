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


import ph.edu.uscDCISMCatcha.data.models.MembershipModel;
import ph.edu.uscDCISMCatcha.data.models.UserModel;
import ph.edu.uscDCISMCatcha.databinding.ItemMembershipRequestBinding;


public class OrgHomePageFragment extends Fragment {


    private static final String TAG = "OrgHomePageFragment";
    private OrgHomePageBinding binding;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault());
    private final SimpleDateFormat shortDate = new SimpleDateFormat("MMM dd", Locale.getDefault());
    private String organizationName;
    private String organizationId;


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
                        organizationId = queryDocumentSnapshots.getDocuments().get(0).getId();
                        organizationName = queryDocumentSnapshots.getDocuments().get(0).getString("name");
                        if (organizationName != null) {
                            fetchAnnouncements();
                            fetchEvents();
                            fetchMembershipRequests();
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error fetching org", e));
    }


    private void fetchMembershipRequests() {
        if (organizationId == null) return;


        db.collection("memberships")
                .whereEqualTo("orgId", organizationId)
                .whereEqualTo("status", "Pending")
                .addSnapshotListener((value, error) -> {
                    if (error != null) return;


                    if (binding != null) {
                        binding.membershipRequestsContainer.removeAllViews();
                        if (value != null && !value.isEmpty()) {
                            binding.sectionMembershipRequests.setVisibility(View.VISIBLE);
                            binding.tvRequestCount.setText(String.valueOf(value.size()));
                            for (QueryDocumentSnapshot doc : value) {
                                MembershipModel request = doc.toObject(MembershipModel.class);
                                addMembershipRequestCard(request, doc.getId());
                            }
                        } else {
                            binding.sectionMembershipRequests.setVisibility(View.GONE);
                        }
                    }
                });
    }


    private void addMembershipRequestCard(MembershipModel request, String membershipId) {
        ItemMembershipRequestBinding reqBinding = ItemMembershipRequestBinding.inflate(
                getLayoutInflater(), binding.membershipRequestsContainer, false);


        // Fetch user details for the name
        db.collection("users").document(request.getUserId()).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        UserModel user = doc.toObject(UserModel.class);
                        if (user != null) {
                            reqBinding.tvUserName.setText(user.getFirstName() + " " + user.getLastName());
                        }
                    }
                });


        if (request.getJoinedAt() != null) {
            reqBinding.tvRequestDate.setText("Requested on " + shortDate.format(request.getJoinedAt().toDate()));
        }


        reqBinding.btnApprove.setOnClickListener(v -> updateMembershipStatus(membershipId, "Active"));
        reqBinding.btnReject.setOnClickListener(v -> showRejectConfirmation(membershipId));


        binding.membershipRequestsContainer.addView(reqBinding.getRoot());
    }


    private void updateMembershipStatus(String membershipId, String newStatus) {
        db.collection("memberships").document(membershipId)
                .update("status", newStatus)
                .addOnSuccessListener(aVoid -> {
                    String msg = "Active".equals(newStatus) ? "Member approved!" : "Request rejected.";
                    Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                });
    }


    private void showRejectConfirmation(String membershipId) {
        new AlertDialog.Builder(getContext())
                .setTitle("Reject Request")
                .setMessage("Are you sure you want to reject this membership request?")
                .setPositiveButton("Reject", (dialog, which) -> {
                    db.collection("memberships").document(membershipId).delete()
                            .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Request rejected and removed.", Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton("Cancel", null)
                .show();
    }


    private void fetchAnnouncements() {
        if (organizationName == null || organizationName.isEmpty()) return;


        // Removed .orderBy() to avoid index requirement for now
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
                                announcement.setAnnouncementId(doc.getId());
                                list.add(announcement);
                            }

                            // Sort locally by timestamp descending
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
            cardBinding.tvAnnouncementDate.setText(dateFormat.format(announcement.getTimestamp()));
        }


        cardBinding.btnEditAnnouncement.setOnClickListener(v -> openCreatePost(true, docId));
        cardBinding.btnDeleteAnnouncement.setOnClickListener(v -> showDeleteConfirmation("announcements", docId));


        binding.announcementsContainer.addView(cardBinding.getRoot());
    }


    private void fetchEvents() {
        if (organizationName == null || organizationName.isEmpty()) return;


        // Removed .orderBy() to avoid index requirement for now
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
                                event.setEventId(doc.getId());
                                list.add(event);
                            }


                            // Sort locally by createdAt descending
                            Collections.sort(list, (a, b) -> {
                                if (a.getCreatedAt() == null || b.getCreatedAt() == null) return 0;
                                return b.getCreatedAt().compareTo(a.getCreatedAt());
                            });


                            for (EventModel event : list) {
                                addEventCard(event, event.getEventId());
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


        ph.edu.uscDCISMCatcha.ui.org.CreatePostFragment fragment = new ph.edu.uscDCISMCatcha.ui.org.CreatePostFragment();
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

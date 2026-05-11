package ph.edu.uscDCISMCatcha.ui.student;

import android.app.AlertDialog;
import android.content.Intent;
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
import ph.edu.uscDCISMCatcha.R;
import ph.edu.uscDCISMCatcha.data.models.EventModel;
import ph.edu.uscDCISMCatcha.data.models.MembershipModel;
import ph.edu.uscDCISMCatcha.data.models.Organization;
import ph.edu.uscDCISMCatcha.databinding.FragmentEventCardBinding;
import ph.edu.uscDCISMCatcha.databinding.OrgProfileBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class OrgProfileFragment extends Fragment implements EventFiltersBottomSheet.OnFiltersAppliedListener {

    private OrgProfileBinding binding;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String orgId;
    private boolean isMember = false;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault());
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        if (getArguments() != null) {
            orgId = getArguments().getString("ORG_ID");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = OrgProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupClickListeners();

        if (orgId != null) {
            fetchOrganizationDetails();
            checkMembershipStatus();
        } else {
            String orgName = getArguments() != null ? getArguments().getString("ORG_NAME") : "Organization";
            binding.orgName.setText(orgName);
        }
    }

    private void setupClickListeners() {
        binding.backButton.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });

        binding.joinButton.setOnClickListener(v -> showRegistrationDialog());
        binding.joinedButton.setOnClickListener(v -> showLeaveDialog());

        binding.filterButton.setOnClickListener(v -> {
            if (!isMember) {
                Toast.makeText(getContext(), "Join the organization to filter events!", Toast.LENGTH_SHORT).show();
                return;
            }
            EventFiltersBottomSheet filterSheet = new EventFiltersBottomSheet();
            filterSheet.setOnFiltersAppliedListener(this);
            filterSheet.show(getChildFragmentManager(), "EventFilters");
        });
    }

    private void checkMembershipStatus() {
        if (mAuth.getCurrentUser() == null) return;

        String uid = mAuth.getCurrentUser().getUid();
        db.collection("memberships")
                .whereEqualTo("userId", uid)
                .whereEqualTo("orgId", orgId)
                .addSnapshotListener((value, error) -> {
                    if (error != null) return;

                    isMember = (value != null && !value.isEmpty());
                    updateMembershipUI();

                    if (isMember) {
                        fetchOrganizationEvents(null, null, null);
                    } else {
                        binding.eventsContainer.removeAllViews();
                        binding.layoutJoinToSeeEvents.setVisibility(View.VISIBLE);
                        binding.tvNoEvents.setVisibility(View.GONE);
                        binding.tvLimitsReached.setVisibility(View.GONE);
                    }
                });
    }

    private void updateMembershipUI() {
        if (isMember) {
            binding.joinButton.setVisibility(View.GONE);
            binding.joinedStatusContainer.setVisibility(View.VISIBLE);
        } else {
            binding.joinButton.setVisibility(View.VISIBLE);
            binding.joinedStatusContainer.setVisibility(View.GONE);
        }
    }

    private void fetchOrganizationDetails() {
        db.collection("organizations").document(orgId)
                .addSnapshotListener((doc, error) -> {
                    if (error != null || doc == null || !doc.exists()) return;

                    Organization org = doc.toObject(Organization.class);
                    if (org != null && binding != null) {
                        binding.orgName.setText(org.getName());
                    }
                });
    }

    private void fetchOrganizationEvents(@Nullable String status, @Nullable String startTimeStr, @Nullable String endTimeStr) {
        binding.layoutJoinToSeeEvents.setVisibility(View.GONE);

        Query query = db.collection("events")
                .whereEqualTo("orgId", orgId);

        query.orderBy("startDateTime", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        binding.tvNoEvents.setVisibility(View.VISIBLE);
                        return;
                    }

                    binding.eventsContainer.removeAllViews();
                    int visibleCount = 0;

                    if (value != null) {
                        for (QueryDocumentSnapshot doc : value) {
                            EventModel event = doc.toObject(EventModel.class);
                            if (event != null) {
                                event.setId(doc.getId()); // Using simplified ID setter
                                if (shouldShowEvent(event, status, startTimeStr, endTimeStr)) {
                                    addEventCard(event);
                                    visibleCount++;
                                }
                            }
                        }
                    }

                    binding.tvNoEvents.setVisibility(visibleCount == 0 ? View.VISIBLE : View.GONE);
                    binding.tvLimitsReached.setVisibility(visibleCount > 0 ? View.VISIBLE : View.GONE);
                    binding.tvEventsCount.setText(String.valueOf(visibleCount));
                });
    }

    private boolean shouldShowEvent(EventModel event, String status, String startT, String endT) {
        // Filter by Status
        if (status != null && !status.isEmpty()) {
            String eventStatus = "UPCOMING";
            long now = System.currentTimeMillis();
            if (event.getStartDateTime() != null && event.getEndDateTime() != null) {
                // FIXED: Convert Timestamp to Date then to Long
                long start = event.getStartDateTime().toDate().getTime();
                long end = event.getEndDateTime().toDate().getTime();
                if (now < start) eventStatus = "UPCOMING";
                else if (now <= end) eventStatus = "ONGOING";
                else eventStatus = "FINISHED";
            }
            if (!eventStatus.equalsIgnoreCase(status)) return false;
        }

        // Filter by Time
        if (startT != null && endT != null && event.getStartDateTime() != null) {
            try {
                Date filterStart = timeFormat.parse(startT);
                Date filterEnd = timeFormat.parse(endT);

                // FIXED: Convert Timestamp to Date
                Date eventDate = event.getStartDateTime().toDate();
                String eventTimeStr = timeFormat.format(eventDate);
                Date eventTime = timeFormat.parse(eventTimeStr);

                if (eventTime.before(filterStart) || eventTime.after(filterEnd)) {
                    return false;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    private void addEventCard(EventModel event) {
        FragmentEventCardBinding cardBinding = FragmentEventCardBinding.inflate(
                getLayoutInflater(), binding.eventsContainer, false);

        cardBinding.tvEventTitle.setText(event.getTitle());
        cardBinding.tvLocation.setText(event.getLocation());
        cardBinding.tvDescription.setText(event.getDescription());

        if (event.getStartDateTime() != null) {
            // FIXED: Convert Timestamp to Date for format()
            cardBinding.tvDate.setText(dateFormat.format(event.getStartDateTime().toDate()));
            cardBinding.tvTime.setText(timeFormat.format(event.getStartDateTime().toDate()));
        }

        cardBinding.tvCapacity.setText(String.format(Locale.getDefault(), "%d/%d slots",
                event.getCurrentRsvpCount(), event.getMaxCapacity()));

        cardBinding.getRoot().setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), EventDetailsActivity.class);
            intent.putExtra("EVENT_ID", event.getId());
            intent.putExtra("EVENT_TITLE", event.getTitle());
            intent.putExtra("EVENT_HOST", event.getOrgName());
            intent.putExtra("EVENT_LOCATION", event.getLocation());
            if (event.getStartDateTime() != null) {
                intent.putExtra("EVENT_DATETIME", dateFormat.format(event.getStartDateTime().toDate()));
            }
            intent.putExtra("EVENT_DESCRIPTION", event.getDescription());
            intent.putExtra("EVENT_STATUS", "UPCOMING");
            intent.putExtra("EVENT_STATUS_COLOR", R.color.yellow);
            startActivity(intent);
        });

        binding.eventsContainer.addView(cardBinding.getRoot());
    }

    @Override
    public void onFiltersApplied(String status, String startTime, String endTime) {
        fetchOrganizationEvents(status, startTime, endTime);
    }

    private void showRegistrationDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_org_registration, null);

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .setCancelable(false)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        dialogView.findViewById(R.id.btnSubmitRegistration).setOnClickListener(v -> {
            joinOrganization();
            dialog.dismiss();
        });

        dialogView.findViewById(R.id.btnCancelRegistration).setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void joinOrganization() {
        if (mAuth.getCurrentUser() == null) return;

        String uid = mAuth.getCurrentUser().getUid();
        MembershipModel membership = new MembershipModel(uid, orgId, binding.orgName.getText().toString(), "Member", "Active");

        db.collection("memberships").add(membership)
                .addOnSuccessListener(doc -> Toast.makeText(getContext(), "Joined successfully!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void showLeaveDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle("Leaving?")
                .setMessage("Are you sure you want to leave this organization?")
                .setPositiveButton("Yes", (dialog, which) -> leaveOrganization())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void leaveOrganization() {
        if (mAuth.getCurrentUser() == null) return;

        String uid = mAuth.getCurrentUser().getUid();
        db.collection("memberships")
                .whereEqualTo("userId", uid)
                .whereEqualTo("orgId", orgId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        doc.getReference().delete();
                    }
                    Toast.makeText(getContext(), "You have left the organization.", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
package ph.edu.uscDCISMCatcha.ui.student;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import ph.edu.uscDCISMCatcha.R;
import ph.edu.uscDCISMCatcha.data.models.EventModel;
import ph.edu.uscDCISMCatcha.data.models.Organization;
import ph.edu.uscDCISMCatcha.databinding.FragmentEventCardBinding;
import ph.edu.uscDCISMCatcha.databinding.OrgProfileBinding;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class OrgProfileFragment extends Fragment {

    private OrgProfileBinding binding;
    private FirebaseFirestore db;
    private String orgId;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault());

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
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
            fetchOrganizationEvents();
        } else {
            // Handle fallback if name was passed instead of ID
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
            // Reuse existing filter logic if needed or show a message
            Toast.makeText(getContext(), "Filters coming soon!", Toast.LENGTH_SHORT).show();
        });
    }

    private void fetchOrganizationDetails() {
        db.collection("organizations").document(orgId)
                .addSnapshotListener((doc, error) -> {
                    if (error != null || doc == null || !doc.exists()) return;

                    Organization org = doc.toObject(Organization.class);
                    if (org != null && binding != null) {
                        binding.orgName.setText(org.getName());
                        // Profile/Banner image loading logic would go here (e.g., Glide/Picasso)
                    }
                });
    }

    private void fetchOrganizationEvents() {
        db.collection("events")
                .whereEqualTo("orgId", orgId)
                .orderBy("startDateTime", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        if (binding != null) binding.tvNoEvents.setVisibility(View.VISIBLE);
                        return;
                    }

                    if (binding != null) {
                        binding.eventsContainer.removeAllViews();
                        if (value != null && !value.isEmpty()) {
                            binding.tvNoEvents.setVisibility(View.GONE);
                            binding.tvEventsCount.setText(String.valueOf(value.size()));
                            for (QueryDocumentSnapshot doc : value) {
                                EventModel event = doc.toObject(EventModel.class);
                                addEventCard(event);
                            }
                        } else {
                            binding.tvNoEvents.setVisibility(View.VISIBLE);
                            binding.tvEventsCount.setText("0");
                        }
                    }
                });
    }

    private void addEventCard(EventModel event) {
        FragmentEventCardBinding cardBinding = FragmentEventCardBinding.inflate(
                getLayoutInflater(), binding.eventsContainer, false);

        cardBinding.tvEventTitle.setText(event.getTitle());
        cardBinding.tvLocation.setText(event.getLocation());
        cardBinding.tvDescription.setText(event.getDescription());
        
        if (event.getStartDateTime() != null) {
            cardBinding.tvDate.setText(dateFormat.format(event.getStartDateTime()));
            cardBinding.tvTime.setText(dateFormat.format(event.getStartDateTime()));
        }

        cardBinding.tvCapacity.setText(String.format(Locale.getDefault(), "%d/%d slots", 
                event.getCurrentRsvpCount(), event.getMaxCapacity()));

        cardBinding.getRoot().setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), EventDetailsActivity.class);
            intent.putExtra("EVENT_TITLE", event.getTitle());
            intent.putExtra("EVENT_HOST", event.getOrgName());
            intent.putExtra("EVENT_LOCATION", event.getLocation());
            if (event.getStartDateTime() != null) {
                intent.putExtra("EVENT_DATETIME", dateFormat.format(event.getStartDateTime()));
            }
            intent.putExtra("EVENT_DESCRIPTION", event.getDescription());
            intent.putExtra("EVENT_STATUS", "UPCOMING"); // Simplified status
            intent.putExtra("EVENT_STATUS_COLOR", R.color.yellow);
            startActivity(intent);
        });

        binding.eventsContainer.addView(cardBinding.getRoot());
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

        View btnSubmit = dialogView.findViewById(R.id.btnSubmitRegistration);
        View btnCancel = dialogView.findViewById(R.id.btnCancelRegistration);

        btnSubmit.setOnClickListener(v -> {
            binding.joinButton.setVisibility(View.GONE);
            binding.joinedStatusContainer.setVisibility(View.VISIBLE);
            Toast.makeText(getContext(), "Application submitted successfully!", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void showLeaveDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle("Leaving?")
                .setMessage("Are you sure you want to leave this organization?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    binding.joinedStatusContainer.setVisibility(View.GONE);
                    binding.joinButton.setVisibility(View.VISIBLE);
                    Toast.makeText(getContext(), "You have left the organization.", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

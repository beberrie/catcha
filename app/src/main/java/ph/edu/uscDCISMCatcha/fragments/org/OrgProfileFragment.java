package ph.edu.uscDCISMCatcha.fragments.org;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import ph.edu.uscDCISMCatcha.R;
import ph.edu.uscDCISMCatcha.activities.EventDetailsActivity;

import java.util.Locale;

public class OrgProfileFragment extends Fragment {

    private Button btnBack;
    private Button btnJoin;
    private Button btnJoined;
    private TextView tvOrgName;
    private LinearLayout joinedStatusContainer;
    private Chip filterButton;

    // Event Cards
    private View eventCard1, eventCard2, eventCard3, eventCard4, eventCard5;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.org_profile, container, false);

        btnBack = view.findViewById(R.id.backButton);
        btnJoin = view.findViewById(R.id.joinButton);
        btnJoined = view.findViewById(R.id.joinedButton);
        tvOrgName = view.findViewById(R.id.orgName);
        joinedStatusContainer = view.findViewById(R.id.joinedStatusContainer);
        filterButton = view.findViewById(R.id.filterButton);

        // Find the dummy cards
        eventCard1 = view.findViewById(R.id.eventCard1);
        eventCard2 = view.findViewById(R.id.eventCard2);
        eventCard3 = view.findViewById(R.id.eventCard3);
        eventCard4 = view.findViewById(R.id.eventCard4);
        eventCard5 = view.findViewById(R.id.eventCard5);

        // Initial State: Not Joined - Show Only Past Events
        showNotJoinedEvents();

        // Retrieve the organization name from arguments
        if (getArguments() != null) {
            String orgName = getArguments().getString("ORG_NAME");
            if (orgName != null && tvOrgName != null) {
                tvOrgName.setText(orgName);
            }
        }

        // Handle Back Navigation
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
            });
        }

        // Handle Join Action - Now shows a registration dialog
        if (btnJoin != null) {
            btnJoin.setOnClickListener(v -> showRegistrationDialog());
        }

        // Handle Leave Action (Clicking the "Joined" button)
        if (btnJoined != null) {
            btnJoined.setOnClickListener(v -> showLeaveDialog());
        }

        // Handle Filter Button
        if (filterButton != null) {
            filterButton.setOnClickListener(v -> showFilterBottomSheet());
        }

        return view;
    }

    private void showFilterBottomSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_event_filters, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        TextView btnCancel = bottomSheetView.findViewById(R.id.btnCancel);
        TextView btnShowResults = bottomSheetView.findViewById(R.id.btnShowResults);
        LinearLayout activeFiltersContainer = bottomSheetView.findViewById(R.id.activeFiltersContainer);
        Chip chipResetAll = bottomSheetView.findViewById(R.id.chipResetAll);
        ChipGroup cgStatus = bottomSheetView.findViewById(R.id.cgStatus);
        AutoCompleteTextView atvStartTime = bottomSheetView.findViewById(R.id.atvStartTime);
        AutoCompleteTextView atvEndTime = bottomSheetView.findViewById(R.id.atvEndTime);
        Button btnSetTime = bottomSheetView.findViewById(R.id.btnSetTime);

        // Populate Time Choices
        String[] timeChoices = {"8:00 AM", "9:00 AM", "10:00 AM", "11:00 AM", "12:00 PM", "1:00 PM", "2:00 PM", "3:00 PM", "4:00 PM", "5:00 PM", "6:00 PM", "7:00 PM", "8:00 PM"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, timeChoices);
        atvStartTime.setAdapter(adapter);
        atvEndTime.setAdapter(adapter);

        // Make sure no check initially as requested
        cgStatus.clearCheck();

        // Listener for Status Tags
        cgStatus.setOnCheckedStateChangeListener((group, checkedIds) -> {
            updateFilterTags(activeFiltersContainer, cgStatus, atvStartTime, atvEndTime, btnShowResults);
        });

        // Listener for Set Time button
        btnSetTime.setOnClickListener(v -> {
            updateFilterTags(activeFiltersContainer, cgStatus, atvStartTime, atvEndTime, btnShowResults);
        });

        // Listener for Reset All
        chipResetAll.setOnClickListener(v -> {
            cgStatus.clearCheck();
            atvStartTime.setText("");
            atvEndTime.setText("");
            updateFilterTags(activeFiltersContainer, cgStatus, atvStartTime, atvEndTime, btnShowResults);
        });

        btnCancel.setOnClickListener(v -> bottomSheetDialog.dismiss());
        btnShowResults.setOnClickListener(v -> bottomSheetDialog.dismiss());

        bottomSheetDialog.show();
    }

    private void updateFilterTags(LinearLayout container, ChipGroup cgStatus, AutoCompleteTextView start, AutoCompleteTextView end, TextView btnShowResults) {
        // Keep only "Reset all" (index 0)
        while (container.getChildCount() > 1) {
            container.removeViewAt(1);
        }

        int tagCount = 0;

        // Add Status Tag
        int checkedId = cgStatus.getCheckedChipId();
        if (checkedId != View.NO_ID) {
            Chip selectedChip = cgStatus.findViewById(checkedId);
            addDynamicTag(container, selectedChip.getText().toString());
            tagCount++;
        }

        // Add Time Tag
        String startTime = start.getText().toString();
        String endTime = end.getText().toString();
        if (!startTime.isEmpty() && !endTime.isEmpty()) {
            addDynamicTag(container, startTime + " - " + endTime);
            tagCount++;
        }

        // Update Show Results button text with count
        if (btnShowResults != null) {
            btnShowResults.setText(String.format(Locale.getDefault(), "Show (%d)", tagCount));
        }
    }

    private void addDynamicTag(LinearLayout container, String text) {
        // Inflate the yellow tag layout to ensure same size format as Reset All
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        Chip chip = (Chip) inflater.inflate(R.layout.item_tag_yellow, container, false);
        chip.setText(text);
        
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMarginStart(8);
        chip.setLayoutParams(params);
        
        container.addView(chip);
    }

    private void showRegistrationDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_org_registration, null);

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .setCancelable(false)
                .create();

        // Set background to transparent to respect card corners
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        Button btnSubmit = dialogView.findViewById(R.id.btnSubmitRegistration);
        Button btnCancel = dialogView.findViewById(R.id.btnCancelRegistration);

        btnSubmit.setOnClickListener(v -> {
            // Actual Join Logic
            btnJoin.setVisibility(View.GONE);
            joinedStatusContainer.setVisibility(View.VISIBLE);
            showJoinedEvents();

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
                    joinedStatusContainer.setVisibility(View.GONE);
                    btnJoin.setVisibility(View.VISIBLE);

                    // Switch back to Not Joined Events
                    showNotJoinedEvents();

                    Toast.makeText(getContext(), "You have left the organization.", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void showNotJoinedEvents() {
        if (eventCard1 != null) {
            setupCard(eventCard1, "Design Thinking Workshop", "Alex Rivera", "USC TC - LB Building", "Oct 25, 2023",
                    "Learn the fundamentals of UI/UX design and prototyping.", "1:00 PM - 4:00 PM", "ENDED", R.color.text_secondary);
        }
        if (eventCard2 != null) {
            setupCard(eventCard2, "Introduction to Flutter", "Jamie Chen", "Online", "Sep 12, 2023",
                    "Getting started with cross-platform mobile development.", "2:00 PM - 5:00 PM", "ENDED", R.color.text_secondary);
        }
        if (eventCard3 != null) {
            setupCard(eventCard3, "Git & GitHub Essentials", "Sam Wilson", "USC TC", "Aug 05, 2023",
                    "Master version control for your collaborative projects.", "9:00 AM - 12:00 PM", "ENDED", R.color.text_secondary);
        }
        if (eventCard4 != null) eventCard4.setVisibility(View.GONE);
        if (eventCard5 != null) eventCard5.setVisibility(View.GONE);
    }

    private void showJoinedEvents() {
        if (eventCard1 != null) {
            setupCard(eventCard1, "Annual Tech Expo 2024", "Maria Santos", "SM Seaside Sky Hall", "Dec 20, 2023",
                    "Join us for the biggest tech exhibition of the year! Live demos ongoing.", "All Day", "ONGOING", R.color.green);
        }
        if (eventCard2 != null) {
            setupCard(eventCard2, "Tech Networking Night", "Chris Jordan", "Ayala Center Cebu", "Jan 12, 2024",
                    "Meet fellow developers and industry leaders over coffee.", "6:00 PM - 9:00 PM", "UPCOMING", R.color.yellow);
        }
        if (eventCard3 != null) {
            setupCard(eventCard3, "Code for a Cause: Hackathon", "Sarah Blake", "Online/Remote", "Feb 05, 2024",
                    "A 24-hour hackathon to build solutions for local communities.", "Starts at 9:00 AM", "UPCOMING", R.color.yellow);
        }
        if (eventCard4 != null) {
            setupCard(eventCard4, "Design Thinking Workshop", "Alex Rivera", "USC TC - LB Building", "Oct 25, 2023",
                    "Learn the fundamentals of UI/UX design and prototyping.", "1:00 PM - 4:00 PM", "ENDED", R.color.text_secondary);
        }
        if (eventCard5 != null) {
            setupCard(eventCard5, "Introduction to Flutter", "Jamie Chen", "Online", "Sep 12, 2023",
                    "Getting started with cross-platform mobile development.", "2:00 PM - 5:00 PM", "ENDED", R.color.text_secondary);
        }
    }

    private void setupCard(View card, String title, String host, String loc, String date, String desc, String time, String status, int statusColor) {
        card.setVisibility(View.VISIBLE);
        setCardData(card, title, host, loc, date, desc, time, status, statusColor);

        card.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), EventDetailsActivity.class);
            intent.putExtra("EVENT_TITLE", title);
            intent.putExtra("EVENT_HOST", host);
            intent.putExtra("EVENT_LOCATION", loc);
            intent.putExtra("EVENT_DATETIME", date + " • " + time);
            intent.putExtra("EVENT_DESCRIPTION", desc);
            intent.putExtra("EVENT_STATUS", status);
            intent.putExtra("EVENT_STATUS_COLOR", statusColor);
            startActivity(intent);
        });
    }

    private void setCardData(View card, String title, String host, String loc, String date, String desc, String time, String status, int statusColor) {
        TextView tvTitle = card.findViewById(R.id.tvEventTitle);
        TextView tvLoc = card.findViewById(R.id.tvLocation);
        TextView tvDate = card.findViewById(R.id.tvDate);
        TextView tvDesc = card.findViewById(R.id.tvDescription);
        TextView tvTime = card.findViewById(R.id.tvTime);
        Chip chipStatus = card.findViewById(R.id.chipStatus);

        if (tvTitle != null) tvTitle.setText(title);
        if (tvLoc != null) tvLoc.setText(loc);
        if (tvDate != null) tvDate.setText(date);
        if (tvDesc != null) tvDesc.setText(desc);
        if (tvTime != null) tvTime.setText(time);
        if (chipStatus != null) {
            chipStatus.setText(status);
            chipStatus.setChipBackgroundColorResource(statusColor);
        }
    }
}
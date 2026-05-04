package ph.edu.uscDCISMCatcha.ui.org;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import ph.edu.uscDCISMCatcha.R;
import ph.edu.uscDCISMCatcha.databinding.FragmentCreatePostBinding;
import ph.edu.uscDCISMCatcha.viewmodel.org.CreatePostViewModel;

public class CreatePostFragment extends Fragment {

    private FragmentCreatePostBinding binding;
    private CreatePostViewModel viewModel;
    private boolean isAnnouncementTab = true;
    private String editId = null;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCreatePostBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(CreatePostViewModel.class);

        setupTabs();
        setupDateTimePickers();
        setupButtons();
        observeViewModel();

        Bundle args = getArguments();
        if (args != null) {
            boolean startOnAnnouncement = args.getBoolean("startOnAnnouncement", true);
            editId = args.getString("EDIT_ID");
            switchTab(startOnAnnouncement);

            if (editId != null) {
                binding.tvNavTitle.setText(startOnAnnouncement ? "Edit Announcement" : "Edit Event");
                binding.btnBroadcast.setText("Update Announcement");
                binding.btnCreateEvent.setText("Update Event");
                
                // Hide tabs when editing to prevent confusion
                binding.layoutTabs.setVisibility(View.GONE);

                if (startOnAnnouncement) {
                    viewModel.fetchAnnouncement(editId);
                } else {
                    viewModel.fetchEvent(editId);
                }
            }
        }
    }

    private void observeViewModel() {
        viewModel.getStatusMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
                if (message.contains("successfully")) {
                    requireActivity().getSupportFragmentManager().popBackStack();
                }
                viewModel.clearStatus();
            }
        });

        viewModel.getExistingAnnouncement().observe(getViewLifecycleOwner(), announcement -> {
            if (announcement != null) {
                binding.etTitle.setText(announcement.getTitle());
                binding.etMessage.setText(announcement.getContent());
            }
        });

        viewModel.getExistingEvent().observe(getViewLifecycleOwner(), event -> {
            if (event != null) {
                binding.etTitle.setText(event.getTitle());
                binding.etLocation.setText(event.getLocation());
                binding.etDescription.setText(event.getDescription());
                binding.etCapacity.setText(String.valueOf(event.getMaxCapacity()));
                
                if (event.getStartDateTime() != null) {
                    binding.tvDate.setText(dateFormat.format(event.getStartDateTime().toDate()));
                    binding.tvTime.setText(timeFormat.format(event.getStartDateTime().toDate()));
                }
                if (event.getEndDateTime() != null) {
                    binding.tvEndTime.setText(timeFormat.format(event.getEndDateTime().toDate()));
                }
            }
        });
    }

    private void setupTabs() {
        binding.btnTabAnnouncement.setOnClickListener(v -> switchTab(true));
        binding.btnTabEvent.setOnClickListener(v -> switchTab(false));

        // Initialize state
        switchTab(true);
    }

    private void switchTab(boolean showAnnouncement) {
        isAnnouncementTab = showAnnouncement;

        if (editId == null) {
            binding.tvNavTitle.setText(showAnnouncement ? "Create post" : "Create event");
        }

        if (showAnnouncement) {
            binding.btnTabAnnouncement.setBackgroundResource(R.drawable.bg_tab_selected);
            binding.btnTabAnnouncement.setTextColor(Color.parseColor("#F5C842"));
            binding.btnTabEvent.setBackgroundColor(Color.TRANSPARENT);
            binding.btnTabEvent.setTextColor(Color.parseColor("#888888"));
        } else {
            binding.btnTabEvent.setBackgroundResource(R.drawable.bg_tab_selected);
            binding.btnTabEvent.setTextColor(Color.parseColor("#F5C842"));
            binding.btnTabAnnouncement.setBackgroundColor(Color.TRANSPARENT);
            binding.btnTabAnnouncement.setTextColor(Color.parseColor("#888888"));
        }

        binding.layoutAnnouncement.setVisibility(showAnnouncement ? View.VISIBLE : View.GONE);
        binding.layoutEvent.setVisibility(showAnnouncement ? View.GONE : View.VISIBLE);
    }

    private void setupDateTimePickers() {
        binding.layoutPickDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            new DatePickerDialog(requireContext(),
                    (datePicker, year, month, day) -> {
                        String date = String.format(Locale.getDefault(),
                                "%d-%02d-%02d", year, month + 1, day);
                        binding.tvDate.setText(date);
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            ).show();
        });

        binding.layoutPickTime.setOnClickListener(v -> showTimePicker(binding.tvTime));
        binding.layoutPickEndTime.setOnClickListener(v -> showTimePicker(binding.tvEndTime));
    }

    private void showTimePicker(TextView targetView) {
        Calendar calendar = Calendar.getInstance();
        new TimePickerDialog(requireContext(),
                (timePicker, hour, minute) -> {
                    String amPm = hour < 12 ? "AM" : "PM";
                    int displayHour = hour % 12 == 0 ? 12 : hour % 12;
                    String time = String.format(Locale.getDefault(),
                            "%d:%02d %s", displayHour, minute, amPm);
                    targetView.setText(time);
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                false
        ).show();
    }

    private void setupButtons() {
        binding.btnBack.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack());

        binding.btnBroadcast.setOnClickListener(v -> {
            String title   = binding.etTitle.getText().toString().trim();
            String message = binding.etMessage.getText().toString().trim();

            if (title.isEmpty()) {
                binding.etTitle.setError("Required");
                return;
            }
            if (message.isEmpty()) {
                Toast.makeText(requireContext(), "Message cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            if (editId != null) {
                viewModel.updateAnnouncement(editId, title, message);
            } else {
                viewModel.postAnnouncement(title, message, binding.switchPushNotification.isChecked());
            }
        });

        binding.btnCreateEvent.setOnClickListener(v -> {
            String title       = binding.etTitle.getText().toString().trim();
            String date        = binding.tvDate.getText().toString();
            String time        = binding.tvTime.getText().toString();
            String endTime     = binding.tvEndTime.getText().toString();
            String location    = binding.etLocation.getText().toString().trim();
            String description = binding.etDescription.getText().toString().trim();
            String capacityStr = binding.etCapacity.getText().toString().trim();

            if (title.isEmpty()) {
                binding.etTitle.setError("Required");
                return;
            }
            if (date.equals("Pick date") || time.equals("Pick time") || location.isEmpty()) {
                Toast.makeText(requireContext(), "Fill in all required fields", Toast.LENGTH_SHORT).show();
                return;
            }

            int capacity = 0;
            if (!capacityStr.isEmpty()) {
                try {
                    capacity = Integer.parseInt(capacityStr);
                } catch (NumberFormatException e) {
                    binding.etCapacity.setError("Invalid number");
                    return;
                }
            }

            if (editId != null) {
                viewModel.updateEvent(editId, title, date, time, endTime, location, description, capacity);
            } else {
                viewModel.createEvent(title, date, time, endTime, location, description, capacity, binding.switchAutoReminders.isChecked());
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

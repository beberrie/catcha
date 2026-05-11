package ph.edu.uscDCISMCatcha.ui.org;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view. View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import java.util.Calendar;
import java.util.Locale;
import ph.edu.uscDCISMCatcha.R;
import ph.edu.uscDCISMCatcha.databinding.FragmentCreatePostBinding;
import ph.edu.uscDCISMCatcha.viewmodel.CreatePostViewModel;

public class CreatePostFragment extends Fragment {

    private FragmentCreatePostBinding binding;
    private CreatePostViewModel viewModel;
    private boolean isAnnouncementTab = true;

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

        viewModel.getStatusMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
                if (message.contains("successfully")) clearInputs();
                viewModel.clearStatus();
            }
        });

        setupTabs();
        setupDateTimePickers();
        setupButtons();

        Bundle args = getArguments();
        if (args != null) {
            switchTab(args.getBoolean("startOnAnnouncement", true));
        }
    }

    private void setupTabs() {
        binding.btnTabAnnouncement.setOnClickListener(v -> switchTab(true));
        binding.btnTabEvent.setOnClickListener(v -> switchTab(false));
        switchTab(true);
    }

    private void switchTab(boolean showAnnouncement) {
        isAnnouncementTab = showAnnouncement;
        binding.tvNavTitle.setText(showAnnouncement ? "Create post" : "Create event");

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
            Calendar c = Calendar.getInstance();
            new DatePickerDialog(requireContext(),
                    (dp, year, month, day) -> binding.tvDate.setText(
                            String.format(Locale.getDefault(), "%d-%02d-%02d",
                                    year, month + 1, day)),
                    c.get(Calendar.YEAR),
                    c.get(Calendar.MONTH),
                    c.get(Calendar.DAY_OF_MONTH)).show();
        });

        binding.layoutPickTime.setOnClickListener(v -> showTimePicker(binding.tvTime));
        binding.layoutPickEndTime.setOnClickListener(v -> showTimePicker(binding.tvEndTime));
    }

    private void showTimePicker(TextView target) {
        Calendar c = Calendar.getInstance();
        new TimePickerDialog(requireContext(),
                (tp, hour, minute) -> {
                    String amPm = hour < 12 ? "AM" : "PM";
                    int h = hour % 12 == 0 ? 12 : hour % 12;
                    target.setText(String.format(Locale.getDefault(),
                            "%d:%02d %s", h, minute, amPm));
                },
                c.get(Calendar.HOUR_OF_DAY),
                c.get(Calendar.MINUTE), false).show();
    }

    private void setupButtons() {
        binding.btnBack.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack());

        binding.btnBroadcast.setOnClickListener(v -> {
            String title   = binding.etTitle.getText().toString().trim();
            String message = binding.etMessage.getText().toString().trim();
            boolean sendPush = binding.switchPushNotification.isChecked();

            if (title.isEmpty()) { binding.etTitle.setError("Required"); return; }
            if (message.isEmpty()) {
                Toast.makeText(requireContext(), "Message cannot be empty",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            viewModel.postAnnouncement(title, message, sendPush);
        });

        binding.btnCreateEvent.setOnClickListener(v -> {
            String title       = binding.etTitle.getText().toString().trim();
            String date        = binding.tvDate.getText().toString();
            String time        = binding.tvTime.getText().toString();
            String endTime     = binding.tvEndTime.getText().toString();
            String location    = binding.etLocation.getText().toString().trim();
            String description = binding.etDescription.getText().toString().trim();
            boolean autoReminders = binding.switchAutoReminders.isChecked();

            if (title.isEmpty()) { binding.etTitle.setError("Required"); return; }
            if (date.equals("Pick date") || time.equals("Pick time") || location.isEmpty()) {
                Toast.makeText(requireContext(), "Fill in all required fields",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            viewModel.createEvent(title, date, time, endTime,
                    location, description, autoReminders);
        });
    }

    private void clearInputs() {
        binding.etTitle.setText("");
        binding.etMessage.setText("");
        binding.etLocation.setText("");
        binding.etDescription.setText("");
        binding.tvDate.setText("Pick date");
        binding.tvTime.setText("Pick time");
        binding.tvEndTime.setText("Pick time");
        binding.switchPushNotification.setChecked(false);
        binding.switchAutoReminders.setChecked(false);
        binding.etTitle.requestFocus();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

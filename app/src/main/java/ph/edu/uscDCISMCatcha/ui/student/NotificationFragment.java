package ph.edu.uscDCISMCatcha.ui.student;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import java.util.ArrayList;
import java.util.List;
import ph.edu.uscDCISMCatcha.R;
import ph.edu.uscDCISMCatcha.adapters.NotificationAdapter;
import ph.edu.uscDCISMCatcha.databinding.FragmentNotificationBinding;
import ph.edu.uscDCISMCatcha.models.NotificationModel;
import ph.edu.uscDCISMCatcha.viewmodel.NotificationViewModel;
import ph.edu.uscDCISMCatcha.viewmodel.SharedAnnouncementViewModel;

public class NotificationFragment extends Fragment
        implements NotificationAdapter.OnMarkAsReadListener {

    private FragmentNotificationBinding binding;
    private NotificationAdapter adapter;
    private NotificationViewModel viewModel;
    private SharedAnnouncementViewModel sharedViewModel;
    private List<NotificationModel> notificationList = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentNotificationBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize ViewModels
        viewModel = new ViewModelProvider(this).get(NotificationViewModel.class);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedAnnouncementViewModel.class);

        // Setup UI components
        setupTabs();
        setupRecyclerView();
        setupBannerButtons();
        observeNotifications();
        observeNewAnnouncement();

        // Load Initial Data
        viewModel.loadDummyNotifications();

        // Mark all as read click listener
        binding.tvMarkAllRead.setOnClickListener(v -> {
            adapter.markAllAsRead();
            viewModel.markAllAsRead();
            binding.layoutPushBanner.setVisibility(View.GONE);
        });
    }

    private void setupTabs() {
        binding.btnTabNotifications.setOnClickListener(v -> switchTab(true));
        binding.btnTabToken.setOnClickListener(v -> switchTab(false));
        switchTab(true); // Default to Notifications tab
    }

    private void switchTab(boolean showNotif) {
        if (showNotif) {
            binding.btnTabNotifications.setBackgroundResource(R.drawable.bg_tab_selected);
            binding.btnTabNotifications.setTextColor(Color.parseColor("#F5C842"));
            binding.btnTabToken.setBackgroundColor(Color.TRANSPARENT);
            binding.btnTabToken.setTextColor(Color.parseColor("#888888"));
        } else {
            binding.btnTabToken.setBackgroundResource(R.drawable.bg_tab_selected);
            binding.btnTabToken.setTextColor(Color.parseColor("#F5C842"));
            binding.btnTabNotifications.setBackgroundColor(Color.TRANSPARENT);
            binding.btnTabNotifications.setTextColor(Color.parseColor("#888888"));
        }
        binding.layoutNotifications.setVisibility(showNotif ? View.VISIBLE : View.GONE);
        binding.layoutPushToken.setVisibility(showNotif ? View.GONE : View.VISIBLE);
    }

    private void setupRecyclerView() {
        adapter = new NotificationAdapter(new ArrayList<>(), this);
        binding.rvNotifications.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvNotifications.setAdapter(adapter);
    }

    private void observeNotifications() {
        viewModel.getNotifications().observe(getViewLifecycleOwner(), list -> {
            if (list != null) {
                notificationList = new ArrayList<>(list);
                adapter.updateItems(notificationList);
            }
        });
    }

    private void observeNewAnnouncement() {
        sharedViewModel.getNewAnnouncement().observe(getViewLifecycleOwner(), newNotif -> {
            if (newNotif == null) return;

            // 1. Show the top push banner
            showBanner(newNotif);

            // 2. Add the notification to the actual list at the top
            notificationList.add(0, newNotif);
            adapter.updateItems(new ArrayList<>(notificationList));

            // 3. Scroll recycler view to top
            binding.rvNotifications.scrollToPosition(0);

            // 4. Clear the LiveData so it doesn't pop up again on rotation
            sharedViewModel.clearNewAnnouncement();
        });
    }

    private void setupBannerButtons() {
        binding.btnBannerDismiss.setOnClickListener(v ->
                binding.layoutPushBanner.setVisibility(View.GONE));

        binding.btnBannerView.setOnClickListener(v -> {
            // Logic for "viewing" the announcement can go here
            binding.layoutPushBanner.setVisibility(View.GONE);
        });
    }

    private void showBanner(NotificationModel notif) {
        binding.layoutPushBanner.setVisibility(View.VISIBLE);
        binding.tvBannerAppName.setText(notif.getOrgName() + " · now");
        binding.tvBannerSubtitle.setText("Announcement");
        binding.tvBannerTitle.setText(notif.getTitle());

        // Truncate body text if too long
        String content = notif.getContent();
        if (content != null && content.length() > 80) {
            content = content.substring(0, 80) + "...";
        }
        binding.tvBannerBody.setText(content);

        binding.layoutBannerActions.setVisibility(View.VISIBLE);
    }

    @Override
    public void onMarkAsRead(NotificationModel item, int position) {
        item.setRead(true);
        adapter.notifyItemChanged(position);
        viewModel.markAsRead(item.getId());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
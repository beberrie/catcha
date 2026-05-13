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
import ph.edu.uscDCISMCatcha.data.models.NotificationModel;
import ph.edu.uscDCISMCatcha.viewmodel.student.SharedAnnouncementViewModel;


public class NotificationFragment extends Fragment
        implements NotificationAdapter.OnMarkAsReadListener {


    private FragmentNotificationBinding binding;
    private NotificationAdapter adapter;
    private SharedAnnouncementViewModel sharedViewModel;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentNotificationBinding.inflate(
                inflater, container, false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        // Same activity scope as CreatePostFragment
        sharedViewModel = new ViewModelProvider(requireActivity())
                .get(SharedAnnouncementViewModel.class);


        setupTabs();
        setupRecyclerView();
        setupBannerButtons();
        observeAnnouncementList();
        observeNewAnnouncement();


        // Mark all as read button
        binding.tvMarkAllRead.setOnClickListener(v -> {
            sharedViewModel.markAllAsRead();
            binding.layoutPushBanner.setVisibility(View.GONE);
        });
    }


    // Observe full list — updates RecyclerView
    private void observeAnnouncementList() {
        sharedViewModel.getAnnouncementList().observe(
                getViewLifecycleOwner(), list -> {
                    if (list != null) {
                        adapter.updateItems(
                                new ArrayList<>(list));
                        // Show empty state if no announcements
                        binding.rvNotifications.setVisibility(
                                list.isEmpty()
                                        ? View.GONE : View.VISIBLE);
                    }
                });
    }


    // Observe only the latest — shows banner pop-up
    private void observeNewAnnouncement() {
        sharedViewModel.getNewAnnouncement().observe(
                getViewLifecycleOwner(), newNotif -> {
                    if (newNotif == null) return;


                    // Show push banner
                    showBanner(newNotif);


                    // Scroll to top of list
                    binding.rvNotifications.scrollToPosition(0);


                    // Clear so it doesn't re-trigger on rotation
                    sharedViewModel.clearNewAnnouncement();
                });
    }


    private void showBanner(NotificationModel notif) {
        binding.layoutPushBanner.setVisibility(View.VISIBLE);
        binding.tvBannerAppName.setText(
                notif.getOrgName() + " · now");
        binding.tvBannerSubtitle.setText("Announcement");
        binding.tvBannerTitle.setText(notif.getTitle());


        String body = notif.getContent();
        binding.tvBannerBody.setText(
                body != null && body.length() > 80
                        ? body.substring(0, 80) + "..."
                        : body);


        binding.layoutBannerActions.setVisibility(View.VISIBLE);
    }


    private void setupTabs() {
        binding.btnTabNotifications.setOnClickListener(
                v -> switchTab(true));
        binding.btnTabToken.setOnClickListener(
                v -> switchTab(false));
        switchTab(true);
    }


    private void switchTab(boolean showNotif) {
        if (showNotif) {
            binding.btnTabNotifications.setBackgroundResource(
                    R.drawable.bg_tab_selected);
            binding.btnTabNotifications.setTextColor(
                    Color.parseColor("#F5C842"));
            binding.btnTabToken.setBackgroundColor(
                    Color.TRANSPARENT);
            binding.btnTabToken.setTextColor(
                    Color.parseColor("#888888"));
        } else {
            binding.btnTabToken.setBackgroundResource(
                    R.drawable.bg_tab_selected);
            binding.btnTabToken.setTextColor(
                    Color.parseColor("#F5C842"));
            binding.btnTabNotifications.setBackgroundColor(
                    Color.TRANSPARENT);
            binding.btnTabNotifications.setTextColor(
                    Color.parseColor("#888888"));
        }
        binding.layoutNotifications.setVisibility(
                showNotif ? View.VISIBLE : View.GONE);
        binding.layoutPushToken.setVisibility(
                showNotif ? View.GONE : View.VISIBLE);
    }


    private void setupBannerButtons() {
        binding.btnBannerDismiss.setOnClickListener(v ->
                binding.layoutPushBanner.setVisibility(View.GONE));
        binding.btnBannerView.setOnClickListener(v ->
                binding.layoutPushBanner.setVisibility(View.GONE));
    }


    private void setupRecyclerView() {
        adapter = new NotificationAdapter(new ArrayList<>(), this);
        binding.rvNotifications.setLayoutManager(
                new LinearLayoutManager(requireContext()));
        binding.rvNotifications.setAdapter(adapter);
    }


    @Override
    public void onMarkAsRead(NotificationModel item, int position) {
        sharedViewModel.markAsRead(item.getId());
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

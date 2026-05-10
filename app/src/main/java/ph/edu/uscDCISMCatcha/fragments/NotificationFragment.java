package ph.edu.uscDCISMCatcha.fragments;

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
import ph.edu.uscDCISMCatcha.R;
import ph.edu.uscDCISMCatcha.adapters.NotificationAdapter;
import ph.edu.uscDCISMCatcha.databinding.FragmentNotificationBinding;
import ph.edu.uscDCISMCatcha.models.NotificationModel;
import ph.edu.uscDCISMCatcha.viewmodel.NotificationViewModel;

public class NotificationFragment extends Fragment
        implements NotificationAdapter.OnMarkAsReadListener {

    private FragmentNotificationBinding binding;
    private NotificationAdapter adapter;
    private NotificationViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentNotificationBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this)
                .get(NotificationViewModel.class);

        setupTabs();
        setupRecyclerView();
        setupBannerButtons();
        observeNotifications();

        viewModel.loadDummyNotifications();

        binding.tvMarkAllRead.setOnClickListener(v -> {
            adapter.markAllAsRead();
            viewModel.markAllAsRead();
            binding.layoutPushBanner.setVisibility(View.GONE);
        });
    }

    private void setupTabs() {
        binding.btnTabNotifications.setOnClickListener(v -> switchTab(true));
        binding.btnTabToken.setOnClickListener(v -> switchTab(false));
        switchTab(true);
    }

    private void switchTab(boolean showNotif) {
        if (showNotif) {
            binding.btnTabNotifications.setBackgroundResource(
                    R.drawable.bg_tab_selected);
            binding.btnTabNotifications.setTextColor(
                    Color.parseColor("#F5C842"));
            binding.btnTabToken.setBackgroundColor(Color.TRANSPARENT);
            binding.btnTabToken.setTextColor(Color.parseColor("#888888"));
        } else {
            binding.btnTabToken.setBackgroundResource(
                    R.drawable.bg_tab_selected);
            binding.btnTabToken.setTextColor(Color.parseColor("#F5C842"));
            binding.btnTabNotifications.setBackgroundColor(Color.TRANSPARENT);
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

    private void observeNotifications() {
        viewModel.getNotifications().observe(getViewLifecycleOwner(),
                list -> {
                    if (list != null) adapter.updateItems(list);
                });
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
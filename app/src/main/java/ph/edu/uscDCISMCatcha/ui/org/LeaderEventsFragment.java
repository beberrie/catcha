package ph.edu.uscDCISMCatcha.ui.org;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import java.util.ArrayList;
import ph.edu.uscDCISMCatcha.R;
import ph.edu.uscDCISMCatcha.adapters.EventPostAdapter;
import ph.edu.uscDCISMCatcha.databinding.FragmentLeaderEventsBinding;
import ph.edu.uscDCISMCatcha.data.models.EventModel;
import ph.edu.uscDCISMCatcha.viewmodel.org.LeaderEventViewModel;

public class LeaderEventsFragment extends Fragment
        implements EventPostAdapter.OnEventMenuListener {

    private FragmentLeaderEventsBinding binding;
    private LeaderEventViewModel viewModel;
    private EventPostAdapter adapter;

    private static final String ARG_ORG_ID    = "orgId";
    private static final String ARG_IS_LEADER = "isLeader";
    private String orgId;
    private boolean isLeader;

    public static LeaderEventsFragment newInstance(
            String orgId, boolean isLeader) {
        LeaderEventsFragment f = new LeaderEventsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ORG_ID, orgId);
        args.putBoolean(ARG_IS_LEADER, isLeader);
        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLeaderEventsBinding.inflate(
                inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();
        orgId    = args != null ? args.getString(ARG_ORG_ID, "")   : "";
        isLeader = args != null && args.getBoolean(ARG_IS_LEADER, false);

        viewModel = new ViewModelProvider(this)
                .get(LeaderEventViewModel.class);

        setupRecyclerView();
        observeEvents();
        setupButtons();

        binding.layoutLeaderBadge.setVisibility(
                isLeader ? View.VISIBLE : View.GONE);

        viewModel.fetchOrgEvents(orgId);

        viewModel.getStatusMessage().observe(
                getViewLifecycleOwner(), msg -> {
                    if (msg != null) {
                        Toast.makeText(requireContext(),
                                msg, Toast.LENGTH_SHORT).show();
                        viewModel.clearStatus();
                    }
                });
    }

    private void setupRecyclerView() {
        adapter = new EventPostAdapter(
                new ArrayList<>(), isLeader, this);
        binding.rvLeaderEvents.setLayoutManager(
                new LinearLayoutManager(requireContext()));
        binding.rvLeaderEvents.setAdapter(adapter);
    }

    private void observeEvents() {
        viewModel.getEvents().observe(
                getViewLifecycleOwner(),
                list -> {
                    if (list != null)
                        adapter.updateItems(list);
                });
    }

    private void setupButtons() {
        binding.btnBack.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager()
                        .popBackStack());

        binding.btnAddEvent.setVisibility(
                isLeader ? View.VISIBLE : View.GONE);

        binding.btnAddEvent.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putBoolean("startOnAnnouncement", false);
            args.putString("orgId", orgId);

            CreatePostFragment fragment = new CreatePostFragment();
            fragment.setArguments(args);

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        });
    }

    @Override
    public void onEdit(EventModel event) {
        Bundle args = new Bundle();
        args.putBoolean("startOnAnnouncement", false);
        args.putString("EDIT_ID", event.getId());

        CreatePostFragment fragment = new CreatePostFragment();
        fragment.setArguments(args);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onDelete(EventModel event) {
        viewModel.deleteEvent(event.getId());
    }

    @Override
    public void onClose(EventModel event, int position) {
        adapter.removeItem(position);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

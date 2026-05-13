package ph.edu.uscDCISMCatcha.ui.student;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.chip.Chip;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ph.edu.uscDCISMCatcha.R;
import ph.edu.uscDCISMCatcha.data.models.EventModel;
import ph.edu.uscDCISMCatcha.data.models.Organization;
import ph.edu.uscDCISMCatcha.databinding.FragmentSearchBinding;
import ph.edu.uscDCISMCatcha.databinding.ItemSearchResultBinding;

public class SearchFragment extends Fragment {

    private FragmentSearchBinding binding;
    private FirebaseFirestore db;
    private final List<Object> searchResults = new ArrayList<>();
    
    private static final String PREFS_NAME = "CatchaPrefs";
    private static final String KEY_RECENT_SEARCHES = "recent_searches";
    private List<String> recentSearches = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = FirebaseFirestore.getInstance();

        loadRecentSearches();
        setupUI();
    }

    private void setupUI() {
        binding.btnBack.setOnClickListener(v -> {
            if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                getParentFragmentManager().popBackStack();
            }
        });

        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    binding.btnClearSearch.setVisibility(View.VISIBLE);
                } else {
                    binding.btnClearSearch.setVisibility(View.GONE);
                    showDefaultState();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        binding.etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                performSearch(binding.etSearch.getText().toString().trim());
                return true;
            }
            return false;
        });

        binding.btnClearSearch.setOnClickListener(v -> {
            binding.etSearch.setText("");
            showDefaultState();
        });

        binding.tvClearRecent.setOnClickListener(v -> clearRecentSearches());

        setupSuggestedChips();
        displayRecentSearches();
    }

    private void setupSuggestedChips() {
        for (int i = 0; i < binding.cgSuggested.getChildCount(); i++) {
            View child = binding.cgSuggested.getChildAt(i);
            if (child instanceof Chip) {
                Chip chip = (Chip) child;
                chip.setOnClickListener(v -> {
                    binding.etSearch.setText(chip.getText());
                    performSearch(chip.getText().toString());
                });
            }
        }
    }

    private void performSearch(String query) {
        if (query.isEmpty()) return;

        saveRecentSearch(query);
        hideKeyboard();

        binding.sectionRecent.setVisibility(View.GONE);
        binding.sectionSuggested.setVisibility(View.GONE);
        binding.sectionResults.setVisibility(View.VISIBLE);
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.tvNoResults.setVisibility(View.GONE);
        binding.rvResults.setVisibility(View.GONE);

        searchResults.clear();
        String lowerQuery = query.toLowerCase().trim();
        
        // Search Orgs
        db.collection("organizations")
                .get()
                .addOnCompleteListener(orgTask -> {
                    if (orgTask.isSuccessful() && orgTask.getResult() != null) {
                        for (QueryDocumentSnapshot doc : orgTask.getResult()) {
                            Organization org = doc.toObject(Organization.class);
                            if (org != null) {
                                org.setId(doc.getId());
                                String name = org.getName();
                                if (name != null && name.toLowerCase().contains(lowerQuery)) {
                                    searchResults.add(org);
                                }
                            }
                        }
                    }
                    
                    // Search Events after Orgs (nested but with completion check)
                    db.collection("events")
                            .get()
                            .addOnCompleteListener(eventTask -> {
                                if (eventTask.isSuccessful() && eventTask.getResult() != null) {
                                    for (QueryDocumentSnapshot doc : eventTask.getResult()) {
                                        EventModel event = doc.toObject(EventModel.class);
                                        if (event != null) {
                                            event.setEventId(doc.getId());
                                            String title = event.getTitle();
                                            if (title != null && title.toLowerCase().contains(lowerQuery)) {
                                                searchResults.add(event);
                                            }
                                        }
                                    }
                                }
                                updateResultsUI(query);
                            });
                });
    }

    private void updateResultsUI(String query) {
        if (binding == null) return;
        
        binding.progressBar.setVisibility(View.GONE);
        if (searchResults.isEmpty()) {
            binding.tvNoResults.setVisibility(View.VISIBLE);
            binding.rvResults.setVisibility(View.GONE);
        } else {
            binding.tvNoResults.setVisibility(View.GONE);
            binding.rvResults.setVisibility(View.VISIBLE);
            binding.rvResults.setLayoutManager(new LinearLayoutManager(getContext()));
            binding.rvResults.setAdapter(new SearchAdapter(searchResults));
        }
        binding.tvResultsCount.setText(getString(R.string.search_results_count, searchResults.size(), query));
    }

    private void showDefaultState() {
        binding.sectionRecent.setVisibility(recentSearches.isEmpty() ? View.GONE : View.VISIBLE);
        binding.sectionSuggested.setVisibility(View.VISIBLE);
        binding.sectionResults.setVisibility(View.GONE);
        binding.progressBar.setVisibility(View.GONE);
    }

    private void loadRecentSearches() {
        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        recentSearches = new ArrayList<>();
        try {
            String saved = prefs.getString(KEY_RECENT_SEARCHES, "");
            if (saved != null && !saved.isEmpty()) {
                for (String s : saved.split(",")) {
                    if (!s.trim().isEmpty()) recentSearches.add(s);
                }
            }
        } catch (ClassCastException e) {
            // If old data was a StringSet, clear it to avoid future crashes
            prefs.edit().remove(KEY_RECENT_SEARCHES).apply();
        }
    }

    private void saveRecentSearch(String query) {
        recentSearches.remove(query);
        recentSearches.add(0, query);
        if (recentSearches.size() > 5) {
            recentSearches.remove(5);
        }
        
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < recentSearches.size(); i++) {
            sb.append(recentSearches.get(i));
            if (i < recentSearches.size() - 1) sb.append(",");
        }
        
        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_RECENT_SEARCHES, sb.toString()).apply();
        displayRecentSearches();
    }

    private void clearRecentSearches() {
        recentSearches.clear();
        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().remove(KEY_RECENT_SEARCHES).apply();
        displayRecentSearches();
        showDefaultState();
    }

    private void displayRecentSearches() {
        binding.cgRecent.removeAllViews();
        for (String search : recentSearches) {
            Chip chip = new Chip(getContext());
            chip.setText(search);
            chip.setChipIconResource(android.R.drawable.ic_menu_recent_history);
            chip.setOnClickListener(v -> {
                binding.etSearch.setText(search);
                performSearch(search);
            });
            binding.cgRecent.addView(chip);
        }
        binding.sectionRecent.setVisibility(recentSearches.isEmpty() ? View.GONE : View.VISIBLE);
    }

    private void hideKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private class SearchAdapter extends androidx.recyclerview.widget.RecyclerView.Adapter<SearchAdapter.SearchViewHolder> {
        private final List<Object> items;

        public SearchAdapter(List<Object> items) { this.items = items; }

        @NonNull
        @Override
        public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemSearchResultBinding b = ItemSearchResultBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new SearchViewHolder(b);
        }

        @Override
        public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
            Object item = items.get(position);
            if (item instanceof Organization) {
                Organization org = (Organization) item;
                holder.b.tvResultTitle.setText(org.getName());
                holder.b.tvResultSubtitle.setText("Organization • " + org.getDepartment());
                holder.b.ivResultIcon.setImageResource(R.drawable.bg_avatar_dark);
                holder.itemView.setOnClickListener(v -> navigateToOrgProfile(org));
            } else {
                EventModel event = (EventModel) item;
                holder.b.tvResultTitle.setText(event.getTitle());
                holder.b.tvResultSubtitle.setText("Event • " + event.getLocation());
                holder.b.ivResultIcon.setImageResource(R.drawable.ic_events);
                holder.itemView.setOnClickListener(v -> navigateToEventDetails(event));
            }
        }

        @Override
        public int getItemCount() { return items.size(); }

        private void navigateToOrgProfile(Organization org) {
            OrgProfileFragment fragment = new OrgProfileFragment();
            Bundle args = new Bundle();
            args.putString("ORG_NAME", org.getName());
            args.putString("ORG_ID", org.getId());
            fragment.setArguments(args);
            getParentFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        }

        private void navigateToEventDetails(EventModel event) {
            // Reusing existing activity for event details if possible
            // Or navigate to a fragment
            // For now, simple Toast or generic implementation
            android.widget.Toast.makeText(getContext(), "Opening " + event.getTitle(), android.widget.Toast.LENGTH_SHORT).show();
        }

        class SearchViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
            ItemSearchResultBinding b;
            SearchViewHolder(ItemSearchResultBinding b) {
                super(b.getRoot());
                this.b = b;
            }
        }
    }
}

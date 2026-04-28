package ph.edu.uscDCISMCatcha.fragments.org;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import ph.edu.uscDCISMCatcha.R;
import java.util.ArrayList;
import java.util.List;

public class EventFiltersBottomSheet extends BottomSheetDialogFragment {

    private TextView btnCancel, btnShowResults;
    private ChipGroup cgStatus;
    private LinearLayout activeFiltersContainer;
    private AutoCompleteTextView atvStartTime, atvEndTime;
    private Chip chipResetAll;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_event_filters, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnCancel = view.findViewById(R.id.btnCancel);
        btnShowResults = view.findViewById(R.id.btnShowResults);
        cgStatus = view.findViewById(R.id.cgStatus);
        activeFiltersContainer = view.findViewById(R.id.activeFiltersContainer);
        atvStartTime = view.findViewById(R.id.atvStartTime);
        atvEndTime = view.findViewById(R.id.atvEndTime);
        chipResetAll = view.findViewById(R.id.chipResetAll);

        setupTimeDropdowns();

        cgStatus.setOnCheckedStateChangeListener((group, checkedIds) -> updateActiveFilters());
        atvStartTime.setOnItemClickListener((parent, v, position, id) -> updateActiveFilters());
        atvEndTime.setOnItemClickListener((parent, v, position, id) -> updateActiveFilters());

        btnCancel.setOnClickListener(v -> dismiss());
        btnShowResults.setOnClickListener(v -> dismiss());
        chipResetAll.setOnClickListener(v -> resetFilters());

        updateActiveFilters();
    }

    private void setupTimeDropdowns() {
        List<String> timeList = new ArrayList<>();
        String[] periods = {"AM", "PM"};
        
        // 12:00 AM
        timeList.add("12:00 AM");
        timeList.add("12:30 AM");
        
        // 1:00 AM to 11:30 AM
        for (int h = 1; h <= 11; h++) {
            timeList.add(h + ":00 AM");
            timeList.add(h + ":30 AM");
        }
        
        // 12:00 PM
        timeList.add("12:00 PM");
        timeList.add("12:30 PM");
        
        // 1:00 PM to 11:30 PM
        for (int h = 1; h <= 11; h++) {
            timeList.add(h + ":00 PM");
            timeList.add(h + ":30 PM");
        }
        
        timeList.add("11:59 PM");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, timeList);
        atvStartTime.setAdapter(adapter);
        atvEndTime.setAdapter(adapter);
        
        // Disable manual typing
        atvStartTime.setInputType(0);
        atvEndTime.setInputType(0);
    }

    private void updateActiveFilters() {
        // Remove all except Reset All chip (first child)
        while (activeFiltersContainer.getChildCount() > 1) {
            activeFiltersContainer.removeViewAt(1);
        }

        int count = 0;

        // Add Status Tag
        int checkedId = cgStatus.getCheckedChipId();
        if (checkedId != View.NO_ID) {
            Chip selectedChip = cgStatus.findViewById(checkedId);
            addActiveTag(selectedChip.getText().toString());
            count++;
        }

        // Add Time Range Tag if changed from default or always? 
        // Design shows it as active.
        String startTime = atvStartTime.getText().toString();
        String endTime = atvEndTime.getText().toString();
        addActiveTag(startTime + " - " + endTime);
        count++;

        btnShowResults.setText("Show (" + count + ")");
    }

    private void addActiveTag(String text) {
        Chip tag = (Chip) getLayoutInflater().inflate(R.layout.item_tag_yellow, activeFiltersContainer, false);
        tag.setText(text);
        tag.setCheckable(false);
        tag.setClickable(false);
        
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32, getResources().getDisplayMetrics())
        );
        params.setMarginStart((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
        tag.setLayoutParams(params);

        activeFiltersContainer.addView(tag);
    }

    private void resetFilters() {
        cgStatus.clearCheck();
        atvStartTime.setText("12:00 AM", false);
        atvEndTime.setText("11:59 PM", false);
        updateActiveFilters();
    }
}
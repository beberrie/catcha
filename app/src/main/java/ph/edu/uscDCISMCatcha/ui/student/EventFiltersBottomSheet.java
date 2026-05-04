package ph.edu.uscDCISMCatcha.ui.student;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
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

    public interface OnFiltersAppliedListener {
        void onFiltersApplied(String status, String startTime, String endTime);
    }

    private OnFiltersAppliedListener listener;
    private TextView btnCancel, btnShowResults;
    private ChipGroup cgStatus;
    private LinearLayout activeFiltersContainer;
    private AutoCompleteTextView atvStartTime, atvEndTime;
    private Chip chipResetAll;
    private Button btnSetTime;
    private boolean isTimeSet = false;

    public void setOnFiltersAppliedListener(OnFiltersAppliedListener listener) {
        this.listener = listener;
    }

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
        btnSetTime = view.findViewById(R.id.btnSetTime);

        setupTimeDropdowns();

        cgStatus.setOnCheckedStateChangeListener((group, checkedIds) -> updateActiveFilters());
        
        btnSetTime.setOnClickListener(v -> {
            isTimeSet = true;
            updateActiveFilters();
        });

        btnCancel.setOnClickListener(v -> dismiss());
        btnShowResults.setOnClickListener(v -> {
            if (listener != null) {
                String status = null;
                int checkedId = cgStatus.getCheckedChipId();
                if (checkedId != View.NO_ID) {
                    Chip selectedChip = cgStatus.findViewById(checkedId);
                    status = selectedChip.getText().toString();
                }
                
                String startTime = isTimeSet ? atvStartTime.getText().toString() : null;
                String endTime = isTimeSet ? atvEndTime.getText().toString() : null;
                
                listener.onFiltersApplied(status, startTime, endTime);
            }
            dismiss();
        });
        chipResetAll.setOnClickListener(v -> resetFilters());

        updateActiveFilters();
    }

    private void setupTimeDropdowns() {
        List<String> timeList = new ArrayList<>();
        
        String[] periods = {"AM", "PM"};
        
        timeList.add("12:00 AM");
        for (int h = 1; h <= 11; h++) {
            timeList.add(h + ":00 AM");
        }
        
        timeList.add("12:00 PM");
        for (int h = 1; h <= 11; h++) {
            timeList.add(h + ":00 PM");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, timeList);
        atvStartTime.setAdapter(adapter);
        atvEndTime.setAdapter(adapter);
        
        atvStartTime.setInputType(0);
        atvEndTime.setInputType(0);
    }

    private void updateActiveFilters() {
        while (activeFiltersContainer.getChildCount() > 1) {
            activeFiltersContainer.removeViewAt(1);
        }

        int count = 0;

        int checkedId = cgStatus.getCheckedChipId();
        if (checkedId != View.NO_ID) {
            Chip selectedChip = cgStatus.findViewById(checkedId);
            addActiveTag(selectedChip.getText().toString());
            count++;
        }

        if (isTimeSet) {
            String startTime = atvStartTime.getText().toString();
            String endTime = atvEndTime.getText().toString();
            addActiveTag(startTime + " - " + endTime);
            count++;
        }

        btnShowResults.setText("Show (" + count + ")");
    }

    private void addActiveTag(String text) {
        Chip tag = (Chip) getLayoutInflater().inflate(R.layout.item_tag_yellow, activeFiltersContainer, false);
        tag.setText(text);
        tag.setCheckable(false);
        tag.setClickable(false);
        
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMarginStart((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
        tag.setLayoutParams(params);

        activeFiltersContainer.addView(tag);
    }

    private void resetFilters() {
        cgStatus.clearCheck();
        atvStartTime.setText("", false);
        atvEndTime.setText("", false);
        isTimeSet = false;
        updateActiveFilters();
    }
}

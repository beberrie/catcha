package ph.edu.uscDCISMCatcha.ui.student;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import ph.edu.uscDCISMCatcha.R;
import java.util.HashMap;
import java.util.Map;

public class OrgFiltersBottomSheet extends BottomSheetDialogFragment {

    private ChipGroup cgActiveFilters, cgSchool, cgDepartment, cgCategory;
    private LinearLayout layoutDepartment, layoutCategory, filterContainer;
    private TextView btnShowResults, btnCancel;
    private Chip chipResetAll;

    private final Map<String, String[]> schoolToDept = new HashMap<String, String[]>() {{
        put("SAS", new String[]{"DCISM", "DMB", "DComm", "DPA", "DPsy", "DPhi"});
        put("SOE", new String[]{"DCPE", "DCE", "DMME", "DCHE", "DIE", "DEEE"});
    }};

    private final Map<String, String[]> deptToCat = new HashMap<String, String[]>() {{
        // SAS Departments
        put("DCISM", new String[]{"Coding", "Hackathon", "Data Science", "Networking", "Web/App Dev"});
        put("DPA", new String[]{"Sociology", "Human Behavior", "Advocacy", "Discussion"});
        put("DMB", new String[]{"Lab Research", "Environmental", "Biotech", "Seminar"});
        put("DComm", new String[]{"Journalism", "Broadcasting", "Public Speaking", "Creative Writing"});
        put("DPsy", new String[]{"Astronomy", "Robotics", "Engineering", "Workshop"});
        put("DPhi", new String[]{"Ethics", "Logic", "Debate", "Literature"});

        // SOE Departments
        put("DCPE", new String[]{"Robotics", "IoT", "Embedded Systems", "AI/ML", "Hardware Prototyping"});
        put("DCE", new String[]{"Structural Design", "Surveying", "Construction Mgmt", "Geotechnology", "Hydraulics"});
        put("DCHE", new String[]{"Unit Operations", "Process Control", "Biotech", "Environmental Engineering", "Plant Design"});
        put("DEEE", new String[]{"Power Systems", "Microelectronics", "Renewable Energy", "Telecommunications", "Circuit Design"});
        put("DIE", new String[]{"Supply Chain", "Logistics", "Operations Research", "Ergonomics", "Quality Control"});
        put("DMME", new String[]{"Thermodynamics", "Mechatronics", "Automotive", "HVAC", "Machining", "CAD/CAM"});
    }};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_org_filters, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        filterContainer = view.findViewById(R.id.filterContainer);
        cgActiveFilters = view.findViewById(R.id.cgActiveFilters);
        cgSchool = view.findViewById(R.id.cgSchool);
        cgDepartment = view.findViewById(R.id.cgDepartment);
        cgCategory = view.findViewById(R.id.cgCategory);
        layoutDepartment = view.findViewById(R.id.layoutDepartment);
        layoutCategory = view.findViewById(R.id.layoutCategory);
        btnShowResults = view.findViewById(R.id.btnShowResults);
        btnCancel = view.findViewById(R.id.btnCancel);
        chipResetAll = view.findViewById(R.id.chipResetAll);

        setupSchools();

        btnCancel.setOnClickListener(v -> dismiss());
        chipResetAll.setOnClickListener(v -> resetFilters());
    }

    private void setupSchools() {
        String[] schools = {"SAS", "SOE"};
        for (String school : schools) {
            Chip chip = createSelectionChip(school);
            chip.setOnClickListener(v -> {
                showDepartments(school);
                updateActiveFilters();
            });
            cgSchool.addView(chip);
        }
    }

    private void showDepartments(String school) {
        cgDepartment.removeAllViews();
        layoutDepartment.setVisibility(View.VISIBLE);
        layoutCategory.setVisibility(View.GONE);
        cgCategory.removeAllViews();

        String[] depts = schoolToDept.get(school);
        if (depts != null) {
            for (String dept : depts) {
                Chip chip = createSelectionChip(dept);
                chip.setOnClickListener(v -> {
                    showCategories(dept);
                    updateActiveFilters();
                });
                cgDepartment.addView(chip);
            }
        }
    }

    private void showCategories(String dept) {
        cgCategory.removeAllViews();
        layoutCategory.setVisibility(View.VISIBLE);

        String[] cats = deptToCat.get(dept);
        if (cats != null) {
            for (String cat : cats) {
                Chip chip = createSelectionChip(cat);
                chip.setOnClickListener(v -> updateActiveFilters());
                cgCategory.addView(chip);
            }
        }
    }

    private Chip createSelectionChip(String text) {
        Chip chip = new Chip(getContext());
        chip.setText(text);
        chip.setCheckable(true);
        chip.setClickable(true);
        chip.setCheckedIconVisible(false); // No check icon when selected
        
        // Setup colors for selected/unselected states
        int blue = ContextCompat.getColor(getContext(), R.color.blue);
        int white = ContextCompat.getColor(getContext(), R.color.white);

        ColorStateList backgroundColor = new ColorStateList(
                new int[][]{new int[]{android.R.attr.state_checked}, new int[]{}},
                new int[]{blue, white}
        );
        ColorStateList textColor = new ColorStateList(
                new int[][]{new int[]{android.R.attr.state_checked}, new int[]{}},
                new int[]{white, blue}
        );

        chip.setChipBackgroundColor(backgroundColor);
        chip.setTextColor(textColor);
        chip.setChipStrokeColorResource(R.color.blue);
        chip.setChipStrokeWidth(2f);
        
        return chip;
    }

    private void updateActiveFilters() {
        // Clear existing active filters except Reset All
        int childCount = cgActiveFilters.getChildCount();
        for (int i = childCount - 1; i >= 0; i--) {
            View child = cgActiveFilters.getChildAt(i);
            if (child.getId() != R.id.chipResetAll) {
                cgActiveFilters.removeViewAt(i);
            }
        }

        addActiveFilterFromGroup(cgSchool);
        addActiveFilterFromGroup(cgDepartment);
        addActiveFilterFromGroup(cgCategory);

        updateResultsCount();
    }

    private void addActiveFilterFromGroup(ChipGroup group) {
        for (int i = 0; i < group.getChildCount(); i++) {
            Chip chip = (Chip) group.getChildAt(i);
            if (chip.isChecked()) {
                addActiveChip(chip.getText().toString(), chip);
            }
        }
    }

    private void addActiveChip(String text, Chip sourceChip) {
        Chip activeChip = new Chip(getContext());
        activeChip.setText(text);
        activeChip.setCloseIconVisible(true);
        activeChip.setCloseIconResource(R.drawable.ic_close);
        activeChip.setCloseIconTintResource(R.color.white);
        
        activeChip.setChipBackgroundColorResource(R.color.blue);
        activeChip.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        
        activeChip.setOnCloseIconClickListener(v -> {
            sourceChip.setChecked(false);
            if (sourceChip.getParent() == cgSchool) {
                resetFromSchool();
            } else if (sourceChip.getParent() == cgDepartment) {
                resetFromDept();
            }
            updateActiveFilters();
        });

        cgActiveFilters.addView(activeChip);
    }

    private void updateResultsCount() {
        int count = 0;
        if (cgSchool.getCheckedChipId() != View.NO_ID) count++;
        if (cgDepartment.getCheckedChipId() != View.NO_ID) count++;
        count += cgCategory.getCheckedChipIds().size();
        btnShowResults.setText("Show (" + count + ")");
    }

    private void resetFilters() {
        cgSchool.clearCheck();
        resetFromSchool();
        updateActiveFilters();
    }

    private void resetFromSchool() {
        cgDepartment.removeAllViews();
        layoutDepartment.setVisibility(View.GONE);
        resetFromDept();
    }

    private void resetFromDept() {
        cgCategory.removeAllViews();
        layoutCategory.setVisibility(View.GONE);
    }
}
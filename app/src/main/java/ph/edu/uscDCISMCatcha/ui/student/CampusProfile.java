package ph.edu.uscDCISMCatcha.ui.student;

import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

//import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import de.hdodenhof.circleimageview.CircleImageView;
import ph.edu.uscDCISMCatcha.R;
import ph.edu.uscDCISMCatcha.utils.CategoryBreakdown;
import ph.edu.uscDCISMCatcha.utils.RSVPevent_camprof;

public class CampusProfile extends AppCompatActivity {

//    private CircleImageView ivProfilePhoto;
    private TextView tvName, tvTotalRsvps, tvShowRate, tvAttended;

    private TextView tvCategoryTech,       tvTechPct,        tvTechEvents;
    private TextView tvCategoryLeadership, tvLeadershipPct,  tvLeadershipEvents;
    private TextView tvCategoryService,    tvServicePct,     tvServiceEvents;
    private TextView tvCategoryArts,       tvArtsPct,        tvArtsEvents;
    private ProgressBar pbTech, pbLeadership, pbService, pbArts;

    private RecyclerView rvRsvpHistory;
    private ph.edu.uscDCISMCatcha.ui.student.RsvpHistoryAdapter rsvpAdapter;

    private FirebaseAuth      mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campus_profile);

        mAuth = FirebaseAuth.getInstance();
        db    = FirebaseFirestore.getInstance();

        initViews();
        loadProfile();
    }

    private void initViews() {
//        ivProfilePhoto = findViewById(R.id.ivProfilePhoto);
        tvName         = findViewById(R.id.tvName);
        tvTotalRsvps   = findViewById(R.id.tvTotalRsvps);
        tvShowRate     = findViewById(R.id.tvShowRate);
        tvAttended     = findViewById(R.id.tvAttended);

        tvCategoryTech       = findViewById(R.id.tvCategoryTech);
        tvTechPct            = findViewById(R.id.tvTechPct);
        tvTechEvents         = findViewById(R.id.tvTechEvents);
        pbTech               = findViewById(R.id.pbTech);

        tvCategoryLeadership = findViewById(R.id.tvCategoryLeadership);
        tvLeadershipPct      = findViewById(R.id.tvLeadershipPct);
        tvLeadershipEvents   = findViewById(R.id.tvLeadershipEvents);
        pbLeadership         = findViewById(R.id.pbLeadership);

        tvCategoryService    = findViewById(R.id.tvCategoryService);
        tvServicePct         = findViewById(R.id.tvServicePct);
        tvServiceEvents      = findViewById(R.id.tvServiceEvents);
        pbService            = findViewById(R.id.pbService);

        tvCategoryArts       = findViewById(R.id.tvCategoryArts);
        tvArtsPct            = findViewById(R.id.tvArtsPct);
        tvArtsEvents         = findViewById(R.id.tvArtsEvents);
        pbArts               = findViewById(R.id.pbArts);

        rvRsvpHistory = findViewById(R.id.rvRsvpHistory);
        rvRsvpHistory.setLayoutManager(new LinearLayoutManager(this));
        rvRsvpHistory.setNestedScrollingEnabled(false);
    }

    private void loadProfile() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;


        db.collection("users").document(user.getUid()).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        String firstName = doc.getString("firstName");
                        String lastName  = doc.getString("lastName");
                        tvName.setText(firstName + " " + lastName);

//                        String photoUrl = doc.getString("photoUrl");
//                        if (photoUrl != null && !photoUrl.isEmpty()) {
//                            Glide.with(this)
//                                    .load(photoUrl)
//                                    .placeholder(R.drawable.ic_profile_placeholder)
//                                    .circleCrop()
//                                    .into(ivProfilePhoto);
//                        }
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error loading user info", Toast.LENGTH_SHORT).show()
                );

        // 2. Load RSVPs
        db.collection("rsvps")
                .whereEqualTo("userId", user.getUid())
                .get()
                .addOnSuccessListener(query -> {
                    int total    = query.size();
                    int attended = 0;

                    Map<String, Integer> categoryCount = new HashMap<>();
                    List<String> categoryOrder = Arrays.asList("Tech", "Leadership", "Service", "Arts");
                    for (String cat : categoryOrder) categoryCount.put(cat, 0);

                    List<RSVPevent_camprof> history = new ArrayList<>();

                    for (DocumentSnapshot doc : query.getDocuments()) {
                        String statusStr = doc.getString("status");
                        String category  = doc.getString("category");
                        String title     = doc.getString("eventTitle");
                        String date      = doc.getString("date");

                        if ("Attended".equals(statusStr)) attended++;

                        if (category != null && categoryCount.containsKey(category)) {
                            categoryCount.put(category, categoryCount.get(category) + 1);
                        }

                        RSVPevent_camprof.AttendanceStatus status;
                        if ("Attended".equals(statusStr))     status = RSVPevent_camprof.AttendanceStatus.ATTENDED;
                        else if ("No-show".equals(statusStr)) status = RSVPevent_camprof.AttendanceStatus.NO_SHOW;
                        else                                  status = RSVPevent_camprof.AttendanceStatus.PENDING;

                        history.add(new RSVPevent_camprof(title, date, category, status));
                    }

                    int showRate = total > 0 ? Math.round((attended * 100f) / total) : 0;
                    tvTotalRsvps.setText(String.valueOf(total));
                    tvShowRate.setText(showRate + " %");
                    tvAttended.setText(String.valueOf(attended));

                    List<CategoryBreakdown> categories = buildCategoryList(categoryCount, total, categoryOrder);
                    bindCategories(categories);

                    rsvpAdapter = new ph.edu.uscDCISMCatcha.ui.student.RsvpHistoryAdapter(history);
                    rvRsvpHistory.setAdapter(rsvpAdapter);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error loading RSVP data", Toast.LENGTH_SHORT).show()
                );
    }

    private List<CategoryBreakdown> buildCategoryList(Map<String, Integer> categoryCount,
                                                      int total,
                                                      List<String> order) {
        List<CategoryBreakdown> list = new ArrayList<>();
        for (String cat : order) {
            int count = categoryCount.containsKey(cat) ? categoryCount.get(cat) : 0;
            int pct   = total > 0 ? Math.round((count * 100f) / total) : 0;
            list.add(new CategoryBreakdown(cat, pct, count));
        }
        return list;
    }

    private void bindCategories(List<CategoryBreakdown> categories) {
        TextView[]    nameViews  = { tvCategoryTech, tvCategoryLeadership, tvCategoryService, tvCategoryArts };
        TextView[]    pctViews   = { tvTechPct,      tvLeadershipPct,      tvServicePct,      tvArtsPct      };
        TextView[]    eventViews = { tvTechEvents,   tvLeadershipEvents,   tvServiceEvents,   tvArtsEvents   };
        ProgressBar[] bars       = { pbTech,         pbLeadership,         pbService,         pbArts         };

        int[] barColors = {
                getColor(R.color.category_tech),
                getColor(R.color.category_leadership),
                getColor(R.color.category_service),
                getColor(R.color.category_arts)
        };

        for (int i = 0; i < categories.size() && i < nameViews.length; i++) {
            CategoryBreakdown cat = categories.get(i);
            nameViews[i].setText(cat.getCategory());
            pctViews[i].setText(cat.getPercentage() + "%");
            eventViews[i].setText(cat.getEventCount() + " event" + (cat.getEventCount() == 1 ? "" : "s"));
            bars[i].setProgress(cat.getPercentage());
            bars[i].setProgressTintList(
                    android.content.res.ColorStateList.valueOf(barColors[i]));
        }
    }
}
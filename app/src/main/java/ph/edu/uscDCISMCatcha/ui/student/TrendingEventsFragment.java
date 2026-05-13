package ph.edu.uscDCISMCatcha.ui.student;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import ph.edu.uscDCISMCatcha.R;
import ph.edu.uscDCISMCatcha.adapters.CommonGroundAdapter;
import ph.edu.uscDCISMCatcha.utils.Constants;

public class TrendingEventsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.trending_events, container, false);

        LinearLayout llEventCards = view.findViewById(R.id.llEventCards);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Button btnView1 = view.findViewById(R.id.btnAction);
        Button btnView2 = view.findViewById(R.id.btn2);

        if (btnView1 != null) {
            btnView1.setOnClickListener(v -> {
                Intent intent = new Intent(requireContext(), EventDetailsActivity.class);
                intent.putExtra(Constants.EXTRA_EVENT_TITLE, "HACKATHON");
                intent.putExtra(Constants.EXTRA_EVENT_HOST, "Innovare & CISCO");
                intent.putExtra(Constants.EXTRA_EVENT_LOCATION, "Bunzel Lawrence Building, Talamban Campus");
                intent.putExtra(Constants.EXTRA_EVENT_DATETIME, "Tomorrow, 1:00 P.M.");
                intent.putExtra(Constants.EXTRA_EVENT_DESCRIPTION, "Join us for an exciting hackathon event!");
                intent.putExtra(Constants.EXTRA_EVENT_STATUS, "UPCOMING");
                intent.putExtra(Constants.EXTRA_EVENT_REGISTRATION_URL, "https://your-registration-url.com");
                startActivity(intent);
            });
        }

        if (btnView2 != null) {
            btnView2.setOnClickListener(v -> {
                Intent intent = new Intent(requireContext(), EventDetailsActivity.class);
                intent.putExtra(Constants.EXTRA_EVENT_TITLE, "DEVCON");
                intent.putExtra(Constants.EXTRA_EVENT_HOST, "Innovare & GDGOC");
                intent.putExtra(Constants.EXTRA_EVENT_LOCATION, "RIGNEY HALL, Bunzel Building");
                intent.putExtra(Constants.EXTRA_EVENT_DATETIME, "May 25, 2026");
                intent.putExtra(Constants.EXTRA_EVENT_DESCRIPTION, " These events focus on AI, robotics, and software development, catering to professionals and students to foster community growth");
                intent.putExtra(Constants.EXTRA_EVENT_STATUS, "UPCOMING");
                intent.putExtra(Constants.EXTRA_EVENT_REGISTRATION_URL, "https://your-registration-url.com");
                startActivity(intent);
            });
        }

        db.collection("events").get()
                .addOnSuccessListener(querySnapshots -> {
                    for (QueryDocumentSnapshot doc : querySnapshots) {
                        String eventId = doc.getId();
                        String title = doc.getString("title");
                        String host = doc.getString("host");
                        String location = doc.getString("location");
                        String dateTime = doc.getString("dateTime");
                        String description = doc.getString("description");
                        String status = doc.getString("status");
                        String registrationUrl = doc.getString("registrationUrl");

                        View eventCard = inflater.inflate(R.layout.other_events_card, llEventCards, false);

                        RecyclerView rvCommonGround = eventCard.findViewById(R.id.rvCommonGround);
                        View tvCommonGroundLabel = eventCard.findViewById(R.id.tvCommonGroundLabel);

                        rvCommonGround.setVisibility(View.GONE);
                        tvCommonGroundLabel.setVisibility(View.GONE);

                        java.util.List<ph.edu.uscDCISMCatcha.models.UserModel> demoUsers = new java.util.ArrayList<>();
                        demoUsers.add(new ph.edu.uscDCISMCatcha.models.UserModel("Beth", "Santos", "beth",
                                "beth@usc.edu.ph", "University of San Carlos", "DCISM", "Student"));
                        demoUsers.add(new ph.edu.uscDCISMCatcha.models.UserModel("Carl", "Gomez", "carl",
                                "carl@usc.edu.ph", "University of San Carlos", "DCISM", "Student"));

                        tvCommonGroundLabel.setVisibility(View.VISIBLE);
                        rvCommonGround.setVisibility(View.VISIBLE);

                        CommonGroundAdapter adapter = new CommonGroundAdapter(requireContext(), demoUsers);
                        rvCommonGround.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
                        rvCommonGround.setAdapter(adapter);

                        Button btnViewCard = eventCard.findViewById(R.id.btnAction);
                        if (btnViewCard != null) {
                            btnViewCard.setOnClickListener(v -> {
                                Intent intent = new Intent(requireContext(), EventDetailsActivity.class);
                                intent.putExtra(Constants.EXTRA_EVENT_ID, eventId);
                                intent.putExtra(Constants.EXTRA_EVENT_TITLE, title);
                                intent.putExtra(Constants.EXTRA_EVENT_HOST, host);
                                intent.putExtra(Constants.EXTRA_EVENT_LOCATION, location);
                                intent.putExtra(Constants.EXTRA_EVENT_DATETIME, dateTime);
                                intent.putExtra(Constants.EXTRA_EVENT_DESCRIPTION, description);
                                intent.putExtra(Constants.EXTRA_EVENT_STATUS, status);
                                intent.putExtra(Constants.EXTRA_EVENT_REGISTRATION_URL, registrationUrl);
                                startActivity(intent);
                            });
                        }

                        llEventCards.addView(eventCard);
                    }
                })
                .addOnFailureListener(e -> {});

        RecyclerView rvTrending1 = view.findViewById(R.id.rvCommonGround);
        View tvTrending1 = view.findViewById(R.id.tvCommonGroundLabel);

        if (rvTrending1 != null && tvTrending1 != null) {
            tvTrending1.setVisibility(View.VISIBLE);
            rvTrending1.setVisibility(View.VISIBLE);

            java.util.List<ph.edu.uscDCISMCatcha.models.UserModel> demoUsers1 = new java.util.ArrayList<>();
            demoUsers1.add(new ph.edu.uscDCISMCatcha.models.UserModel("Beth", "Santos", "beth",
                    "beth@usc.edu.ph", "University of San Carlos", "DCISM", "Student"));
            demoUsers1.add(new ph.edu.uscDCISMCatcha.models.UserModel("Carl", "Gomez", "carl",
                    "carl@usc.edu.ph", "University of San Carlos", "DCISM", "Student"));

            CommonGroundAdapter adapter1 = new CommonGroundAdapter(requireContext(), demoUsers1);
            rvTrending1.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
            rvTrending1.setAdapter(adapter1);
        }

        RecyclerView rvTrending2 = view.findViewById(R.id.rvCommonGround2);
        View tvTrending2 = view.findViewById(R.id.tvCommonGroundLabel2);

        if (rvTrending2 != null && tvTrending2 != null) {
            tvTrending2.setVisibility(View.VISIBLE);
            rvTrending2.setVisibility(View.VISIBLE);

            java.util.List<ph.edu.uscDCISMCatcha.models.UserModel> demoUsers2 = new java.util.ArrayList<>();
            demoUsers2.add(new ph.edu.uscDCISMCatcha.models.UserModel("Dana", "Cruz", "dana",
                    "dana@usc.edu.ph", "University of San Carlos", "DCISM", "Student"));

            CommonGroundAdapter adapter2 = new CommonGroundAdapter(requireContext(), demoUsers2);
            rvTrending2.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
            rvTrending2.setAdapter(adapter2);
        }

        return view;
    }
}
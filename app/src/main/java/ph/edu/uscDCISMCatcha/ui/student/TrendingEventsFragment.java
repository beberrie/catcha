package ph.edu.uscDCISMCatcha.ui.student;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import ph.edu.uscDCISMCatcha.models.EventModel;
import ph.edu.uscDCISMCatcha.utils.CommonGroundUtils;
public class TrendingEventsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.trending_events, container, false);

        LinearLayout llEventCards = view.findViewById(R.id.llEventCards);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("events").get()
                .addOnSuccessListener(querySnapshots -> {
                    for (QueryDocumentSnapshot doc : querySnapshots) {
                        String eventId = doc.getId();
                        android.util.Log.d("EventID", "Event ID: " + eventId);

                        View eventCard = inflater.inflate(
                                R.layout.fragment_event_card, llEventCards, false);

                        RecyclerView rvCommonGround = eventCard.findViewById(R.id.rvCommonGround);
                        View tvCommonGroundLabel = eventCard.findViewById(R.id.tvCommonGroundLabel);

                        rvCommonGround.setVisibility(View.GONE);
                        tvCommonGroundLabel.setVisibility(View.GONE);

                        // HARDCODED FOR DEMO
                        java.util.List<ph.edu.uscDCISMCatcha.models.UserModel> demoUsers = new java.util.ArrayList<>();
                        demoUsers.add(new ph.edu.uscDCISMCatcha.models.UserModel("Beth", "Santos", "beth",
                                "beth@usc.edu.ph", "University of San Carlos", "DCISM", "Student"));
                        demoUsers.add(new ph.edu.uscDCISMCatcha.models.UserModel("Carl", "Gomez", "carl",
                                "carl@usc.edu.ph", "University of San Carlos", "DCISM", "Student"));

                        tvCommonGroundLabel.setVisibility(View.VISIBLE);
                        rvCommonGround.setVisibility(View.VISIBLE);

                        CommonGroundAdapter adapter = new CommonGroundAdapter(requireContext(), demoUsers);
                        rvCommonGround.setLayoutManager(
                                new LinearLayoutManager(requireContext(),
                                        LinearLayoutManager.HORIZONTAL, false));
                        rvCommonGround.setAdapter(adapter);

                        llEventCards.addView(eventCard);
                    }
                })
                .addOnFailureListener(e -> {
                });

        return view;
    }
}
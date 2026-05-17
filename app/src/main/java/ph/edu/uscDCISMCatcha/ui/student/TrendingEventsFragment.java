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
import ph.edu.uscDCISMCatcha.adapters.CommonGroundTrendingAdapter;
import ph.edu.uscDCISMCatcha.utils.CommonGroundUtils;

public class TrendingEventsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.trending_events, container, false);

        LinearLayout llEventCards = view.findViewById(R.id.llEventCards);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Other Events - real Firebase data
        db.collection("events").get()
                .addOnSuccessListener(querySnapshots -> {
                    for (QueryDocumentSnapshot doc : querySnapshots) {
                        String eventId = doc.getId();

                        View eventCard = inflater.inflate(
                                R.layout.other_events_card, llEventCards, false);

                        RecyclerView rvCommonGround = eventCard.findViewById(R.id.rvCommonGround);
                        View tvCommonGroundLabel = eventCard.findViewById(R.id.tvCommonGroundLabel);

                        rvCommonGround.setVisibility(View.GONE);
                        tvCommonGroundLabel.setVisibility(View.GONE);

                        CommonGroundUtils.getFriendsAttending(eventId, friendsAttending -> {
                            if (!isAdded()) return; // ← ADD THIS CHECK
                            if (!friendsAttending.isEmpty()) {
                                tvCommonGroundLabel.setVisibility(View.VISIBLE);
                                rvCommonGround.setVisibility(View.VISIBLE);
                                CommonGroundAdapter adapter = new CommonGroundAdapter(requireContext(), friendsAttending);
                                rvCommonGround.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
                                rvCommonGround.setAdapter(adapter);
                            }
                        });

                        // Real Firebase Common Ground
                        CommonGroundUtils.getFriendsAttending(eventId, friendsAttending -> {
                            if (!friendsAttending.isEmpty()) {
                                tvCommonGroundLabel.setVisibility(View.VISIBLE);
                                rvCommonGround.setVisibility(View.VISIBLE);
                                CommonGroundAdapter adapter = new CommonGroundAdapter(
                                        requireContext(), friendsAttending);
                                rvCommonGround.setLayoutManager(new LinearLayoutManager(
                                        requireContext(), LinearLayoutManager.HORIZONTAL, false));
                                rvCommonGround.setAdapter(adapter);
                            }
                        });

                        llEventCards.addView(eventCard);
                    }
                })
                .addOnFailureListener(e -> {});

        // Trending cards - real Firebase data
        db.collection("events").limit(2).get()
                .addOnSuccessListener(trendingSnapshots -> {
                    java.util.List<QueryDocumentSnapshot> trendingDocs = new java.util.ArrayList<>();
                    for (QueryDocumentSnapshot doc : trendingSnapshots) {
                        trendingDocs.add(doc);
                    }

                    // Trending card 1
                    if (trendingDocs.size() >= 1) {
                        String trendingEventId1 = trendingDocs.get(0).getId();
                        RecyclerView rvTrending1 = view.findViewById(R.id.rvCommonGround);
                        View tvTrending1 = view.findViewById(R.id.tvCommonGroundLabel);

                        if (rvTrending1 != null && tvTrending1 != null) {
                            CommonGroundUtils.getFriendsAttending(trendingEventId1, friendsAttending -> {
                                if (!friendsAttending.isEmpty()) {
                                    tvTrending1.setVisibility(View.VISIBLE);
                                    rvTrending1.setVisibility(View.VISIBLE);
                                    CommonGroundTrendingAdapter adapter1 = new CommonGroundTrendingAdapter(
                                            requireContext(), friendsAttending);
                                    rvTrending1.setLayoutManager(new LinearLayoutManager(
                                            requireContext(), LinearLayoutManager.HORIZONTAL, false));
                                    rvTrending1.setAdapter(adapter1);
                                }
                            });
                        }
                    }

                    // Trending card 2
                    if (trendingDocs.size() >= 2) {
                        String trendingEventId2 = trendingDocs.get(1).getId();
                        RecyclerView rvTrending2 = view.findViewById(R.id.rvCommonGround2);
                        View tvTrending2 = view.findViewById(R.id.tvCommonGroundLabel2);

                        if (rvTrending2 != null && tvTrending2 != null) {
                            CommonGroundUtils.getFriendsAttending(trendingEventId2, friendsAttending -> {
                                if (!friendsAttending.isEmpty()) {
                                    tvTrending2.setVisibility(View.VISIBLE);
                                    rvTrending2.setVisibility(View.VISIBLE);
                                    CommonGroundTrendingAdapter adapter2 = new CommonGroundTrendingAdapter(
                                            requireContext(), friendsAttending);
                                    rvTrending2.setLayoutManager(new LinearLayoutManager(
                                            requireContext(), LinearLayoutManager.HORIZONTAL, false));
                                    rvTrending2.setAdapter(adapter2);
                                }
                            });
                        }
                    }
                })
                .addOnFailureListener(e -> {});

        return view;
    }
}
package ph.edu.uscDCISMCatcha.ui.student;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;

import ph.edu.uscDCISMCatcha.R;
import ph.edu.uscDCISMCatcha.data.models.EventModel;
import ph.edu.uscDCISMCatcha.utils.Constants;
import ph.edu.uscDCISMCatcha.viewmodel.student.EventViewModel;

public class EventDetailsActivity extends AppCompatActivity {

    private EventViewModel viewModel;
    private String eventId; // To be passed via intent

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        viewModel = new ViewModelProvider(this).get(EventViewModel.class);

        MaterialButton btnBack = findViewById(R.id.btnBack);
        MaterialButton btnRSVP = findViewById(R.id.btnRSVP);
        TextView tvTitle = findViewById(R.id.tvEventTitle);
        TextView tvHost = findViewById(R.id.tvHostName);
        TextView tvLocation = findViewById(R.id.tvLocation);
        TextView tvDateTime = findViewById(R.id.tvDateTime);
        TextView tvDescription = findViewById(R.id.tvDescription);
        Chip chipStatus = findViewById(R.id.chipStatus);
        LinearLayout qrSection = findViewById(R.id.qrSection);

        // Get data from intent
        eventId = getIntent().getStringExtra(Constants.EXTRA_EVENT_ID);
        String title = getIntent().getStringExtra(Constants.EXTRA_EVENT_TITLE);
        String host = getIntent().getStringExtra(Constants.EXTRA_EVENT_HOST);
        String location = getIntent().getStringExtra(Constants.EXTRA_EVENT_LOCATION);
        String dateTime = getIntent().getStringExtra(Constants.EXTRA_EVENT_DATETIME);
        String description = getIntent().getStringExtra(Constants.EXTRA_EVENT_DESCRIPTION);
        String status = getIntent().getStringExtra(Constants.EXTRA_EVENT_STATUS);
        int statusColor = getIntent().getIntExtra(Constants.EXTRA_EVENT_STATUS_COLOR, R.color.yellow);

        // Populate views
        if (title != null) tvTitle.setText(title);
        if (host != null && tvHost != null) tvHost.setText("Event Host: " + host);
        if (location != null) tvLocation.setText(location);
        if (dateTime != null) tvDateTime.setText(dateTime);
        if (description != null) tvDescription.setText(description);

        if (status != null) {
            chipStatus.setText(status);
            chipStatus.setChipBackgroundColorResource(statusColor);

            if (qrSection != null) {
                if ("UPCOMING".equalsIgnoreCase(status)) {
                    qrSection.setVisibility(View.VISIBLE);
                } else {
                    qrSection.setVisibility(View.GONE);
                }
            }
        }

        // Observe RSVP Status for errors (like "Event is full!")
        viewModel.rsvpStatus.observe(this, result -> {
            if (result != null) {
                Toast.makeText(this, result, Toast.LENGTH_LONG).show();
                if (result.contains("Successful")) {
                    btnRSVP.setEnabled(false);
                    btnRSVP.setText("RSVP Sent");
                }
            }
        });

        btnRSVP.setOnClickListener(v -> {
            // Create a temporary EventModel for the RSVP call
            EventModel event = new EventModel();
            event.setEventId(eventId != null ? eventId : "dummy_id");
            event.setTitle(title);
            
            viewModel.rsvpToEvent(event, Constants.STATUS_GOING);
        });

        btnBack.setOnClickListener(v -> finish());
    }
}

package ph.edu.uscDCISMCatcha.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import ph.edu.uscDCISMCatcha.R;

public class EventDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        MaterialButton btnBack = findViewById(R.id.btnBack);
        TextView tvTitle = findViewById(R.id.tvEventTitle);
        TextView tvHost = findViewById(R.id.tvHostName);
        TextView tvLocation = findViewById(R.id.tvLocation);
        TextView tvDateTime = findViewById(R.id.tvDateTime);
        TextView tvDescription = findViewById(R.id.tvDescription);
        Chip chipStatus = findViewById(R.id.chipStatus);
        LinearLayout qrSection = findViewById(R.id.qrSection);

        // Get data from intent
        String title = getIntent().getStringExtra("EVENT_TITLE");
        String host = getIntent().getStringExtra("EVENT_HOST");
        String location = getIntent().getStringExtra("EVENT_LOCATION");
        String dateTime = getIntent().getStringExtra("EVENT_DATETIME");
        String description = getIntent().getStringExtra("EVENT_DESCRIPTION");
        String status = getIntent().getStringExtra("EVENT_STATUS");
        int statusColor = getIntent().getIntExtra("EVENT_STATUS_COLOR", R.color.yellow);

        // Populate views
        if (title != null) tvTitle.setText(title);
        if (host != null && tvHost != null) tvHost.setText("Event Host: " + host);
        if (location != null) tvLocation.setText(location);
        if (dateTime != null) tvDateTime.setText(dateTime);
        if (description != null) tvDescription.setText(description);

        if (status != null) {
            chipStatus.setText(status);
            chipStatus.setChipBackgroundColorResource(statusColor);

            // Conditional Visibility logic
            if (qrSection != null) {
                // Show QR code for all "UPCOMING" events
                if ("UPCOMING".equalsIgnoreCase(status)) {
                    qrSection.setVisibility(View.VISIBLE);
                } else {
                    qrSection.setVisibility(View.GONE);
                }
            }
        }

        btnBack.setOnClickListener(v -> finish());
    }
}
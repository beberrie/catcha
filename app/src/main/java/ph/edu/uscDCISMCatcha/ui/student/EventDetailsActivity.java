package ph.edu.uscDCISMCatcha.ui.student;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

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
        ImageView ivQRCode = findViewById(R.id.ivQRCode);

        // Get data from intent
        eventId = getIntent().getStringExtra(Constants.EXTRA_EVENT_ID);
        String title = getIntent().getStringExtra(Constants.EXTRA_EVENT_TITLE);
        String host = getIntent().getStringExtra(Constants.EXTRA_EVENT_HOST);
        String location = getIntent().getStringExtra(Constants.EXTRA_EVENT_LOCATION);
        String dateTime = getIntent().getStringExtra(Constants.EXTRA_EVENT_DATETIME);
        String description = getIntent().getStringExtra(Constants.EXTRA_EVENT_DESCRIPTION);
        String status = getIntent().getStringExtra(Constants.EXTRA_EVENT_STATUS);
        int statusColor = getIntent().getIntExtra(Constants.EXTRA_EVENT_STATUS_COLOR, R.color.yellow);
        String registrationUrl = getIntent().getStringExtra(Constants.EXTRA_EVENT_REGISTRATION_URL);

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
                if ("UPCOMING".equalsIgnoreCase(status) && registrationUrl != null && !registrationUrl.isEmpty()) {
                    qrSection.setVisibility(View.VISIBLE);
                    generateQRCode(registrationUrl, ivQRCode);
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

    private void generateQRCode(String content, ImageView imageView) {
        QRCodeWriter writer = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, 512, 512);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            imageView.setImageBitmap(bmp);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }
}

package com.example.medvault;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.journeyapps.barcodescanner.*;

public class QRScanActivity extends AppCompatActivity {

    private DecoratedBarcodeView barcodeView;
    private TextView resultText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrscan);

        barcodeView = findViewById(R.id.barcodeView);
        resultText = findViewById(R.id.resultText);

        barcodeView.decodeContinuous(callback);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Health Records");
        }
    }

    private BarcodeCallback callback = result -> {
        if (result != null) {
            // Directly display the QR data without decryption
            String scannedData = result.getText();
            resultText.setText(scannedData);
            barcodeView.pause();
        }
    };


    @Override
    protected void onResume() {
        super.onResume();
        barcodeView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        barcodeView.pause();
    }

    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
        return true;
    }
}

package com.example.medvault;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import androidx.appcompat.widget.Toolbar;

import java.io.IOException;
import java.io.OutputStream;

public class EmergencyQRActivity extends AppCompatActivity {

    EditText nameInput, bloodInput, allergyInput, contactInput;
    Button generateBtn, scanBtn;
    ImageView qrImageView;
    SharedPreferences prefs;
    Bitmap generatedQRBitmap;

    private ActivityResultLauncher<Intent> createPDFLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_qractivity);

        nameInput = findViewById(R.id.nameInput);
        bloodInput = findViewById(R.id.bloodInput);
        allergyInput = findViewById(R.id.allergyInput);
        contactInput = findViewById(R.id.contactInput);
        generateBtn = findViewById(R.id.generateBtn);
        scanBtn = findViewById(R.id.scanBtn);
        qrImageView = findViewById(R.id.qrImageView);

        registerForContextMenu(qrImageView);


        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Health Records");
        }

        prefs = getSharedPreferences("emergency_qr", MODE_PRIVATE);
        loadSavedData();

        generateBtn.setOnClickListener(v -> {
            if (nameInput.getText().toString().isEmpty() ||
                    bloodInput.getText().toString().isEmpty() ||
                    allergyInput.getText().toString().isEmpty() ||
                    contactInput.getText().toString().isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            String data = "Name: " + nameInput.getText().toString().trim() + "\n"
                    + "Blood Group: " + bloodInput.getText().toString().trim() + "\n"
                    + "Allergies: " + allergyInput.getText().toString().trim() + "\n"
                    + "Emergency Contact: " + contactInput.getText().toString().trim();

            saveData();  // save before generating
            generateQRCode(data);
        });

        scanBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, QRScanActivity.class);
            startActivity(intent);
        });

        createPDFLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        saveQRToPdf(uri); // ✅ CALL THE FIXED METHOD HERE
                    }
                }
        );


    }

    private void generateQRCode(String text) {
        QRCodeWriter writer = new QRCodeWriter();
        try {
            BitMatrix matrix = writer.encode(text, BarcodeFormat.QR_CODE, 500, 500);
            Bitmap bitmap = Bitmap.createBitmap(500, 500, Bitmap.Config.RGB_565);

            for (int x = 0; x < 500; x++) {
                for (int y = 0; y < 500; y++) {
                    bitmap.setPixel(x, y, matrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }

            qrImageView.setImageBitmap(bitmap);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    Bitmap toBitmap(com.google.zxing.common.BitMatrix matrix) {
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                bmp.setPixel(x, y, matrix.get(x, y) ? android.graphics.Color.BLACK : android.graphics.Color.WHITE);
            }
        }
        return bmp;
    }

    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
        return true;
    }

    void saveData() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("name", nameInput.getText().toString());
        editor.putString("blood", bloodInput.getText().toString());
        editor.putString("allergy", allergyInput.getText().toString());
        editor.putString("contact", contactInput.getText().toString());
        editor.apply();
    }

    void loadSavedData() {
        nameInput.setText(prefs.getString("name", ""));
        bloodInput.setText(prefs.getString("blood", ""));
        allergyInput.setText(prefs.getString("allergy", ""));
        contactInput.setText(prefs.getString("contact", ""));
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.qrImageView) {
            menu.setHeaderTitle("QR Options");
            menu.add(0, 1, 0, "Copy to Clipboard");
            menu.add(0, 2, 1, "Save QR as PDF");
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                // Copy bitmap to clipboard
                copyQRToClipboard();
                return true;
            case 2:
                // Save QR as PDF
                promptUserToSavePDF();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void copyQRToClipboard() {
        if (generatedQRBitmap != null) {
            String path = MediaStore.Images.Media.insertImage(getContentResolver(), generatedQRBitmap, "EmergencyQR", null);
            Uri uri = Uri.parse(path);

            ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newUri(getContentResolver(), "QR", uri);
            clipboard.setPrimaryClip(clip);

            Toast.makeText(this, "QR copied to clipboard", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No QR to copy", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveQRBitmapToPDF(Uri uri) {
        if (generatedQRBitmap == null) {
            Toast.makeText(this, "No QR to save", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            OutputStream outputStream = getContentResolver().openOutputStream(uri);
            if (outputStream == null) {
                Toast.makeText(this, "Failed to open file", Toast.LENGTH_SHORT).show();
                return;
            }

            PdfDocument document = new PdfDocument();
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(
                    generatedQRBitmap.getWidth(),
                    generatedQRBitmap.getHeight(),
                    1
            ).create();

            PdfDocument.Page page = document.startPage(pageInfo);
            Canvas canvas = page.getCanvas();
            canvas.drawBitmap(generatedQRBitmap, 0, 0, null);
            document.finishPage(page);

            document.writeTo(outputStream);
            document.close();
            outputStream.close();

            Toast.makeText(this, "QR PDF saved!", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error saving QR PDF: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


    private void promptUserToSavePDF() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");
        intent.putExtra(Intent.EXTRA_TITLE, "EmergencyQR_" + System.currentTimeMillis() + ".pdf");

        createPDFLauncher.launch(intent);
    }

    private void saveQRToPdf(Uri uri) {
        if (generatedQRBitmap == null || uri == null) {
            Toast.makeText(this, "Nothing to save", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Create PDF document
            PdfDocument pdfDocument = new PdfDocument();
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo
                    .Builder(generatedQRBitmap.getWidth(), generatedQRBitmap.getHeight(), 1)
                    .create();

            PdfDocument.Page page = pdfDocument.startPage(pageInfo);
            Canvas canvas = page.getCanvas();
            canvas.drawBitmap(generatedQRBitmap, 0, 0, null);
            pdfDocument.finishPage(page);

            // Write PDF to the URI
            OutputStream outputStream = getContentResolver().openOutputStream(uri);
            if (outputStream != null) {
                pdfDocument.writeTo(outputStream);
                outputStream.close();
            }

            pdfDocument.close();

            Toast.makeText(this, "QR saved as PDF successfully!", Toast.LENGTH_LONG).show();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error saving QR PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


}
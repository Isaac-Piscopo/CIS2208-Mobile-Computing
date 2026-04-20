package com.isaacpiscopo.wifinote;

import android.Manifest;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.isaacpiscopo.wifinote.databinding.ActivityQrDetailBinding;
import com.isaacpiscopo.wifinote.model.Network;
import com.isaacpiscopo.wifinote.util.QrUtils;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Displays the QR code for a saved network credential.
 * Allows the user to reveal the password and save the QR image to the gallery.
 */
public class QrDetailActivity extends AppCompatActivity {

    /** Intent extra key for passing the {@link Network} to display. */
    public static final String EXTRA_NETWORK = "extra_network";

    private ActivityQrDetailBinding binding;
    private Network network;
    private Bitmap qrBitmap;
    private boolean passwordVisible = false;

    private final ActivityResultLauncher<String> requestStoragePermission =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                if (granted) {
                    saveQrToGallery();
                } else {
                    Toast.makeText(this,
                            getString(R.string.permission_denied_message), Toast.LENGTH_SHORT).show();
                }
            });

    /** Inflates the layout, generates the QR bitmap, and populates the screen. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQrDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        network = (Network) getIntent().getSerializableExtra(EXTRA_NETWORK);
        if (network == null) {
            finish();
            return;
        }

        binding.toolbarQr.setNavigationOnClickListener(v -> finish());

        binding.textQrSsid.setText(network.getSsid());

        // Generate QR bitmap
        qrBitmap = QrUtils.encodeWifiQr(network);
        if (qrBitmap != null) {
            binding.imageQr.setImageBitmap(qrBitmap);
        }

        // Password reveal toggle
        binding.textPassword.setText(maskPassword(network.getPassword()));
        binding.btnTogglePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordVisible = !passwordVisible;
                binding.textPassword.setText(
                        passwordVisible ? network.getPassword() : maskPassword(network.getPassword()));
                binding.btnTogglePassword.setText(
                        passwordVisible ? getString(R.string.cd_hide_password)
                                        : getString(R.string.qr_tap_to_reveal));
            }
        });

        // Save to gallery
        binding.fabSaveGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    // WRITE_EXTERNAL_STORAGE required on API 28 and below
                    if (ContextCompat.checkSelfPermission(QrDetailActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        requestStoragePermission.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                        return;
                    }
                }
                saveQrToGallery();
            }
        });
    }

    /**
     * Saves the generated QR bitmap to the device gallery using MediaStore.
     * Uses scoped storage on API 29+ (no permission required).
     */
    private void saveQrToGallery() {
        if (qrBitmap == null) {
            Toast.makeText(this, getString(R.string.qr_save_failed), Toast.LENGTH_SHORT).show();
            return;
        }

        String fileName = "wifinote_" + network.getSsid().replaceAll("[^a-zA-Z0-9]", "_") + ".png";

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/WifiNote");

        Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        if (uri == null) {
            Toast.makeText(this, getString(R.string.qr_save_failed), Toast.LENGTH_SHORT).show();
            return;
        }

        try (OutputStream out = getContentResolver().openOutputStream(uri)) {
            if (out != null) {
                qrBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                Toast.makeText(this, getString(R.string.qr_saved), Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            Toast.makeText(this, getString(R.string.qr_save_failed), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Returns a masked representation of the password for display.
     *
     * @param password the raw password string.
     * @return a string of bullet characters matching the password length.
     */
    private String maskPassword(String password) {
        if (password == null || password.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < password.length(); i++) {
            sb.append('•');
        }
        return sb.toString();
    }
}

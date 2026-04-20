package com.isaacpiscopo.wifinote;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.isaacpiscopo.wifinote.databinding.ActivityOnboardingBinding;

/**
 * First-launch onboarding screen that requests CAMERA and (on API ≤28) storage
 * permissions, then marks the app as onboarded via {@link SharedPreferences}.
 */
public class OnboardingActivity extends AppCompatActivity {

    /** SharedPreferences key used by {@link MainActivity} to gate this screen. */
    public static final String PREF_ONBOARDED = "onboarded";

    private ActivityOnboardingBinding binding;

    private final ActivityResultLauncher<String[]> requestPermissions =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(),
                    results -> completeOnboarding());

    /** Inflates the layout and wires the Grant and Skip buttons. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOnboardingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnGrant.setOnClickListener(v -> requestRequiredPermissions());
        binding.btnSkip.setOnClickListener(v -> completeOnboarding());
    }

    /**
     * Requests CAMERA permission and, on API 28 and below, WRITE_EXTERNAL_STORAGE.
     * Results are handled by {@link #requestPermissions}.
     */
    private void requestRequiredPermissions() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            requestPermissions.launch(new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            });
        } else {
            requestPermissions.launch(new String[]{
                    Manifest.permission.CAMERA
            });
        }
    }

    /**
     * Sets the {@code onboarded} flag in default SharedPreferences and finishes this Activity,
     * returning the user to {@link MainActivity}.
     */
    private void completeOnboarding() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit().putBoolean(PREF_ONBOARDED, true).apply();
        Toast.makeText(this, getString(R.string.onboarding_done), Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}

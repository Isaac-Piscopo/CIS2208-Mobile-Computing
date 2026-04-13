package com.isaacpiscopo.wifinote;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.isaacpiscopo.wifinote.data.DbHelper;
import com.isaacpiscopo.wifinote.data.SeedData;
import com.isaacpiscopo.wifinote.databinding.ActivityMainBinding;

/** Main activity hosting the bottom navigation and navigation graph. */
public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    /** Creates the activity and binds bottom navigation to the navigation graph. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (!prefs.getBoolean(OnboardingActivity.PREF_ONBOARDED, false)) {
            startActivity(new Intent(this, OnboardingActivity.class));
            finish();
            return;
        }
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Seed example data on first launch.
        SeedData.seedIfEmpty(new DbHelper(this));

        BottomNavigationView navView = binding.navView;
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_activity_main);
        NavController navController = navHostFragment.getNavController();
        NavigationUI.setupWithNavController(navView, navController);
    }

    /** Handles action bar up navigation. */
    @Override
    public boolean onSupportNavigateUp() {
        return super.onSupportNavigateUp();
    }
}

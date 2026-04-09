package com.isaacpiscopo.wifinote;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
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
        }
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Seed example data on first launch.
        SeedData.seedIfEmpty(new DbHelper(this));

        BottomNavigationView navView = binding.navView;
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_networks,
                R.id.navigation_backup,
                R.id.navigation_share,
                R.id.navigation_settings
        ).build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }

    /** Handles action bar up navigation. */
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        return navController.navigateUp() || super.onSupportNavigateUp();
    }
}

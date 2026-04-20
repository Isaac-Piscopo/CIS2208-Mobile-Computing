package com.isaacpiscopo.wifinote.ui.settings;

import android.os.Bundle;
import android.widget.Toast;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.isaacpiscopo.wifinote.BuildConfig;
import com.isaacpiscopo.wifinote.R;

/**
 * Settings screen implemented as a {@link PreferenceFragmentCompat}.
 * Preferences are persisted automatically via {@link android.content.SharedPreferences}.
 */
public class SettingsFragment extends PreferenceFragmentCompat {

    /**
     * Inflates the preference hierarchy from {@code res/xml/preferences.xml} and
     * wires the version summary and placeholder click listeners.
     *
     * @param savedInstanceState saved state bundle (unused).
     * @param rootKey            preference root key (unused -- full screen).
     */
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        Preference versionPref = findPreference("version");
        if (versionPref != null) {
            versionPref.setSummary(BuildConfig.VERSION_NAME);
        }

        Preference privacyPref = findPreference("privacy_policy");
        if (privacyPref != null) {
            privacyPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Toast.makeText(requireContext(),
                            getString(R.string.pref_title_privacy), Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
        }

        Preference termsPref = findPreference("terms");
        if (termsPref != null) {
            termsPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Toast.makeText(requireContext(),
                            getString(R.string.pref_title_terms), Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
        }
    }
}

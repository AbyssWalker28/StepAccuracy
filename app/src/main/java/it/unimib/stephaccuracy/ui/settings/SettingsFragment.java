package it.unimib.stephaccuracy.ui.settings;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import it.unimib.stephaccuracy.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

    }
}
package com.ethical_techniques.notemaker.frags;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.ethical_techniques.notemaker.R;


/**
 * SettingsFragment extends PreferenceFragmentCompat and manages the applications SharedPreferences
 *
 *
 * @author Harry Dulaney
 *
 */
public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings, rootKey);
    }

}

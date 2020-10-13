package com.ethical_techniques.notemaker.frags;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;

import androidx.preference.PreferenceFragmentCompat;

import com.ethical_techniques.notemaker.R;


/**
 * SettingsFragment extends PreferenceFragmentCompat and manages the applications SharedPreferences
 */
public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.note_list_settings, rootKey);
    }

}

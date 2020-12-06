package com.ethical_techniques.notemaker.auth;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.ethical_techniques.notemaker.R;
import com.ethical_techniques.notemaker.utils.CloudStorageUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;


/**
 * The type Base activity.
 *
 * @author Harry Dulaney
 */
public class BaseActivity extends AppCompatActivity {

    private static final String TAG = "BaseActivity.class";
    protected SharedPreferences sharedPreferences;
    private SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener;
    static boolean hasCamera;
    static boolean hasWidgets;
    static boolean hasFingerPrint;
    static boolean signedIn;
    protected String NOTE_SORT_BY_PREF_KEY;
    protected String NOTE_ORDER_PREF_KEY;
    protected String SORT_BY_DEFAULT;
    protected String SORT_ORDER_DEFAULT;
    protected String SORT_BY_PREFERENCE;
    protected String SORT_ORDER_PREFERENCE;


    /**
     * Hide keyboard.
     *
     * @param views the views
     */
    protected void hideKeyboard(Context context, View... views) {
        final InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            for (View view : views) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSharedPreferences(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));

    }

    protected void initCloudStorageUtil() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            CloudStorageUtil.setRootStorageReference(FirebaseStorage.getInstance().getReference());
        }
    }

    private void setSharedPreferences(SharedPreferences sharedPreferences) {
        if (this.sharedPreferences != null) {

        } else {
            NOTE_SORT_BY_PREF_KEY = getString(R.string.SORT_BY_PREF_ID);
            NOTE_ORDER_PREF_KEY = getString(R.string.SORT_ORDER_PREF_ID);
            SORT_BY_DEFAULT = getString(R.string.SORT_BY_DATE);
            SORT_ORDER_DEFAULT = getString(R.string.SORT_ORDER_HIGH_TO_LOW);

            this.sharedPreferences = sharedPreferences;
            SORT_BY_PREFERENCE = sharedPreferences.getString(NOTE_SORT_BY_PREF_KEY, SORT_BY_DEFAULT);
            SORT_ORDER_PREFERENCE = sharedPreferences.getString(NOTE_ORDER_PREF_KEY, SORT_ORDER_DEFAULT);
            this.sharedPreferences.registerOnSharedPreferenceChangeListener(preferenceChangeListener);
//            Map<String, ?> map = sharedPreferences.getAll();
//            Set<String> keys = map.keySet();
//            for (String key : keys) {
//                Log.i(TAG, "Preference Key from default preferences: " + key.toString());
//            }
        }
    }
}
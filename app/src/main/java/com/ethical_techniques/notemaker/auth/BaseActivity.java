package com.ethical_techniques.notemaker.auth;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.ethical_techniques.notemaker.ListActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;
import java.util.prefs.PreferenceChangeListener;


/**
 * The type Base activity.
 *
 * @author Harry Dulaney
 */
public class BaseActivity extends AppCompatActivity {

    private static final String TAG = "BaseActivity.class";
    /**
     * The constant LOGIN_BUNDLE_KEY.
     */
    protected static final String LOGIN_BUNDLE_KEY = "com.ethical_techniques.notemaker.FirebaseUser";
    /**
     * The constant USER_BUNDLE_KEY.
     */
    protected static final String USER_BUNDLE_KEY = "com.ethical_techniques.notemaker.UserProfile";
    /**
     * The constant PREF_KEY_LOCAL_MODE.
     */
    protected static final String PREF_KEY_LOCAL_MODE = "com.ethical_techniques.user.pref.key.local.mode";

    static final int REQUEST_IMAGE_CAPTURE = 12471;


    /**
     * Hide keyboard.
     *
     * @param views the views
     */
    protected void hideKeyboard(Context context, List<View> views) {
        final InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            for (View view : views) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }
}
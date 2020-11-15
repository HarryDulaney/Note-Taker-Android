package com.ethical_techniques.notemaker.auth;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.ethical_techniques.notemaker.ListActivity;
import com.ethical_techniques.notemaker.R;
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

    static final int REQUEST_IMAGE_CAPTURE = 12471;
    static boolean hasCamera;
    static boolean hasWidgets;
    static boolean hasFingerPrint;


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
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

    }
}
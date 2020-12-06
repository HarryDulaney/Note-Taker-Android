package com.ethical_techniques.notemaker.auth;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;

import com.ethical_techniques.notemaker.DAL.DBUtil;
import com.ethical_techniques.notemaker.ListActivity;
import com.ethical_techniques.notemaker.R;
import com.google.firebase.auth.FirebaseAuth;

/**
 * The type App flow activity.
 *
 * @author Harry Dulaney
 */
public class AppFlowActivity extends BaseActivity {
    private static final String LAUNCH_FROM_NOTE_KEY = "com.ethical_techniques.notemaker.NoteActivity";

    //TODO: Read phone features, and set User Preferences
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkEnvFeatures();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkEnvAPIDependentFeatures();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        boolean hasNotes = false;
        try {
            hasNotes = DBUtil.hasNotes(getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        delegateFlow(fAuth, hasNotes);

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void delegateFlow(FirebaseAuth fa, boolean hasLclNotes) {
        if (fa.getCurrentUser() != null) {// If user is already signed in to cloud account
            signedIn = true;
            flow(ListActivity.class); //Open the Main Menu Notes List
        } else if (hasLclNotes) {  //If the user has locally saved Notes (They are not signed in)
            signedIn = false;
            flow(getString(R.string.launch_key), ListActivity.class);
        } else {
            flow(UserLoginActivity.class); //Open the Sign in Activity

        }
    }

    private void flow(Class<?> clazz) {
        Intent i1 = new Intent(this, clazz);
        startActivity(i1);
    }

    private void flow(String message, Class<?> clazz) {
        Intent i1 = new Intent(this, clazz);
        i1.putExtra(message, true);
        startActivity(i1);
    }

    private void checkEnvFeatures() {
        hasCamera = getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
        hasWidgets = getPackageManager().hasSystemFeature(PackageManager.FEATURE_APP_WIDGETS);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkEnvAPIDependentFeatures() {
        hasFingerPrint = getPackageManager().hasSystemFeature(PackageManager.FEATURE_FINGERPRINT);
    }

}

package com.ethical_techniques.notemaker.auth;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;

import com.ethical_techniques.notemaker.ListActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;


public class BaseActivity extends AppCompatActivity {

    private static final String TAG = "BaseActivity.class";

    private static Boolean signedIn = false;
    protected static String LOGIN_BUNDLE_KEY = "com.ethical_techniques.notemaker.FirebaseUser";
    protected static String USER_BUNDLE_KEY = "com.ethical_techniques.notemaker.UserProfile";

    @Override
    protected void onStart() {
        super.onStart();

    }

    protected void hideKeyboard(List<View> views) {
        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            for (View view : views) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    Boolean signIn(FirebaseAuth fAuth) {
        if (fAuth.getCurrentUser() != null) {
            signedIn = true;
        } else {
            signedIn = false;
        }
        return signedIn;
    }

    protected Boolean isSignedIn() {
        return signedIn;
    }
}
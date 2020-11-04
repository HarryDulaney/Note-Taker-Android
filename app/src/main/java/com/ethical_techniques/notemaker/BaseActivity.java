package com.ethical_techniques.notemaker;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;


public class BaseActivity extends AppCompatActivity {

    private static final String TAG = "BaseActivity.class";

    FirebaseAuth firebaseAuth = null;
    FirebaseUser firebaseUser = null;
    private static Boolean loggedIn = false;

    protected static String BUNDLE_KEY = "bundle_key_one";


    @Override
    protected void onCreate(Bundle savedInstanceBundle) {
        super.onCreate(savedInstanceBundle);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseUser = firebaseAuth.getCurrentUser();
    }

    public void hideKeyboard(List<View> views) {
        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            for (View view : views) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    protected void signOutFirebase(FirebaseUser firebaseUser) {
        if (firebaseUser != null) {
            FirebaseAuth.getInstance().signOut();
            Log.i(TAG, "Fbase user " + firebaseUser.getDisplayName() + " logged out or their account.");
        }
    }

    protected boolean signedIn() {
        if (!loggedIn) return false;
        return firebaseUser != null;
    }

    protected static void setLoggedIn(Boolean bool) {
        BaseActivity.loggedIn = bool;
    }

}
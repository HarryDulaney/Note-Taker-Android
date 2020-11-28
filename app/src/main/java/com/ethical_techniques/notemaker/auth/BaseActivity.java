package com.ethical_techniques.notemaker.auth;

import android.content.Context;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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

    static boolean hasCamera;
    static boolean hasWidgets;
    static boolean hasFingerPrint;
    static boolean signedIn;


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

    protected void initCloudStorageUtil() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            CloudStorageUtil.setRootStorageReference(FirebaseStorage.getInstance().getReference());
        }
    }
}
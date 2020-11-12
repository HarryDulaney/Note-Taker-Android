package com.ethical_techniques.notemaker.auth;

import android.content.Intent;
import android.os.Bundle;

import com.ethical_techniques.notemaker.DAL.DBUtil;
import com.ethical_techniques.notemaker.ListActivity;
import com.ethical_techniques.notemaker.R;
import com.google.firebase.auth.FirebaseAuth;

import javax.annotation.Nullable;

/**
 * The type App flow activity.
 *
 * @author Harry Dulaney
 */
public class AppFlowActivity extends BaseActivity {
    private static final String LAUNCH_MESSAGE_KEY = "com.ethical_techniques.notemaker.showLoginDecisionDialog";
    private static final String LAUNCH_FROM_NOTE_KEY = "com.ethical_techniques.notemaker.NoteActivity";

    //TODO: Network Security configuration
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
            flow(ListActivity.class); //Open the Main Menu Notes List
        } else if (hasLclNotes) {  //If the user has locally saved Notes (They are not signed in)
            flow(LAUNCH_MESSAGE_KEY, ListActivity.class);
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
        i1.putExtra(LAUNCH_MESSAGE_KEY, true);
        startActivity(i1);
    }

    /**
     * Gets launch key.
     *
     * @return the launch key
     */
    public static String getLaunchKey() {
        return LAUNCH_MESSAGE_KEY;
    }


}

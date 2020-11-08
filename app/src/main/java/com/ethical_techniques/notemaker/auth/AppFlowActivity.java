package com.ethical_techniques.notemaker.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.ethical_techniques.notemaker.ListActivity;
import com.ethical_techniques.notemaker.auth.BaseActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AppFlowActivity extends BaseActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onStart() {
        super.onStart();
        signIn(firebaseAuth);
    }

    private void delegateFlow() {
        if (isSignedIn()) {
            openMainMenu(this, ListActivity.class);
        }
    }

    private void openMainMenu(Context packageContext, Class<?> cls) {
        Intent i1 = new Intent(packageContext, cls);
        i1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        packageContext.startActivity(i1);
    }


}

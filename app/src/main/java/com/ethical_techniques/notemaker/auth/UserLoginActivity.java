package com.ethical_techniques.notemaker.auth;

import android.content.Context;
import android.os.Bundle;

import com.ethical_techniques.notemaker.BaseActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.ethical_techniques.notemaker.R;

public class UserLoginActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);
        Toolbar toolbar = findViewById(R.id.action_bar_login);
        setSupportActionBar(toolbar);
    }

}
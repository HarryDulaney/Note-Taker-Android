package com.ethical_techniques.notemaker.auth;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.ethical_techniques.notemaker.BaseActivity;
import com.ethical_techniques.notemaker.ListActivity;
import com.ethical_techniques.notemaker.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import android.text.TextUtils;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.time.Duration;

/**
 * The type User login activity.
 */
public class UserLoginActivity extends BaseActivity {

    private static final String TAG = "UserLoginActivity";
    private FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_user_login);
        Toolbar toolbar = findViewById(R.id.action_bar_login);
        setSupportActionBar(toolbar);
    }


    private void openMainMenu() {
        Intent i1 = new Intent(UserLoginActivity.this, ListActivity.class);
        i1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        UserLoginActivity.this.startActivity(i1);

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_bar_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.skip_login_button) {
            Intent i1 = new Intent(UserLoginActivity.this, ListActivity.class);
            i1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            UserLoginActivity.this.startActivity(i1);

        } else {
            return super.onOptionsItemSelected(item);
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onStart() {
        super.onStart();
        if (fAuth != null) {
            FirebaseUser fUser = fAuth.getCurrentUser();
            if (fUser != null) {
                openMainMenu();
            }
        }
    }


    /**
     * Handle open register user.
     *
     * @param view the view
     */
    public void handleOpenRegisterUser(View view) {
        Intent intent = new Intent(UserLoginActivity.this, UserRegisterActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        UserLoginActivity.this.startActivity(intent);
    }

    /**
     * Handle user login event.
     *
     * @param view the view
     */
    public void handleUserLoginEvent(View view) {
        final EditText edTextEmail = findViewById(R.id.editTextLoginEmailAddress);
        final EditText edTextPword = findViewById(R.id.editTextPwordLogin);
        if (TextUtils.isEmpty(edTextEmail.getText()) || TextUtils.isEmpty(edTextPword.getText())) {
            Snackbar.make(view.getRootView(), "Both email and password must be filled in to validate your sign in credentials", BaseTransientBottomBar.LENGTH_LONG).show();
        } else {
            fAuth.signInWithEmailAndPassword(edTextEmail.getText().toString(), edTextPword.getText().toString())
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            setLoggedIn(true);
                            Log.d(TAG, "User sign In with email address: success");
                            FirebaseUser user = fAuth.getCurrentUser();
                            Toast.makeText(UserLoginActivity.this, "Welcome back " + user.getDisplayName() + " you are now logged into your account",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            setLoggedIn(false);
                            Log.w(TAG, "Email Login In to Firebase status: failed", task.getException());
                            Toast.makeText(UserLoginActivity.this, "We could not find a user matching those credentials. " +
                                            "Please double check the email address and password are correct.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}

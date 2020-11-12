package com.ethical_techniques.notemaker.auth;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;

import com.ethical_techniques.notemaker.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

/**
 * The type Update profile activity.
 *
 * @author Harry Dulaney
 */
public class UpdateProfileActivity extends BaseActivity implements View.OnClickListener {

    private final String TAG = getClass().getName();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


    }

    private void initUIProfileInfo(FirebaseUser firebaseUser) {
        String fbaseUsername = firebaseUser.getDisplayName();
        String email = firebaseUser.getEmail();
        Uri uriPhoto = firebaseUser.getPhotoUrl();

        EditText userName = findViewById(R.id.editTextInputUserNameUpdate);
        if (fbaseUsername != null) {
            if (!fbaseUsername.isEmpty()) {
                userName.setText(fbaseUsername);
            } else {
                userName.setText("Set Display Name Here");
            }
        }
        TextView emailView = findViewById(R.id.emailAddressDisplay);
        if (email != null) {
            if (!email.isEmpty()) {
                emailView.setText(email);
            } else {
                emailView.setText("No Email Set");
            }
        }
        ImageView photoView = findViewById(R.id.imageView);
        if (uriPhoto != null) {
            if (!uriPhoto.toString().isEmpty()) {
                photoView.setImageURI(uriPhoto);
            }
        }


    }


    @Override
    protected void onStart() {
        super.onStart();
        initUIProfileInfo(FirebaseAuth.getInstance().getCurrentUser());
    }

    /**
     * Handle update user profile.
     *
     * @param view the view
     */
    public void handleUpdateUserProfile(View view) {
    }

    /**
     * Handle update user email.
     *
     * @param view the view
     */
    public void handleUpdateUserEmail(View view) {
    }

    /**
     * Send email verification.
     */
    public void sendEmailVerification() {
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        if (fUser != null) {
            fUser.sendEmailVerification()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this,
                                    "We sent a confirmation to your the email address associated with your account." +
                                            " Please check your email to complete verification.",
                                    Toast.LENGTH_LONG).show();
                            Log.d(TAG, "Verification email sent.");
                        }
                    });
        }
    }


    /**
     * Update name.
     *
     * @param name  the name
     * @param fUser the f user
     */
    protected void updateName(String name, FirebaseUser fUser) {
        UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .setPhotoUri(Uri.parse("https://"))
                .build();

        fUser.updateProfile(profileChangeRequest)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Your display name has been set to: " + fUser.getDisplayName(), Toast.LENGTH_LONG).show();
                    }
                });
    }


    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.changeProfilePic) {

        } else if (id == R.id.updateEmailAddressButton) {

        } else if (id == R.id.handleRegisterSubmit) {

        } else if (id == R.id.exitProfileUpdate) {

        }

    }
}

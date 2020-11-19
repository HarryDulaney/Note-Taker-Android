package com.ethical_techniques.notemaker.auth;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import com.ethical_techniques.notemaker.R;
import com.ethical_techniques.notemaker.listeners.TextWatcherImpl;
import com.ethical_techniques.notemaker.utils.DialogUtil;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthEmailException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The type Update profile activity.
 *
 * @author Harry Dulaney
 */
public class UpdateProfileActivity extends BaseActivity {

    private final String TAG = getClass().getName();
    private ViewGroup mainLayout;
    private ViewGroup emailUpdateLayout;
    private ValueHolder valueHolder;
    private boolean emailUpdateScreen;
    private Menu menu;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        mainLayout = findViewById(R.id.layoutUpdateUserProfile);
        emailUpdateLayout = findViewById(R.id.email_update_layout);

    }

    private void initUIProfileInfo(FirebaseUser firebaseUser) {
        String pPath = "";
        String dName = "";
        String email = "";
        if (firebaseUser.getDisplayName() != null) {
            dName = firebaseUser.getDisplayName();
        }
        if (firebaseUser.getPhotoUrl() != null) {
            pPath = firebaseUser.getPhotoUrl().getPath();
        }
        if (firebaseUser.getEmail() != null) {
            email = firebaseUser.getEmail();
        }

        valueHolder = new ValueHolder(dName, email, pPath);

        TextInputEditText username = findViewById(R.id.editTextInputUserNameUpdate);
        if (!valueHolder.currDisName.isEmpty()) {
            username.setText(valueHolder.currDisName);
        } else {
            username.setText(R.string.dis_name_empty_message);

        }

        username.addTextChangedListener(new TextWatcherImpl() {
            @Override
            public void afterTextChanged(Editable s) {
                valueHolder.currDisName = username.getText().toString();
            }
        });

        TextView emailView = findViewById(R.id.emailAddressDisplay);
        if (!valueHolder.currEmail.isEmpty()) {
            emailView.setText(valueHolder.currEmail);
        } else {
            emailView.setText(R.string.no_email_set);

        }
        ImageView photoView = findViewById(R.id.imageView);
        Uri uriPhoto = firebaseUser.getPhotoUrl();

        if (!valueHolder.currPicPath.isEmpty()) {
            photoView.setImageURI(uriPhoto);
        }

        MaterialButton mb = findViewById(R.id.verifyButton);
        if (firebaseUser.isEmailVerified()) {
            mb.setVisibility(View.GONE);
        } else {
            mb.setVisibility(View.VISIBLE);
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        if (fUser != null) {
            initUIProfileInfo(fUser);
        } else {
            throw new SecurityException("Access Denied");
        }
    }

    /**
     * Send email verification.
     *
     * @param fUser the f user
     */
    public void sendEmailVerification(FirebaseUser fUser) {
        if (fUser != null) {
            fUser.sendEmailVerification()
                    .addOnCompleteListener(task -> {
                        if (task.isComplete()) {
                            Toast.makeText(this,
                                    "We sent a confirmation link to your email address." +
                                            " Please check your email and click the link to complete verification.",
                                    Toast.LENGTH_LONG).show();
                            Log.d(TAG, "Verification email sent.");
                        } else {
                            Toast.makeText(this,
                                    "Something went wrong, we couldn't send the verification link. Please try again later.",
                                    Toast.LENGTH_LONG).show();
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
                .build();

        fUser.updateProfile(profileChangeRequest)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Your display name has been set to: " + fUser.getDisplayName(), Toast.LENGTH_LONG).show();
                    }
                });
    }


    private void launchTakePicture() {
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            startActivityForResult(i, REQUEST_IMAGE_CAPTURE);
        } catch (ActivityNotFoundException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private void launchPicFromStorage() {


    }

    /**
     * Handle exit profile update.
     *
     * @param view the view
     */
    public void handleExitProfileUpdate(View view) {
        hideKeyboard(this, view.getRootView());
        finish();
    }

    /**
     * Handle verify email address.
     *
     * @param view the view
     */
    public void handleVerifyEmailAddress(View view) {

        //Launch verify email event sequence
        hideKeyboard(this, view.getRootView());
        sendEmailVerification(FirebaseAuth.getInstance().getCurrentUser());
    }

    /**
     * Handle clear form.
     *
     * @param view the view
     */
    public void handleClearForm(View view) {

        hideKeyboard(this, view.getRootView());
        TextInputEditText nooEmail = findViewById(R.id.editTextNooEmail);
        TextInputEditText nooEmailCheck = findViewById(R.id.editTextEmailCheck);
        TextInputEditText pw = findViewById(R.id.pword);
        Objects.requireNonNull(nooEmail.getText()).clear();
        Objects.requireNonNull(nooEmailCheck.getText()).clear();
        Objects.requireNonNull(pw.getText()).clear();

    }

    /**
     * Handle submit email address.
     *
     * @param view the view
     */
    public void handleSubmitEmailAddress(View view) {
        hideKeyboard(this, view.getRootView());

        final TextInputEditText nooEmail = findViewById(R.id.editTextNooEmail);
        final TextInputEditText nooEmailCheck = findViewById(R.id.editTextEmailCheck);
        final TextInputEditText pw = findViewById(R.id.pword);

        if (nooEmail.length() < 3 || nooEmailCheck.length() < 3 || pw.length() < 8) {

            if (nooEmail.getText().equals(nooEmailCheck.getText())) {
                if (validateEmail(nooEmail.getText())) {
                    if (validatePassword(pw.getText())) {

                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        AuthCredential creds = EmailAuthProvider
                                .getCredential(valueHolder.currEmail, pw.getText().toString());
                        user.reauthenticate(creds)
                                .addOnCompleteListener(task -> {
                                    Log.d(TAG, "User re-authenticated.");
                                    try {
                                        updateUserEmailAddress(user, nooEmail.getText().toString());
                                    } catch (FirebaseAuthEmailException | FirebaseAuthInvalidCredentialsException fee) {
                                        fee.printStackTrace();
                                    } catch (FirebaseAuthUserCollisionException ffce) {
                                        Toast.makeText(this, "The email address you entered already belongs to an existing account, please re-try with a different email address.", Toast.LENGTH_LONG).show();

                                    }

                                });

                    } else {
                        Toast.makeText(this, "The password you entered appears to be in an invalid format, please check again and re-try.", Toast.LENGTH_SHORT).show();

                    }
                } else {
                    Toast.makeText(this, "The email address you entered is not a valid format, please double check.", Toast.LENGTH_SHORT).show();

                }
            } else {
                Toast.makeText(this, "The both email address above must be equal, please check again.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Please fill in all of the fields before pressing the Submit button.", Toast.LENGTH_SHORT).show();

        }
    }

    private void updateUserEmailAddress(FirebaseUser firebaseUser, String str) throws FirebaseAuthInvalidCredentialsException,
            FirebaseAuthEmailException, FirebaseAuthUserCollisionException {


        firebaseUser.updateEmail(str)
                .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Snackbar.make(emailUpdateLayout, "Your email address has been updated, please use the button on the next screen to verify.", Snackbar.LENGTH_LONG);
                                Log.d(TAG, "User email address updated.");
                                reInitMainLayout();
                            } else {
                                Snackbar.make(emailUpdateLayout, "Something went wrong, we could not update your email address. Please visit our github page for help.", Snackbar.LENGTH_LONG);
                                reInitMainLayout();
                            }
                        }
                );
    }

    private void initUpdateEmail() {
        emailUpdateScreen = true;
        mainLayout.setVisibility(View.GONE);
        emailUpdateLayout.setVisibility(View.VISIBLE);
        onPrepareOptionsMenu(menu);

    }

    private void reInitMainLayout() {
        emailUpdateScreen = false;
        emailUpdateLayout.setVisibility(View.GONE);
        mainLayout.setVisibility(View.VISIBLE);
        onPrepareOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        if (emailUpdateScreen) {
            reInitMainLayout();
        } else {
            FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
            if (fuser != null) {
                updateName(valueHolder.currDisName, fuser);
            }
            super.onBackPressed();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.update_profile_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        this.menu = menu;
        MenuItem cancelButton = menu.findItem(R.id.cancel);
        if (emailUpdateScreen) {
            cancelButton.setVisible(true);

        }
        if (!emailUpdateScreen) {
            cancelButton.setVisible(false);
        }
        return true;
    }

    /**
     * @param item that was selected
     * @return true if the item is recognized
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (emailUpdateScreen) {
                reInitMainLayout();
            } else {
                onBackPressed();
            }
        } else if (id == R.id.cancel) {
            hideKeyboard(this, emailUpdateLayout.getRootView());
            reInitMainLayout();
        }

        return true;

    }

    private static boolean validatePassword(CharSequence charSequence) {
        if (charSequence == null || charSequence.length() < 8 || charSequence.length() > 20) {
            return false;
        }
        String pwRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,20}$";
        Pattern pwPattern = Pattern.compile(pwRegex);
        Matcher matcher = pwPattern.matcher(charSequence);
        return matcher.matches();
    }

    private static boolean validateEmail(CharSequence charSequence) {
        if (charSequence == null || charSequence.length() < 3) {
            return false;
        }
        final String emailValidationRegEx = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
        Pattern emailPat = Pattern.compile(emailValidationRegEx);
        Matcher matcher = emailPat.matcher(charSequence);
        return matcher.matches();
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_" + "notes_for_android_prof_pic";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        valueHolder.currPicPath = image.getAbsolutePath();
        return image;
    }

    /**
     * Handle change prof pic.
     *
     * @param view the view
     */
    public void handleChangeProfPic(View view) {
        //Change picture options
        DialogUtil.makeAndShow(this,
                "Set Profile Picture",
                "From where do you want to retrieve your profile picture?",
                "Take Picture",
                "Open File Explorer",
                "Close Popup",
                this::launchTakePicture,
                this::launchPicFromStorage,
                () -> {
                    Toast.makeText(this, "Operation Canceled", Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Handle email address change.
     *
     * @param view the view
     */
    public void handleEmailAddressChange(View view) {
        //launch email update ui and events
        hideKeyboard(this, findViewById(R.id.verifyButton).getRootView());
        initUpdateEmail();
    }

    /**
     * Handle submit button.
     *
     * @param view the view
     */
    public void handleSubmitButton(View view) {
        hideKeyboard(this, findViewById(R.id.handleDoneButton).getRootView());
        //Submit update account user profile
        onBackPressed();
    }

    /**
     * Handle edit display name.
     *
     * @param view the view
     */
    public void handleEditDisplayName(View view) {
    }

    private static class ValueHolder {

        private String currPicPath;
        private String currEmail;
        private String currDisName;

        /**
         * Instantiates a new Value holder.
         */
        ValueHolder() {
            throw new SecurityException("Unsupported Operation");
        }

        /**
         * Instantiates a new Value holder.
         *
         * @param currDisName the curr dis name
         * @param currEmail   the curr email
         * @param currPicPath the curr pic path
         */
        ValueHolder(String currDisName, String currEmail, String currPicPath) {
            this.currDisName = currDisName;
            this.currEmail = currEmail;
            this.currPicPath = currPicPath;

        }
    }

}
